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

package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.image.importer.BufferedImageImageImporter;

import java.io.IOException;
import java.io.InputStream;

public class GifToAsciiConverter<O> extends AbstractToAsciiConverter<InputStream, O> {

	public GifToAsciiConverter() {}

	@Override
	public void convert(final InputStream source) throws IOException {

		getImporter().setSource(source);

		// initialize converters
		final int frameCount = getImporter().getFrames();
		final MultiFrameAsciiExporter<O> exporter = getExporter();
		exporter.setCharacterCache(getCharacterCache());
		exporter.initFrames(frameCount);
		for (int i = 0; i < frameCount; i++) {
			final ImageToAsciiConverter frameConverter = new ImageToAsciiConverter();
			final BufferedImageImageImporter frameImporter = new BufferedImageImageImporter();
			frameConverter.setImporter(frameImporter);
			frameConverter.setCharacterFitStrategy(getCharacterFitStrategy());
			frameConverter.setCharacterCache(getCharacterCache());
			frameConverter.setExporter(exporter);
			frameConverter.convert(getImporter().read());
		}
		exporter.finalizeFrames();
	}
}
