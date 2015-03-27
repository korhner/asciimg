package io.korhner.asciimg.image;

import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;

import java.util.Map.Entry;

public class AsciiToStringConverter extends AsciiConverter<String> {

	private StringBuffer buffer;

	public AsciiToStringConverter(AsciiImgCache characterCacher,
			final BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String initializeOutput(int imageWidth, int imageHeight) {
		buffer = new StringBuffer();
		return "";
	}

	@Override
	protected void finalizeOutput(int[] sourceImagePixels, int imageWidth,
			int imageHeight) {
		this.output = buffer.toString();

	}

	@Override
	protected void addCharacterToOutput(
			Entry<Character, GrayscaleMatrix> characterEntry,
			int[] sourceImagePixels, int tileX, int tileY, int imageWidth) {

		buffer.append(characterEntry.getKey());

		// append new line at the end of the row
		if ((tileX + 1)
				* this.characterCache.getCharacterImageSize().getWidth() == imageWidth) {
			buffer.append(System.lineSeparator());
		}

	}

}
