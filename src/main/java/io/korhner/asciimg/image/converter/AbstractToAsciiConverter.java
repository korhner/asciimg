package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.MultiFrameAsciiExporter;
import io.korhner.asciimg.image.importer.ImageImporter;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

/**
 * Basic interface implementation.
 *
 * @param <I> input type of the image (or similar) to be converted to ASCII art
 */
public abstract class AbstractToAsciiConverter<I> implements ToAsciiConverter<I> {

	private ImageImporter importer;
	private CharacterFitStrategy characterFitStrategy;
	private AsciiImgCache characterCache;
	private MultiFrameAsciiExporter exporter;

	protected AbstractToAsciiConverter() { }

	@Override
	public ImageImporter getImporter() {
		return importer;
	}

	@Override
	public void setImporter(ImageImporter importer) {
		this.importer = importer;
	}

	@Override
	public CharacterFitStrategy getCharacterFitStrategy() {
		return this.characterFitStrategy;
	}

	@Override
	public void setCharacterFitStrategy(final CharacterFitStrategy characterFitStrategy) {
		this.characterFitStrategy = characterFitStrategy;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public MultiFrameAsciiExporter getExporter() {
		return exporter;
	}

	@Override
	public void setExporter(final MultiFrameAsciiExporter exporter) {
		this.exporter = exporter;
	}
}
