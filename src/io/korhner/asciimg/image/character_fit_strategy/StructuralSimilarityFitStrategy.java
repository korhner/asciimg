package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.GrayscaleMatrix;

import java.util.Map.Entry;

public class StructuralSimilarityFitStrategy implements
		BestCharacterFitStrategy {

	private final float K1 = 0.01f;
	private final float K2 = 0.03f;
	private float L = 255f;
	private final int minSize = 1;

	@Override
	public Entry<Character, GrayscaleMatrix> findBestFit(
			AsciiImgCache characterCache, GrayscaleMatrix tile) {

		
		float maxScore = Float.MIN_VALUE;
		Entry<Character, GrayscaleMatrix> bestFit = null;
		for (Entry<Character, GrayscaleMatrix> charImage : characterCache) {
			GrayscaleMatrix charPixels = charImage.getValue();

			float score = computeSSIM(tile, charPixels);

			if (score > maxScore) {
				maxScore = score;
				bestFit = charImage;
			}
		}
		
		return bestFit;
	}

	public float computeSSIM(GrayscaleMatrix img1, GrayscaleMatrix img2) {
		if (img1.getWidth() < this.minSize || img1.getHeight() < this.minSize
				|| img2.getWidth() < this.minSize
				|| img2.getHeight() < this.minSize) {
			throw new IllegalArgumentException(
					"Image dimensions must be higher than " + this.minSize);
		}
		// uses notation from paper
		// automatic downsampling
		int f = (int) Math.max(1,
				Math.round(Math.min(img1.getWidth(), img1.getHeight()) / 256f));
		if (f > 1) { // downsampling by f
						// use a simple low-pass filter and subsample by f
			img1 = GrayscaleMatrix.createFromSubsample(img1, f);
			img2 = GrayscaleMatrix.createFromSubsample(img2, f);
		}

		// normalize window - todo - do in window set {}
		GrayscaleMatrix window = GrayscaleMatrix.createFromGaussian(minSize,
				1.5f);
		float scale = 1.0f / window.getTotal();
		window.scale(scale);

		// image statistics
		GrayscaleMatrix mu1 = GrayscaleMatrix.createFromFilter(img1, window);
		GrayscaleMatrix mu2 = GrayscaleMatrix.createFromFilter(img2, window);

		GrayscaleMatrix mu1mu2 = GrayscaleMatrix.multiply(mu1, mu2);
		GrayscaleMatrix mu1SQ = GrayscaleMatrix.multiply(mu1, mu1);
		GrayscaleMatrix mu2SQ = GrayscaleMatrix.multiply(mu2, mu2);

		GrayscaleMatrix sigma12 = GrayscaleMatrix.subtract(
				GrayscaleMatrix.createFromFilter(
						GrayscaleMatrix.multiply(img1, img2), window), mu1mu2);
		GrayscaleMatrix sigma1SQ = GrayscaleMatrix.subtract(
				GrayscaleMatrix.createFromFilter(
						GrayscaleMatrix.multiply(img1, img1), window), mu1SQ);
		GrayscaleMatrix sigma2SQ = GrayscaleMatrix.subtract(
				GrayscaleMatrix.createFromFilter(
						GrayscaleMatrix.multiply(img2, img2), window), mu2SQ);

		// constants from the paper
		float C1 = K1 * L;
		C1 *= C1;
		float C2 = K2 * L;
		C2 *= C2;

		GrayscaleMatrix ssim_map = new GrayscaleMatrix(mu1.getWidth(),
				mu1.getHeight());
		if ((C1 > 0) && (C2 > 0)) {
			for (int i = 0; i < ssim_map.getData().length; i++) {
				ssim_map.getData()[i] = (2 * mu1mu2.getData()[i] + C1)
						* (2 * sigma12.getData()[i] + C2)
						/ (mu1SQ.getData()[i] + mu2SQ.getData()[i] + C1)
						/ (sigma1SQ.getData()[i] + sigma2SQ.getData()[i] + C2);
			}

		} else {
			GrayscaleMatrix num1 = GrayscaleMatrix.createFromLinear(2, mu1mu2,
					C1);
			GrayscaleMatrix num2 = GrayscaleMatrix.createFromLinear(2, sigma12,
					C2);
			GrayscaleMatrix den1 = GrayscaleMatrix.createFromLinear(1,
					GrayscaleMatrix.add(mu1SQ, mu2SQ), C1);
			GrayscaleMatrix den2 = GrayscaleMatrix.createFromLinear(1,
					GrayscaleMatrix.add(sigma1SQ, sigma2SQ), C2);

			GrayscaleMatrix den = GrayscaleMatrix.multiply(den1, den2); // total
																		// denominator
			for (int i = 0; i < ssim_map.getData().length; ++i) {
				ssim_map.getData()[i] = 1;
				if (den.getData()[i] > 0) {
					ssim_map.getData()[i] = num1.getData()[i]
							* num2.getData()[i]
							/ (den1.getData()[i] * den2.getData()[i]);
				} else if ((den1.getData()[i] != 0) && (den2.getData()[i] == 0)) {
					ssim_map.getData()[i] = num1.getData()[i]
							/ den1.getData()[i];
				}

			}
		}
		// average all values
		return ssim_map.getTotal()
				/ (ssim_map.getWidth() * ssim_map.getHeight());

	}
}
