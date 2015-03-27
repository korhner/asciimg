package io.korhner.asciimg.image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class TiledGrayscaleMatrix implements Iterable<GrayscaleMatrix> {

	private final List<GrayscaleMatrix> tiles;
	private final int tileXSize;
	private final int tileYSize;
	private final int tilesX;
	private final int tilesY;

	public int getTileXSize() {
		return tileXSize;
	}

	public int getTileYSize() {
		return tileYSize;
	}

	public int getTilesX() {
		return tilesX;
	}

	public int getTilesY() {
		return tilesY;
	}

	public TiledGrayscaleMatrix(GrayscaleMatrix matrix, int tileWidth,
			int tileHeight) {

		if (matrix.getWidth() < tileWidth || matrix.getHeight() < tileHeight) {
			throw new IllegalArgumentException(
					"Tile size must be smaller than original matrix!");
		}

		if (tileWidth <= 0 || tileHeight <= 0) {
			throw new IllegalArgumentException("Illegal tile size!");
		}

		this.tileXSize = tileWidth;
		this.tileYSize = tileHeight;

		// we won't allow partial tiles
		this.tilesX = matrix.getWidth() / tileWidth;
		this.tilesY = matrix.getHeight() / tileHeight;
		int roundedWidth = tilesX * tileWidth;
		int roundedHeight = tilesY * tileHeight;

		tiles = new ArrayList<GrayscaleMatrix>(roundedWidth * roundedHeight);

		for (int i = 0; i < tilesY; i++) {
			for (int j = 0; j < tilesX; j++) {
				tiles.add(GrayscaleMatrix.createFromRegion(matrix, tileWidth,
						tileHeight, this.tileXSize * j, this.tileYSize * i));
			}
		}
	}

	@Override
	public Iterator<GrayscaleMatrix> iterator() {
		return tiles.iterator();
	}
}
