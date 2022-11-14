package mutata.com.github.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Utils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy H:mm");

    public static String encodeFromIsoToUTF8(String content) {
        return new String(content.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public static String encodeAvatar(byte[] data) {
        return Base64.getEncoder().withoutPadding().encodeToString(data);
    }

    public static BufferedImage cropAnImage(double x, double y, double scaleX, double scaleY, BufferedImage src, double width, double height) {
        return src.getSubimage((int) (x * scaleX),(int) (y * scaleY),(int) width,(int) height);
    }
    public static byte[] convertBufferedImageToBytes(BufferedImage image,String format) {
        byte[] bytes = null;
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            bytes = baos.toByteArray();
        }  catch (IOException exception) {
            exception.printStackTrace();
        }
        return bytes;
    }
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
}
