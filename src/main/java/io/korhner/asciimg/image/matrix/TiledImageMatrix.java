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
