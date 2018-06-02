package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;
import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.utils.GifDecoder;
import java.io.IOException;
import java.io.InputStream;

public class GifToAsciiConvert implements ToAsciiConverter<InputStream> {

	private CharacterFitStrategy characterFitStrategy;
	private AsciiImgCache characterCache;
	private MultiFrameAsciiExporter exporter;

	public GifToAsciiConvert() {}

	@Override
	public void convert(final InputStream source) throws IOException {
		final GifDecoder decoder = new GifDecoder();
		final int status = decoder.read(source);
		if (status != 0) {
			throw new IOException("Failed to read GIF source from " + String.valueOf(source));
		}
		// initialize converters
		final int frameCount = decoder.getFrameCount();
		exporter.setCharacterCache(getCharacterCache());
		exporter.initFrames(frameCount);
		for (int i = 0; i < frameCount; i++) {
			final ImageToAsciiConverter frameConverter = new ImageToAsciiConverter();
			frameConverter.setCharacterFitStrategy(getCharacterFitStrategy());
			frameConverter.setCharacterCache(getCharacterCache());
			frameConverter.setExporter(exporter);
			frameConverter.convert(decoder.getFrame(i));
		}
		exporter.finalizeFrames();
	}

	@Override
	public CharacterFitStrategy getCharacterFitStrategy() {
		return this.characterFitStrategy;
	}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void setCharacterFitStrategy(final CharacterFitStrategy characterFitStrategy) {
		this.characterFitStrategy = characterFitStrategy;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	@Override
	public AsciiExporter getExporter() {
		return exporter;
	}

	@Override
	public void setExporter(final AsciiExporter exporter) {

		if (exporter instanceof MultiFrameAsciiExporter) {
			this.exporter = (MultiFrameAsciiExporter) exporter;
		} else {
			throw new IllegalArgumentException("We need an instance of " + MultiFrameAsciiExporter.class.getSimpleName());
		}
	}
}
