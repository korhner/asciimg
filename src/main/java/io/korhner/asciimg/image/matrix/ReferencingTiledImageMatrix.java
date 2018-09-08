package io.korhner.asciimg.image.matrix;

import io.korhner.asciimg.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Referencing implementation of {@link TiledImageMatrix}.
 */
public class ReferencingTiledImageMatrix<V> implements TiledImageMatrix<V> {

	private final ImageMatrix<V> original;
	private final ImageMatrixInfo metaData;

	/** The tiles. */
	private final List<ImageMatrix<V>> tiles;

	/** Width of a tile. */
	private final int tileWidth;

	/** Height of a tile. */
	private final int tileHeight;

	/** Number of tiles on the x axis. */
	private final int tilesX;

	/** Number of tiles on the y axis. */
	private final int tilesY;

	/**
	 * Instantiates a new tiled image matrix.
	 *
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
		final int imageWidth = original.getDimensions().getWidth();
		final int imageHeight = original.getDimensions().getHeight();
		final int tileWidth = tileDimensions.getWidth();
		final int tileHeight = tileDimensions.getHeight();

		if (tileWidth <= 0) {
			throw new IllegalArgumentException("Tile width has to be positive!");
		}
		if (tileHeight <= 0) {
			throw new IllegalArgumentException("Tile height has to be positive!");
		}
		if (tileWidth > imageWidth) {
			throw new IllegalArgumentException("Tile width larger then original images width!");
		}
		if (tileHeight > imageHeight) {
			throw new IllegalArgumentException("Tile height larger then original images height!");
		}
		if (imageWidth % tileWidth != 0) {
			throw new IllegalArgumentException("Tile width does not divide the original images width!");
		}
		if (imageHeight % tileHeight != 0) {
			throw new IllegalArgumentException("Tile height does not divide the original images height!");
		}

		this.original = original;
		this.metaData = metaData;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		// we won't allow partial tiles
		this.tilesX = imageWidth / tileWidth;
		this.tilesY = imageHeight / tileHeight;
		final int roundedWidth = tilesX * tileWidth;
		final int roundedHeight = tilesY * tileHeight;

		tiles = new ArrayList<>(roundedWidth * roundedHeight);

		// create each tile as a sub-region, referencing the original matrix
		for (int i = 0; i < tilesY; i++) {
			for (int j = 0; j < tilesX; j++) {
				tiles.add(new RegionImageMatrix<>(
						original,
						tileDimensions,
						this.tileWidth * j, this.tileHeight * i));
			}
		}
	}

	@Override
	public ImageMatrixInfo getMetadata() {
		return metaData;
	}

	@Override
	public ImageMatrix<V> getTile(final int index) {
		return this.tiles.get(index);
	}

	@Override
	public ImageMatrix<V> getTile(final int x, final int y) {
		return this.tiles.get(ArrayUtils.convert2DTo1D(x, y, getTilesX()));
	}

	@Override
	public int getTileCount() {
		return this.tiles.size();
	}

	@Override
	public int getTileHeight() {
		return this.tileHeight;
	}

	@Override
	public int getTilesX() {
		return this.tilesX;
	}

	@Override
	public int getTilesY() {
		return this.tilesY;
	}

	@Override
	public int getTileWidth() {
		return this.tileWidth;
	}

	@Override
	public ImageMatrixDimensions getImageDimensions() {
		return original.getDimensions();
	}
}
