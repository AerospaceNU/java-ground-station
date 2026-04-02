package com.nuli.groundstation;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nuli.proto.NULIMessageProto.NULIMessage;

/**
 * Generic framed packet encoder/decoder.
 *
 * Packet format:
 *   [START_FLAG] [...BYTE-STUFFED PAYLOAD + CRC...] [STOP_FLAG]
 *
 * Pipeline:
 *   Encode: serialize payload → append CRC-16 → byte-stuff → frame with flags
 *   Decode: strip flags → un-stuff → validate CRC-16 → deserialize payload
 *
 * The payload serialization/deserialization is delegated to a {@link PayloadCodec},
 * so this class works with any message format (protobuf, custom binary, etc.).
 */
public class PacketUtils {

    // --- Frame constants (override via constructor if needed) ---
    private static final byte START_FLAG   = 0x7E;
    private static final byte STOP_FLAG    = 0x7F;
    private static final byte CONTROL_FLAG = 0x7D;
    private static final byte ESCAPE_XOR   = 0x20;

    private static final int CRC_BYTES = 2;

    // --- Exceptions ---
    public static class PacketException extends Exception {
        public PacketException(String msg) { super(msg); }
        public PacketException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class InvalidStartFlagException extends PacketException {
        public InvalidStartFlagException(byte got) {
            super(String.format("Expected start 0x%02X, got 0x%02X", START_FLAG & 0xFF, got & 0xFF));
        }
    }

    public static class InvalidStopFlagException extends PacketException {
        public InvalidStopFlagException(byte got) {
            super(String.format("Expected stop 0x%02X, got 0x%02X", STOP_FLAG & 0xFF, got & 0xFF));
        }
    }

    public static class InvalidCRCException extends PacketException {
        public InvalidCRCException(int received, int calculated) {
            super(String.format("CRC mismatch: received 0x%04X, calculated 0x%04X", received, calculated));
        }
    }

    // --- Codec interface ---
    /**
     * Implement this to plug in any serialization format.
     * For protobuf: serialize() = message.toByteArray(), deserialize() = YourMessage.parseFrom(data).
     */
    public interface PayloadCodec<T> {
        byte[] serialize(T message) throws PacketException;
        T deserialize(byte[] data) throws PacketException;
    }

    // --- Built-in NULIMessage codec ---
    /**
     * PayloadCodec for NULIMessage protobuf messages.
     * Use: new PacketUtils.NULIMessageCodec()
     */
    public static class NULIMessageCodec implements PayloadCodec<NULIMessage> {

        @Override
        public byte[] serialize(NULIMessage message) throws PacketException {
            try {
                return message.toByteArray();
            } catch (Exception e) {
                throw new PacketException("Protobuf serialization failed", e);
            }
        }

        @Override
        public NULIMessage deserialize(byte[] data) throws PacketException {
            try {
                return NULIMessage.parseFrom(data);
            } catch (InvalidProtocolBufferException e) {
                throw new PacketException("Protobuf deserialization failed", e);
            }
        }
    }

    // --- Encode ---
    /**
     * Encodes an object into a fully framed packet.
     *
     * @param message the application-layer message
     * @param codec   serializer/deserializer for the message type
     * @return the complete framed packet as a byte array
     */
    public <T> byte[] encode(T message, PayloadCodec<T> codec) throws PacketException {
        // 1. Serialize payload
        byte[] payload = codec.serialize(message);

        // 2. Calculate CRC over the raw payload
        int crc = calculateCRC(payload, 0, payload.length);

        // 3. Append CRC (little-endian) to payload
        byte[] payloadWithCRC = new byte[payload.length + CRC_BYTES];
        System.arraycopy(payload, 0, payloadWithCRC, 0, payload.length);
        payloadWithCRC[payload.length]     = (byte) (crc & 0xFF);
        payloadWithCRC[payload.length + 1] = (byte) ((crc >> 8) & 0xFF);

        // 4. Byte-stuff
        byte[] stuffed = addByteStuffing(payloadWithCRC);

        // 5. Frame: [START] [stuffed] [STOP]
        byte[] packet = new byte[1 + stuffed.length + 1];
        packet[0] = START_FLAG;
        System.arraycopy(stuffed, 0, packet, 1, stuffed.length);
        packet[packet.length - 1] = STOP_FLAG;

        return packet;
    }

    // --- Decode ---
    /**
     * Decodes a fully framed packet back into an application-layer message.
     *
     * @param buffer the raw packet bytes (including start/stop flags)
     * @param codec  serializer/deserializer for the message type
     * @return the decoded message
     */
    public <T> T decode(byte[] buffer, PayloadCodec<T> codec) throws PacketException {
        if (buffer.length < 1 + CRC_BYTES + 1) {
            throw new PacketException("Buffer too short: " + buffer.length + " bytes");
        }

        // 1. Validate flags
        if (buffer[0] != START_FLAG) {
            throw new InvalidStartFlagException(buffer[0]);
        }
        if (buffer[buffer.length - 1] != STOP_FLAG) {
            throw new InvalidStopFlagException(buffer[buffer.length - 1]);
        }

        // 2. Extract stuffed region (between flags)
        byte[] stuffedPayload = new byte[buffer.length - 2];
        System.arraycopy(buffer, 1, stuffedPayload, 0, stuffedPayload.length);

        // 3. Remove byte stuffing
        byte[] unstuffed = removeByteStuffing(stuffedPayload);

        if (unstuffed.length < CRC_BYTES) {
            throw new PacketException("Unstuffed payload too short to contain CRC");
        }

        // 4. Split payload and CRC
        int payloadLen = unstuffed.length - CRC_BYTES;
        int receivedCRC = (unstuffed[payloadLen] & 0xFF)
                        | ((unstuffed[payloadLen + 1] & 0xFF) << 8);
        int actualCRC = calculateCRC(unstuffed, 0, payloadLen);

        if (receivedCRC != actualCRC) {
            throw new InvalidCRCException(receivedCRC, actualCRC);
        }

        // 5. Deserialize
        byte[] payloadBytes = new byte[payloadLen];
        System.arraycopy(unstuffed, 0, payloadBytes, 0, payloadLen);
        return codec.deserialize(payloadBytes);
    }

    // --- Byte stuffing ---
    private byte[] addByteStuffing(byte[] src) {
        ByteArrayOutputStream dst = new ByteArrayOutputStream(src.length * 2);
        for (byte b : src) {
            if (b == START_FLAG || b == STOP_FLAG || b == CONTROL_FLAG) {
                dst.write(CONTROL_FLAG & 0xFF);
                dst.write((b ^ ESCAPE_XOR) & 0xFF);
            } else {
                dst.write(b & 0xFF);
            }
        }
        return dst.toByteArray();
    }

    private byte[] removeByteStuffing(byte[] src) throws PacketException {
        ByteArrayOutputStream dst = new ByteArrayOutputStream(src.length);
        for (int i = 0; i < src.length; i++) {
            if (src[i] == CONTROL_FLAG) {
                if (i + 1 >= src.length) {
                    throw new PacketException("Truncated escape sequence");
                }
                i++;
                dst.write((src[i] ^ ESCAPE_XOR) & 0xFF);
            } else {
                dst.write(src[i] & 0xFF);
            }
        }
        return dst.toByteArray();
    }

    // --- CRC-16/Modbus ---
    private int calculateCRC(byte[] buffer, int offset, int length) {
        int crc = 0xFFFF;
        for (int i = offset; i < offset + length; i++) {
            crc ^= (buffer[i] & 0xFF);
            for (int bit = 0; bit < 8; bit++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc >>>= 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
