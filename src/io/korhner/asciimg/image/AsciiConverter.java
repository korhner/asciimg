package io.korhner.asciimg.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

public abstract class AsciiConverter<Output> {

	protected final AsciiImgCache characterCache;

	protected final BestCharacterFitStrategy characterFitStrategy;

	protected Output output;

	public AsciiConverter(final AsciiImgCache characterCacher,
			final BestCharacterFitStrategy characterFitStrategy) {
		this.characterCache = characterCacher;
		this.characterFitStrategy = characterFitStrategy;
	}

	protected abstract Output initializeOutput(int imageWidth, int imageHeight);

	protected abstract void finalizeOutput(int[] sourceImagePixels,
			int imageWidth, int imageHeight);

	protected abstract void addCharacterToOutput(
			Entry<Character, int[]> characterEntry, int tileX, int tileY,
			int[] sourceImagePixels, int imageWidth);

	/**
	 * Produces a new image that is an ascii art of the supplied image.
	 *
	 * @param source
	 *            the source
	 * @return the buffered image
	 */
	public Output convertImage(final BufferedImage source) {
		Dimension tile = this.characterCache.getCharacterImageSize();

		int outputImageWidth = (source.getWidth() / tile.width) * tile.width;
		int outputImageHeight = (source.getHeight() / tile.height)
				* tile.height;

		int[] imagePixels = source.getRGB(0, 0, outputImageWidth,
				outputImageHeight, null, 0, outputImageWidth);

		this.output = initializeOutput(outputImageWidth, outputImageHeight);

		for (int i = 0; i < outputImageHeight / tile.getHeight(); i++) {
			for (int j = 0; j < outputImageWidth / tile.getWidth(); j++) {
				Entry<Character, int[]> characterEntry = this.characterFitStrategy
						.findBestFit(this.characterCache, imagePixels,
								outputImageWidth, j, i);

				addCharacterToOutput(characterEntry, j, i, imagePixels,
						outputImageWidth);
			}
		}

		finalizeOutput(imagePixels, outputImageWidth, outputImageHeight);

		return this.output;

	}

}
