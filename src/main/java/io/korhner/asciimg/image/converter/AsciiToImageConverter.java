package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import io.korhner.asciimg.utils.ArrayUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

/**
 * Converts ASCII art to a BufferedImage.
 */
public class AsciiToImageConverter extends AsciiConverter<BufferedImage> {

	/**
	 * Instantiates a new ASCII to image converter.
	 *
	 * @param characterCacher
	 *            the character cacher
	 * @param characterFitStrategy
	 *            the character fit strategy
	 */
	public AsciiToImageConverter(final AsciiImgCache characterCacher, final BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
	}

	/**
	 * Copy image data over the source pixels image.
	 */
	@Override
	public void addCharacterToOutput(
			final Entry<Character, GrayScaleMatrix> characterEntry,
			final int[] sourceImagePixels, final int tileX, final int tileY, final int imageWidth) {
		final int startCoordinateX = tileX
				* this.getCharacterCache().getCharacterImageSize().width;
		final int startCoordinateY = tileY
				* this.getCharacterCache().getCharacterImageSize().height;

		// copy winner character
		for (int i = 0; i < characterEntry.getValue().getData().length; i++) {
			final int xOffset = i % this.getCharacterCache().getCharacterImageSize().width;
			final int yOffset = i / this.getCharacterCache().getCharacterImageSize().width;

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
	protected void finalizeOutput(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {
		this.getOutput().setRGB(0, 0, imageWidth, imageHeight, sourceImagePixels, 0, imageWidth);

	}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	protected BufferedImage initializeOutput(final int imageWidth, final int imageHeight) {
		return new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}
}
