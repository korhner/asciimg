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
 * Calculates Structural Similarity index (SSIM) between the images.
 *
 * See http://en.wikipedia.org/wiki/Structural_similarity for more info.
 */
public class StructuralSimilarityCharacterFitStrategy implements CharacterFitStrategy {

	private static final float K_1 = 0.01f;
	private static final float K_2 = 0.03f;
	private static final float L = 255f;
	private static final float C_1 = (float) Math.pow(K_1 * L, 2);
	private static final float C_2 = (float) Math.pow(K_2 * L, 2);

	@Override
	public float calculateError(final ImageMatrix<Short> character, final ImageMatrix<Short> tile) {

		float score = 0f;
		for (int cpx = 0; cpx < character.getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < character.getDimensions().getHeight(); cpy++) {
				final float pixelVal1 = character.getValue(cpx, cpy);
				final float pixelVal2 = tile.getValue(cpx, cpy);

				score += (2 * pixelVal1 * pixelVal2 + C_1) * (2 + C_2)
						/ (pixelVal1 * pixelVal1 + pixelVal2 * pixelVal2 + C_1) / C_2;
			}
		}

		final int numPixels = character.getDimensions().getWidth() * character.getDimensions().getHeight();
		// average and convert score to error
		return 1 - (score / numPixels);
	}
}
