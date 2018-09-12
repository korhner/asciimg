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
public class BasicImageMatrixInfo implements ImageMatrixInfo {

	private final int valuesPerDataPoint;
	private final Class dataPointClass;
	private final int bitsPerValue;

	public BasicImageMatrixInfo(
			final int valuesPerDataPoint,
			final Class dataPointClass,
			final int bitsPerValue)
	{
		this.valuesPerDataPoint = valuesPerDataPoint;
		this.dataPointClass = dataPointClass;
		this.bitsPerValue = bitsPerValue;
	}

	@Override
	public boolean isGrayScale() {
		return !isColored() && !isBlackAndWhite();
	}

	@Override
	public boolean isBlackAndWhite() {
		return !isColored() && bitsPerValue == 1;
	}

	@Override
	public boolean isColored() {
		return valuesPerDataPoint > 2;
	}

	@Override
	public boolean isWithAlpha() {
		return valuesPerDataPoint == 2 || valuesPerDataPoint == 4;
	}

	@Override
	public int getValuesPerDataPoint() {
		return valuesPerDataPoint;
	}

	@Override
	public Class getDataPointClass() {
		return dataPointClass;
	}

	@Override
	public int getBitsPerValue() {
		return bitsPerValue;
	}
}
