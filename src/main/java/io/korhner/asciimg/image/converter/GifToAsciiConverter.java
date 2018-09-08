package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.utils.GifDecoder;
import java.io.IOException;
import java.io.InputStream;

public class GifToAsciiConverter extends AbstractToAsciiConverter<InputStream> {

	public GifToAsciiConverter() {}

	@Override
	public void convert(final InputStream source) throws IOException {
		final GifDecoder decoder = new GifDecoder();
		final int status = decoder.read(source);
		if (status != 0) {
			throw new IOException("Failed to read GIF source from " + String.valueOf(source));
		}
		// initialize converters
		final int frameCount = decoder.getFrameCount();
		final MultiFrameAsciiExporter exporter = (MultiFrameAsciiExporter) getExporter();
		exporter.setCharacterCache(getCharacterCache());
		exporter.initFrames(frameCount);
		for (int i = 0; i < frameCount; i++) {
			final BufferedImageToAsciiConverter frameConverter = new BufferedImageToAsciiConverter();
			frameConverter.setCharacterFitStrategy(getCharacterFitStrategy());
			frameConverter.setCharacterCache(getCharacterCache());
			frameConverter.setExporter(exporter);
			frameConverter.convert(decoder.getFrame(i));
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
