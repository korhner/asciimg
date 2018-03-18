package io.korhner.asciimg;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.exporter.ImageAsciiExporter;
import io.korhner.asciimg.image.converter.ImageToAsciiConverter;
import io.korhner.asciimg.image.exporter.TextAsciiExporter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageToAsciiConverterTest {

	public static final boolean DEBUG = false;
	public static final String DEBUG_OUTPUT_DIR = "/tmp";

	public static byte[] readFully(final InputStream input) throws IOException {

		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = input.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}

	private Object convert(
			final BufferedImage origImage,
			final String expectedResourcePath,
			final BestCharacterFitStrategy fitStrategy,
			final AsciiImgCache cache,
			final ImageToAsciiConverter converter)
			throws IOException {

		converter.setCharacterCache(cache);
		converter.setCharacterFitStrategy(fitStrategy);
		converter.convert(origImage);

		return converter.getExporter().getOutput();
	}

	private void convertToImageAndCheck(
			final BufferedImage origImage,
			final String expectedResourcePath,
			final BestCharacterFitStrategy fitStrategy,
			final AsciiImgCache cache,
			final ImageToAsciiConverter converter)
			throws IOException {

		converter.setCharacterCache(cache);
		converter.setCharacterFitStrategy(fitStrategy);
		final ImageAsciiExporter imageAsciiExporter = new ImageAsciiExporter();
		converter.setExporter(imageAsciiExporter);

		converter.convert(origImage);

		final BufferedImage expected = ImageIO.read(getClass().getResourceAsStream(expectedResourcePath));
		final BufferedImage actual = imageAsciiExporter.getOutput();

		// TODO implement comparison
//		actual.getData().getDataBuffer().getSize()

		if (DEBUG) {
			final File actualTestImgFile = new File(DEBUG_OUTPUT_DIR, new File(expectedResourcePath).getName());
			System.err.println("Writing actual file to: " + actualTestImgFile.getAbsolutePath());
			ImageIO.write(actual, "png", actualTestImgFile);
		}
	}

	private static AsciiImgCache smallFontCache;
	private static AsciiImgCache mediumBlackAndWhiteCache;
	private static AsciiImgCache largeFontCache;
	private static BufferedImage portraitImage;
	private static BestCharacterFitStrategy squareErrorStrategy;
	private static BestCharacterFitStrategy ssimStrategy;
	private static ImageToAsciiConverter imageConverter;
	private static ImageToAsciiConverter stringConverter;

	@BeforeClass
	public static void imagesConversion() throws IOException {

		// initialize caches
		smallFontCache = AsciiImgCache.create(
				new Font("Courier", Font.BOLD, 6));
		mediumBlackAndWhiteCache = AsciiImgCache.create(
				new Font("Courier", Font.BOLD, 10), new char[]{'\\', ' ', '/'});
		largeFontCache = AsciiImgCache.create(
				new Font("Courier", Font.PLAIN, 16));

		// load image
		portraitImage = ImageIO.read(ImageToAsciiConverterTest.class.getResourceAsStream(
				"/examples/portrait/orig.png"));

		// initialize algorithms
		squareErrorStrategy = new ColorSquareErrorFitStrategy();
		ssimStrategy = new StructuralSimilarityFitStrategy();

		// initialize converters
		imageConverter = new ImageToAsciiConverter();
		stringConverter = new ImageToAsciiConverter();
	}

	@Test
	public void smallFontImagesSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_small_square_error.png",
				squareErrorStrategy,
				smallFontCache,
				imageConverter);
	}

	@Test
	public void mediumFontImagesSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_medium_square_error.png",
				squareErrorStrategy,
				mediumBlackAndWhiteCache,
				imageConverter);
	}

	@Test
	public void largeFontImagesSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_large_square_error.png",
				squareErrorStrategy,
				largeFontCache,
				imageConverter);
	}

	@Test
	public void smallFontImagesSSIM() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_small_ssim.png",
				ssimStrategy,
				smallFontCache,
				imageConverter);
	}

	@Test
	public void mediumFontImagesSSIM() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_medium_ssim.png",
				ssimStrategy,
				mediumBlackAndWhiteCache,
				imageConverter);
	}

	@Test
	public void largeFontImagesSSIM() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				"/examples/portrait/converted_large_ssim.png",
				ssimStrategy,
				largeFontCache,
				imageConverter);
	}

	@Test
	public void toTextOnConsole() throws IOException {

		TextAsciiExporter textAsciiExporter = new TextAsciiExporter();
		stringConverter.setExporter(textAsciiExporter);
		final String actual = (String) convert(
				portraitImage,
				"/examples/portrait/converted_large_ssim.png",
				ssimStrategy,
				largeFontCache,
				stringConverter);
		System.out.println(actual);
	}
}
