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

package io.korhner.asciimg.image.transformer;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;
import io.korhner.asciimg.image.matrix.RegionImageMatrix;

/**
 * Discards superfluous pixels on the right and on the top of the image.
 */
public class TruncatingImageTransformer implements ImageTransformer<Integer, Integer> {

	private AsciiImgCache characterCache;

	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	@Override
	public ImageMatrix<Integer> transform(final ImageMatrix<Integer> source) {

		// dimension of each tile
		final ImageMatrixDimensions tileSize = getCharacterCache().getCharacterImageSize();

		final ImageMatrixDimensions sourcePixelsSize = source.getDimensions();
		// the number of characters that fit fully into the source image
		// only these will be used, and pixels to the right and below the image that are not covered by these
		// will be ignored, and thus are not represented in the output
		final ImageMatrixDimensions destCharactersSize = new ImageMatrixDimensions(
				sourcePixelsSize.getWidth() / tileSize.getWidth(),
				sourcePixelsSize.getHeight() / tileSize.getHeight());
		// destination image width and height in pixels; truncated, so we avoid partial characters
		final ImageMatrixDimensions truncatedPixelsSize = new ImageMatrixDimensions(
				destCharactersSize.getWidth() * tileSize.getWidth(),
				destCharactersSize.getHeight() * tileSize.getHeight());

		// do the truncating
		return new RegionImageMatrix<>(source, truncatedPixelsSize, 0, 0);
	}
}
