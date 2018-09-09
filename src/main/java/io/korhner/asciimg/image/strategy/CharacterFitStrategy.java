package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Evaluates how well a given character fits to a tile (part of an image).
 */
public interface CharacterFitStrategy {

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
	float calculateError(final ImageMatrix<Short> character, final ImageMatrix<Short> tile);
}
