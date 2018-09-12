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

/**
 * Abstract implementation of {@link ImageMatrix}, just missing the data.
 */
public abstract class AbstractImageMatrix<T> implements ImageMatrix<T> {

	private final ImageMatrixInfo metaData;
	private final ImageMatrixDimensions dimensions;

	/**
	 * Creates an image with the given dimensions.
	 *
	 * @param metaData
	 *            image meta data
	 * @param dimensions
	 *            image width and height
	 */
	public AbstractImageMatrix(final ImageMatrixInfo metaData, final ImageMatrixDimensions dimensions) {

		this.metaData = metaData;
		this.dimensions = dimensions;
	}

	@Override
	public ImageMatrixInfo getMetaData() {
		return metaData;
	}

	@Override
	public ImageMatrixDimensions getDimensions() {
		return dimensions;
	}
}
