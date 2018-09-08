package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.TiledImageMatrix;
import io.korhner.asciimg.utils.ArrayUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

/**
 * Converts ASCII art to a BufferedImage.
 */
public class ImageAsciiExporter implements AsciiExporter<BufferedImage> {

	private AsciiImgCache characterCache;
	private int imageWidth;
	private int imageHeight;
	private int[] outputImagePixels;
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
			final Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		final int startCoordinateX = tileX * characterCache.getCharacterImageSize().getWidth();
		final int startCoordinateY = tileY * characterCache.getCharacterImageSize().getHeight();

		// copy winner character
		for (int cpx = 0; cpx < characterEntry.getValue().getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < characterEntry.getValue().getDimensions().getHeight(); cpy++) {
				final int component = (int) characterEntry.getValue().getValue(cpx, cpy);
				outputImagePixels[ArrayUtils.convert2DTo1D(
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
	public void imageEnd() {
		this.getOutput().setRGB(0, 0, imageWidth, imageHeight, outputImagePixels, 0, imageWidth);

	}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final TiledImageMatrix<?> source) {

		this.imageWidth = source.getImageDimensions().getWidth();
		this.imageHeight = source.getImageDimensions().getHeight();
		this.outputImagePixels = new int[imageWidth * imageHeight];
		this.output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public BufferedImage getOutput() {
		return output;
	}
}
