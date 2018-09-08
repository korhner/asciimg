package io.korhner.asciimg.image.matrix;

/**
 * Contains basic image meta data.
 */
public interface ImageMatrixInfo {

	boolean isGrayScale();
	boolean isBlackAndWhite();
	boolean isColored();
	boolean isWithAlpha();

	/**
	 * Indicates the number of values per data point.
	 * Examples:
	 * - ARGB: 4
	 * - RGB: 3
	 * - grey scale: 1
	 * - black & white: 1
	 * @return the number of values per data point
	 */
	int getValuesPerDataPoint();

	Class getDataPointClass();

	/**
	 * Indicates the number of bits used to represent each value (see {@link #getValuesPerDataPoint()}).
	 * Examples:
	 * - ARGB: 8
	 * - RGB: 8
	 * - grey scale: 8
	 * - black & white: 1
	 * @return the number of bits used to represent each value withing the data-point
	 */
	int getBitsPerValue();

}
