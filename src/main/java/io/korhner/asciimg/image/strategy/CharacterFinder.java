package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;

import java.util.Map;

/**
 * Encapsulates the algorithm for choosing the best fit character.
 */
public class CharacterFinder {

	private final AsciiImgCache characterCache;
	private final CharacterFitStrategy characterFitStrategy;

	public CharacterFinder(AsciiImgCache characterCache, CharacterFitStrategy characterFitStrategy) {

		this.characterCache = characterCache;
		this.characterFitStrategy = characterFitStrategy;
	}

	/**
	 * Returns the best fit character for a given tile (part of an image).
	 *
	 * @param tile
	 *            the tile to find a fit for
	 * @return the character with minimum error
	 */
	public Map.Entry<Character, ImageMatrix<Short>> findBestFit(final ImageMatrix<Short> tile) {

		Map.Entry<Character, ImageMatrix<Short>> bestFit = null;

		// XXX This could be speed up (in case of a large character set), by arranging characters in a tree, with the non-leafs being lower-resolution representations of their children. this requires creating a lower-resolution version of each tile, though.
		// TODO Maybe check how libcaca does this, as it is probably quite heavily optimized
		float minError = Float.MAX_VALUE;
		for (final Map.Entry<Character, ImageMatrix<Short>> charImage : getCharacterCache()) {
			final ImageMatrix<Short> charPixels = charImage.getValue();

			final float error = getCharacterFitStrategy().calculateError(charPixels, tile);

			if (error < minError) {
				minError = error;
				bestFit = charImage;
			}
		}

		return bestFit;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	protected CharacterFitStrategy getCharacterFitStrategy() {
		return characterFitStrategy;
	}
}
