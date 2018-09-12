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

import io.korhner.asciimg.utils.ArrayUtils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Referencing implementation of {@link TiledImageMatrix}.
 */
public class ReferencingTiledImageMatrix<V> extends AbstractList<ImageMatrix<V>> implements TiledImageMatrix<V> {

	private final ImageMatrix<V> original;
	private final ImageMatrixInfo metaData;

	/** The tiles. */
	private final List<ImageMatrix<V>> tiles;

	/** Dimensions of a tile in data points. */
	private final ImageMatrixDimensions tileSize;

	/** Number of tiles on the x and y axes. */
	private final ImageMatrixDimensions sizeInTiles;

	/**
	 * Instantiates a new tiled image matrix.
	 *
	 * @param metaData
	 *            image meta data
	 * @param original
	 *            the source matrix
	 * @param tileDimensions
	 *            the tile width and height
	 */
	public ReferencingTiledImageMatrix(
			final ImageMatrixInfo metaData,
			final ImageMatrix<V> original,
			final ImageMatrixDimensions tileDimensions)
	{
		final int tileWidth = tileDimensions.getWidth();
		if (tileWidth <= 0) {
			throw new IllegalArgumentException("Tile width has to be positive!");
		}
		final int tileHeight = tileDimensions.getHeight();
		if (tileHeight <= 0) {
			throw new IllegalArgumentException("Tile height has to be positive!");
		}
		final int imageWidth = original.getDimensions().getWidth();
		if (tileWidth > imageWidth) {
			throw new IllegalArgumentException("Tile width larger then original images width!");
		}
		final int imageHeight = original.getDimensions().getHeight();
		if (tileHeight > imageHeight) {
			throw new IllegalArgumentException("Tile height larger then original images height!");
		}
		// we won't allow partial tiles
		if (imageWidth % tileWidth != 0) {
			throw new IllegalArgumentException("Tile width does not divide the original images width!");
		}
		if (imageHeight % tileHeight != 0) {
			throw new IllegalArgumentException("Tile height does not divide the original images height!");
		}

		this.original = original;
		this.metaData = metaData;
		this.tileSize = tileDimensions;

		this.sizeInTiles = new ImageMatrixDimensions(
				imageWidth / tileWidth,
				imageHeight / tileHeight);

		tiles = new ArrayList<>(imageWidth * imageHeight);

		// create each tile as a sub-region, referencing the original matrix
		for (int y = 0; y < sizeInTiles.getHeight(); y++) {
			for (int x = 0; x < sizeInTiles.getWidth(); x++) {
				tiles.add(new RegionImageMatrix<>(
						original,
						tileDimensions,
						tileWidth * x, tileHeight * y));
			}
		}
	}

	@Override
	public ImageMatrixInfo getMetaData() {
		return metaData;
	}

	@Override
	public ImageMatrixDimensions getDimensions() {
		return original.getDimensions();
	}

	@Override
	public V getValue(final int posX, final int posY) {
		return original.getValue(posX, posY);
	}

	@Override
	public ImageMatrix<V> getTile(final int index) {
		return this.tiles.get(index);
	}

	@Override
	public ImageMatrix<V> getTile(final int x, final int y) {
		return this.tiles.get(ArrayUtils.convert2DTo1D(x, y, sizeInTiles.getWidth()));
	}

	@Override
	public int getTileCount() {
		return this.tiles.size();
	}

	@Override
	public ImageMatrixDimensions getTileSize() {
		return tileSize;
	}

	@Override
	public ImageMatrixDimensions getSizeInTiles() {
		return sizeInTiles;
	}

	@Override
	public ImageMatrix<V> get(final int index) {
		return getTile(index);
	}

	@Override
	public int size() {
		return getTileCount();
	}
}
