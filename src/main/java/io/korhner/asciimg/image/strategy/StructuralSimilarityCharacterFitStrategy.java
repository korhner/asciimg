package io.korhner.asciimg.image.strategy;

import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Calculates Structural Similarity index (SSIM) between the images.
 *
 * See http://en.wikipedia.org/wiki/Structural_similarity for more info.
 */
public class StructuralSimilarityCharacterFitStrategy implements CharacterFitStrategy {

	private static final float K_1 = 0.01f;
	private static final float K_2 = 0.03f;
	private static final float L = 255f;
	private static final float C_1 = (float) Math.pow(K_1 * L, 2);
	private static final float C_2 = (float) Math.pow(K_2 * L, 2);

	@Override
	public float calculateError(final ImageMatrix<Short> character, final ImageMatrix<Short> tile) {

		float score = 0f;
		for (int cpx = 0; cpx < character.getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < character.getDimensions().getHeight(); cpy++) {
				final float pixelVal1 = character.getValue(cpx, cpy);
				final float pixelVal2 = tile.getValue(cpx, cpy);

				score += (2 * pixelVal1 * pixelVal2 + C_1) * (2 + C_2)
						/ (pixelVal1 * pixelVal1 + pixelVal2 * pixelVal2 + C_1) / C_2;
			}
		}

		final int numPixels = character.getDimensions().getWidth() * character.getDimensions().getHeight();
		// average and convert score to error
		return 1 - (score / numPixels);
	}
}
