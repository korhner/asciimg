package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;

/**
 * Calculates squared mean error between each pixel.
 */
public class ColorSquareErrorFitStrategy implements BestCharacterFitStrategy {

	@Override
	public float calculateError(final GrayScaleMatrix character, final GrayScaleMatrix tile) {
		float error = 0;
		for (int i = 0; i < character.getData().length; i++) {
			error += (character.getData()[i] - tile.getData()[i])
					* (character.getData()[i] - tile.getData()[i]);
		}

		return error / character.getData().length;
	}
}
