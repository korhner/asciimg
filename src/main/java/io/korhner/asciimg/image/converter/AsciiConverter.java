package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.matrix.GrayscaleMatrix;
import io.korhner.asciimg.image.matrix.TiledGrayscaleMatrix;
import io.korhner.asciimg.utils.ArrayUtils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

/**
 * A class used to convert an image to an ascii art. Output and conversion
 * argorithm are decoupled.
 *
 * @param <Output>
 *            output type of the ascii art
 */
public abstract class AsciiConverter<Output> {

	/** The character cache. */
	protected AsciiImgCache characterCache;

	/**
	 * The character fit strategy used to determine the best character for each
	 * source image tile.
	 */
	protected BestCharacterFitStrategy characterFitStrategy;

	/** The output. */
	protected Output output;

	/**
	 * Instantiates a new ascii converter.
	 *
	 * @param characterCache
	 *            the character cache
	 * @param characterFitStrategy
	 *            the character fit strategy
	 */
	public AsciiConverter(final AsciiImgCache characterCache,
			final BestCharacterFitStrategy characterFitStrategy) {
		this.characterCache = characterCache;
		this.characterFitStrategy = characterFitStrategy;
	}

	/**
	 * Override this to insert the character at a specified position in the
	 * output.
	 *
	 * @param characterEntry
	 *            character choosen as best fit
	 * @param sourceImagePixels
	 *            source image pixels. Can be
	 * @param tileX
	 *            the tile x
	 * @param tileY
	 *            the tile y
	 * @param imageWidth
	 *            the image width
	 */
	protected abstract void addCharacterToOutput(
			final Entry<Character, GrayscaleMatrix> characterEntry,
			final int[] sourceImagePixels, final int tileX, final int tileY,
			final int imageWidth);

	/**
	 * Produces an output that is an ascii art of the supplied image.
	 *
	 * @param source
	 *            the source
	 * @return the buffered image
	 */
	public Output convertImage(final BufferedImage source) {
		// dimension of each tile
		Dimension tileSize = this.characterCache.getCharacterImageSize();

		// round the width and height so we avoid partial characters
		int outputImageWidth = (source.getWidth() / tileSize.width)
				* tileSize.width;
		int outputImageHeight = (source.getHeight() / tileSize.height)
				* tileSize.height;

		// extract pixels from source image
		int[] imagePixels = source.getRGB(0, 0, outputImageWidth,
				outputImageHeight, null, 0, outputImageWidth);

		// process the pixels to a grayscale matrix
		GrayscaleMatrix sourceMatrix = new GrayscaleMatrix(imagePixels,
				outputImageWidth, outputImageHeight);

		// divide matrix into tiles for easy processing
		TiledGrayscaleMatrix tiledMatrix = new TiledGrayscaleMatrix(
				sourceMatrix, tileSize.width, tileSize.height);

		this.output = initializeOutput(outputImageWidth, outputImageHeight);

		// compare each tile to every character to determine best fit
		for (int i = 0; i < tiledMatrix.getTileCount(); i++) {

			GrayscaleMatrix tile = tiledMatrix.getTile(i);

			float minError = Float.MAX_VALUE;
			Entry<Character, GrayscaleMatrix> bestFit = null;

			for (Entry<Character, GrayscaleMatrix> charImage : characterCache) {
				GrayscaleMatrix charPixels = charImage.getValue();

				float error = this.characterFitStrategy.calculateError(
						charPixels, tile);

				if (error < minError) {
					minError = error;
					bestFit = charImage;
				}
			}

			int tileX = ArrayUtils.convert1DtoX(i, tiledMatrix.getTilesX());
			int tileY = ArrayUtils.convert1DtoY(i, tiledMatrix.getTilesX());

			// copy character to output
			addCharacterToOutput(bestFit, imagePixels, tileX, tileY,
					outputImageWidth);
		}

		finalizeOutput(imagePixels, outputImageWidth, outputImageHeight);

		return this.output;

	}

	/**
	 * Override this if any action needs to be done at the end of the
	 * conversion.
	 *
	 * @param sourceImagePixels
	 *            source image pixels data. Can be
	 * @param imageWidth
	 *            source image width
	 * @param imageHeight
	 *            source image height
	 */
	protected abstract void finalizeOutput(final int[] sourceImagePixels,
			final int imageWidth, final int imageHeight);

	/**
	 * Gets the character fit strategy.
	 *
	 * @return the character fit strategy
	 */
	public BestCharacterFitStrategy getCharacterFitStrategy() {
		return this.characterFitStrategy;
	}

	/**
	 * Override this to return an empty output object that will be filled during
	 * the ascii art conversion.
	 *
	 * @param imageWidth
	 *            source image width
	 * @param imageHeight
	 *            source image height
	 * @return the output
	 */
	protected abstract Output initializeOutput(final int imageWidth,
			final int imageHeight);

	/**
	 * Sets the character cache.
	 *
	 * @param characterCache new character cache
	 */
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	/**
	 * Sets the character fit strategy.
	 *
	 * @param characterFitStrategy
	 *            new character fit strategy
	 */
	public void setCharacterFitStrategy(
			final BestCharacterFitStrategy characterFitStrategy) {
		this.characterFitStrategy = characterFitStrategy;
	}
}
