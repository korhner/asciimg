package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.matrix.*;
import io.korhner.asciimg.utils.ArrayUtils;
import java.util.Map.Entry;

/**
 * A class used to convert an abstract 32bit ARGB image to an ASCII art.
 * Output and conversion algorithm are decoupled.
 */
public class ImageToAsciiConverter extends AbstractToAsciiConverter<ImageMatrix<Short>> {

	public ImageToAsciiConverter() {}

	@Override
	public void convert(final ImageMatrix<Short> source) {

		// dimension of each tile
		final ImageMatrixDimensions tileSize = this.getCharacterCache().getCharacterImageSize();

		// divide matrix into tiles for easy processing
		final ReferencingTiledImageMatrix<Short> tiledMatrix = new ReferencingTiledImageMatrix<>(
				source.getMetaData(), source, tileSize);

		getExporter().setCharacterCache(getCharacterCache());
		getExporter().init(tiledMatrix);

		// compare each tile to every character to determine best fit
		// XXX This could be speed up (in case of a large character set), by arranging characters in a tree, with the non-leafs being lower-resolution representations of their children. this requires creating a lower-resolution version of each tile, though.
		for (int i = 0; i < tiledMatrix.getTileCount(); i++) {

			final ImageMatrix<Short> tile = tiledMatrix.getTile(i);

			float minError = Float.MAX_VALUE;
			Entry<Character, ImageMatrix<Short>> bestFit = null;

			for (final Entry<Character, ImageMatrix<Short>> charImage : getCharacterCache()) {
				final ImageMatrix<Short> charPixels = charImage.getValue();

				final float error = this.getCharacterFitStrategy().calculateError(charPixels, tile);

				if (error < minError) {
					minError = error;
					bestFit = charImage;
				}
			}

			final int tileX = ArrayUtils.convert1DtoX(i, tiledMatrix.getTilesX());
			final int tileY = ArrayUtils.convert1DtoY(i, tiledMatrix.getTilesX());

			// copy character to output
			getExporter().addCharacter(bestFit, tileX, tileY);
		}

		getExporter().imageEnd();
	}
}
