/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.service.utils;

/**
 * @author YangRunkang(cruise)
 * @date 2021/01/22 21:57
 */

import com.upupor.framework.CcConstant;
import com.upupor.framework.config.UpuporConfig;
import com.upupor.framework.utils.CcUtils;
import com.upupor.framework.utils.SpringContextUtils;

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

import static com.upupor.framework.utils.CcUtils.checkEnvIsDev;

public class AvatarHelper {


    public static String generateAvatar(Integer hashCode) {
        try {
            String fileName = "profile_system/" + CcUtils.getUuId() + CcConstant.ONE_DOTS + "png";
            if (checkEnvIsDev()) {
                return null;
            }
            OssUtils.uploadToOss(fileName, create(hashCode));
            String fileHost = SpringContextUtils.getBean(UpuporConfig.class).getUploadFilePrefix();
            return fileHost + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
