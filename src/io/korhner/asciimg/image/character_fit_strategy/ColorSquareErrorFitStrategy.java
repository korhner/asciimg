package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.matrix.GrayscaleMatrix;

/**
 * Calculates squared mean error between each pixel.
 */
public class ColorSquareErrorFitStrategy implements BestCharacterFitStrategy {

	/**
	 * @see io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy#calculateError(io.korhner.asciimg.image.matrix.GrayscaleMatrix, io.korhner.asciimg.image.matrix.GrayscaleMatrix)
	 */
	@Override
	public float calculateError(GrayscaleMatrix character, GrayscaleMatrix tile) {
		float error = 0;
		for (int i = 0; i < character.getData().length; i++) {
			error += (character.getData()[i] - tile.getData()[i])
					* (character.getData()[i] - tile.getData()[i]);
		}

		return error / character.getData().length;

	}

}
