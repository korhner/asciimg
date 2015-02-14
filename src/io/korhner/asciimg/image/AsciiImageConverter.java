package io.korhner.asciimg.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

public class AsciiImageConverter {

	private final AsciiImgCache characterCache;

	private static final int THRESHOLD = 175;

	public AsciiImageConverter(final AsciiImgCache characterCacher) {
		this.characterCache = characterCacher;
	}

	/**
	 * Converts 2D coordinates to 1D.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param imageWidth
	 *            the image width
	 * @return the int
	 */
	private int convert2DTo1D(final int x, final int y, final int imageWidth) {
		return y * imageWidth + x;
	}

	/**
	 * Produces a new image that is an ascii art of the supplied image.
	 *
	 * @param source
	 *            the source
	 * @return the buffered image
	 */
	public BufferedImage convertImage(final BufferedImage source) {
		Dimension tile = this.characterCache.getCharacterImageSize();

		int outputImageWidth = (source.getWidth() / tile.width) * tile.width;
		int outputImageHeight = (source.getHeight() / tile.height) * tile.height;

		int[] imagePixels = source.getRGB(0, 0, outputImageWidth, outputImageHeight, null, 0, outputImageWidth);

		BufferedImage outputImage = new BufferedImage(outputImageWidth, outputImageHeight, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < outputImageHeight / tile.getHeight(); i++) {
			for (int j = 0; j < outputImageWidth / tile.getWidth(); j++) {
				convertTileToAscii(outputImageWidth, outputImageHeight, imagePixels, j, i);
			}
		}

		outputImage.setRGB(0, 0, outputImageWidth, outputImageHeight, imagePixels, 0, outputImageWidth);
		return outputImage;

	}

	/**
	 * Replaces a tile of source image with a character image.
	 *
	 * @param sourceImageWidth the source image width
	 * @param sourceImageHeight the source image height
	 * @param pixelData the pixel data
	 * @param tileX the tile x
	 * @param tileY the tile y
	 */
	public void convertTileToAscii(final int sourceImageWidth, final int sourceImageHeight, final int[] pixelData,
			final int tileX, final int tileY) {
		int startCoordinateX = tileX * this.characterCache.getCharacterImageSize().width;
		int startCoordinateY = tileY * this.characterCache.getCharacterImageSize().height;

		double minError = Double.MAX_VALUE;
		Entry<Character, int[]> bestFit = null;

		for (Entry<Character, int[]> charImages : this.characterCache) {
			int[] charPixels = charImages.getValue();

			double totalError = 0;
			for (int i = 0; i < charPixels.length; i++) {

				int r1 = (charPixels[i] >> 16) & 0xFF;
				int g1 = (charPixels[i] >> 8) & 0xFF;
				int b1 = charPixels[i] & 0xFF;

				int xOffset = i % this.characterCache.getCharacterImageSize().width;
				int yOffset = i / this.characterCache.getCharacterImageSize().width;
				int sourcePixel = pixelData[convert2DTo1D(startCoordinateX + xOffset, startCoordinateY + yOffset,
						sourceImageWidth)];

				int r2 = (sourcePixel >> 16) & 0xFF;
				int g2 = (sourcePixel >> 8) & 0xFF;
				int b2 = sourcePixel & 0xFF;

				// threashold
				int thresholded = (r2 + g2 + b2) / 3 < THRESHOLD ? 0 : 255;

				totalError += Math.sqrt((r1 - thresholded) * (r1 - thresholded) + (g1 - thresholded)
						* (g1 - thresholded) + (b1 - thresholded) * (b1 - thresholded));

			}

			if (totalError < minError) {
				minError = totalError;
				bestFit = charImages;
			}

		}

		// copy winner character
		int[] charPixels = bestFit.getValue();
		for (int i = 0; i < charPixels.length; i++) {
			int xOffset = i % this.characterCache.getCharacterImageSize().width;
			int yOffset = i / this.characterCache.getCharacterImageSize().width;

			pixelData[convert2DTo1D(startCoordinateX + xOffset, startCoordinateY + yOffset, sourceImageWidth)] = charPixels[i];
		}
	}

}
