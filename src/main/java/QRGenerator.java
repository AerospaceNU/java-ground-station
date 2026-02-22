

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class QRGenerator {
    public static BufferedImage generateQRCodeImage(String link, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(
            link,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        );

        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public static void generateQRCode(String link, String filePath, int size) throws IOException
    {
        try {
            BufferedImage image = generateQRCodeImage(link, size);
            ImageIO.write(image, "PNG", new File(filePath));

            System.out.println("QR Code generated successfully at " + filePath);

        } catch (WriterException e) {
            System.out.println("Error generating QR code: " + e.getMessage());
        }

    }

}
