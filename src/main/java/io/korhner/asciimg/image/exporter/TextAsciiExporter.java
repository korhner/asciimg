package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.TiledImageMatrix;

import java.util.Map.Entry;

/**
 * Converts ASCII art to text.
 */
public class TextAsciiExporter implements AsciiExporter<String> {

	private AsciiImgCache characterCache;
	private int imageWidth;
	private StringBuilder output;

	public TextAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void init(final TiledImageMatrix<?> source) {

		this.imageWidth = source.getTileWidth() * source.getTilesX();
		// each tile and each new-line is a char
		output = new StringBuilder(source.getTileCount() + source.getTilesY());
	}

	@Override
	public void imageEnd() {}

	/**
	 * Append chosen character to the output buffer.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		output.append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1) * characterCache.getCharacterImageSize().getWidth() == imageWidth) {
			output.append(System.lineSeparator());
		}
	}

	@Override
	public String getOutput() {
		return output.toString();
	}
}
