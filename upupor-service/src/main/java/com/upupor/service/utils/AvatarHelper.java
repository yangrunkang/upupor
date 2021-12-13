package com.upupor.service.utils;

/**
 * @author YangRunkang(cruise)
 * @date 2021/01/22 21:57
 */

import com.upupor.service.common.CcConstant;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Random;

import static com.upupor.service.common.CcConstant.DEFAULT_PROFILE_PHOTO;
import static com.upupor.service.utils.OssUtils.checkEnvIsDev;

public class AvatarHelper {


    public static String generateAvatar(Integer hashCode) {
        String url;
        try {
            String fileName = "profile_system/" + CcUtils.getUuId() + CcConstant.ONE_DOTS + "png";
            if (checkEnvIsDev()) {
                return DEFAULT_PROFILE_PHOTO;
            }
            OssUtils.uploadToOss(fileName, create(hashCode));
            url = CcUtils.getProperty("codingvcr.oss.file-host") + fileName;
        } catch (Exception e) {
            url = DEFAULT_PROFILE_PHOTO;
        }
        return url;
    }

    /**
     * 根据id生成一个头像，颜色随机。如果是使用hashCode()值的话，值可能为负数。需要要注意。
     *
     * @param id
     * @return
     * @throws IOException
     */
    private static InputStream create(int id) throws IOException {
        int width = 20;
        int grid = 5;
        int padding = width / 2;
        int size = width * grid + width;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setColor(new Color(240, 240, 240));
        graphics2d.fillRect(0, 0, size, size);
        graphics2d.setColor(randomColor(80, 200));
        char[] idchars = createIdent(id);
        int i = idchars.length;
        for (int x = 0; x < Math.ceil(grid / 2.0); x++) {
            for (int y = 0; y < grid; y++) {
                if (idchars[--i] < 53) {
                    graphics2d.fillRect((padding + x * width), (padding + y * width), width, width);
                    if (x < Math.floor(grid / 2)) {
                        graphics2d.fillRect((padding + ((grid - 1) - x) * width), (padding + y * width), width, width);
                    }
                }
            }
        }
        graphics2d.dispose();
        return getImageStream(img);
    }

    private static Color randomColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(Math.abs(bc - fc));
        int g = fc + random.nextInt(Math.abs(bc - fc));
        int b = fc + random.nextInt(Math.abs(bc - fc));
        return new Color(r, g, b);
    }

    private static char[] createIdent(int id) {
        BigInteger biContent = new BigInteger((id + "").getBytes());
        BigInteger bi = new BigInteger(id + "upupor" + CcUtils.getUuId() + id, 36);
        bi = bi.xor(biContent);
        return bi.toString(10).toCharArray();
    }

    private static InputStream getImageStream(BufferedImage bimage) {
        InputStream is = null;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageOutputStream imOut;
        try {
            imOut = ImageIO.createImageOutputStream(bs);
            ImageIO.write(bimage, "png", imOut);
            is = new ByteArrayInputStream(bs.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
