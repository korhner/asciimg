package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.importer.BufferedImageImageImporter;
import io.korhner.asciimg.image.matrix.*;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

import java.awt.image.BufferedImage;
import java.io.IOException;

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
	public void convert(final BufferedImage source) throws IOException {

		final BufferedImageImageImporter importer = new BufferedImageImageImporter();
		importer.setSource(source);

		final ImageMatrix<Integer> sourceArgbMatrix = importer.read();

		innerConverter.convert(sourceArgbMatrix);
	}
}
