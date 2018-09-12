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
 * A referencing representation of a sub-region of an image.
 */
public class RegionImageMatrix<T> extends AbstractImageMatrix<T> {

	/**
	 * Origin/source/referencing image.
	 */
	private final ImageMatrix<T> origin;
	private final int startPixelX;
	private final int startPixelY;

	/**
	 * @param origin
	 *            original/referenced image
	 * @param regionDimensions
	 *            width and height of the sub-region (in number of data points) to be represented
	 * @param startPixelX
	 *            start data point index of the sub-region on the x-axis
	 * @param startPixelY
	 *            start data point index of the sub-region on the y-axis
	 */
	public RegionImageMatrix(
			final ImageMatrix<T> origin,
			final ImageMatrixDimensions regionDimensions,
			final int startPixelX, final int startPixelY)
	{
		super(origin.getMetaData(), regionDimensions);

		this.origin = origin;
		this.startPixelX = startPixelX;
		this.startPixelY = startPixelY;

		assert startPixelX >= 0;
		assert startPixelY >= 0;
		assert regionDimensions.getWidth() > 0;
		assert regionDimensions.getHeight() > 0;
		assert startPixelX + regionDimensions.getWidth() <= origin.getDimensions().getWidth();
		assert startPixelY + regionDimensions.getHeight() <= origin.getDimensions().getHeight();
	}

	@Override
	public T getValue(final int posX, final int posY) {
		return origin.getValue(startPixelX + posX, startPixelY + posY);
	}
}
