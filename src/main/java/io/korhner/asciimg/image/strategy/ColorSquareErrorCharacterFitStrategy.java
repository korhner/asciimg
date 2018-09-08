package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Calculates the squared mean error over all pixels between two images.
 */
public class ColorSquareErrorCharacterFitStrategy implements CharacterFitStrategy {

	@Override
	public float calculateError(final ImageMatrix<Short> character, final ImageMatrix<Short> tile) {

		float error = 0;

		// calculate sum of squared difference over all character pixels
		for (int cpx = 0; cpx < character.getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < character.getDimensions().getHeight(); cpy++) {
				final short pixelVal1 = character.getValue(cpx, cpy);
				final short pixelVal2 = tile.getValue(cpx, cpy);

				final float colorDiff = pixelVal1 - pixelVal2;
				error += colorDiff * colorDiff;
			}
		}

		final int numPixels = character.getDimensions().getWidth() * character.getDimensions().getHeight();
		return error / numPixels;
	}
}
