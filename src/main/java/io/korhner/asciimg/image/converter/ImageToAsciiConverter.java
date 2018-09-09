package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.matrix.*;
import io.korhner.asciimg.image.strategy.CharacterFinder;
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

		// find best fitting character for each tile
		// NOTE We go through Y in the outer loop to improve locality
		//      -> low level performance optimization
		for (int tileY = 0; tileY < tiledMatrix.getTilesY(); tileY++) {
			for (int tileX = 0; tileX < tiledMatrix.getTilesX(); tileX++) {
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
}
