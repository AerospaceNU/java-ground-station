package com.nuli.groundstation;



import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class QRGenerator {
    public static void generateQRCode(String link, String filePath, int size) throws IOException
    {
        String qrData = link;
        // QR code configuration
        Map<EncodeHintType, Object> hints = new HashMap<>();
        // Set error correction level to H (high) which allows for logos to be embedded
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                qrData, 
                BarcodeFormat.QR_CODE, 
                size, 
                size, 
                hints
        );

        MatrixToImageWriter.writeToPath(
            matrix, 
            "PNG", 
            Paths.get(filePath)
        );

        System.out.println("QR Code generated successfully at " + filePath);

    } catch (WriterException e) {
        System.out.println("Error generating QR code: " + e.getMessage());
    }

        System.out.println("QR Code generated successfully at " + filePath);

    }

}
