package io.korhner.asciimg.image.importer;

import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.utils.GifDecoder;

import java.io.IOException;
import java.io.InputStream;

public class GifImageImporter implements ImageImporter<InputStream, Integer> {

	private InputStream source;
	private GifDecoder decoder;
	private int frameCount = -1;
	private int framesRead = -1;

	@Override
	public void setSource(final InputStream source) {
		this.source = source;
	}

	private void initDecoder() throws IOException {

		if (decoder == null) {
			if (source == null) {
				throw new IOException("Input source not set");
			}

			decoder = new GifDecoder();
			final int status = decoder.read(source);
			if (status != 0) {
				throw new IOException(String.format(
						"Failed to read GIF source from '%s', error: %d",
						String.valueOf(source), status));
			}
			frameCount = decoder.getFrameCount();
			framesRead = 0;
		}
	}

	@Override
	public int getFrames() throws IOException {

		initDecoder();

		return frameCount;
	}

	@Override
	public ImageMatrix<Integer> read() throws IOException {

		initDecoder();

		ImageMatrix<Integer> result;
		if (framesRead < frameCount) {
			final BufferedImageImageImporter frameImporter = new BufferedImageImageImporter();
			frameImporter.setSource(decoder.getFrame(framesRead));
			result = frameImporter.read();
			framesRead++;
		} else {
			throw new IOException("No more frames to be read");
		}

		return result;
	}
}
