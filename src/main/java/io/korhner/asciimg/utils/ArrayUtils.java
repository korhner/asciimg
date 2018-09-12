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

package io.korhner.asciimg.utils;

/**
 * An utility class used for various array utilities.
 */
public final class ArrayUtils {

	private ArrayUtils() {}

	/**
	 * Converts from 1D array index to 1D on x axis.
	 *
	 * @param index
	 *            The index of 1D array.
	 * @param arrayWidth
	 *            2D Array width (length of rows on x axis).
	 * @return Corresponding index of x axis.
	 */
	public static int convert1DtoX(final int index, final int arrayWidth) {
		return index % arrayWidth;
	}

	/**
	 * Converts from 1D array index to 1D on y axis.
	 *
	 * @param index
	 *            The index of 1D array.
	 * @param arrayWidth
	 *            2D Array width (length of rows on x axis).
	 * @return Corresponding index of y axis.
	 */
	public static int convert1DtoY(final int index, final int arrayWidth) {
		return index / arrayWidth;
	}

	/**
	 * Converts from 2D array index to 1D.
	 *
	 * @param xPos
	 *            The index on xPos axis.
	 * @param yPos
	 *            The index on xPos axis.
	 * @param arrayWidth
	 *            2D Array width (length of rows on xPos axis).
	 * @return Corresponding index if the array was 1D.
	 */
	public static int convert2DTo1D(final int xPos, final int yPos, final int arrayWidth) {
		return yPos * arrayWidth + xPos;
	}
}
