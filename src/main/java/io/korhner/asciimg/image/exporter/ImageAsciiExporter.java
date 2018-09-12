package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Converts ASCII art to a BufferedImage.
 */
public class ImageAsciiExporter implements MultiFrameAsciiExporter<List<BufferedImage>> {

	private AsciiImgCache characterCache;
	private List<BufferedImage> output;
	private BufferedImage currentOutput;

	public ImageAsciiExporter() {}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	@Override
	public void initFrames(int numFrame) {
		output = new ArrayList<>(numFrame);
	}

	/**
	 * Copy image data over the source pixels image.
	 */
	@Override
	public void addCharacter(
			final Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		final int startCoordinateX = tileX * characterCache.getCharacterImageSize().getWidth();
		final int startCoordinateY = tileY * characterCache.getCharacterImageSize().getHeight();

		// copy winner character
		for (int cpx = 0; cpx < characterEntry.getValue().getDimensions().getWidth(); cpx++) {
			for (int cpy = 0; cpy < characterEntry.getValue().getDimensions().getHeight(); cpy++) {
				final int component = (int) characterEntry.getValue().getValue(cpx, cpy);
				currentOutput.setRGB(
						startCoordinateX + cpx,
						startCoordinateY + cpy,
						new Color(component, component, component).getRGB());
			}
		}
	}

	@Override
	public void imageEnd() {}

	/**
	 * Create an empty buffered image.
	 */
	@Override
	public void init(final ImageMatrixDimensions targetDimensions) {

		final int imageWidth = targetDimensions.getWidth() * characterCache.getCharacterImageSize().getWidth();
		final int imageHeight = targetDimensions.getHeight() * characterCache.getCharacterImageSize().getHeight();
		currentOutput = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		output.add(currentOutput);
	}

	@Override
	public List<BufferedImage> getOutput() {
		return output;
	}

	@Override
	public void finalizeFrames() {}
}
