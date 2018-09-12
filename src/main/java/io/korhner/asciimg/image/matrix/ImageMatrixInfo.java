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
 * Contains basic image meta data.
 */
public interface ImageMatrixInfo {

	boolean isGrayScale();
	boolean isBlackAndWhite();
	boolean isColored();
	boolean isWithAlpha();

	/**
	 * Indicates the number of values per data point.
	 * Examples:
	 * - ARGB: 4
	 * - RGB: 3
	 * - grey scale: 1
	 * - black &amp; white: 1
	 * @return the number of values per data point
	 */
	int getValuesPerDataPoint();

	Class getDataPointClass();

	/**
	 * Indicates the number of bits used to represent each value (see {@link #getValuesPerDataPoint()}).
	 * Examples:
	 * - ARGB: 8
	 * - RGB: 8
	 * - grey scale: 8
	 * - black &amp; white: 1
	 * @return the number of bits used to represent each value withing the data-point
	 */
	int getBitsPerValue();

}
