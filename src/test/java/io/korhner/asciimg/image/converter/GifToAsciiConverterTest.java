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
import io.korhner.asciimg.image.importer.GifImageImporter;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;
import io.korhner.asciimg.image.strategy.ColorSquareErrorCharacterFitStrategy;
import io.korhner.asciimg.image.strategy.StructuralSimilarityCharacterFitStrategy;
import io.korhner.asciimg.image.exporter.AnimatedGifMultiFrameAsciiExporter;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Font;
import java.io.*;

public class GifToAsciiConverterTest {

	public static final String ORIGIN_RESOURCE_PATH = "/examples/animation/orig";
	public static final String EXPECTED_RESOURCE_PATH = "/examples/animation/ascii_expected_%s";
	public static final String RESOURCE_SUFFIX = ".gif";

	private void testAnimationConversion(final CharacterFitStrategy characterFitStrategy, final String specifier) throws IOException {

		// initialize caches
		final AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));
		final AnimatedGifMultiFrameAsciiExporter exporter = new AnimatedGifMultiFrameAsciiExporter();

		final int delay = 100; // ms
		final int repeat = 0; // times

		final GifToAsciiConverter asciiConvert = new GifToAsciiConverter();
		asciiConvert.setImporter(new GifImageImporter());
		asciiConvert.setCharacterFitStrategy(characterFitStrategy);
		asciiConvert.setCharacterCache(smallFontCache);
		asciiConvert.setExporter(exporter);
		exporter.setDelay(delay);
		exporter.setRepeat(repeat);

		final String expectedResStr = String.format(EXPECTED_RESOURCE_PATH, specifier);

		final InputStream origSrc = getClass().getResourceAsStream(ORIGIN_RESOURCE_PATH + RESOURCE_SUFFIX);
		asciiConvert.convert(origSrc);
		final byte[] actual = exporter.getOutput();
		final File actualTestImgFile = File.createTempFile(new File(expectedResStr).getName(), RESOURCE_SUFFIX);
		if (ImageToAsciiConverterTest.DELETE_FILES) {
			actualTestImgFile.deleteOnExit();
		}
		final OutputStream output = new FileOutputStream(actualTestImgFile);
		output.write(actual);
		output.close();

		final InputStream expectedSrc = getClass().getResourceAsStream(expectedResStr + RESOURCE_SUFFIX);
		final byte[] expected = ImageToAsciiConverterTest.readFully(expectedSrc);

		// NOTE It is probably unlikely that we will get the exact same result on different systems,
		//   so we might have to revise or disable this check.
		Assert.assertArrayEquals("generated and expected animated giff differ", expected, actual);
	}

	@Test
	public void testAnimationConversionSsim() throws IOException {
		testAnimationConversion(new StructuralSimilarityCharacterFitStrategy(), "ssim");
	}

	@Test
	public void testAnimationConversionSquareError() throws IOException {
		testAnimationConversion(new ColorSquareErrorCharacterFitStrategy(), "square_error");
	}
}
