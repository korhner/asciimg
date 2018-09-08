package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
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
	 * @param srcPxWidth
	 *            source image width in pixels
	 * @param srcPxHeight
	 *            source image height in pixels
	 * @param charsWidth
	 *            image width in (ASCII art) characters
	 * @param charsHeight
	 *            image height in (ASCII art) characters
	 * @param sourceImagePixels
	 *            source image pixels. Can be modified, and therefore be used as output buffer
	 * @param imageWidth
	 *            the image width
	 */
	void init(int srcPxWidth, int srcPxHeight, int charsWidth, int charsHeight, int[] sourceImagePixels, int imageWidth);

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
			Entry<Character, GrayScaleMatrix> characterEntry,
			int tileX,
			int tileY);

	/**
	 * Finalizes the inner state, including the output of this exporter.
	 *
	 * @param imageWidth
	 *            source image width
	 * @param imageHeight
	 *            source image height
	 */
	void imageEnd(int imageWidth, int imageHeight);

	/**
	 * @return the output container.
	 */
	O getOutput();
}
