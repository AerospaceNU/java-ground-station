package com.nuli.groundstation;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GpsParser {

    // mirrors the C struct layout used previously

    private static final int DIV = 10_000_000;
    private static final int PACKET_SIZE = 17; //all the configs add up to 17 bytes.

    public static String parseSerial(InputStream in) throws Exception {
        byte[] buffer = new byte[PACKET_SIZE]; 
        int bytesRead = in.readNBytes(buffer, 0, PACKET_SIZE);
        if (bytesRead < PACKET_SIZE) return null; //if bytesRead < PACKET_SIZE, this means that it doesn't feature all the configs so we won't do anything

        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN); //order we read in is reversed with LITTLE_ENDIAN

        //all the different configs. 
        //rawLat, rawLon, and satellites are 4 bytes long each. The rest are 1 byte long each.
        int startFlag = bb.get(); 
        int type = bb.get();
        int length = bb.get();
        int rawLat = bb.getInt();       // msg->latitude 
        int rawLon = bb.getInt();       // msg->longitude
        int satellites = bb.getInt(); // msg->satellites
        int crc = bb.get();
        int endFlag = bb.get();


        return parseAndPrint(rawLat, rawLon, satellites);
    }

    public static String parseAndPrint(int rawLat, int rawLon, int satellites) {
        // Take the raw data and normalize it with the DIV
        int lat  = (rawLat / DIV);
        int latd = (Math.abs(rawLat) % DIV);
        int lon  = (rawLon / DIV);
        int lond = (Math.abs(rawLon) % DIV) ;

        // printing out the lat, latd, lon, lond, and satellites
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

        // Optional: version with doubles, don't think this one works though
        double latDouble = rawLat / (double) DIV;
        double lonDouble = rawLon / (double) DIV;
        System.out.printf("Parsed coords: %.7f, %.7f%n", latDouble, lonDouble);
        System.out.println(rawLat);
        return url;
    }
}