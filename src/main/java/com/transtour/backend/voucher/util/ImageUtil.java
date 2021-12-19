package com.transtour.backend.voucher.util;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

public class ImageUtil {

    private static final int IMG_WIDTH = 600;
    private static final int IMG_HEIGHT = 300;


    public static BufferedImage convertToBufferedImage(Image img) {

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        // Create a buffered image with transparency
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
    }

    public static Image resizeImage (String document) throws Exception {
        BufferedImage image = null;
        byte[] imageByte;
        BASE64Decoder decoder = new BASE64Decoder();
        imageByte = decoder.decodeBuffer(document);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

        BufferedImage bi = ImageIO.read(bis);
        Image newResizedImage = bi.getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_AREA_AVERAGING);
        bis.close();

        return newResizedImage;

    }

}
