package src;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GpsParser {

    private static final int DIV = 10_000_000; // 1e7 — change if yours differs

    // Mirrors your C struct layout — adjust field order/sizes to match yours
    // Assumes: int32 latitude, int32 longitude, uint8 satellites

    private static final int PACKET_SIZE = 17;

    public static void parseSerial(InputStream in) throws Exception {
        byte[] buffer = new byte[PACKET_SIZE];
        int packetNum = 0;

        while (true) {
            int bytesRead = in.readNBytes(buffer, 0, PACKET_SIZE);
            if (bytesRead < PACKET_SIZE) break;
            ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);

            int rawLat = bb.getInt();       // msg->latitude
            int rawLon = bb.getInt();       // msg->longitude
            int satellites = bb.get() & 0xFF; // msg->satellites (unsigned byte)

            parseAndPrint(rawLat, rawLon, satellites);
        }
    }

    public static String parseAndPrint(int rawLat, int rawLon, int satellites) {
        // Replicate the C math
        int lat  = rawLat / DIV;
        int latd = Math.abs(rawLat) % DIV;
        int lon  = rawLon / DIV;
        int lond = Math.abs(rawLon) % DIV;

        // Match your Serial.printf output
        System.out.printf(
            "(latitude %d.%07d) (longitude %d.%07d) (satellites %d)%n",
            lat, latd, lon, lond, satellites
        );

        // Google Maps URL
        String url = String.format(
            "https://maps.google.com/maps?z=12&t=m&q=loc:%d.%07d+%d.%07d",
            lat, latd, lon, lond
        );
        System.out.println(url);

        // Optional: build as a proper double for other uses
        double latDouble = rawLat / (double) DIV;
        double lonDouble = rawLon / (double) DIV;
        System.out.printf("Parsed coords: %.7f, %.7f%n", latDouble, lonDouble);
        return url;
    }
}