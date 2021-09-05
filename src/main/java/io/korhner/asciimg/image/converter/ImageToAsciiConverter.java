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

package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;
import io.korhner.asciimg.image.matrix.ReferencingTiledImageMatrix;
import io.korhner.asciimg.image.strategy.CharacterFinder;
import io.korhner.asciimg.image.transformer.ToGrayscaleImageTransformer;
import io.korhner.asciimg.image.transformer.TruncatingImageTransformer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map.Entry;

/**
 * A class used to convert an abstract 32bit ARGB image to an ASCII art.
 * Output and conversion algorithm are decoupled.
 */
public class ImageToAsciiConverter<O> extends AbstractToAsciiConverter<BufferedImage, O> {

	public ImageToAsciiConverter() {}

	public void convert(final ImageMatrix input) { // HACK

		// truncate to tile-able size
		final TruncatingImageTransformer truncater = new TruncatingImageTransformer();
		truncater.setCharacterCache(getCharacterCache());
		final ImageMatrix truncated = truncater.transform(input);

		// convert to gray-scale
		final ToGrayscaleImageTransformer grayScaler = new ToGrayscaleImageTransformer();
		final ImageMatrix grayScaled = grayScaler.transform(truncated);

		// dimension of each tile
		final ImageMatrixDimensions tileSize = getCharacterCache().getCharacterImageSize();

		// divide matrix into tiles for easy processing
		final ReferencingTiledImageMatrix<Short> tiledMatrix = new ReferencingTiledImageMatrix<>(
				grayScaled.getMetaData(), grayScaled, tileSize);

		getExporter().setCharacterCache(getCharacterCache());
		getExporter().init(tiledMatrix.getSizeInTiles());

		// find best fitting character for each tile
		// NOTE We go through Y in the outer loop to improve locality
		//      -> low level performance optimization
		for (int tileY = 0; tileY < tiledMatrix.getSizeInTiles().getHeight(); tileY++) {
			for (int tileX = 0; tileX < tiledMatrix.getSizeInTiles().getWidth(); tileX++) {
				// find best fit
				final Entry<Character, ImageMatrix<Short>> bestFit = new CharacterFinder(
						getCharacterCache(),
						getCharacterFitStrategy()).findBestFit(tiledMatrix.getTile(tileX, tileY));

				// copy character to output
				getExporter().addCharacter(bestFit, tileX, tileY);
			}
		}

		getExporter().imageEnd();
	}

	@Override
	public void convert(final BufferedImage source) throws IOException {

		getExporter().initFrames(1);

		getImporter().setSource(source);
		final ImageMatrix input = getImporter().read();

		convert(input);

		getExporter().finalizeFrames();
	}
}
