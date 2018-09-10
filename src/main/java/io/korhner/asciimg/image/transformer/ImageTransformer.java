package io.korhner.asciimg.image.transformer;

import io.korhner.asciimg.image.matrix.ImageMatrix;

/**
 * Transforms one internal image representation into an other.
 *
 * @param <I>
 *            data-point value type of the input image
 * @param <O>
 *            data-point value type of the output image
 */
public interface ImageTransformer<I, O> {

	/**
	 * Transforms one internal image representation into an other.
	 *
	 * @param source
	 *            data-point value type of the input image
	 * @return data-point value type of the output image
	 */
	ImageMatrix<O> transform(ImageMatrix<I> source);
}
