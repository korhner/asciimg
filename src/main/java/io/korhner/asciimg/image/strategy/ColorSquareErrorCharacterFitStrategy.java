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

package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Calculates the squared mean error over all pixels between two images.
 */
public class ColorSquareErrorCharacterFitStrategy implements CharacterFitStrategy {

	@Override
	public float calculateError(final ImageMatrix<Short> character, final ImageMatrix<Short> tile) {

		float error = 0;

		// calculate sum of squared difference over all character pixels
		for (int cpx = 0; cpx < character.getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < character.getDimensions().getHeight(); cpy++) {
				final short pixelVal1 = character.getValue(cpx, cpy);
				final short pixelVal2 = tile.getValue(cpx, cpy);

				final float colorDiff = pixelVal1 - pixelVal2;
				error += colorDiff * colorDiff;
			}
		}

		final int numPixels = character.getDimensions().getWidth() * character.getDimensions().getHeight();
		return error / numPixels;
	}
}
