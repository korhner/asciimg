package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.strategy.CharacterFitStrategy;
import io.korhner.asciimg.image.exporter.AsciiExporter;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import io.korhner.asciimg.image.matrix.TiledGrayScaleMatrix;
import io.korhner.asciimg.utils.ArrayUtils;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

/**
 * A class used to convert an image to an ASCII art. Output and conversion
 * algorithm are decoupled.
 */
public class ImageToAsciiConverter implements ToAsciiConverter<BufferedImage> {

	private CharacterFitStrategy characterFitStrategy;
	private AsciiImgCache characterCache;
	private AsciiExporter exporter;

	public ImageToAsciiConverter() {}

	@Override
	public void convert(final BufferedImage source) {
		// dimension of each tile
		final Dimension tileSize = this.getCharacterCache().getCharacterImageSize();

		final Dimension sourcePixelsSize = new Dimension(source.getWidth(), source.getHeight());
		// the number of characters that fit fully into the source image
		final Dimension destCharactersSize = new Dimension(
				sourcePixelsSize.width / tileSize.width,
				sourcePixelsSize.height / tileSize.height);
		// destination image width and height, truncated, so we avoid partial characters
		final Dimension truncatedPixelsSize = new Dimension(
				destCharactersSize.width * tileSize.width,
				destCharactersSize.height * tileSize.height);

		// extract pixels from source image
		final int[] imagePixels = source.getRGB(
				0, 0, truncatedPixelsSize.width, truncatedPixelsSize.height, null, 0, truncatedPixelsSize.width);

		// process the pixels to a gray-scale matrix
		final GrayScaleMatrix sourceMatrix = new GrayScaleMatrix(imagePixels, truncatedPixelsSize.width, truncatedPixelsSize.height);

		// divide matrix into tiles for easy processing
		final TiledGrayScaleMatrix tiledMatrix = new TiledGrayScaleMatrix(
				sourceMatrix, tileSize.width, tileSize.height);

		getExporter().setCharacterCache(getCharacterCache());
		getExporter().init(truncatedPixelsSize.width, truncatedPixelsSize.height, destCharactersSize.width, destCharactersSize.height);

		// compare each tile to every character to determine best fit
		for (int i = 0; i < tiledMatrix.getTileCount(); i++) {

			final GrayScaleMatrix tile = tiledMatrix.getTile(i);

			float minError = Float.MAX_VALUE;
			Entry<Character, GrayScaleMatrix> bestFit = null;

			for (final Entry<Character, GrayScaleMatrix> charImage : getCharacterCache()) {
				final GrayScaleMatrix charPixels = charImage.getValue();

				final float error = this.getCharacterFitStrategy().calculateError(charPixels, tile);

				if (error < minError) {
					minError = error;
					bestFit = charImage;
				}
			}

			final int tileX = ArrayUtils.convert1DtoX(i, tiledMatrix.getTilesX());
			final int tileY = ArrayUtils.convert1DtoY(i, tiledMatrix.getTilesX());

			// copy character to output
			getExporter().addCharacter(bestFit, imagePixels, tileX, tileY, truncatedPixelsSize.width);
		}

		getExporter().imageEnd(imagePixels, truncatedPixelsSize.width, truncatedPixelsSize.height);
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
		this.exporter = exporter;
	}
}
