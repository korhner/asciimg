package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.matrix.*;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;

import java.awt.image.BufferedImage;

/**
 * A class used to convert an arbitrary sized image to an ASCII art,
 * discarding superfluous pixels on the right and on the top of the image.
 */
public class TruncatingImageToAsciiConverter extends AbstractToAsciiConverter<ImageMatrix<Integer>> {

	private ImageToAsciiConverter innerConverter;

	public TruncatingImageToAsciiConverter() {

		this.innerConverter = new ImageToAsciiConverter();
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
	public void convert(final ImageMatrix<Integer> source) {

		// dimension of each tile
		final ImageMatrixDimensions tileSize = this.getCharacterCache().getCharacterImageSize();

		final ImageMatrixDimensions sourcePixelsSize = source.getDimensions();
		// the number of characters that fit fully into the source image
		// only these will be used, and pixels to the right and below the image that are not covered by these
		// will be ignored, and thus are not represented in the output
		final ImageMatrixDimensions destCharactersSize = new ImageMatrixDimensions(
				sourcePixelsSize.getWidth() / tileSize.getWidth(),
				sourcePixelsSize.getHeight() / tileSize.getHeight());
		// destination image width and height in pixels; truncated, so we avoid partial characters
		final ImageMatrixDimensions truncatedPixelsSize = new ImageMatrixDimensions(
				destCharactersSize.getWidth() * tileSize.getWidth(),
				destCharactersSize.getHeight() * tileSize.getHeight());

		// do the truncating
		final ImageMatrix<Integer> truncatedArgbMatrix = new RegionImageMatrix<>(source, truncatedPixelsSize, 0, 0);
		// process the pixels to a gray-scale matrix
		final GrayScaleMatrix sourceGrayMatrix = new GrayScaleMatrix(truncatedArgbMatrix);

		innerConverter.convert(sourceGrayMatrix);
	}
}
