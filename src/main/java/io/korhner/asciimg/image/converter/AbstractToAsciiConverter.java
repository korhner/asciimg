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

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.image.importer.ImageImporter;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

/**
 * Basic interface implementation.
 *
 * @param <I> input type of the image (or similar) to be converted to ASCII art
 * @param <O> input type of the converted image or animation, the ASCII art
 */
public abstract class AbstractToAsciiConverter<I, O> implements ToAsciiConverter<I, O> {

	private ImageImporter<I, ?> importer;
	private CharacterFitStrategy characterFitStrategy;
	private AsciiImgCache characterCache;
	private MultiFrameAsciiExporter<O> exporter;

	protected AbstractToAsciiConverter() { }

	@Override
	public ImageImporter<I, ?> getImporter() {
		return importer;
	}

	@Override
	public void setImporter(final ImageImporter<I, ?> importer) {
		this.importer = importer;
	}

	@Override
	public CharacterFitStrategy getCharacterFitStrategy() {
		return this.characterFitStrategy;
	}

	@Override
	public void setCharacterFitStrategy(final CharacterFitStrategy characterFitStrategy) {
		this.characterFitStrategy = characterFitStrategy;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public MultiFrameAsciiExporter<O> getExporter() {
		return exporter;
	}

	@Override
	public void setExporter(final MultiFrameAsciiExporter<O> exporter) {
		this.exporter = exporter;
	}
}
