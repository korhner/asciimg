package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;

/**
 * Encapsulates the algorithm for choosing best fit character.
 */
public interface BestCharacterFitStrategy {

	/**
	 * Returns the error between the character and tile matrices.
	 * The character with minimum error wins.
	 *
	 * @param character
	 *            the character
	 * @param tile
	 *            the tile
	 * @return error. Less values mean better fit. Least value character will be
	 *         chosen as best fit.
	 */
	float calculateError(final GrayScaleMatrix character,
			final GrayScaleMatrix tile);
}
