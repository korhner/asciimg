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
 * Separates an input image into multiple tiles.
 * You may want ot think of the original image a sa chess-board,
 * and the tiles as the fields of the board.
 *
 * @param <D>
 *            data point value type, as in, the class of a "pixel" of the image
 */
public interface TiledImageMatrix<D> extends ImageMatrix<D>, Iterable<ImageMatrix<D>> {

	/**
	 * Gets the tile at a specific index.
	 *
	 * @param index
	 *            tile index
	 * @return the tile
	 */
	ImageMatrix<D> getTile(final int index);

	/**
	 * Gets the tile at a specific y and z location.
	 *
	 * @param x
	 *            x location of the tile to fetch
	 * @param y
	 *            y location of the tile to fetch
	 * @return the tile
	 */
	ImageMatrix<D> getTile(final int x, final int y);

	/**
	 * @return the number of tiles
	 */
	int getTileCount();

	/**
	 * @return size of a tile in data points
	 */
	ImageMatrixDimensions getTileSize();

	/**
	 * @return number of tiles on the x and y axis
	 */
	ImageMatrixDimensions getSizeInTiles();
}
