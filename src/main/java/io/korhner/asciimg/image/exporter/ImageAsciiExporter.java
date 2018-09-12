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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Converts ASCII art to a BufferedImage.
 */
public class ImageAsciiExporter implements MultiFrameAsciiExporter<List<BufferedImage>> {

	private AsciiImgCache characterCache;
	private List<BufferedImage> output;
	private BufferedImage currentOutput;

	public ImageAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void initFrames(final int numFrame) {
		output = new ArrayList<>(numFrame);
	}

	/**
	 * Copy image data over the source pixels image.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		final int startCoordinateX = tileX * characterCache.getCharacterImageSize().getWidth();
		final int startCoordinateY = tileY * characterCache.getCharacterImageSize().getHeight();

		// copy winner character
		for (int cpx = 0; cpx < characterEntry.getValue().getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < characterEntry.getValue().getDimensions().getHeight(); cpy++) {
				final int component = (int) characterEntry.getValue().getValue(cpx, cpy);
				currentOutput.setRGB(
						startCoordinateX + cpx,
						startCoordinateY + cpy,
						new Color(component, component, component).getRGB());
			}
		}
	}

	@Override
	public void imageEnd() {}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final ImageMatrixDimensions targetDimensions) {

		final int imageWidth = targetDimensions.getWidth() * characterCache.getCharacterImageSize().getWidth();
		final int imageHeight = targetDimensions.getHeight() * characterCache.getCharacterImageSize().getHeight();
		currentOutput = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		output.add(currentOutput);
	}

	@Override
	public List<BufferedImage> getOutput() {
		return output;
	}

	@Override
	public void finalizeFrames() {}
}
