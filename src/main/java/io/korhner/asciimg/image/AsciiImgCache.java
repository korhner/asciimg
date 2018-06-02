package io.korhner.asciimg.image;

import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Character cache that keeps a map of pre-calculated pixel data of each
 * character that is eligible for ASCII art.
 */
public class AsciiImgCache implements Iterable<Entry<Character, GrayScaleMatrix>> {

	private final Map<Character, GrayScaleMatrix> imageCache;

	/** Some empirically chosen characters that give good results. */
	private static final char[] DEFAULT_CHARACTERS
			= "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ".toCharArray();

	/** Dimension of character image data. */
	private final Dimension characterImageSize;

	/**
	 * Calculate character rectangle for the given font metrics.
	 *
	 * @param font used to calculate the font metrics
	 * @return the rectangle
	 */
	private static Dimension calculateCharacterRectangle(final Font font, final char[] characters) {
		final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = (Graphics2D) img.getGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setFont(font);
		final FontMetrics fontMetrics = graphics2D.getFontMetrics();

		final Dimension maxCharacter = new Dimension();
		for (final char chr : characters) {
			final String character = Character.toString(chr);

			final Rectangle characterRectangle = new TextLayout(character, fontMetrics.getFont(),
					fontMetrics.getFontRenderContext()).getOutline(null).getBounds();

			if (maxCharacter.width < characterRectangle.getWidth()) {
				maxCharacter.width = (int) characterRectangle.getWidth();
			}

			if (maxCharacter.height < characterRectangle.getHeight()) {
				maxCharacter.height = (int) characterRectangle.getHeight();
			}
		}

		return maxCharacter;
	}

	/**
	 * Creates the cache with supplied font.
	 *
	 * @param font
	 *            the font
	 * @return the ASCII img cache
	 */
	public static AsciiImgCache create(final Font font) {

		return create(font, DEFAULT_CHARACTERS);
	}

	/**
	 * Initialize a new character cache with the supplied font.
	 *
	 * @param font the font used for the characters
	 * @param characters the characters whose images are to be cached
	 * @return the ASCII img cache
	 */
	public static AsciiImgCache create(final Font font, final char[] characters) {

		final Dimension maxCharacterImageSize = calculateCharacterRectangle(font, characters);
		final Map<Character, GrayScaleMatrix> imageCache = createCharacterImages(
				font, maxCharacterImageSize, characters);

		return new AsciiImgCache(maxCharacterImageSize, imageCache, characters);

	}

	/**
	 * Creates the character images.
	 *
	 * @param font
	 *            the font
	 * @param characterSize
	 *            the character size
	 * @return the map
	 */
	private static Map<Character, GrayScaleMatrix> createCharacterImages(
			final Font font,
			final Dimension characterSize,
			final char[] characters) {
		// create each image
		final BufferedImage img = new BufferedImage(characterSize.width, characterSize.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics2D = (Graphics2D) img.getGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics2D.setFont(font);
		final FontMetrics fontMetrics = graphics2D.getFontMetrics();

		final Map<Character, GrayScaleMatrix> imageCache = new HashMap<>();

		for (final char chr : characters) {
			final String character = Character.toString(chr);

			graphics2D.setColor(Color.WHITE);
			graphics2D.fillRect(0, 0, characterSize.width, characterSize.height);
			graphics2D.setColor(Color.BLACK);

			final Rectangle rect = new TextLayout(character, fontMetrics.getFont(),
					fontMetrics.getFontRenderContext()).getOutline(null).getBounds();

			graphics2D.drawString(character, 0, (int) (rect.getHeight() - rect.getMaxY()));

			final int[] pixels = img.getRGB(0, 0, characterSize.width,
					characterSize.height, null, 0, characterSize.width);
			final GrayScaleMatrix matrix = new GrayScaleMatrix(pixels, characterSize.width, characterSize.height);
			imageCache.put(chr, matrix);
		}

		return imageCache;
	}

	/**
	 * Instantiates a new ASCII img cache.
	 *
	 * @param characterImageSize
	 *            the character image size
	 * @param imageCache
	 *            the image cache
	 */
	private AsciiImgCache(
			final Dimension characterImageSize,
			final Map<Character, GrayScaleMatrix> imageCache,
			final char[] characters) {
		this.characterImageSize = characterImageSize;
		this.imageCache = imageCache;
	}

	/**
	 * Gets the character image dimensions.
	 *
	 * @return character image dimensions
	 */
	public Dimension getCharacterImageSize() {

		return characterImageSize;
	}

	@Override
	public Iterator<Entry<Character, GrayScaleMatrix>> iterator() {

		return getImageCache().entrySet().iterator();
	}

	/**
	 * Returns the image cache.
	 * @return a map of characters to their bitmaps
	 */
	protected Map<Character, GrayScaleMatrix> getImageCache() {
		return imageCache;
	}
}
