/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 korhner <korhner@gmail.com>
 * Copyright (c) 2018 hoijui <hoijui.quaero@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
