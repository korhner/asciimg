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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Converts ASCII art to text.
 */
public class TextAsciiExporter implements MultiFrameAsciiExporter<List<String>> {

	/** Width of the output in characters. */
	private int width;
	private List<String> output;
	private StringBuilder currentOutput;

	public TextAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {}

	@Override
	public void initFrames(final int numFrame) {
		output = new ArrayList<>(numFrame);
	}

	@Override
	public void init(final ImageMatrixDimensions targetDimensions) {

		this.width = targetDimensions.getWidth();
		// each tile and each new-line is a char
		currentOutput = new StringBuilder((targetDimensions.getWidth() + 1) * targetDimensions.getHeight());
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
		if ((tileX + 1) == width) {
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
