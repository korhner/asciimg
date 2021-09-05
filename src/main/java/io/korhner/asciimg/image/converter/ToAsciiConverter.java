/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 korhner <korhner@gmail.com>
 * Copyright (c) 2018 hoijui <hoijui.quaero@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.image.importer.ImageImporter;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

import java.io.IOException;

/**
 * A class used to convert an image to an ASCII art.
 *
 * @param <I>
 *            input type of the image (or similar) to be converted to ASCII art
 * @param <O>
 *            input type of the image (or similar) to be converted to ASCII art
 */
public interface ToAsciiConverter<I, O> {

	ImageImporter<I, ?> getImporter();

	void setImporter(ImageImporter<I, ?> importer);

	/**
	 * The character fit strategy used to determine the best character for each
	 * source image tile.
	 *
	 * @return the character fit strategy
	 */
	CharacterFitStrategy getCharacterFitStrategy();

	void setCharacterFitStrategy(CharacterFitStrategy characterFitStrategy);

	void setCharacterCache(AsciiImgCache characterCache);

	MultiFrameAsciiExporter<O> getExporter();

	void setExporter(MultiFrameAsciiExporter<O> exporter);

	/**
	 * Produces an output that is an ASCII art of the supplied image.
	 *
	 * @param source the source, non-ASCII image
	 * @throws IOException on any kind of source input error, or output error
	 */
	void convert(I source) throws IOException;
}
