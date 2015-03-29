package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.matrix.GrayscaleMatrix;

/**
 * Calculates Structural Similarity index (SSIM) between the images.
 * 
 * See http://en.wikipedia.org/wiki/Structural_similarity for more info.
 */
public class StructuralSimilarityFitStrategy implements
		BestCharacterFitStrategy {

	private final float K1 = 0.01f;
	private final float K2 = 0.03f;
	private float L = 255f;

	@Override
	public float calculateError(GrayscaleMatrix character, GrayscaleMatrix tile) {

		float C1 = K1 * L;
		C1 *= C1;
		float C2 = K2 * L;
		C2 *= C2;

		final int imgLength = character.getData().length;

		float score = 0f;
		for (int i = 0; i < imgLength; i++) {
			float pixelImg1 = character.getData()[i];
			float pixelImg2 = tile.getData()[i];

			score += (2 * pixelImg1 * pixelImg2 + C1) * (2 + C2)
					/ (pixelImg1 * pixelImg1 + pixelImg2 * pixelImg2 + C1) / C2;
		}

		// average and convert score to error
		return 1 - (score / imgLength);

	}

}
