package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import java.util.Map.Entry;

/**
 * Converts ASCII art to text.
 */
public class TextAsciiExporter implements AsciiExporter<String> {

	private AsciiImgCache characterCache;
	private StringBuilder output;

	public TextAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void init(final int srcPxWidth, final int srcPxHeight, final int charsWidth, final int charsHeight) {
		output = new StringBuilder(charsWidth* charsHeight);
	}

	@Override
	public void imageEnd(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {}

	/**
	 * Append chosen character to the output buffer.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, GrayScaleMatrix> characterEntry,
			final int[] sourceImagePixels,
			final int tileX,
			final int tileY,
			final int imageWidth) {

		output.append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1) * characterCache.getCharacterImageSize().width == imageWidth) {
			output.append(System.lineSeparator());
		}
	}

	@Override
	public String getOutput() {
		return output.toString();
	}
}
