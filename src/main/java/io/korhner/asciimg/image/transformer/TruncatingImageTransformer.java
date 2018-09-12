package io.korhner.asciimg.image.transformer;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;
import io.korhner.asciimg.image.matrix.RegionImageMatrix;

/**
 * Discards superfluous pixels on the right and on the top of the image.
 */
public class TruncatingImageTransformer implements ImageTransformer<Integer, Integer> {

	private AsciiImgCache characterCache;

	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	protected AsciiImgCache getCharacterCache() {
		return characterCache;
	}

	@Override
	public ImageMatrix<Integer> transform(final ImageMatrix<Integer> source) {

		// dimension of each tile
		final ImageMatrixDimensions tileSize = getCharacterCache().getCharacterImageSize();

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
		return new RegionImageMatrix<>(source, truncatedPixelsSize, 0, 0);
	}
}
