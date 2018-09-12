package io.korhner.asciimg.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class GifDecoder - Decodes a GIF file into one or more frames.
 *
 * <blockquote><pre>{@code
 * Example:
 *    GifDecoder d = new GifDecoder();
 *    d.read("sample.gif");
 *    int n = d.getFrameCount();
 *    for (int i = 0; i < n; i++) {
 *       BufferedImage frame = d.getFrame(i);  // frame i
 *       int t = d.getDelay(i);  // display duration of frame input milliseconds
 *       // do something with frame
 *    }
 * }</pre></blockquote>
 * No copyright asserted on the source code of this class.  May be used for
 * any purpose, however, refer to the Unisys LZW patent for any additional
 * restrictions.  Please forward any corrections to questions at fmsware.com.
 *
 * @author Kevin Weiner, FM Software; LZW decoder adapted from John Cristy's ImageMagick.
 * @version 1.03 November 2003
 */
public class GifDecoder {

	/**
	 * File read status: No errors.
	 */
	public static final int STATUS_OK = 0;

	/**
	 * File read status: Error decoding file (may be partially decoded)
	 */
	public static final int STATUS_FORMAT_ERROR = 1;

	/**
	 * File read status: Unable to open source.
	 */
	public static final int STATUS_OPEN_ERROR = 2;

	private BufferedInputStream input;
	private int status;

	private int width; // full image width
	private int height; // full image height
	private boolean gctFlag; // global color table used
	private int gctSize; // size of global color table
	private int loopCount; // iterations; 0 = repeat forever

	private int[] gct; // global color table
	private int[] lct; // local color table
	private int[] act; // active color table

	private int bgIndex; // background color index
	private int bgColor; // background color
	private int lastBgColor; // previous bg color
	private int pixelAspect; // pixel aspect ratio

	private boolean lctFlag; // local color table flag
	private boolean interlace; // interlace flag
	private int lctSize; // local color table size

	// current image rectangle
	private int imgX;
	private int imgY;
	private int imgWidth;
	private int imgHeight;
	private Rectangle lastRect; // last image rect
	private BufferedImage image; // current frame
	private BufferedImage lastImage; // previous frame

	private final byte[] block; // current data block
	private int blockSize; // block size

	// last graphic control extension info
	private int dispose;
	// 0=no action; 1=leave input place; 2=restore to bg; 3=restore to prev
	private int lastDispose;
	private boolean transparency; // use transparent color
	private int delay; // delay input milliseconds
	private int transIndex; // transparent color index

	// max decoder pixel stack size
	private static final int MAX_STACK_SIZE = 4096;

	// LZW decoder working arrays
	private short[] prefix;
	private byte[] suffix;
	private byte[] pixelStack;
	private byte[] pixels;

	private List<GifFrame> frames; // frames read from current file
	private int frameCount;

	public GifDecoder() {
		loopCount = 1;
		block = new byte[256];
		blockSize = 0;
		dispose = 0;
		lastDispose = 0;
		transparency = false;
		delay = 0;
	}

	static class GifFrame {

		public final BufferedImage image;
		public final int delay;

		GifFrame(final BufferedImage image, final int delay) {
			this.image = image;
			this.delay = delay;
		}
	}

	/**
	 * Gets display duration for specified frame.
	 *
	 * @param frameIdx int index of frame
	 * @return delay input milliseconds
	 */
	public int getDelay(final int frameIdx) {

		delay = -1;
		if ((frameIdx >= 0) && (frameIdx < frameCount)) {
			delay = frames.get(frameIdx).delay;
		}
		return delay;
	}

	/**
	 * Gets the number of frames read from file.
	 * @return frame count
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * Gets the first (or only) image read.
	 *
	 * @return BufferedImage containing first frame, or null if none.
	 */
	public BufferedImage getImage() {
		return getFrame(0);
	}

	/**
	 * Gets the "Netscape" iteration count, if any.
	 * A count of 0 means repeat indefinitely.
	 *
	 * @return iteration count if one was specified, else 1.
	 */
	public int getLoopCount() {
		return loopCount;
	}

	/**
	 * Creates new frame image from current data (and previous
	 * frames as specified by their disposition codes).
	 */
	protected void setPixels() {
		// expose destination image's pixels as int array
		final int[] dest = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		// fill input starting image contents based on last image's dispose code
		if (lastDispose > 0) {
			if (lastDispose == 3) {
				// use image before last
				final int num = frameCount - 2;
				if (num > 0) {
					lastImage = getFrame(num - 1);
				} else {
					lastImage = null;
				}
			}

			if (lastImage != null) {
				final int[] prev = ((DataBufferInt) lastImage.getRaster().getDataBuffer()).getData();
				// copy pixels
				System.arraycopy(prev, 0, dest, 0, width * height);

				if (lastDispose == 2) {
					// fill last image rect area with background color
					final Graphics2D graphics = image.createGraphics();
					final Color color;
					if (transparency) {
						color = new Color(0, 0, 0, 0); 	// assume background is transparent
					} else {
						color = new Color(lastBgColor); // use given background color
					}
					graphics.setColor(color);
					graphics.setComposite(AlphaComposite.Src); // replace area
					graphics.fill(lastRect);
					graphics.dispose();
				}
			}
		}

		// copy each source line to the appropriate place input the destination
		int pass = 1;
		int inc = 8;
		int iLine = 0;
		for (int i = 0; i < imgHeight; i++) {
			int line = i;
			if (interlace) {
				if (iLine >= imgHeight) {
					pass++;
					switch (pass) {
						case 2 :
							iLine = 4;
							break;
						case 3 :
							iLine = 2;
							inc = 4;
							break;
						case 4 :
							iLine = 1;
							inc = 2;
							break;
						default:
					}
				}
				line = iLine;
				iLine += inc;
			}
			line += imgY;
			if (line < height) {
				final int k = line * width;
				int destX = k + imgX; // start of line input dest
				int destLim = destX + imgWidth; // end of dest line
				if ((k + width) < destLim) {
					destLim = k + width; // past dest edge
				}
				int sourceX = i * imgWidth; // start of line input source
				while (destX < destLim) {
					// map color and insert input destination
					final int index = ((int) pixels[sourceX++]) & 0xff;
					final int color = act[index];
					if (color != 0) {
						dest[destX] = color;
					}
					destX++;
				}
			}
		}
	}

	/**
	 * Gets the image contents of frame frameNum.
	 *
	 * @param frameNum number of the frame to be fetched
	 * @return BufferedImage representation of frame, or null if frameNum is invalid.
	 */
	public BufferedImage getFrame(final int frameNum) {
		BufferedImage img = null;
		if ((frameNum >= 0) && (frameNum < frameCount)) {
			img = frames.get(frameNum).image;
		}
		return img;
	}

	/**
	 * Gets image size.
	 *
	 * @return GIF image dimensions
	 */
	public Dimension getFrameSize() {

		return new Dimension(width, height);
	}

	/**
	 * Reads GIF image from stream
	 *
	 * @param inp containing GIF file.
	 * @return read status code (0 = no errors)
	 */
	public int read(final BufferedInputStream inp) {
		init();
		if (inp == null) {
			status = STATUS_OPEN_ERROR;
		} else {
			this.input = inp;
			readHeader();
			if (!err()) {
				readContents();
				if (frameCount < 0) {
					status = STATUS_FORMAT_ERROR;
				}
			}
			try {
				inp.close();
			} catch (final IOException exc) {
			}
		}

		return status;
	}

	/**
	 * Reads GIF image from stream
	 *
	 * @param inp containing GIF file.
	 * @return read status code (0 = no errors)
	 */
	public int read(final InputStream inp) {

		BufferedInputStream binp;
		if (inp instanceof BufferedInputStream) {
			binp = (BufferedInputStream) inp;
		} else {
			binp = new BufferedInputStream(inp);
		}
		return read(binp);
	}

	/**
	 * Reads GIF file from specified file/URL source
	 * (URL assumed if name contains ":/" or "file:")
	 *
	 * @param name String containing source
	 * @return read status code (0 = no errors)
	 */
	public int read(final String name) {
		status = STATUS_OK;
		try {
			final String nameTrimmed = name.trim().toLowerCase();
			if (nameTrimmed.contains("file:") || (nameTrimmed.indexOf(":/") > 0)) {
				final URL url = new URL(nameTrimmed);
				input = new BufferedInputStream(url.openStream());
			} else {
				input = new BufferedInputStream(new FileInputStream(nameTrimmed));
			}
			status = read(input);
		} catch (final IOException exc) {
			status = STATUS_OPEN_ERROR;
		}

		return status;
	}

	/**
	 * Decodes LZW image data into pixel array.
	 * Adapted from John Cristy's ImageMagick.
	 */
	protected void decodeImageData() {
		final int nullCode = -1;
		final int nPix = imgWidth * imgHeight;

		if ((pixels == null) || (pixels.length < nPix)) {
			pixels = new byte[nPix]; // allocate new pixel array
		}
		if (prefix == null) {
			prefix = new short[MAX_STACK_SIZE];
		}
		if (suffix == null) {
			suffix = new byte[MAX_STACK_SIZE];
		}
		if (pixelStack == null) {
			pixelStack = new byte[MAX_STACK_SIZE + 1];
		}

		//  Initialize GIF data stream decoder.

		final int dataSize = read();
		final int clear = 1 << dataSize;
		final int endOfInformation = clear + 1;
		int available = clear + 2;
		int oldCode = nullCode;
		int codeSize = dataSize + 1;
		int codeMask = (1 << codeSize) - 1;
		int code;
		for (code = 0; code < clear; code++) {
			prefix[code] = 0;
			suffix[code] = (byte) code;
		}

		//  Decode GIF pixel stream.

		int datum = 0;
		int bits = 0;
		int count = 0;
		int first = 0;
		int top = 0;
		int pixelIdx;
		int byteIdx = 0;

		for (pixelIdx = 0; pixelIdx < nPix;) {
			if (top == 0) {
				if (bits < codeSize) {
					//  Load bytes until there are enough bits for a code.
					if (count == 0) {
						// Read a new data block.
						count = readBlock();
						if (count <= 0) {
							break;
						}
						byteIdx = 0;
					}
					datum += (((int) block[byteIdx]) & 0xff) << bits;
					bits += 8;
					byteIdx++;
					count--;
					continue;
				}

				//  Get the next code.

				code = datum & codeMask;
				datum >>= codeSize;
				bits -= codeSize;

				//  Interpret the code

				if ((code > available) || (code == endOfInformation)) {
					break;
				}
				if (code == clear) {
					//  Reset decoder.
					codeSize = dataSize + 1;
					codeMask = (1 << codeSize) - 1;
					available = clear + 2;
					oldCode = nullCode;
					continue;
				}
				if (oldCode == nullCode) {
					pixelStack[top++] = suffix[code];
					oldCode = code;
					first = code;
					continue;
				}
				final int inCode = code;
				if (code == available) {
					pixelStack[top++] = (byte) first;
					code = oldCode;
				}
				while (code > clear) {
					pixelStack[top++] = suffix[code];
					code = prefix[code];
				}
				first = ((int) suffix[code]) & 0xff;

				//  Add a new string to the string table,

				if (available >= MAX_STACK_SIZE) {
					break;
				}
				pixelStack[top++] = (byte) first;
				prefix[available] = (short) oldCode;
				suffix[available] = (byte) first;
				available++;
				if (((available & codeMask) == 0)
					&& (available < MAX_STACK_SIZE))
				{
					codeSize++;
					codeMask += available;
				}
				oldCode = inCode;
			}

			//  Pop a pixel off the pixel stack.

			top--;
			pixels[pixelIdx++] = pixelStack[top];
		}

		for (int ggg = pixelIdx; ggg < nPix; ggg++) {
			pixels[ggg] = 0; // clear missing pixels
		}
	}

	/**
	 * Checks if an error was encountered during reading/decoding
	 * @return <code>true</code> if an error was encountered, <code>false</code> otherwise
	 */
	protected boolean err() {
		return status != STATUS_OK;
	}

	/**
	 * Initializes or re-initializes reader
	 */
	protected void init() {
		status = STATUS_OK;
		frameCount = 0;
		frames = new ArrayList<>();
		gct = null;
		lct = null;
	}

	/**
	 * Reads a single byte from the input stream.
	 * @return the byte read
	 */
	protected int read() {
		int curByte = 0;
		try {
			curByte = input.read();
		} catch (final IOException exc) {
			status = STATUS_FORMAT_ERROR;
		}
		return curByte;
	}

	/**
	 * Reads next variable length block from input.
	 *
	 * @return number of bytes stored input "buffer"
	 */
	protected int readBlock() {
		blockSize = read();
		int totalBytesRead = 0;
		if (blockSize > 0) {
			try {
				while (totalBytesRead < blockSize) {
					final int count = input.read(block, totalBytesRead, blockSize - totalBytesRead);
					if (count == -1) {
						break;
					}
					totalBytesRead += count;
				}
			} catch (final IOException exc) {
			}

			if (totalBytesRead < blockSize) {
				status = STATUS_FORMAT_ERROR;
			}
		}
		return totalBytesRead;
	}

	/**
	 * Reads color table as 256 RGB integer values
	 *
	 * @param nColors int number of colors to read
	 * @return int array containing 256 colors (packed ARGB with full alpha)
	 */
	protected int[] readColorTable(final int nColors) {
		final int nBytes = 3 * nColors;
		int[] tab = null;
		final byte[] color = new byte[nBytes];
		int nColorComps = 0;
		try {
			nColorComps = input.read(color);
		} catch (IOException exc) {
		}
		if (nColorComps < nBytes) {
			status = STATUS_FORMAT_ERROR;
		} else {
			tab = new int[256]; // max size to avoid bounds checks
			int i = 0;
			int j = 0;
			while (i < nColors) {
				final int red = ((int) color[j++]) & 0xff;
				final int green = ((int) color[j++]) & 0xff;
				final int blue = ((int) color[j++]) & 0xff;
				tab[i++] = 0xff000000 | (red << 16) | (green << 8) | blue;
			}
		}
		return tab;
	}

	/**
	 * Main file parser.  Reads GIF content blocks.
	 */
	protected void readContents() {
		// read GIF file content blocks
		boolean done = false;
		while (!(done || err())) {
			int code = read();
			switch (code) {

				case 0x2C : // image separator
					readImage();
					break;

				case 0x21 : // extension
					code = read();
					switch (code) {
						case 0xf9 : // graphics control extension
							readGraphicControlExt();
							break;

						case 0xff : // application extension
							readBlock();
							final StringBuilder app = new StringBuilder();
							for (int i = 0; i < 11; i++) {
								app.append((char) block[i]);
							}
							if (app.toString().equals("NETSCAPE2.0")) {
								readNetscapeExt();
							} else {
								skip(); // don't care
							}
							break;

						default : // uninteresting extension
							skip();
					}
					break;

				case 0x3b : // terminator
					done = true;
					break;

				case 0x00 : // bad byte, but keep going and see what happens
					break;

				default :
					status = STATUS_FORMAT_ERROR;
			}
		}
	}

	/**
	 * Reads Graphics Control Extension values
	 */
	protected void readGraphicControlExt() {
		read(); // block size
		final int packed = read(); // packed fields
		dispose = (packed & 0x1c) >> 2; // disposal method
		if (dispose == 0) {
			dispose = 1; // elect to keep old image if discretionary
		}
		transparency = (packed & 1) != 0;
		delay = readShort() * 10; // delay input milliseconds
		transIndex = read(); // transparent color index
		read(); // block terminator
	}

	/**
	 * Reads GIF file header information.
	 */
	protected void readHeader() {
		final StringBuilder idBuilder = new StringBuilder("");
		for (int i = 0; i < 6; i++) {
			idBuilder.append((char) read());
		}
		if (!idBuilder.toString().startsWith("GIF")) {
			status = STATUS_FORMAT_ERROR;
			return;
		}

		readLSD();
		if (gctFlag && !err()) {
			gct = readColorTable(gctSize);
			bgColor = gct[bgIndex];
		}
	}

	/**
	 * Reads next frame image
	 */
	protected void readImage() {
		imgX = readShort(); // (sub)image position & size
		imgY = readShort();
		imgWidth = readShort();
		imgHeight = readShort();

		final int packed = read();
		lctFlag = (packed & 0x80) != 0; // 1 - local color table flag
		interlace = (packed & 0x40) != 0; // 2 - interlace flag
		// 3 - sort flag
		// 4-5 - reserved
		lctSize = 2 << (packed & 7); // 6-8 - local color table size

		if (lctFlag) {
			lct = readColorTable(lctSize); // read table
			act = lct; // make local table active
		} else {
			act = gct; // make global table active
			if (bgIndex == transIndex) {
				bgColor = 0;
			}
		}
		int save = 0;
		if (transparency) {
			save = act[transIndex];
			act[transIndex] = 0; // set transparent color if specified
		}

		if (act == null) {
			status = STATUS_FORMAT_ERROR; // no color table defined
		}

		if (!err()) {
			decodeImageData(); // decode pixel data
			skip();

			if (!err()) {
				frameCount++;

				// create new image to receive frame data
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

				setPixels(); // transfer pixel data to image

				frames.add(new GifFrame(image, delay)); // add image to frame list

				if (transparency) {
					act[transIndex] = save;
				}
				resetFrame();
			}
		}
	}

	/**
	 * Reads Logical Screen Descriptor
	 */
	protected void readLSD() {

		// logical screen size
		width = readShort();
		height = readShort();

		// packed fields
		final int packed = read();
		gctFlag = (packed & 0x80) != 0; // 1   : global color table flag
		// 2-4 : color resolution
		// 5   : gct sort flag
		gctSize = 2 << (packed & 7); // 6-8 : gct size

		bgIndex = read(); // background color index
		pixelAspect = read(); // pixel aspect ratio
	}

	/**
	 * Reads Netscape extension to obtain iteration count
	 */
	protected void readNetscapeExt() {
		do {
			readBlock();
			if (block[0] == 1) {
				// loop count sub-block
				final int block1 = ((int) block[1]) & 0xff;
				final int block2 = ((int) block[2]) & 0xff;
				loopCount = (block2 << 8) | block1;
			}
		} while ((blockSize > 0) && !err());
	}

	/**
	 * Reads next 16-bit value, LSB first.
	 * @return the double-byte read
	 */
	protected int readShort() {
		// read 16-bit value, LSB first
		return read() | (read() << 8);
	}

	/**
	 * Resets frame state for reading next image.
	 */
	protected void resetFrame() {
		lastDispose = dispose;
		lastRect = new Rectangle(imgX, imgY, imgWidth, imgHeight);
		lastImage = image;
		lastBgColor = bgColor;
		lct = null;
	}

	/**
	 * Skips variable length blocks up to and including
	 * next zero length block.
	 */
	protected void skip() {
		do {
			readBlock();
		} while ((blockSize > 0) && !err());
	}
}
