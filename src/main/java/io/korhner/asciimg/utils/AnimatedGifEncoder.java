package io.korhner.asciimg.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Encodes a GIF file consisting of one or more frames.
 * <pre>
 * Example:
 *    AnimatedGifEncoder e = new AnimatedGifEncoder();
 *    e.start(outputFileName);
 *    e.setDelay(1000);   // 1 frame per sec
 *    e.addFrame(image1);
 *    e.addFrame(image2);
 *    e.finish();
 * </pre>
 * No copyright asserted on the source code of this class. May be used
 * for any purpose, however, refer to the Unisys LZW patent for restrictions
 * on use of the associated LZWEncoder class. Please forward any corrections
 * to questions at fmsware.com.
 *
 * @author Kevin Weiner, FM Software
 * @version 1.03 November 2003
 */
public class AnimatedGifEncoder {

	private static final int DEFAULT_WIDTH = 320;
	private static final int DEFAULT_HEIGHT = 240;

	private int width; // image size
	private int height;
	private Color transparent; // transparent color if given
	private int transIndex; // transparent index input color table
	private int repeat; // no repeat
	private int delay; // frame delay (hundredths)
	private boolean started; // ready to output frames
	private OutputStream out;
	private BufferedImage image; // current frame
	private byte[] pixels; // BGR byte array from frame
	private byte[] indexedPixels; // converted frame indexed to palette
	private int colorDepth; // number of bit planes
	private byte[] colorTab; // RGB palette
	private final boolean[] usedEntry; // active palette entries
	private int palSize; // color table size (bits-1)
	private int dispose; // disposal code (-1 = use default)
	private boolean closeStream; // imageEnd stream when finished
	private boolean firstFrame;
	private boolean sizeSet; // if false, get size from first frame
	private int sample; // default sample interval for quantizer

	public AnimatedGifEncoder() {
		transparent = null;
		repeat = -1;
		delay = 0;
		started = false;
		usedEntry = new boolean[256];
		palSize = 7;
		dispose = -1;
		closeStream = false;
		firstFrame = true;
		sizeSet = false;
		sample = 10;
	}

	/**
	 * Sets the delay time between each frame, or changes it
	 * for subsequent frames (applies to last frame added).
	 *
	 * @param delayMs delay time in milliseconds
	 */
	public void setDelay(final int delayMs) {
		delay = Math.round(delayMs / 10.0f);
	}

	/**
	 * Sets the GIF frame disposal code for the last added frame
	 * and any subsequent frames.  Default is 0 if no transparent
	 * color has been set, otherwise 2.
	 * @param code disposal code.
	 */
	public void setDispose(final int code) {
		if (code >= 0) {
			dispose = code;
		}
	}

	/**
	 * Sets the number of times the set of GIF frames
	 * should be played.  Default is 1; 0 means play
	 * indefinitely.  Must be invoked before the first
	 * image is added.
	 *
	 * @param iterations number of iterations.
	 */
	public void setRepeat(final int iterations) {
		if (iterations >= 0) {
			repeat = iterations;
		}
	}

	/**
	 * Sets the transparent color for the last added frame
	 * and any subsequent frames.
	 * Since all colors are subject to modification
	 * input the quantization process, the color input the final
	 * palette for each frame closest to the given color
	 * becomes the transparent color for that frame.
	 * May be set to null to indicate no transparent color.
	 *
	 * @param color to be treated as transparent onto the display.
	 */
	public void setTransparent(final Color color) {
		transparent = color;
	}

	/**
	 * Adds next GIF frame.  The frame is not written immediately, but is
	 * actually deferred until the next frame is received so that timing
	 * data can be inserted.  Invoking <code>finish()</code> flushes all
	 * frames.  If <code>setSize</code> was not invoked, the size of the
	 * first image is used for all subsequent frames.
	 *
	 * @param img contains the frame to write.
	 * @return true if successful.
	 */
	public boolean addFrame(final BufferedImage img) {

		boolean success;
		if ((img == null) || !started) {
			success = false;
		} else {
			try {
				if (!sizeSet) {
					// use first frame's size
					setSize(img.getWidth(), img.getHeight());
				}
				this.image = img;
				getImagePixels(); // convert to correct format if necessary
				analyzePixels(); // build color table & map pixels
				if (firstFrame) {
					writeLSD(); // logical screen descriptor
					writePalette(); // global color table
					if (repeat >= 0) {
						// use NS app extension to indicate reps
						writeNetscapeExt();
					}
				}
				writeGraphicCtrlExt(); // write graphic control extension
				writeImageDesc(); // image descriptor
				if (!firstFrame) {
					writePalette(); // local color table
				}
				writePixels(); // encode and write pixel data
				firstFrame = false;
				success = true;
			} catch (final IOException exc) {
				success = false;
			}
		}

		return success;
	}

	/**
	 * Flushes any pending data and closes output file.
	 * If writing to a stream, the stream is not
	 * closed.
	 * @return <code>true</code> if the output stream was successfully closed
	 */
	public boolean finish() {

		boolean success;
		if (started) {
			started = false;
			try {
				out.write(0x3b); // gif trailer
				out.flush();
				if (closeStream) {
					out.close();
				}
				success = true;
			} catch (final IOException exc) {
				success = false;
			}

			// reset for subsequent use
			transIndex = 0;
			out = null;
			image = null;
			pixels = null;
			indexedPixels = null;
			colorTab = null;
			closeStream = false;
			firstFrame = true;
		} else {
			success = false;
		}

		return success;
	}

	/**
	 * Sets frame rate input frames per second.  Equivalent to
	 * <code>setDelay(1000/fps)</code>.
	 *
	 * @param fps frame rate (frames per second)
	 */
	public void setFrameRate(final float fps) {
		if (fps != 0f) {
			delay = Math.round(100f / fps);
		}
	}

	/**
	 * Sets quality of color quantization (conversion of images
	 * to the maximum 256 colors allowed by the GIF specification).
	 * Lower values (minimum = 1) produce better colors, but slow
	 * processing significantly.  10 is the default, and produces
	 * good color mapping at reasonable speeds.  Values greater
	 * than 20 do not yield significant improvements input speed.
	 *
	 * @param quality greater than 0.
	 */
	public void setQuality(final int quality) {
		if (quality < 1) {
			sample = 1;
		} else {
			sample = quality;
		}
	}

	/**
	 * Sets the GIF frame size.
	 * The default size is the size of the first frame added if this method is
	 * not invoked.
	 *
	 * @param newWidth frame width.
	 * @param newHeight frame width.
	 */
	public void setSize(final int newWidth, final int newHeight) {
		if (!started || firstFrame) {
			width = newWidth;
			height = newHeight;
			if (width < 1) {
				width = DEFAULT_WIDTH;
			}
			if (height < 1) {
				height = DEFAULT_HEIGHT;
			}
			sizeSet = true;
		}
	}

	/**
	 * Initiates GIF file creation on the given stream.
	 * The stream is not closed automatically.
	 *
	 * @param output stream onto which GIF images are written.
	 * @return false if initial write failed.
	 */
	public boolean start(final OutputStream output) {

		boolean success;
		if (output == null) {
			success = false;
		} else {
			closeStream = false;
			this.out = output;
			try {
				writeString("GIF89a"); // header
				success = true;
			} catch (final IOException exc) {
				success = false;
			}
			started = success;
		}

		return success;
	}

	/**
	 * Initiates writing of a GIF file with the specified name.
	 *
	 * @param file String containing output file name.
	 * @return false if open or initial write failed.
	 */
	public boolean start(final String file) {

		boolean success;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			success = start(out);
			closeStream = true;
		} catch (IOException exc) {
			success = false;
		}
		started = success;

		return success;
	}

	/**
	 * Analyzes image colors and creates a color map.
	 */
	protected void analyzePixels() {
		final int len = pixels.length;
		final int nPix = len / 3;
		indexedPixels = new byte[nPix];
		final NeuQuant neuQuant = new NeuQuant(pixels, len, sample);
		// initialize quantizer
		colorTab = neuQuant.process(); // create reduced palette
		// convert map from BGR to RGB
		for (int i = 0; i < colorTab.length; i += 3) {
			final byte temp = colorTab[i];
			colorTab[i] = colorTab[i + 2];
			colorTab[i + 2] = temp;
			usedEntry[i / 3] = false;
		}
		// map image pixels to new palette
		int pixColorIdx = 0;
		for (int pixelIdx = 0; pixelIdx < nPix; pixelIdx++) {
			final int index = neuQuant.map(
					pixels[pixColorIdx++] & 0xff,
					pixels[pixColorIdx++] & 0xff,
					pixels[pixColorIdx++] & 0xff);
			usedEntry[index] = true;
			indexedPixels[pixelIdx] = (byte) index;
		}
		pixels = null;
		colorDepth = 8;
		palSize = 7;
		// get closest match to transparent color if specified
		if (transparent != null) {
			transIndex = findClosest(transparent);
		}
	}

	/**
	 * Returns the index of the palette color closest to the supplied color.
	 * @param color color to look for
	 * @return pixel index with most similar color
	 */
	protected int findClosest(final Color color) {

		int minPos;
		if (colorTab == null) {
			minPos = -1;
		} else {
			final int red = color.getRed();
			final int green = color.getGreen();
			final int blue = color.getBlue();
			minPos = 0;
			int diffMin = 256 * 256 * 256;
			final int len = colorTab.length;
			for (int i = 0; i < len;) {
				final int diffRed = red - (colorTab[i++] & 0xff);
				final int diffGreen = green - (colorTab[i++] & 0xff);
				final int diffBlue = blue - (colorTab[i] & 0xff);
				final int diffTotal = diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue;
				final int index = i / 3;
				if (usedEntry[index] && (diffTotal < diffMin)) {
					diffMin = diffTotal;
					minPos = index;
				}
				i++;
			}
		}

		return minPos;
	}

	/**
	 * Extracts image pixels into byte array "pixels"
	 */
	protected void getImagePixels() {
		final int imageWidth = image.getWidth();
		final int imageHeight = image.getHeight();
		final int type = image.getType();
		if ((imageWidth != width)
			|| (imageHeight != height)
			|| (type != BufferedImage.TYPE_3BYTE_BGR)) {
			// create new image with right size/format
			final BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			final Graphics2D graphics = temp.createGraphics();
			graphics.drawImage(image, 0, 0, null);
			image = temp;
		}
		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	}

	/**
	 * Writes Graphic Control Extension.
	 * @throws IOException on output error
	 */
	protected void writeGraphicCtrlExt() throws IOException {
		out.write(0x21); // extension introducer
		out.write(0xf9); // GCE label
		out.write(4); // data block size
		final int trans;
		int dis;
		if (transparent == null) {
			trans = 0;
			dis = 0; // dispose = no action
		} else {
			trans = 1;
			dis = 2; // force clear if using transparent color
		}
		if (dispose >= 0) {
			dis = dispose & 7; // user override
		}
		dis <<= 2;

		// packed fields
		out.write(
//				0 | // 1:3 reserved
				dis | // 4:6 disposal
//				0 | // 7   user input - 0 = none
				trans); // 8   transparency flag

		writeShort(delay); // delay x 1/100 sec
		out.write(transIndex); // transparent color index
		out.write(0); // block terminator
	}

	/**
	 * Writes an Image Descriptor.
	 * @throws IOException on output error
	 */
	protected void writeImageDesc() throws IOException {
		out.write(0x2c); // image separator
		writeShort(0); // image position x,y = 0,0
		writeShort(0);
		writeShort(width); // image size
		writeShort(height);
		// packed fields
		if (firstFrame) {
			// no LCT  - GCT is used for first (or only) frame
			out.write(0);
		} else {
			// specify normal LCT
			out.write(
					0x80 | // 1 local color table  1=yes
					0 | // 2 interlace - 0=no
					0 | // 3 sorted - 0=no
					0 | // 4-5 reserved
					palSize); // 6-8 size of color table
		}
	}

	/**
	 * Writes Logical Screen Descriptor.
	 * @throws IOException on output error
	 */
	protected void writeLSD() throws IOException {
		// logical screen size
		writeShort(width);
		writeShort(height);
		// packed fields
		out.write(
				0x80 | // 1   : global color table flag = 1 (gct used)
				0x70 | // 2-4 : color resolution = 7
				0x00 | // 5   : gct sort flag = 0
				palSize); // 6-8 : gct size

		out.write(0); // background color index
		out.write(0); // pixel aspect ratio - assume 1:1
	}

	/**
	 * Writes Netscape application extension to define
	 * repeat count.
	 * @throws IOException on output error
	 */
	protected void writeNetscapeExt() throws IOException {
		out.write(0x21); // extension introducer
		out.write(0xff); // app extension label
		out.write(11); // block size
		writeString("NETSCAPE" + "2.0"); // app id + auth code
		out.write(3); // sub-block size
		out.write(1); // loop sub-block id
		writeShort(repeat); // loop count (extra iterations, 0=repeat forever)
		out.write(0); // block terminator
	}

	/**
	 * Writes a color table.
	 * @throws IOException on output error
	 */
	protected void writePalette() throws IOException {
		out.write(colorTab, 0, colorTab.length);
		final int num = (3 * 256) - colorTab.length;
		for (int i = 0; i < num; i++) {
			out.write(0);
		}
	}

	/**
	 * Encodes and writes pixel data.
	 * @throws IOException on output error
	 */
	protected void writePixels() throws IOException {
		final LZWEncoder encoder = new LZWEncoder(width, height, indexedPixels, colorDepth);
		encoder.encode(out);
	}

	/**
	 * Write 16-bit value, LSB first.
	 * @param value to be written
	 * @throws IOException on output error
	 */
	protected void writeShort(final int value) throws IOException {
		out.write(value & 0xff);
		out.write((value >> 8) & 0xff);
	}

	/**
	 * Writes a string.
	 * @param str to be written
	 * @throws IOException on output error
	 */
	protected void writeString(final String str) throws IOException {
		for (int i = 0; i < str.length(); i++) {
			out.write((byte) str.charAt(i));
		}
	}
}
