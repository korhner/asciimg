package io.korhner.asciimg.image.matrix;

import io.korhner.asciimg.utils.ArrayUtils;

/**
 * Basic implementation of {@link ImageMatrix}, backed by an <code>int[]</code>.
 */
public class BasicInt1DImageMatrix extends AbstractImageMatrix<Integer> {

	/**
	 * The images data points.
	 */
	private final int[] data;

	/**
	 * Creates an empty image with the given dimensions.
	 *
	 * @param metaData
	 *            image meta data
	 * @param data
	 *            image data points
	 * @param width
	 *            image width in number of data points
	 */
	public BasicInt1DImageMatrix(final ImageMatrixInfo metaData, final int[] data, final int width) {
		super(metaData, new ImageMatrixDimensions(width, data.length / width));

		if (data.length % width != 0) {
			throw new IllegalArgumentException("width does not divide data");
		}

		this.data = data;
	}

	@Override
	public Integer getValue(final int posX, final int posY) {
		return data[ArrayUtils.convert2DTo1D(posX, posY, getDimensions().getWidth())];
	}
}
