package io.korhner.asciimg.image.matrix;

/**
 * Abstract implementation of {@link ImageMatrix}, just missing the data.
 */
public abstract class AbstractImageMatrix<T> implements ImageMatrix<T> {

	private final ImageMatrixInfo metaData;
	private final ImageMatrixDimensions dimensions;

	/**
	 * Creates an image with the given dimensions.
	 *
	 * @param metaData
	 *            image meta data
	 * @param dimensions
	 *            image width and height
	 */
	public AbstractImageMatrix(final ImageMatrixInfo metaData, final ImageMatrixDimensions dimensions) {

		this.metaData = metaData;
		this.dimensions = dimensions;
	}

	@Override
	public ImageMatrixInfo getMetaData() {
		return metaData;
	}

	@Override
	public ImageMatrixDimensions getDimensions() {
		return dimensions;
	}
}
