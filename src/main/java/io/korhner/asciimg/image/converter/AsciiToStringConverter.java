package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.matrix.GrayscaleMatrix;

import java.util.Map.Entry;

/**
 * Converts ascii art to String.
 */
public class AsciiToStringConverter extends AsciiConverter<StringBuffer> {

	/**
	 * Instantiates a new ascii to string converter.
	 *
	 * @param characterCacher
	 *            the character cacher
	 * @param characterFitStrategy
	 *            the character fit strategy
	 */
	public AsciiToStringConverter(final AsciiImgCache characterCacher,
			final BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
	}

	/**
	 * Creates an empty string buffer.
	 */
	@Override
	protected StringBuffer initializeOutput(final int imageWidth, final int imageHeight) {
		return new StringBuffer();
	}

	@Override
	protected void finalizeOutput(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {}

	/**
	 * Append chosen character to StringBuffer.
	 */
	@Override
	public void addCharacterToOutput(
			final Entry<Character, GrayscaleMatrix> characterEntry,
			final int[] sourceImagePixels, final int tileX, final int tileY,
			final int imageWidth) {

		this.getOutput().append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1) * this.getCharacterCache().getCharacterImageSize().width == imageWidth) {
			this.getOutput().append(System.lineSeparator());
		}
	}
}
