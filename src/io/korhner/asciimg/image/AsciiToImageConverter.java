package io.korhner.asciimg.image;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

public class AsciiToImageConverter extends AsciiConverter<BufferedImage> {

	public AsciiToImageConverter(AsciiImgCache characterCacher, final BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
	}

	@Override
	protected BufferedImage initializeOutput(int imageWidth, int imageHeight) {
		return new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	protected void finalizeOutput(int[] sourceImagePixels,
			int imageWidth, int imageHeight) {
		this.output.setRGB(0, 0, imageWidth, imageHeight, sourceImagePixels, 0, imageWidth);

	}

	@Override
	protected void addCharacterToOutput(Entry<Character, int[]> characterEntry,
			int tileX, int tileY, int[] sourceImagePixels, int imageWidth) {
		int startCoordinateX = tileX * this.characterCache.getCharacterImageSize().width;
		int startCoordinateY = tileY * this.characterCache.getCharacterImageSize().height;
		
		// copy winner character
		int[] charPixels = characterEntry.getValue();
		for (int i = 0; i < charPixels.length; i++) {
			int xOffset = i % this.characterCache.getCharacterImageSize().width;
			int yOffset = i / this.characterCache.getCharacterImageSize().width;

			sourceImagePixels[ImageUtils.convert2DTo1D(startCoordinateX + xOffset, startCoordinateY + yOffset, imageWidth)] = charPixels[i];
		}
		
	}
	


}
