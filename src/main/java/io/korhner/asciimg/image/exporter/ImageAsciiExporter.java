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
	private int[] sourceImagePixels;
	private int imageWidth;
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
			final int tileX,
			final int tileY) {
		final int startCoordinateX = tileX * characterCache.getCharacterImageSize().width;
		final int startCoordinateY = tileY
				* characterCache.getCharacterImageSize().height;

		// copy winner character
		for (int cpx = 0; cpx < characterEntry.getValue().getWidth(); cpx++) {
			for (int cpy = 0; cpy < characterEntry.getValue().getHeight(); cpy++) {
				final int component = (int) characterEntry.getValue().getValue(cpx, cpy);
				sourceImagePixels[ArrayUtils.convert2DTo1D(
						startCoordinateX + cpx,
						startCoordinateY + cpy,
						imageWidth)]
						= new Color(component, component, component).getRGB();
			}
		}
	}

	/**
	 * Write pixels to output image.
	 */
	@Override
	public void imageEnd(final int imageWidth, final int imageHeight) {
		this.getOutput().setRGB(0, 0, imageWidth, imageHeight, sourceImagePixels, 0, imageWidth);

	}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final int srcPxWidth, final int srcPxHeight, final int charsWidth, final int charsHeight, final int[] sourceImagePixels, final int imageWidth) {

		this.sourceImagePixels = sourceImagePixels;
		this.imageWidth = imageWidth;
		output = new BufferedImage(srcPxWidth, srcPxHeight, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public BufferedImage getOutput() {
		return output;
	}
}
