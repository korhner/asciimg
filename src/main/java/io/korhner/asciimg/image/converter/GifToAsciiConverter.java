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
