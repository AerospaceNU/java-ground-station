/**
 * Simple round-trip test: build a message, encode it into a framed packet,
 * decode it back, and verify the fields survived the trip.
 *
 * Replace the dummy SimpleMessage with your real protobuf type (or any
 * serializable payload) and swap in the matching codec.
 */
public class PacketUtilsTest {

    // --- Dummy message to stand in for a protobuf type ---
    // Replace this with your actual message class.
    static class SimpleMessage {
        int timestampMs;
        int messageId;
        String payloadType; // e.g. "ping", "deploy", "gps", etc.

        // Add whatever fields your real message has
        float deploymentAltitude;

        byte[] toBytes() {
            // Minimal hand-rolled serialization (swap for protobuf's toByteArray())
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(out);
            try {
                dos.writeInt(timestampMs);
                dos.writeInt(messageId);
                dos.writeUTF(payloadType != null ? payloadType : "");
                dos.writeFloat(deploymentAltitude);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return out.toByteArray();
        }

        static SimpleMessage fromBytes(byte[] data) {
            java.io.DataInputStream dis =
                new java.io.DataInputStream(new java.io.ByteArrayInputStream(data));
            try {
                SimpleMessage msg = new SimpleMessage();
                msg.timestampMs = dis.readInt();
                msg.messageId = dis.readInt();
                msg.payloadType = dis.readUTF();
                msg.deploymentAltitude = dis.readFloat();
                return msg;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // --- Codec wiring ---
    static final PacketUtils.PayloadCodec<SimpleMessage> CODEC = new PacketUtils.PayloadCodec<>() {
        @Override
        public byte[] serialize(SimpleMessage msg) throws PacketUtils.PacketException {
            return msg.toBytes();
        }

        @Override
        public SimpleMessage deserialize(byte[] data) throws PacketUtils.PacketException {
            try {
                return SimpleMessage.fromBytes(data);
            } catch (Exception e) {
                throw new PacketUtils.PacketException("Deserialization failed", e);
            }
        }
    };

    // --- Test ---
    public static void main(String[] args) {
        PacketUtils packetUtils = new PacketUtils();

        // Encode
        SimpleMessage outgoing = new SimpleMessage();
        outgoing.timestampMs = 23;
        outgoing.messageId = 126;
        outgoing.payloadType = "ping";
        outgoing.deploymentAltitude = 2.3f;

        byte[] packet;
        try {
            packet = packetUtils.encode(outgoing, CODEC);
        } catch (PacketUtils.PacketException e) {
            System.out.println("Encoding failed: " + e.getMessage());
            return;
        }

        System.out.println("Encoded packet: " + packet.length + " bytes");
        System.out.print("Raw hex: ");
        for (byte b : packet) System.out.printf("%02X ", b & 0xFF);
        System.out.println();

        // Decode
        try {
            SimpleMessage incoming = packetUtils.decode(packet, CODEC);

            System.out.println("Timestamp:            " + incoming.timestampMs);
            System.out.println("Message Id:           " + incoming.messageId);
            System.out.println("Payload Type:         " + incoming.payloadType);
            System.out.println("Deployment Altitude:  " + incoming.deploymentAltitude);

            // Verify
            assert incoming.timestampMs == 23 : "timestamp mismatch";
            assert incoming.messageId == 126 : "messageId mismatch";
            assert "ping".equals(incoming.payloadType) : "payloadType mismatch";
            assert Math.abs(incoming.deploymentAltitude - 2.3f) < 0.001f : "altitude mismatch";

            System.out.println("\nAll checks passed.");
        } catch (PacketUtils.PacketException e) {
            System.out.println("Decoding failed: " + e.getMessage());
        }
    }
}