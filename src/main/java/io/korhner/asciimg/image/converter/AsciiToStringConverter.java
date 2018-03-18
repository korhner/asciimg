package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;

import java.util.Map.Entry;

/**
 * Converts ASCII art to String.
 */
public class AsciiToStringConverter extends AsciiConverter<StringBuilder> {

	/**
	 * Instantiates a new ASCII to string converter.
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

	@Override
	protected StringBuilder initializeOutput(final int imageWidth, final int imageHeight) {

		return new StringBuilder();
	}

	@Override
	protected void finalizeOutput(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {}

	/**
	 * Append chosen character to the output buffer.
	 */
	@Override
	public void addCharacterToOutput(
			final Entry<Character, GrayScaleMatrix> characterEntry,
			final int[] sourceImagePixels, final int tileX, final int tileY,
			final int imageWidth) {

		getOutput().append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1) * getCharacterCache().getCharacterImageSize().width == imageWidth) {
			getOutput().append(System.lineSeparator());
		}
	}
}
