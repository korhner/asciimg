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

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;

import java.util.Map;

/**
 * Encapsulates the algorithm for choosing the best fit character.
 */
public class CharacterFinder {

	private final AsciiImgCache characterCache;
	private final CharacterFitStrategy characterFitStrategy;

	public CharacterFinder(final AsciiImgCache characterCache, final CharacterFitStrategy characterFitStrategy) {

		this.characterCache = characterCache;
		this.characterFitStrategy = characterFitStrategy;
	}

	/**
	 * Returns the best fit character for a given tile (part of an image).
	 *
	 * @param tile
	 *            the tile to find a fit for
	 * @return the character with minimum error
	 */
	public Map.Entry<Character, ImageMatrix<Short>> findBestFit(final ImageMatrix<Short> tile) {

		Map.Entry<Character, ImageMatrix<Short>> bestFit = null;

		// XXX This could be speed up (in case of a large character set), by arranging characters in a tree, with the non-leafs being lower-resolution representations of their children. this requires creating a lower-resolution version of each tile, though.
		// TODO Maybe check how libcaca does this, as it is probably quite heavily optimized
		float minError = Float.MAX_VALUE;
		for (final Map.Entry<Character, ImageMatrix<Short>> charImage : getCharacterCache()) {
			final ImageMatrix<Short> charPixels = charImage.getValue();

			final float error = getCharacterFitStrategy().calculateError(charPixels, tile);

			if (error < minError) {
				minError = error;
				bestFit = charImage;
			}
		}

		return bestFit;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	protected CharacterFitStrategy getCharacterFitStrategy() {
		return characterFitStrategy;
	}
}
