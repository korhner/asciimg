package io.korhner.asciimg.image.matrix;

/**
 * Contains 2D image dimensions.
 */
public class ImageMatrixDimensions {

	private final int width;
	private final int height;

	public ImageMatrixDimensions(final int width, final int height) {

		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
