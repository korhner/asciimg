package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import io.korhner.asciimg.utils.ArrayUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

/**
 * Converts ASCII art to a BufferedImage.
 */
public class ImageAsciiExporter implements AsciiExporter<BufferedImage> {

	private AsciiImgCache characterCache;
	private BufferedImage output;

	public ImageAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	/**
	 * Copy image data over the source pixels image.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, GrayScaleMatrix> characterEntry,
			final int[] sourceImagePixels,
			final int tileX,
			final int tileY,
			final int imageWidth) {
		final int startCoordinateX = tileX * characterCache.getCharacterImageSize().width;
		final int startCoordinateY = tileY
				* characterCache.getCharacterImageSize().height;

		// copy winner character
		for (int i = 0; i < characterEntry.getValue().getData().length; i++) {
			final int xOffset = i % characterCache.getCharacterImageSize().width;
			final int yOffset = i / characterCache.getCharacterImageSize().width;

			final int component = (int) characterEntry.getValue().getData()[i];
			sourceImagePixels[ArrayUtils.convert2DTo1D(
					startCoordinateX + xOffset,
					startCoordinateY + yOffset,
					imageWidth)]
					= new Color(component, component, component).getRGB();
		}
	}

	/**
	 * Write pixels to output image.
	 */
	@Override
	public void imageEnd(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {
		this.getOutput().setRGB(0, 0, imageWidth, imageHeight, sourceImagePixels, 0, imageWidth);

	}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final int imageWidth, final int imageHeight) {
		output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public BufferedImage getOutput() {
		return output;
	}
}
