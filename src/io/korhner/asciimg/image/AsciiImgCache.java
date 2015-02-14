package io.korhner.asciimg.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

public class AsciiImgCache implements Iterable<Entry<Character, int[]>> {

	/** A map of characters to their bitmaps. */
	private final Map<Character, int[]> imageCache;

	/** Minimum value of ASCII char to take into consideration. */
	private static final int MIN_CHAR = 65;

	/** Minimum value of ASCII char to take into consideration. */
	private static final int MAX_CHAR = 126;

	/**
	 * @return the characterImageSize
	 */
	public Dimension getCharacterImageSize() {
		return characterImageSize;
	}

	private final Dimension characterImageSize;

	/**
	 * Instantiates a new ascii img cache.
	 *
	 * @param characterImageSize
	 *            the character image size
	 * @param imageCache
	 *            the image cache
	 */
	private AsciiImgCache(final Dimension characterImageSize, final Map<Character, int[]> imageCache) {
		this.characterImageSize = characterImageSize;
		this.imageCache = imageCache;
	}

	/**
	 * Initialize a new character cache with supplied font.
	 *
	 * @param font the font
	 * @return the ascii img cache
	 */
	public static AsciiImgCache create(final Font font) {

		Dimension maxCharacterImageSize = calculateCharacterRectangle(font);
		Map<Character, int[]> imageCache = createCharacterImages(font, maxCharacterImageSize);

		return new AsciiImgCache(maxCharacterImageSize, imageCache);

	}

	/**
	 * Creates the character images.
	 *
	 * @param font the font
	 * @param characterSize the character size
	 * @return the map
	 */
	private static Map<Character, int[]> createCharacterImages(final Font font, final Dimension characterSize) {
		// create each image
		BufferedImage img = new BufferedImage(characterSize.width, characterSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(font);
		FontMetrics fm = graphics.getFontMetrics();

		Map<Character, int[]> imageCache = new HashMap<>();

		for (int i = MIN_CHAR; i <= MAX_CHAR; i++) {
			String character = Character.toString((char) i);

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, characterSize.width, characterSize.height);
			g.setColor(Color.BLACK);

			Rectangle rect = new TextLayout(Character.toString((char) i), fm.getFont(), fm.getFontRenderContext())
					.getOutline(null).getBounds();

			g.drawString(character, 0, (int) (rect.getHeight() - rect.getMaxY()));

			imageCache.put((char) i,
					img.getRGB(0, 0, characterSize.width, characterSize.height, null, 0, characterSize.width));
		}

		return imageCache;
	}

	/**
	 * Calculate character rectangle for the given font metrics.
	 *
	 * @param fontMetrics
	 *            the font metrics
	 * @return the rectangle
	 */
	private static Dimension calculateCharacterRectangle(final Font font) {
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(font);
		FontMetrics fm = graphics.getFontMetrics();

		Dimension maxCharacter = new Dimension();
		for (int i = MIN_CHAR; i <= MAX_CHAR; i++) {
			Rectangle characterRectangle = new TextLayout(Character.toString((char) i), fm.getFont(),
					fm.getFontRenderContext()).getOutline(null).getBounds();

			if (maxCharacter.width < characterRectangle.getWidth()) {
				maxCharacter.width = (int) characterRectangle.getWidth();
			}

			if (maxCharacter.height < characterRectangle.getHeight()) {
				maxCharacter.height = (int) characterRectangle.getHeight();
			}
		}

		return maxCharacter;
	}

	@Override
	public Iterator<Entry<Character, int[]>> iterator() {
		return imageCache.entrySet().iterator();
	}

}
