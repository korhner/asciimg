package io.korhner.asciimg.utils;

/**
 * An utility class used for various array utilities.
 */
public class ArrayUtils {

	/**
	 * Converts from 1D array index to 1D on x axis.
	 *
	 * @param index
	 *            The index of 1D array.
	 * @param arrayWidth
	 *            2D Array width (length of rows on x axis).
	 * @return Corresponding index of x axis.
	 */
	public static int convert1DtoX(final int index, final int arrayWidth) {
		return index % arrayWidth;
	}

	/**
	 * Converts from 1D array index to 1D on y axis.
	 *
	 * @param index
	 *            The index of 1D array.
	 * @param arrayWidth
	 *            2D Array width (length of rows on x axis).
	 * @return Corresponding index of y axis.
	 */
	public static int convert1DtoY(final int index, final int arrayWidth) {
		return index / arrayWidth;
	}

	/**
	 * Converts from 2D array index to 1D.
	 *
	 * @param x
	 *            The index on x axis.
	 * @param y
	 *            The index on x axis.
	 * @param arrayWidth
	 *            2D Array width (length of rows on x axis).
	 * @return Corresponding index if the array was 1D.
	 */
	public static int convert2DTo1D(final int x, final int y,
			final int arrayWidth) {
		return y * arrayWidth + x;
	}
}
