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

	private void testAnimationConversion(final CharacterFitStrategy characterFitStrategy, final String specifier) throws IOException {

		// initialize caches
		final AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));
		final AnimatedGifMultiFrameAsciiExporter exporter = new AnimatedGifMultiFrameAsciiExporter();

		final String originResourcePath = "/examples/animation/orig.gif";
		final String expectedResourcePath = "/examples/animation/ascii_expected_%s.gif";
		final int delay = 100; // ms
		final int repeat = 0; // times

		final GifToAsciiConverter asciiConvert = new GifToAsciiConverter();
		asciiConvert.setImporter(new GifImageImporter());
		asciiConvert.setCharacterFitStrategy(characterFitStrategy);
		asciiConvert.setCharacterCache(smallFontCache);
		asciiConvert.setExporter(exporter);
		exporter.setDelay(delay);
		exporter.setRepeat(repeat);

		final String expectedResStr = String.format(expectedResourcePath, specifier);

		final InputStream origSrc = getClass().getResourceAsStream(originResourcePath);
		asciiConvert.convert(origSrc);
		final byte[] actual = exporter.getOutput();
		if (ImageToAsciiConverterTest.DEBUG) {
			final File actualTestImgFile = new File(ImageToAsciiConverterTest.DEBUG_OUTPUT_DIR, new File(expectedResStr).getName());
			System.err.println("Writing actual file to: " + actualTestImgFile.getAbsolutePath());
			final OutputStream output = new FileOutputStream(actualTestImgFile);
			output.write(actual);
			output.close();
		}

		final InputStream expectedSrc = getClass().getResourceAsStream(expectedResStr);
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
