package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.matrix.GrayscaleMatrix;

/**
 * Calculates Structural Similarity index (SSIM) between the images.
 *
 * See http://en.wikipedia.org/wiki/Structural_similarity for more info.
 */
public class StructuralSimilarityFitStrategy implements
		BestCharacterFitStrategy {

	private final float k1 = 0.01f;
	private final float k2 = 0.03f;
	private float l = 255f;

	@Override
	public float calculateError(final GrayscaleMatrix character, final GrayscaleMatrix tile) {

		float c1 = k1 * l;
		c1 *= c1;
		float c2 = k2 * l;
		c2 *= c2;

		final int imgLength = character.getData().length;

		float score = 0f;
		for (int i = 0; i < imgLength; i++) {
			float pixelImg1 = character.getData()[i];
			float pixelImg2 = tile.getData()[i];

			score += (2 * pixelImg1 * pixelImg2 + c1) * (2 + c2)
					/ (pixelImg1 * pixelImg1 + pixelImg2 * pixelImg2 + c1) / c2;
		}

		// average and convert score to error
		return 1 - (score / imgLength);

	}

}
