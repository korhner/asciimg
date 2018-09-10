package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.TiledImageMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Converts ASCII art to text.
 */
public class TextAsciiExporter implements MultiFrameAsciiExporter<List<String>> {

	private AsciiImgCache characterCache;
	private int imageWidth;
	private List<String> output;
	private StringBuilder currentOutput;

	public TextAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void initFrames(int numFrame) {
		output = new ArrayList<>(numFrame);
	}

	@Override
	public void init(final TiledImageMatrix<?> source) {

		this.imageWidth = source.getTileWidth() * source.getTilesX();
		// each tile and each new-line is a char
		currentOutput = new StringBuilder(source.getTileCount() + source.getTilesY());
	}

	@Override
	public void imageEnd() {
		output.add(currentOutput.toString());
	}

	/**
	 * Append chosen character to the output buffer.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		currentOutput.append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1) * characterCache.getCharacterImageSize().getWidth() == imageWidth) {
			currentOutput.append(System.lineSeparator());
		}
	}

	@Override
	public List<String> getOutput() {
		return output;
	}

	@Override
	public void finalizeFrames() {}
}
