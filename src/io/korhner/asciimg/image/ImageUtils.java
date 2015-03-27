package io.korhner.asciimg.image;

public class ImageUtils {

	/**
	 * Converts 2D coordinates to 1D.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param imageWidth
	 *            the image width
	 * @return the int
	 */
	public static int convert2DTo1D(final int x, final int y,
			final int imageWidth) {
		return y * imageWidth + x;
	}

	public static int convert1DtoX(final int index, final int imageWidth) {
		return index % imageWidth;
	}

	public static int convert1DtoY(final int index, final int imageWidth) {
		return index / imageWidth;
	}
}
