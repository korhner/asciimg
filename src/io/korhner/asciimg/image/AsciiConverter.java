package io.korhner.asciimg.image;

import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

public abstract class AsciiConverter<Output> {

	protected final AsciiImgCache characterCache;

	protected BestCharacterFitStrategy characterFitStrategy;

	public BestCharacterFitStrategy getCharacterFitStrategy() {
		return characterFitStrategy;
	}

	public void setCharacterFitStrategy(
			BestCharacterFitStrategy characterFitStrategy) {
		this.characterFitStrategy = characterFitStrategy;
	}

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
			Entry<Character, GrayscaleMatrix> characterEntry,
			int[] sourceImagePixels, int tileX, int tileY, int imageWidth);

	/**
	 * Produces a new image that is an ascii art of the supplied image.
	 *
	 * @param source
	 *            the source
	 * @return the buffered image
	 */
	public Output convertImage(final BufferedImage source) {
		Dimension tileSize = this.characterCache.getCharacterImageSize();

		long startTime = 0;
		long endTime = 0;
		
		int outputImageWidth = (source.getWidth() / tileSize.width)
				* tileSize.width;
		int outputImageHeight = (source.getHeight() / tileSize.height)
				* tileSize.height;

		startTime = System.currentTimeMillis();
		int[] imagePixels = source.getRGB(0, 0, outputImageWidth,
				outputImageHeight, null, 0, outputImageWidth);
		preprocessSourceImage(imagePixels, outputImageWidth, outputImageHeight);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("Creating pixel array took " + endTime);
		
		startTime = System.currentTimeMillis();
		GrayscaleMatrix sourceMatrix = new GrayscaleMatrix(imagePixels,
				outputImageWidth, outputImageHeight);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("Creating matrix took " + endTime);
		
		preprocessGrayscaleMatrix(sourceMatrix);

		startTime = System.currentTimeMillis();
		TiledGrayscaleMatrix tiledMatrix = new TiledGrayscaleMatrix(
				sourceMatrix, tileSize.width, tileSize.height);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("Creating tile matrix took " + endTime);
		
		this.output = initializeOutput(outputImageWidth, outputImageHeight);
		
		int i = 0;
		
		for (GrayscaleMatrix tile : tiledMatrix) {
			Entry<Character, GrayscaleMatrix> characterEntry = this.characterFitStrategy
					.findBestFit(this.characterCache, tile);
			
			int tileX = ImageUtils.convert1DtoX(i, tiledMatrix.getTilesX());
			int tileY = ImageUtils.convert1DtoY(i, tiledMatrix.getTilesX());
			
			addCharacterToOutput(characterEntry, imagePixels, tileX, tileY, outputImageWidth);
			i++;
		}
		
		
		startTime = System.currentTimeMillis();
		finalizeOutput(imagePixels, outputImageWidth, outputImageHeight);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("finalizing took " + endTime);
		
		return this.output;

	}

	protected void preprocessSourceImage(final int[] pixels, final int width,
			final int height) {

	}

	protected void preprocessGrayscaleMatrix(final GrayscaleMatrix matrix) {

	}

}
