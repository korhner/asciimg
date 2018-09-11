package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.importer.BufferedImageImageImporter;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;
import io.korhner.asciimg.image.strategy.ColorSquareErrorCharacterFitStrategy;
import io.korhner.asciimg.image.strategy.StructuralSimilarityCharacterFitStrategy;
import io.korhner.asciimg.image.exporter.ImageAsciiExporter;
import io.korhner.asciimg.image.exporter.TextAsciiExporter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageToAsciiConverterTest {

	/**
	 * Indicates whether to delete files created during unit test runs.
	 * You might want to manually set this to false, in case of test errors,
	 * so you can manually inspect them after tests finished.
	 */
	public static final boolean DELETE_FILES = true;
	private static final String ORIGIN_RESOURCE_PATH = "/examples/portrait/orig";
	private static final String EXPECTED_RESOURCE_PATH = "/examples/portrait/ascii_expected_%s";
	private static final String RESOURCE_SUFFIX = ".png";

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
			final CharacterFitStrategy characterFitStrategy,
			final AsciiImgCache cache,
			final ImageToAsciiConverter converter)
			throws IOException
	{
		converter.setImporter(new BufferedImageImageImporter());
		converter.setCharacterCache(cache);
		converter.setCharacterFitStrategy(characterFitStrategy);
		converter.convert(origImage);

		// TODO implement comparison

		return converter.getExporter().getOutput();
	}

	private void convertToImageAndCheck(
			final BufferedImage origImage,
			final String expectedResourcePath,
			final CharacterFitStrategy characterFitStrategy,
			final AsciiImgCache cache,
			final ImageToAsciiConverter converter)
			throws IOException
	{
		converter.setImporter(new BufferedImageImageImporter());
		converter.setCharacterCache(cache);
		converter.setCharacterFitStrategy(characterFitStrategy);
		final ImageAsciiExporter imageAsciiExporter = new ImageAsciiExporter();
		converter.setExporter(imageAsciiExporter);

		converter.convert(origImage);

		final BufferedImage expected = ImageIO.read(getClass().getResourceAsStream(expectedResourcePath + RESOURCE_SUFFIX));
		final BufferedImage actual = imageAsciiExporter.getOutput().get(0);

		// TODO implement comparison
//		actual.getData().getDataBuffer().getSize()

		if (!DELETE_FILES) {
			final File actualTestImgFile = File.createTempFile(new File(expectedResourcePath).getName(), RESOURCE_SUFFIX);
			System.err.println("Writing actual file to: " + actualTestImgFile.getAbsolutePath());
			ImageIO.write(actual, "png", actualTestImgFile);
		}
	}

	private static AsciiImgCache smallFontCache;
	private static AsciiImgCache mediumBlackAndWhiteCache;
	private static AsciiImgCache largeFontCache;
	private static BufferedImage portraitImage;
	private static CharacterFitStrategy squareErrorStrategy;
	private static CharacterFitStrategy ssimStrategy;
	private static ImageToAsciiConverter imageConverter;
	private static ImageToAsciiConverter stringConverter;

	@BeforeClass
	public static void initConversionRequirements() throws IOException {

		// initialize caches
		smallFontCache = AsciiImgCache.create(
				new Font("Courier", Font.BOLD, 6));
		mediumBlackAndWhiteCache = AsciiImgCache.create(
				new Font("Courier", Font.BOLD, 10), new char[]{'\\', ' ', '/'});
		largeFontCache = AsciiImgCache.create(
				new Font("Courier", Font.PLAIN, 16));

		// load image
		portraitImage = ImageIO.read(ImageToAsciiConverterTest.class.getResourceAsStream(
				ORIGIN_RESOURCE_PATH + RESOURCE_SUFFIX));

		// initialize algorithms
		squareErrorStrategy = new ColorSquareErrorCharacterFitStrategy();
		ssimStrategy = new StructuralSimilarityCharacterFitStrategy();

		// initialize converters
		imageConverter = new ImageToAsciiConverter();
		stringConverter = new ImageToAsciiConverter();
	}

	@Test
	public void testToImageSmallFontSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "small_square_error"),
				squareErrorStrategy,
				smallFontCache,
				imageConverter);
	}

	@Test
	public void testToImageMediumBwFontSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "medium_square_error"),
				squareErrorStrategy,
				mediumBlackAndWhiteCache,
				imageConverter);
	}

	@Test
	public void testToImageLargeFontSquareError() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "large_square_error"),
				squareErrorStrategy,
				largeFontCache,
				imageConverter);
	}

	@Test
	public void testToImageSmallFontSsim() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "small_ssim"),
				ssimStrategy,
				smallFontCache,
				imageConverter);
	}

	@Test
	public void testToImageMediumBwFontSsim() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "medium_ssim"),
				ssimStrategy,
				mediumBlackAndWhiteCache,
				imageConverter);
	}

	@Test
	public void testToImageLargeFontSsim() throws IOException {

		convertToImageAndCheck(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "large_ssim"),
				ssimStrategy,
				largeFontCache,
				imageConverter);
	}

	@Test
	public void testToTextLargeFontSsim() throws IOException {

		TextAsciiExporter textAsciiExporter = new TextAsciiExporter();
		stringConverter.setExporter(textAsciiExporter);
		final List<String> actual = (List<String>) convert(
				portraitImage,
				String.format(EXPECTED_RESOURCE_PATH, "large_ssim"),
				ssimStrategy,
				largeFontCache,
				stringConverter);
		System.out.println(actual.get(0));
	}
}
