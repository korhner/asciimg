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

package io.korhner.asciimg.image.matrix;

import io.korhner.asciimg.utils.ArrayUtils;

/**
 * Basic implementation of {@link ImageMatrix}, backed by an <code>int[]</code>.
 */
public class BasicInt1DImageMatrix extends AbstractImageMatrix<Integer> {

	/**
	 * The images data points.
	 */
	private final int[] data;

	/**
	 * Creates an empty image with the given dimensions.
	 *
	 * @param metaData
	 *            image meta data
	 * @param data
	 *            image data points
	 * @param width
	 *            image width in number of data points
	 */
	public BasicInt1DImageMatrix(final ImageMatrixInfo metaData, final int[] data, final int width) {
		super(metaData, new ImageMatrixDimensions(width, data.length / width));

		if (data.length % width != 0) {
			throw new IllegalArgumentException("width does not divide data");
		}

		this.data = data;
	}

	@Override
	public Integer getValue(final int posX, final int posY) {
		return data[ArrayUtils.convert2DTo1D(posX, posY, getDimensions().getWidth())];
	}
}
