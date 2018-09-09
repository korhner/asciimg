package io.korhner.asciimg.image.importer;

import io.korhner.asciimg.image.matrix.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A class used to read/import an image through AWT.
 */
public class BufferedImageImageImporter implements ImageImporter<BufferedImage, Integer> {

	public static final ImageMatrixInfo META_DATA
			= new BasicImageMatrixInfo(4, Integer.class, 8);
	private BufferedImage source;

	@Override
	public void setSource(final BufferedImage source) {
		this.source = source;
	}

	@Override
	public int getFrames() {
		return 1;
	}

	@Override
	public ImageMatrix<Integer> read() throws IOException {

		if (source == null) {
			throw new IOException("Input source not set");
		}

		final ImageMatrixDimensions sourcePixelsSize = new ImageMatrixDimensions(source.getWidth(), source.getHeight());

		// extract pixels from source image
		final int[] imagePixels = source.getRGB(
				0, 0,
				sourcePixelsSize.getWidth(), sourcePixelsSize.getHeight(),
				null, 0, sourcePixelsSize.getWidth());

		// process the pixels to a gray-scale matrix
		return new BasicInt1DImageMatrix(META_DATA, imagePixels, sourcePixelsSize.getWidth());
	}
}
