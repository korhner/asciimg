package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import java.util.Map.Entry;

/**
 * Exports ASCII art to custom output formats.
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
	 * @param imageWidth
	 *            source image width in pixels
	 * @param imageHeight
	 *            source image height in pixels
	 */
	void init(int imageWidth, int imageHeight);

	/**
	 * Appends one ASCII art character to the internal output.
	 *
	 * @param characterEntry
	 *            character chosen as best fit
	 * @param sourceImagePixels
	 *            source image pixels. Can be
	 * @param tileX
	 *            the tile x position
	 * @param tileY
	 *            the tile y position
	 * @param imageWidth
	 *            the image width
	 */
	void addCharacter(
			Entry<Character, GrayScaleMatrix> characterEntry,
			int[] sourceImagePixels,
			int tileX,
			int tileY,
			int imageWidth);

	/**
	 * Finalizes the inner state, including the output of this exporter.
	 *
	 * @param sourceImagePixels
	 *            source image pixels data. Can be
	 * @param imageWidth
	 *            source image width
	 * @param imageHeight
	 *            source image height
	 */
	void finalize(int[] sourceImagePixels, int imageWidth, int imageHeight);

	/**
	 * Returns the output container.
	 */
	O getOutput();
}
