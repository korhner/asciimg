package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.image.importer.BufferedImageImageImporter;

import java.io.IOException;
import java.io.InputStream;

public class GifToAsciiConverter extends AbstractToAsciiConverter<InputStream> {

	public GifToAsciiConverter() {}

	@Override
	public void convert(final InputStream source) throws IOException {

		getImporter().setSource(source);

		// initialize converters
		final int frameCount = getImporter().getFrames();
		final MultiFrameAsciiExporter exporter = (MultiFrameAsciiExporter) getExporter();
		exporter.setCharacterCache(getCharacterCache());
		exporter.initFrames(frameCount);
		for (int i = 0; i < frameCount; i++) {
			final ImageToAsciiConverter frameConverter = new ImageToAsciiConverter();
			final BufferedImageImageImporter frameImporter = new BufferedImageImageImporter();
			frameConverter.setImporter(frameImporter);
			frameConverter.setCharacterFitStrategy(getCharacterFitStrategy());
			frameConverter.setCharacterCache(getCharacterCache());
			frameConverter.setExporter(exporter);
			frameConverter.convert(getImporter().read()); // HACK
		}
		exporter.finalizeFrames();
	}

	@Override
	public void setExporter(final AsciiExporter exporter) {

		if (!(exporter instanceof MultiFrameAsciiExporter)) {
			throw new IllegalArgumentException("We need an instance of " + MultiFrameAsciiExporter.class.getSimpleName());
		}
		super.setExporter(exporter);
	}
}
