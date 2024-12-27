package com.example.demo.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 */

/**
 * Created on Discription:[convert GIF->JPG GIF->PNG PNG->GIF(X)
 * PNG->JPG ]
 *
 * @author xinghuafang
 */

public class ImgConvert {
    /**
     * @param file 图片
     * @return boolean true：符合要求
     * @description 图片文件转化为BufferedImage
     */
    public static BufferedImage fileToBufferedImage(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        BufferedImage bufferedImage = ImageIO.read(file);
        return bufferedImage;
    }
    /**
     * @param fileByteArray
     * @return BufferedImage
     * @throws IOException
     * @description 图片文件字节数组转化为BufferedImage
     */
    public static BufferedImage fileToBufferedImage(byte[] fileByteArray) throws IOException {
        if (fileByteArray == null || fileByteArray.length == 0) {
            return null;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(fileByteArray);
        BufferedImage bufferedImage = ImageIO.read(stream);
        return bufferedImage;
    }
    public static String BufferedImageToBase64(BufferedImage bufferedImage) {
        //io流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //写入流中
            ImageIO.write(bufferedImage, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转换成字节
        byte[] bytes = baos.toByteArray();
        //转换成base64串
        String png_base64=Base64.encodeBase64String(bytes).trim();
        //删除 \r\n
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");
        return png_base64;
    }

    public static void main(String[] args) throws IOException {
//		byte[] fileStream = FileStreamUtils.getFileStream("https://img-blog.csdnimg.cn/img_convert/1e2573ce1500a6dcc955779729a24f7e.png");
//		//byte[] fileStream = FileStreamUtils.getFileStream("https://imgai.buaa.edu.cn/image/21/3690c36d229a7c40ab77f3cef333bc3e.png");
//		//byte[] fileStream = FileStreamUtils.getFileStream("https://img.zcool.cn/community/01bcca5e593f36a801216518b5f8e9.jpg@1280w_1l_2o_100sh.jpg");
//		BufferedImage bufferedImage = ImgConverter.fileToBufferedImage(fileStream);
//		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
//				bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
//		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
//
////		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(new ByteArrayOutputStream());
////
////		boolean jpg = ImageIO.write(newBufferedImage, "jpg",ChunkStream);
//		String base64 = ImgConverter.BufferedImageToBase64(newBufferedImage);
//		//System.out.println(b);
//		System.out.println(base64);
        String s = pngCoverTojpg("https://img-blog.csdnimg.cn/img_convert/1e2573ce1500a6dcc955779729a24f7e.png");
        System.out.println(s);
    }

    /**
     * png图片url转化为jpg 的 base64码流
     * @param url
     * @return
     * @throws IOException
     */
    public static String pngCoverTojpg(String url) throws IOException {
        byte[] fileStream = FileStreamUtils.getFileStream(url);
        //byte[] fileStream = FileStreamUtils.getFileStream("https://img-blog.csdnimg.cn/img_convert/1e2573ce1500a6dcc955779729a24f7e.png");
        //byte[] fileStream = FileStreamUtils.getFileStream("https://imgai.buaa.edu.cn/image/21/3690c36d229a7c40ab77f3cef333bc3e.png");
        //byte[] fileStream = FileStreamUtils.getFileStream("https://img.zcool.cn/community/01bcca5e593f36a801216518b5f8e9.jpg@1280w_1l_2o_100sh.jpg");
        BufferedImage bufferedImage = fileToBufferedImage(fileStream);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
        String base64 = BufferedImageToBase64(newBufferedImage);
        //System.out.println(base64);
        return base64;
    }

    /**
     * png码流转化为jpg的base64编码
     * @param fileStream
     * @return
     * @throws IOException
     */
    public static String pngStreamCoverTojpg(byte[] fileStream) throws IOException {
        //图片文件字节数组转化为BufferedImage
        BufferedImage bufferedImage = fileToBufferedImage(fileStream);
        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        //创建一个RBG图像，24位深度，成功将32位图转化成24位
        //createGraphics()生成Graphics2D，drawImage()可以用来绘制这个图像。
        // 从(0,0坐标)在图像的非不透明部分下绘制白色背景
        newBufferedImage.createGraphics().drawImage(bufferedImage,
                0,
                0,
                Color.WHITE, null);
        //BufferedImage转为base64
        String base64 = BufferedImageToBase64(newBufferedImage);
        return base64;
    }

    public static String dealOrgName(String name){
        if(StringUtils.isNotBlank(name)){
            if(name.length() > 32){
                name = name.substring(0, 32);
            }
            //     '/\:*?"<>|
            return name.replaceAll("'", "").replaceAll("/", "-")
                    .replaceAll("\\\\", "-").replaceAll(":", "-")
                    .replaceAll("\\*", "").replaceAll("\\?", "")
                    .replaceAll("\"", "").replaceAll("<", "")
                    .replaceAll(">", "").replaceAll("\\|", "");
        }
        return name;
    }

    public static String dealPersonName(String name){
        if(StringUtils.isNotBlank(name)){
            if(name.length() > 32){
                name = name.substring(0, 32);
            }
            //     '/\:*?"<>|
            return name.replaceAll("'", "").replaceAll("/", "-")
                    .replaceAll("\\\\", "-").replaceAll(":", "-")
                    .replaceAll("\\*", "").replaceAll("\\?", "")
                    .replaceAll("\"", "").replaceAll("<", "")
                    .replaceAll(">", "").replaceAll("\\|", "");
        }
        return name;
    }

}