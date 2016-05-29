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
	 * Creates an empty string buffer;
	 * 
	 * @see io.korhner.asciimg.image.converter.AsciiConverter#initializeOutput(int,
	 *      int)
	 */
	@Override
	protected StringBuffer initializeOutput(final int imageWidth,
			final int imageHeight) {
		return new StringBuffer();
	}

	/**
	 * @see io.korhner.asciimg.image.converter.AsciiConverter#finalizeOutput(int[],
	 *      int, int)
	 */
	@Override
	protected void finalizeOutput(final int[] sourceImagePixels,
			final int imageWidth, int imageHeight) {

	}

	/**
	 * Append choosen character to StringBuffer.
	 * 
	 * @see io.korhner.asciimg.image.converter.AsciiConverter#addCharacterToOutput(java.util.Map.Entry,
	 *      int[], int, int, int)
	 */
	@Override
	public void addCharacterToOutput(
			final Entry<Character, GrayscaleMatrix> characterEntry,
			final int[] sourceImagePixels, final int tileX, final int tileY,
			final int imageWidth) {

		this.output.append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1)
				* this.characterCache.getCharacterImageSize().getWidth() == imageWidth) {
			this.output.append(System.lineSeparator());
		}

	}

}
