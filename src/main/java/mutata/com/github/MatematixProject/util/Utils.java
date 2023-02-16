package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.dto.CommentDTO;
import mutata.com.github.MatematixProject.service.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class Utils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy H:mm");

    public static String encodeFromIsoToUTF8(String content) {
        return new String(content.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
    }

    public static String encodeAvatar(byte[] data) {
        return Base64.getEncoder().withoutPadding().encodeToString(data);
    }
    public static byte[] decodeAvatar(String base64) {
        return Base64.getDecoder().decode(base64);
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
    public static Date parseDate(String string) throws ParseException {
        return dateFormat.parse(string);
    }

    public static <T> List<T> reversedView(final List<T> list)
    {
        return new AbstractList<>()
        {
            @Override
            public T get(int index)
            {
                return list.get(list.size()-1-index);
            }

            @Override
            public int size()
            {
                return list.size();
            }
        };
    }
    public static Comment toComment(CommentDTO commentDTO, UserService service) {
        Comment comment =new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRating(commentDTO.getRating());
        comment.setReceiver(service.findByNameIgnoreCase(commentDTO.getRecipient()));
        comment.setAuthor(commentDTO.getUsername());
        try {
            comment.setDate(new Date(commentDTO.getDate()));
        } catch (Exception exception) {
            exception.printStackTrace();
            comment.setDate(new Date());
        }
        return comment;
    }
}
