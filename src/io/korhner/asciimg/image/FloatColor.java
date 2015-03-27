package io.korhner.asciimg.image;

/**
 * The class is used to encapsulate calculations with float color values and
 * converting colors from and to float and byte representation.
 */
public class FloatColor {

	/** The Constant ONE_BYTE_SHIFT. */
	private static final int ONE_BYTE_SHIFT = 8;

	/** The Constant TWO_BYTE_SHIFT. */
	private static final int TWO_BYTE_SHIFT = 16;

	/** The Constant THREE_BYTE_SHIFT. */
	private static final int THREE_BYTE_SHIFT = 24;

	/** The Constant FF. */
	private static final int FF = 0xFF;

	/** The Constant MAX_COLOR_VALUE. */
	private static final int MAX_COLOR_VALUE = 255;

	/**
	 * Gets the alpha.
	 * 
	 * @param color
	 *            the color
	 * @return the alpha
	 */
	public static int extractAlpha(final int color) {
		return (color >> THREE_BYTE_SHIFT) & FF;
	}

	/**
	 * Gets the blue.
	 * 
	 * @param color
	 *            the color
	 * @return the blue
	 */
	public static int extractBlue(final int color) {
		return color & FF;
	}

	/**
	 * Gets the green.
	 * 
	 * @param color
	 *            the color
	 * @return the green
	 */
	public static int extractGreen(final int color) {
		return (color >> ONE_BYTE_SHIFT) & FF;
	}

	/**
	 * Gets the red.
	 * 
	 * @param color
	 *            the color
	 * @return the red
	 */
	public static int extractRed(final int color) {
		return (color >> TWO_BYTE_SHIFT) & FF;
	}

	/** The red. */
	private float red;

	/** The green. */
	private float green;

	/** The blue. */
	private float blue;

	/** The alpha. */
	private float alpha;

	/**
	 * Gets the alpha.
	 * 
	 * @return the alpha
	 */
	public float getAlpha() {
		return this.alpha;
	}

	/**
	 * Gets the blue.
	 * 
	 * @return the blue
	 */
	public float getBlue() {
		return this.blue;
	}

	/**
	 * Gets the color in byte representation.
	 * 
	 * @return the color
	 */
	public int getColor() {
		int a = (int) (this.alpha * MAX_COLOR_VALUE);
		int r = (int) (this.red * MAX_COLOR_VALUE);
		int g = (int) (this.green * MAX_COLOR_VALUE);
		int b = (int) (this.blue * MAX_COLOR_VALUE);

		return getColorFromComponents(a, r, g, b);
	}

	public static int getColorFromComponents(int a, int r, int g, int b) {
		return ((a & FF) << THREE_BYTE_SHIFT) | ((r & FF) << TWO_BYTE_SHIFT)
				| ((g & FF) << ONE_BYTE_SHIFT) | ((b & FF));
	}

	/**
	 * Gets the green.
	 * 
	 * @return the green
	 */
	public float getGreen() {
		return this.green;
	}

	/**
	 * Gets the red.
	 * 
	 * @return the red
	 */
	public float getRed() {
		return this.red;
	}

	/**
	 * Sets the alpha.
	 * 
	 * @param alpha
	 *            the new alpha
	 */
	public void setAlpha(final float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Sets the blue.
	 * 
	 * @param blue
	 *            the new blue
	 */
	public void setBlue(final float blue) {
		this.blue = blue;
	}

	/**
	 * Sets the color.
	 * 
	 * @param color
	 *            the new color
	 */
	public void setColor(final int color) {
		this.red = (float) extractRed(color) / (float) MAX_COLOR_VALUE;
		this.green = (float) extractGreen(color) / (float) MAX_COLOR_VALUE;
		this.blue = (float) extractBlue(color) / (float) MAX_COLOR_VALUE;
		this.alpha = (float) extractAlpha(color) / (float) MAX_COLOR_VALUE;
	}

	/**
	 * Sets the green.
	 * 
	 * @param green
	 *            the new green
	 */
	public void setGreen(final float green) {
		this.green = green;
	}

	/**
	 * Sets the red.
	 * 
	 * @param red
	 *            the new red
	 */
	public void setRed(final float red) {
		this.red = red;
	}

	public void convertToGrayscale() {

		float grayscale = 0.3f * this.red + 0.59f * this.green + 0.11f
				* this.blue;
		this.red = grayscale;
		this.green = grayscale;
		this.blue = grayscale;
	}

	public float getSquareError(final FloatColor color) {
		return (this.red - color.red) * (this.red - color.red)
				+ (this.green - color.green) * (this.green - color.green)
				+ (this.blue - color.blue) * (this.blue - color.blue);
	}

}
