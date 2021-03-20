package com.github.px.common.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


/**
 * <p>根据图片的方向进行旋转</p>
 *
 * @author <a href="mailto:xipan@bigvisiontech.com">panxi</a>
 * @version 1.0.0
 * @date 2020/4/23 11:44
 * @since 1.0
 */
@Slf4j
public class RotateImage {

    /**
     * 调整手机上传的图片方向
     *
     * @param userHeadImage
     * @param userHeadImage
     * @return
     */
    public static byte[] rotate(MultipartFile userHeadImage, String format) {
        Image src = null;
        int angel = 0;
        try {
            src = ImageIO.read(userHeadImage.getInputStream());
            File file = File.createTempFile("tmp", null);
            userHeadImage.transferTo(file);
            file.deleteOnExit();
            angel = getRotateAngleForPhoto(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int srcWidth = src.getWidth(null);
        int srcHeight = src.getHeight(null);
        // calculate the new image size
        Rectangle rectDes = calcRotatedSize(new Rectangle(new Dimension(
                srcWidth, srcHeight)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rectDes.width, rectDes.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform
        g2.translate((rectDes.width - srcWidth) / 2,
                (rectDes.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);
        g2.drawImage(src, null, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        try {
            ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
            //scaledImage1为BufferedImage，jpg为图像的类型
            ImageIO.write(res, format, imOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bs.toByteArray();
    }

    public static Rectangle calcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angelAlpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angelDaltaWidth = Math.atan((double) src.height / src.width);
        double angelDaltaHeight = Math.atan((double) src.width / src.height);
        int lenDaltaWidth = (int) (len * Math.cos(Math.PI - angelAlpha
                - angelDaltaWidth));
        int lenDaltaHeight = (int) (len * Math.cos(Math.PI - angelAlpha
                - angelDaltaHeight));
        int desWidth = src.width + lenDaltaWidth * 2;
        int desHeight = src.height + lenDaltaHeight * 2;
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    public static int getRotateAngleForPhoto(File file) {
        int angle = 0;
        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
            Directory directory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            if (directory != null && directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
                // Exif信息中方向　　
                int orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
                // 原图片的方向信息
                if (6 == orientation) {
                    //6旋转90
                    angle = 90;
                } else if (3 == orientation) {
                    //3旋转180
                    angle = 180;
                } else if (8 == orientation) {
                    //8旋转90
                    angle = 270;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return angle;
    }
}
