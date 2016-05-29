package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.matrix.GrayscaleMatrix;

/**
 * Encapsulates the algorith for choosing best fit character.
 */
public interface BestCharacterFitStrategy {

	/**
	 * Returns the error between the character and tile matrices. The character
	 * with minimun error wins.
	 *
	 * @param character
	 *            the character
	 * @param tile
	 *            the tile
	 * @return error. Less values mean better fit. Least value character will be
	 *         chosen as best fit.
	 */
	float calculateError(final GrayscaleMatrix character,
			final GrayscaleMatrix tile);
}
