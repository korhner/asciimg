package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;

/**
 * Calculates the squared mean error over all pixels between two images.
 */
public class ColorSquareErrorFitStrategy implements BestCharacterFitStrategy {

	@Override
	public float calculateError(final GrayScaleMatrix character, final GrayScaleMatrix tile) {

		float error = 0;

		// calculate sum of squared difference over all character pixels
		for (int cpx = 0; cpx < character.getWidth(); cpx++) {
			for (int cpy = 0; cpy < character.getHeight(); cpy++) {
				final float pixelVal1 = character.getValue(cpx, cpy);
				final float pixelVal2 = tile.getValue(cpx, cpy);

				final float colorDiff = pixelVal1 - pixelVal2;
				error += colorDiff * colorDiff;
			}
		}

		final int numPixels = character.getWidth() * character.getHeight();
		return error / numPixels;
	}
}
