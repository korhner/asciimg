package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.matrix.*;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

import java.awt.image.BufferedImage;

/**
 * A class used to convert an AWT image to an ASCII art.
 */
public class BufferedImageToAsciiConverter extends AbstractToAsciiConverter<BufferedImage> {

	private TruncatingImageToAsciiConverter innerConverter;

	public BufferedImageToAsciiConverter() {

		this.innerConverter = new TruncatingImageToAsciiConverter();
	}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		super.setCharacterCache(characterCache);

		innerConverter.setCharacterCache(characterCache);
	}

	@Override
	public void setCharacterFitStrategy(final CharacterFitStrategy characterFitStrategy) {
		super.setCharacterFitStrategy(characterFitStrategy);

		innerConverter.setCharacterFitStrategy(characterFitStrategy);
	}

	@Override
	public void setExporter(final AsciiExporter exporter) {
		super.setExporter(exporter);

		innerConverter.setExporter(exporter);
	}

	@Override
	public void convert(final BufferedImage source) {

		final ImageMatrixDimensions sourcePixelsSize = new ImageMatrixDimensions(source.getWidth(), source.getHeight());

		// extract pixels from source image
		final int[] imagePixels = source.getRGB(
				0, 0,
				sourcePixelsSize.getWidth(), sourcePixelsSize.getHeight(),
				null, 0, sourcePixelsSize.getWidth());

		// process the pixels to a gray-scale matrix
		final ImageMatrix<Integer> sourceArgbMatrix = new BasicInt1DImageMatrix(
				new BasicImageMatrixInfo(4, Integer.class, 8),
				imagePixels, sourcePixelsSize.getWidth());

		innerConverter.convert(sourceArgbMatrix);
	}
}
