package io.korhner.asciimg.image.transformer;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Transforms a colored input into gray-scale output.
 */
public class ToGrayscaleImageTransformer implements ImageTransformer<Integer, Short> {

	@Override
	public ImageMatrix<Short> transform(ImageMatrix<Integer> source) {

		return new GrayScaleMatrix(source);
	}
}
