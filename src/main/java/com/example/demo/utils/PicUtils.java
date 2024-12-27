package com.example.demo.utils;
/*
 * @ProjectName: 智能建筑
 * @Copyright: 2012 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2019-06-24 19:56
 * @Description: 本内容仅限于杭州海康威视数字技术系统公司内部使用，禁止转发.
 */

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Base64;

/**
 * <p></p>
 *
 * @author sunhuan 2019-06-24 19:56
 * @version V1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2019-06-24
 * @modify by reason:{方法名}:{原因}
 */
public class PicUtils {

    private static Logger logger = LoggerFactory.getLogger(PicUtils.class);

    /**
     * 人脸图片最大值
     */
    public static final int FACE_PIC_MAX_SIZE = 185;
    /**
     * 人脸图片最小值
     */
    public static final int FACE_PIC_MIN_SIZE = 10;
    /**
     * 人脸图片放大值
     */
    public static final int FACE_PIC_ZOOM_SIZE = 25;

    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @param desFileSize 指定图片大小，单位kb
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScale(byte[] imageBytes, long desFileSize) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        try {
            //图片压缩质量比
            double accuracy = getAccuracy(srcSize / 1024);
            int i = 1;
            while (imageBytes.length >= desFileSize * 1024) {
                logger.info("图片被压缩次数：" + (i++));
                //源图片字节数组转为流对象
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                //用来存储压缩后的图像
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                //源文件流
                Thumbnails.of(inputStream)
                        //压缩大小
                        .scale(accuracy)
                        //压缩质量
                        .outputQuality(accuracy)
                        //输出到流
                        .toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
            }
            logger.info("【图片压缩】图片原大小={}kb | 压缩后大小={}kb", srcSize / 1024, imageBytes.length / 1024);
        } catch (Exception e) {
            logger.error("【图片压缩】msg=图片压缩失败!", e);
        }
        return imageBytes;
    }

    /**
     * 根据指定大小压缩图片
     *
     * @param imageBytes  源图片字节数组
     * @return 压缩质量后的图片字节数组
     */
    public static byte[] compressPicForScale(byte[] imageBytes) {
        long desFileSize = FACE_PIC_MAX_SIZE;
        return compressPicForScale(imageBytes, desFileSize);
    }

    /**
     * 自动调节精度(经验数值)
     *
     * @param size 源图片大小
     * @return 图片压缩质量比
     */
    private static double getAccuracy(long size) {
        double accuracy;
        if (size < 500) {
            accuracy = 0.98;
        } else if (size < 2047) {
            accuracy = 0.85;
        } else if (size < 3275) {
            accuracy = 0.78;
        } else {
            accuracy = 0.65;
        }
        return accuracy;
    }


    public static byte[] fileToBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (null != fis) {
                        fis.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return buffer;
    }


    public static void bytesToFile(byte[] buffer, final String filePath) {
        File file = new File(filePath);
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            output = new FileOutputStream(file);

            bufferedOutput = new BufferedOutputStream(output);

            bufferedOutput.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedOutput) {
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void base64ToFile(String base64Str, final String filePath) {
        byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(base64Str);
        File file = new File(filePath);
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            output = new FileOutputStream(file);

            bufferedOutput = new BufferedOutputStream(output);

            bufferedOutput.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedOutput) {
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void zoomImage(String src, String dest, Integer size) throws Exception {
        File srcFile = new File(src);
        File destFile = new File(dest);
        long fileSize = srcFile.length();
        Double rate = (size * 1024 * 0.5) / fileSize;
        BufferedImage bufImg = ImageIO.read(srcFile);
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        Image imgTemp = ato.filter(bufImg, null);
        ImageIO.write((BufferedImage) imgTemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
    }

    public static byte[] zoomImage(String picUrl, Integer fileSize, Integer zoomSize) {
        byte[] bytes = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(picUrl);
            Double rate = (zoomSize * 1024 * 0.5) / fileSize;
            BufferedImage bufImg = ImageIO.read(url);
            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
            Image imgTemp = ato.filter(bufImg, null);
            ImageIO.write((BufferedImage) imgTemp, TYPE_JPG, baos);
            bytes = baos.toByteArray();
        } catch (Exception e) {
            logger.error("【图片放大】msg=图片放大失败!", e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                logger.error("关闭输出流发生异常!", e);
            }
        }
        return bytes;
    }

    /**
     * 方法的功能描述：获取图片文件类型
     *
     * @param
     * @return
     * @throws
     * @author sunhuan 2017-11-29 13:48:26
     * @modificationHistory =========================逻辑或功能性重大变更记录
     * @modify by user: {修改人} 2017-11-29
     * @modify by reason:{原因}
     */
    public static String getPicType(File imgFile) {
        FileInputStream fis = null;
        byte[] b = new byte[4];
        try {
            fis = new FileInputStream(imgFile);
            fis.read(b, 0, b.length);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return TYPE_JPG;
            } else if (type.contains("89504E47")) {
                return TYPE_PNG;
            } else if (type.contains("47494638")) {
                return TYPE_GIF;
            } else if (type.contains("424D")) {
                return TYPE_BMP;
            } else {
                return TYPE_UNKNOWN;
            }
        } catch (IOException e) {
            logger.error("获取图片文件类型发生异常!", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("关闭输入流发生异常!", e);
                }
            }
        }
        return null;
    }

    /**
     * 判断图片base64字符串的文件格式
     * @param base64
     * @return
     */
    public static String getImgBase64Type(String base64) {
        byte[] b = Base64.getDecoder().decode(base64);
        String type = "other";
        if (0x424D == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            type = "bmp";
        } else if (0x8950 == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            type = "png";
        } else if (0xFFD8 == ((b[0] & 0xff) << 8 | (b[1] & 0xff))) {
            type = "jpg";
        } else if (0x49492A00 == ((b[0] & 0xff) << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff))) {
            type = "tif";
        }
        return type;
    }

}
