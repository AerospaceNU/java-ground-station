
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GpsParser {

    // Mirrors your C struct layout — adjust field order/sizes to match yours

    private static final int DIV = 10_000_000;
    private static final int PACKET_SIZE = 17;
    private static final int START_FLAG = 0xB5;
    private static final int END_FLAG = 0x62;

    public static String parseSerial(InputStream in) throws Exception {
        int firstByte;
        do {
            firstByte = in.read();
            if (firstByte < 0) {
                return null;
            }
        } while (firstByte != START_FLAG);

        byte[] packet = new byte[PACKET_SIZE];
        packet[0] = (byte) firstByte;

        byte[] remainingBytes = in.readNBytes(PACKET_SIZE - 1);
        if (remainingBytes.length < (PACKET_SIZE - 1)) {
            return null;
        }
        System.arraycopy(remainingBytes, 0, packet, 1, PACKET_SIZE - 1);

        ByteBuffer bb = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);

        int startFlag = Byte.toUnsignedInt(bb.get());
        int type = Byte.toUnsignedInt(bb.get());
        int length = Byte.toUnsignedInt(bb.get());
        int rawLat = bb.getInt();       // msg->latitude
        int rawLon = bb.getInt();       // msg->longitude
        int satellites = bb.getInt(); // msg->satellites
        int crc = Byte.toUnsignedInt(bb.get());
        int endFlag = Byte.toUnsignedInt(bb.get());

        if (startFlag != START_FLAG || endFlag != END_FLAG) {
            return null;
        }


        return parseAndPrint(rawLat, rawLon, satellites);
    }

    public static String parseAndPrint(int rawLat, int rawLon, int satellites) {
        // Replicate the C math
        int lat  = (rawLat / DIV);
        int latd = (Math.abs(rawLat) % DIV);
        int lon  = (rawLon / DIV);
        int lond = (Math.abs(rawLon) % DIV) ;

        // Match your Serial.printf output
        // System.out.printf(
        //     "(latitude %d.%07d) (longitude %d.%07d) (satellites %d)%n",
        //     lat, latd, lon, lond, satellites
        // );

        // Google Maps URL
        String url = String.format(
            "https://maps.google.com/maps?z=12&t=m&q=loc:%d.%07d+%d.%07d",
            lat, latd, lon, lond
        );
        // System.out.println(url);

        // Optional: build as a proper double for other uses
        double latDouble = rawLat / (double) DIV;
        double lonDouble = rawLon / (double) DIV;
        // System.out.printf("Parsed coords: %.7f, %.7f%n", latDouble, lonDouble);
        // System.out.println(rawLat);
        return url;
    }
}