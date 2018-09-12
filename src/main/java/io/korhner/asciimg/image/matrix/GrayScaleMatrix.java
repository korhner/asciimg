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
 * Encapsulates a gray-scale image.
 * Gray scale values are ints with values from black=0, to white=255.
 */
public class GrayScaleMatrix extends AbstractImageMatrix<Short> {

	public static final ImageMatrixInfo META_DATA
			= new BasicImageMatrixInfo(1, Short.class, 8);
	private final ImageMatrix<Integer> argbImage;

	/**
	 * Instantiates a new gray-scale matrix, backed by an ARGB bitmap image.
	 *
	 * @param argbImage
	 *            pixel data in 32bit ARGB format
	 */
	public GrayScaleMatrix(final ImageMatrix<Integer> argbImage) {
		super(META_DATA, argbImage.getDimensions());

		this.argbImage = argbImage;
	}

	/**
	 * Convert 32bit ARGB color to 8bit gray-scale value.
	 *
	 * @param rgbColor
	 *            ARGB color with 8bit per color component
	 * @return gray-scale value between 0 and 255.
	 */
	private static short convertRGBToGrayScale(final int rgbColor) {

		// extract components
		final int red = (rgbColor >> 16) & 0xFF;
		final int green = (rgbColor >> 8) & 0xFF;
		final int blue = rgbColor & 0xFF;

		// convert to gray-scale
		final float grayScale = 0.3f * red + 0.59f * green + 0.11f * blue;

		// This should not be required
		//return (short) Math.min(Math.round(grayScale), 255);
		return (short) grayScale;
	}

	@Override
	public Short getValue(final int posX, final int posY) {
		return convertRGBToGrayScale(argbImage.getValue(posX, posY));
	}
}
