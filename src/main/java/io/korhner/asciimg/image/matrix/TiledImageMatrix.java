package io.korhner.asciimg.image.matrix;

/**
 * Separates an input image into multiple tiles.
 * You may want ot think of the original image a sa chess-board,
 * and the tiles as the fields of the board.
 */
public interface TiledImageMatrix<V> {

	ImageMatrixInfo getMetadata();

	/**
	 * Gets the tile at a specific index.
	 *
	 * @param index
	 *            tile index
	 * @return the tile
	 */
	ImageMatrix<V> getTile(final int index);

	/**
	 * Gets the tile at a specific y and z location.
	 *
	 * @param x
	 *            x location of the tile to fetch
	 * @param y
	 *            y location of the tile to fetch
	 * @return the tile
	 */
	ImageMatrix<V> getTile(final int x, final int y);

	/**
	 * @return the number of tiles
	 */
	int getTileCount();

	/**
	 * @return the tile y size
	 */
	int getTileHeight();

	/**
	 * @return number of tiles on x axis
	 */
	int getTilesX();

	/**
	 * @return number of tiles on y axis
	 */
	int getTilesY();

	/**
	 * @return tile width
	 */
	int getTileWidth();

	/**
	 * @return tile width
	 */
	ImageMatrixDimensions getImageDimensions();
}
