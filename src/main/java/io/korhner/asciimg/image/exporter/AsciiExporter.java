package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;

import java.util.Map.Entry;

/**
 * Exports ASCII art to custom output formats.
 * This is a state-full object, so each exporter may only be used
 * for one single image, and may only be used once.
 *
 * @param <O>
 *            output container type of the ASCII art
 */
public interface AsciiExporter<O> {

	void setCharacterCache(AsciiImgCache characterCache);

	/**
	 * Initializes the inner state of this exporter
	 * to be ready to call {@link #addCharacter}.
	 *
	 * @param targetDimensions
	 *            dimensions of the ASCII art "image" in characters
	 */
	void init(ImageMatrixDimensions targetDimensions);

	/**
	 * Appends one ASCII art character to the internal output.
	 *
	 * @param characterEntry
	 *            character chosen as best fit
	 * @param tileX
	 *            the tile x position
	 * @param tileY
	 *            the tile y position
	 */
	void addCharacter(
			Entry<Character, ImageMatrix<Short>> characterEntry,
			int tileX,
			int tileY);

	/**
	 * Finalizes the inner state, including the output of this exporter.
	 */
	void imageEnd();

	/**
	 * @return the output container.
	 */
	O getOutput();
}
