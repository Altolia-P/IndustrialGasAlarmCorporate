package com.niit.industrialgasalarmcorporate.common.utils;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CaptchaGenerator {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LENGTH = 4;
    private static final int WIDTH = 130;
    private static final int HEIGHT = 40;
    private static final SecureRandom RANDOM = new SecureRandom();

    private String lastText;

    public String generateText() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        lastText = sb.toString();
        return lastText;
    }

    public String generateBase64Image(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g.setColor(new Color(245, 250, 255));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Noise lines
        g.setColor(new Color(200, 210, 225));
        for (int i = 0; i < 8; i++) {
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // Text
        Font[] fonts = {
                new Font("Arial", Font.BOLD, 24),
                new Font("Arial", Font.ITALIC, 24),
        };
        for (int i = 0; i < text.length(); i++) {
            g.setFont(fonts[RANDOM.nextInt(fonts.length)]);
            g.setColor(new Color(30 + RANDOM.nextInt(80), 60 + RANDOM.nextInt(100), 120 + RANDOM.nextInt(100)));
            int x = 10 + i * 28 + RANDOM.nextInt(6);
            int y = 26 + RANDOM.nextInt(8);
            g.drawString(String.valueOf(text.charAt(i)), x, y);
        }

        g.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
}
