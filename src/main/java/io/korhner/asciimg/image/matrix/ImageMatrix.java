package io.korhner.asciimg.image.matrix;

/**
 * Encapsulates an images raw, basic data.
 * This could be characters of an ASCII image, or gray scale or RGB values of a bitmap image.
 */
public interface ImageMatrix<T> {

	ImageMatrixInfo getMetaData();

	/**
	 * Returns the value at a specified position.
	 *
	 * @param posX x-coordinate of the data point to fetch
	 * @param posY y-coordinate of the data point to fetch
	 * @return data point value
	 */
	T getValue(final int posX, final int posY);

	/**
	 * @return the images dimensions
	 */
	ImageMatrixDimensions getDimensions();
}
