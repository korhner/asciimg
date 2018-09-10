package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.matrix.*;
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
public class ImageToAsciiConverter extends AbstractToAsciiConverter<BufferedImage> {

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

	@Override
	public void convert(final BufferedImage source) throws IOException {

		getImporter().setSource(source);
		final ImageMatrix input = getImporter().read();

		convert(input);
	}
}
