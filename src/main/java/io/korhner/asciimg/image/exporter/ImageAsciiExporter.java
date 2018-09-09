package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.TiledImageMatrix;
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
				getOutput().setRGB(
						startCoordinateX + cpx,
						startCoordinateY + cpy,
						new Color(component, component, component).getRGB());
			}
		}
	}

	@Override
	public void imageEnd() {}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final TiledImageMatrix<?> source) {

		final int imageWidth = source.getImageDimensions().getWidth();
		final int imageHeight = source.getImageDimensions().getHeight();
		output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public BufferedImage getOutput() {
		return output;
	}
}
