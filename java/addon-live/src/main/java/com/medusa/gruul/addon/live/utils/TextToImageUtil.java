package com.medusa.gruul.addon.live.utils;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Base64;
public class TextToImageUtil {
    @SneakyThrows
    public static String textToBase64Image(String firstText, String secondText){
        // 创建一个BufferedImage对象，宽度为200，高度为50
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        // 获取Graphics对象
        Graphics graphics = image.getGraphics();
        // 设置背景色为白色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 100);
        // 设置字体为Arial，粗体，大小为20
        graphics.setFont(new Font("宋体", Font.PLAIN, 16));
        // 设置字体颜色为黑色
        graphics.setColor(Color.BLACK);
        // 在图片中绘制文本
        graphics.drawString(firstText, 0, 30);
        graphics.drawString(secondText, 0, 70);
        // 释放Graphics对象
        graphics.dispose();
        // 创建一个字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 将BufferedImage对象写入字节数组输出流中
        ImageIO.write(image, "png", baos);
        // 使用Base64编码将字节数组转换为字符串
        // 返回Base64编码的字符串
        return StrUtil.concat(true,"data:image/png;base64,", Base64.getEncoder().encodeToString(baos.toByteArray()));
    }
}
