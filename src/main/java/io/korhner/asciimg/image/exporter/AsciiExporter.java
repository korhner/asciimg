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
