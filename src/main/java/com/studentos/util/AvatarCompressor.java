package com.studentos.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public final class AvatarCompressor {
    private static final long MAX_UPLOAD_BYTES = 5L * 1024 * 1024;
    private static final int MAX_STORED_BYTES = 75 * 1024;
    private static final int[] DIMENSIONS = {256, 224, 192, 160};
    private static final float[] QUALITIES = {0.68f, 0.58f, 0.48f, 0.40f};

    private AvatarCompressor() {
    }

    public static byte[] compressToJpeg(InputStream input, long originalSize) throws IOException {
        if (originalSize <= 0 || originalSize > MAX_UPLOAD_BYTES) {
            throw new IllegalArgumentException("Profile image must be smaller than 5 MB.");
        }

        BufferedImage original = ImageIO.read(input);
        if (original == null || original.getWidth() <= 0 || original.getHeight() <= 0) {
            throw new IllegalArgumentException("Upload a valid JPG or PNG image.");
        }

        byte[] smallest = null;
        for (int dimension : DIMENSIONS) {
            BufferedImage resized = resizePreservingContent(original, dimension);
            for (float quality : QUALITIES) {
                byte[] candidate = writeJpeg(resized, quality);
                if (smallest == null || candidate.length < smallest.length) {
                    smallest = candidate;
                }
                if (candidate.length <= MAX_STORED_BYTES) {
                    return candidate;
                }
            }
        }
        if (smallest == null) {
            throw new IllegalArgumentException("We could not process that image.");
        }
        return smallest;
    }

    private static BufferedImage resizePreservingContent(BufferedImage original, int maximumDimension) {
        double scale = Math.min(1.0, (double) maximumDimension / Math.max(original.getWidth(), original.getHeight()));
        int width = Math.max(1, (int) Math.round(original.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(original.getHeight() * scale));
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(original, 0, 0, width, height, null);
        graphics.dispose();
        return resized;
    }

    private static byte[] writeJpeg(BufferedImage image, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("JPEG compression is unavailable.");
        }
        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(imageOutput);
            ImageWriteParam parameters = writer.getDefaultWriteParam();
            parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            parameters.setCompressionQuality(quality);
            writer.write(null, new IIOImage(image, null, null), parameters);
            writer.dispose();
            return output.toByteArray();
        }
    }
}
