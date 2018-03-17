package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;

/**
 * Calculates Structural Similarity index (SSIM) between the images.
 *
 * See http://en.wikipedia.org/wiki/Structural_similarity for more info.
 */
public class StructuralSimilarityFitStrategy implements BestCharacterFitStrategy {

	private static final float K_1 = 0.01f;
	private static final float K_2 = 0.03f;
	private static final float L = 255f;
	private static final float C_1 = (float) Math.pow(K_1 * L, 2);
	private static final float C_2 = (float) Math.pow(K_2 * L, 2);

	@Override
	public float calculateError(final GrayScaleMatrix character, final GrayScaleMatrix tile) {

		final int imgLength = character.getData().length;

		float score = 0f;
		for (int i = 0; i < imgLength; i++) {
			final float pixelImg1 = character.getData()[i];
			final float pixelImg2 = tile.getData()[i];

			score += (2 * pixelImg1 * pixelImg2 + C_1) * (2 + C_2)
					/ (pixelImg1 * pixelImg1 + pixelImg2 * pixelImg2 + C_1) / C_2;
		}

		// average and convert score to error
		return 1 - (score / imgLength);
	}
}
