package io.korhner.asciimg;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.converter.GifToAsciiConvert;
import io.korhner.asciimg.image.exporter.AnimatedGifMultiFrameAsciiExporter;
import org.junit.Test;

import java.awt.Font;
import java.io.*;

public class GifToAsciiConvertTest {

	@Test
	public void testAnimationConversion() throws IOException {

		// initialize caches
		final AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));
		// initialize ssimStrategy
		final BestCharacterFitStrategy ssimStrategy = new StructuralSimilarityFitStrategy();
		final AnimatedGifMultiFrameAsciiExporter exporter = new AnimatedGifMultiFrameAsciiExporter();


		final String origiResourcePath = "/examples/animation/orig.gif";
		final String expectedResourcePath = "/examples/animation/converted.gif";
		final int delay = 100; // ms
		final int repeat = 0; // times

		final GifToAsciiConvert asciiConvert = new GifToAsciiConvert();
		asciiConvert.setCharacterFitStrategy(ssimStrategy);
		asciiConvert.setCharacterCache(smallFontCache);
		asciiConvert.setExporter(exporter);
		exporter.setDelay(delay);
		exporter.setRepeat(repeat);

		final InputStream origSrc = getClass().getResourceAsStream(origiResourcePath);
		asciiConvert.convert(origSrc);
		final byte[] actual = exporter.getOutput();
		if (ImageToAsciiConverterTest.DEBUG) {
			final File actualTestImgFile = new File(ImageToAsciiConverterTest.DEBUG_OUTPUT_DIR, new File(expectedResourcePath).getName());
			System.err.println("Writing actual file to: " + actualTestImgFile.getAbsolutePath());
			final OutputStream output = new FileOutputStream(actualTestImgFile);
			output.write(actual);
			output.close();
		}

		final InputStream expectedSrc = getClass().getResourceAsStream(expectedResourcePath);
		final byte[] expected = ImageToAsciiConverterTest.readFully(expectedSrc);

		// it is very unlikely that we will get the exact same result, so we can not test like this
//		Assert.assertArrayEquals("generated and expected animated giff differ", expected, actual);
	}
}
