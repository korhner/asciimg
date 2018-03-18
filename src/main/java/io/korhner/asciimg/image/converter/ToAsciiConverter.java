package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.exporter.AsciiExporter;

import java.io.IOException;

/**
 * A class used to convert an image to an ASCII art.
 *
 * @param <I>
 *            input type of the image (or similar) to be converted to ASCII art
 */
public interface ToAsciiConverter<I> {

	/**
	 * The character fit strategy used to determine the best character for each
	 * source image tile.
	 *
	 * @return the character fit strategy
	 */
	BestCharacterFitStrategy getCharacterFitStrategy();

	void setCharacterFitStrategy(BestCharacterFitStrategy characterFitStrategy);

	void setCharacterCache(AsciiImgCache characterCache);

	AsciiExporter getExporter();

	void setExporter(AsciiExporter exporter);

	/**
	 * Produces an output that is an ASCII art of the supplied image.
	 *
	 * @param source
	 *            the source, non-ASCII image
	 */
	void convert(I source) throws IOException;
}
