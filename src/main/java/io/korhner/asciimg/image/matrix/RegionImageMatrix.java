package io.korhner.asciimg.image.matrix;

/**
 * A referencing representation of a sub-region of an image.
 */
public class RegionImageMatrix<T> extends AbstractImageMatrix<T> {

	/**
	 * Origin/source/referencing image.
	 */
	private final ImageMatrix<T> origin;
	private final int startPixelX;
	private final int startPixelY;

	/**
	 * @param origin
	 *            original/referenced image
	 * @param regionDimensions
	 *            width and height of the sub-region (in number of data points) to be represented
	 * @param startPixelX
	 *            start data point index of the sub-region on the x-axis
	 * @param startPixelY
	 *            start data point index of the sub-region on the y-axis
	 */
	public RegionImageMatrix(
			final ImageMatrix<T> origin,
			final ImageMatrixDimensions regionDimensions,
			final int startPixelX, final int startPixelY)
	{
		super(origin.getMetaData(), regionDimensions);

		this.origin = origin;
		this.startPixelX = startPixelX;
		this.startPixelY = startPixelY;

		assert startPixelX >= 0;
		assert startPixelY >= 0;
		assert regionDimensions.getWidth() > 0;
		assert regionDimensions.getHeight() > 0;
		assert startPixelX + regionDimensions.getWidth() <= origin.getDimensions().getWidth();
		assert startPixelY + regionDimensions.getHeight() <= origin.getDimensions().getHeight();
	}

	@Override
	public T getValue(final int posX, final int posY) {
		return origin.getValue(startPixelX + posX, startPixelY + posY);
	}
}
