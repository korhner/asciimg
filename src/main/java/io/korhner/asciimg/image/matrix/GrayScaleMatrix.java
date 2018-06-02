package io.korhner.asciimg.image.matrix;

import io.korhner.asciimg.utils.ArrayUtils;

/**
 * Encapsulates a gray-scale image.
 * Color values are floats with values between 0.0f and 255.0f.
 */
public class GrayScaleMatrix {

	/** Gray-scale pixel data. Values are between 0.0f and 255.0f. */
	private final float[][] data;

	/**
	 * Creates a new matrix from a sub region.
	 *
	 * @param source
	 *            source matrix
	 * @param width
	 *            sub region width
	 * @param height
	 *            sub-region height
	 * @param startPixelX
	 *            x coordinate of sub region start
	 * @param startPixelY
	 *            y coordinate of sub region start
	 * @return matrix containing the specified sub region
	 */
	public static GrayScaleMatrix createFromRegion(
			final GrayScaleMatrix source, final int width, final int height,
			final int startPixelX, final int startPixelY) {
		if (width <= 0 || height <= 0 || width > source.getWidth() || height > source.getHeight()) {
			throw new IllegalArgumentException("Illegal sub region size!");
		}

		final GrayScaleMatrix output = new GrayScaleMatrix(width, height);

		for (int cpx = 0; cpx < width; cpx++) {
			for (int cpy = 0; cpy < height; cpy++) {
				output.data[cpx][cpy] = source.data[startPixelX + cpx][startPixelY + cpy];
			}
		}

		return output;
	}

	/**
	 * Creates an empty image with the given dimensions.
	 *
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 */
	public GrayScaleMatrix(final int width, final int height) {
		this.data = new float[width][height];
	}

	/**
	 * Instantiates a new gray-scale matrix from a ARGB bitmap image.
	 *
	 * @param pixels
	 *            pixel data input ARGB format
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 */
	public GrayScaleMatrix(final int[] pixels, final int width, final int height) {
		this(width, height);

		if (width * height != pixels.length) {
			throw new IllegalArgumentException(
					"Pixels array does not match specified width and height!");
		}

		for (int cpx = 0; cpx < width; cpx++) {
			for (int cpy = 0; cpy < height; cpy++) {
				data[cpx][cpy] = convertRGBToGrayScale(pixels[ArrayUtils.convert2DTo1D(cpx, cpy, width)]);
			}
		}
	}

	/**
	 * Convert ARGB color to gray-scale float.
	 *
	 * @param rgbColor
	 *            ARGB color
	 * @return gray-scale float with value between 0.0f and 255.0f.
	 */
	private static float convertRGBToGrayScale(final int rgbColor) {
		// extract components
		final int red = (rgbColor >> 16) & 0xFF;
		final int green = (rgbColor >> 8) & 0xFF;
		final int blue = rgbColor & 0xFF;

		// convert to gray-scale
		return 0.3f * red + 0.59f * green + 0.11f * blue;
	}

	/**
	 * Returns the pixel value at a specified position.
	 *
	 * @return pixel gray-scale value
	 */
	public float getValue(final int posX, final int posY) {
		return data[posX][posY];
	}

	/**
	 * Gets the image height.
	 *
	 * @return the height
	 */
	public int getHeight() {
		return data[0].length;
	}

	/**
	 * Gets the image width.
	 *
	 * @return image width
	 */
	public int getWidth() {
		return data.length;
	}
}
