package ru.bareapi.lib.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class BareImageUtils {
	
	/**
	 * Меняет размер входящего bufferedImage изображения на 250x150
	 * @param originalImage Оригинальное изображение капчи
	 * @return Вернёт изображение с изменённым разрешением
	 */
    public static BufferedImage resizeImage(BufferedImage originalImage) {
    	int width = 250;
    	int height = 150;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }
	
	/**
	 * Преобразовать BufferedImage в строчку base64
	 * @param image Входная BufferedImage капча
	 * @return Вовзращает Base64 строку исходя из BufferredImage капчи
	 * @throws IOException
	 */
    public static String imageToBase64(BufferedImage image) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "png", byteArrayOutputStream);
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}
}
