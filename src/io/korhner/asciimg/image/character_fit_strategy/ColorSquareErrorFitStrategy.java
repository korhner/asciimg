package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.FloatColor;
import io.korhner.asciimg.image.GrayscaleMatrix;
import io.korhner.asciimg.image.ImageUtils;

import java.util.Map.Entry;

public class ColorSquareErrorFitStrategy implements BestCharacterFitStrategy {

	@Override
	public Entry<Character, GrayscaleMatrix> findBestFit(
			AsciiImgCache characterCache, GrayscaleMatrix tile) {
		
		float minError = Float.MAX_VALUE;
		Entry<Character, GrayscaleMatrix> bestFit = null;

		for (Entry<Character, GrayscaleMatrix> charImage : characterCache) {
			GrayscaleMatrix charPixels = charImage.getValue();

			float error = tile.calculateMeanSquareError(charPixels);

			if (error < minError) {
				minError = error;
				bestFit = charImage;
			}
		}
		
		return bestFit;
	}

}
