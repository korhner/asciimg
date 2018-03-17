package io.korhner.asciimg.utils;

import java.io.OutputStream;
import java.io.IOException;

//==============================================================================
//  Adapted from Jef Poskanzer's Java port by way of J. M. G. Elliott.
//  K Weiner 12/00

class LZWEncoder {

	private static final int EOF = -1;

	private int imgW, imgH;
	private byte[] pixels;
	private int initCodeSize;
	private int remaining;
	private int curPixel;

	// GIFCOMPR.C       - GIF Image compression routines
	//
	// Lempel-Ziv compression based on 'compress'.  GIF modifications by
	// David Rowley (mgardi@watdcsu.waterloo.edu)

	// General DEFINEs

	static final int BITS = 12;

	static final int H_SIZE = 5003; // 80% occupancy

	// GIF Image compression - modified 'compress'
	//
	// Based on: compress.c - File compression ala IEEE Computer, June 1984.
	//
	// By Authors:  Spencer W. Thomas      (decvax!harpo!utah-cs!utah-gr!thomas)
	//              Jim McKie              (decvax!mcvax!jim)
	//              Steve Davies           (decvax!vax135!petsd!peora!srd)
	//              Ken Turkowski          (decvax!decwrl!turtlevax!ken)
	//              James A. Woods         (decvax!ihnp4!ames!jaw)
	//              Joe Orost              (decvax!vax135!petsd!joe)

	int nBits; // number of bits/code
	int maxBits = BITS; // user settable max # bits/code
	int maxCode; // maximum code, given nBits
	int maxMaxCode = 1 << BITS; // should NEVER generate this code

	int[] hTab = new int[H_SIZE];
	int[] codeTab = new int[H_SIZE];

	int hSize = H_SIZE; // for dynamic table sizing

	int freeEnt = 0; // first unused entry

	// block compression parameters -- after all codes are used up,
	// and compression rate changes, start over.
	boolean clearFlag = false;

	// Algorithm:  use open addressing double hashing (no chaining) on the
	// prefix code / next character combination.  We do a variant of Knuth's
	// algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime
	// secondary probe.  Here, the modular division first probe is gives way
	// to a faster exclusive-or manipulation.  Also do block compression with
	// an adaptive reset, whereby the code table is cleared when the compression
	// ratio decreases, but after the table fills.  The variable-length output
	// codes are re-sized at this point, and a special CLEAR code is generated
	// for the decompressor.  Late addition:  construct the table according to
	// file size for noticeable speed improvement on small files.  Please direct
	// questions about this implementation to ames!jaw.

	int gInitBits;

	int clearCode;
	int eofCode;

	// output
	//
	// Output the given code.
	// Inputs:
	//      code:   A nBits-bit integer.  If == -1, then EOF.  This assumes
	//              that nBits =< wordsize - 1.
	// Outputs:
	//      Outputs code to the file.
	// Assumptions:
	//      Chars are 8 bits long.
	// Algorithm:
	//      Maintain a BITS character long buffer (so that 8 codes will
	// fit in it exactly).  Use the VAX insv instruction to insert each
	// code in turn.  When the buffer fills up empty it and start over.

	int curAccum = 0;
	int curBits = 0;

	int[] masks =
		{
			0x0000,
			0x0001,
			0x0003,
			0x0007,
			0x000F,
			0x001F,
			0x003F,
			0x007F,
			0x00FF,
			0x01FF,
			0x03FF,
			0x07FF,
			0x0FFF,
			0x1FFF,
			0x3FFF,
			0x7FFF,
			0xFFFF };

	// Number of characters so far in this 'packet'
	int aCount;

	// Define the storage for the packet accumulator
	byte[] accum = new byte[256];

	//----------------------------------------------------------------------------
	LZWEncoder(final int width, final int height, final byte[] pixels, final int colorDepth) {
		imgW = width;
		imgH = height;
		this.pixels = pixels;
		initCodeSize = Math.max(2, colorDepth);
	}

	/**
	 * Add a character to the end of the current packet, and if it is 254
	 * characters, flush the packet to disk.
	 */
	void charOut(final byte chr, final OutputStream outs) throws IOException {
		accum[aCount++] = chr;
		if (aCount >= 254)
			flushChar(outs);
	}

	// Clear out the hash table

	/** table clear for block compress */
	void clBlock(final OutputStream outs) throws IOException {
		clHash(hSize);
		freeEnt = clearCode + 2;
		clearFlag = true;

		output(clearCode, outs);
	}

	/** reset code table */
	void clHash(final int hsize) {
		for (int i = 0; i < hsize; ++i)
			hTab[i] = -1;
	}

	void compress(final int initBits, final OutputStream outs) throws IOException {
		int fCode;
		int i /* = 0 */;
		int c;
		int ent;
		int disp;
		int hSizeReg;
		int hShift;

		// Set up the globals:  gInitBits - initial number of bits
		gInitBits = initBits;

		// Set up the necessary values
		clearFlag = false;
		nBits = gInitBits;
		maxCode = maxCode(nBits);

		clearCode = 1 << (initBits - 1);
		eofCode = clearCode + 1;
		freeEnt = clearCode + 2;

		aCount = 0; // clear packet

		ent = nextPixel();

		hShift = 0;
		for (fCode = hSize; fCode < 65536; fCode *= 2)
			++hShift;
		hShift = 8 - hShift; // set hash code range bound

		hSizeReg = hSize;
		clHash(hSizeReg); // clear hash table

		output(clearCode, outs);

		outer_loop : while ((c = nextPixel()) != EOF) {
			fCode = (c << maxBits) + ent;
			i = (c << hShift) ^ ent; // xor hashing

			if (hTab[i] == fCode) {
				ent = codeTab[i];
				continue;
			} else if (hTab[i] >= 0) { // non-empty slot
				disp = hSizeReg - i; // secondary hash (after G. Knott)
				if (i == 0)
					disp = 1;
				do {
					i -= disp;
					if (i < 0)
						i += hSizeReg;

					if (hTab[i] == fCode) {
						ent = codeTab[i];
						continue outer_loop;
					}
				} while (hTab[i] >= 0);
			}
			output(ent, outs);
			ent = c;
			if (freeEnt < maxMaxCode) {
				codeTab[i] = freeEnt++; // code -> hashtable
				hTab[i] = fCode;
			} else
				clBlock(outs);
		}
		// Put out the final code.
		output(ent, outs);
		output(eofCode, outs);
	}

	//----------------------------------------------------------------------------
	void encode(final OutputStream os) throws IOException {
		os.write(initCodeSize); // write "initial code size" byte

		remaining = imgW * imgH; // reset navigation variables
		curPixel = 0;

		compress(initCodeSize + 1, os); // compress and write the pixel data

		os.write(0); // write block terminator
	}

	// Flush the packet to disk, and reset the accumulator
	void flushChar(final OutputStream outs) throws IOException {
		if (aCount > 0) {
			outs.write(aCount);
			outs.write(accum, 0, aCount);
			aCount = 0;
		}
	}

	static final int maxCode(final int nBits) {
		return (1 << nBits) - 1;
	}

	//----------------------------------------------------------------------------
	// Return the next pixel from the image
	//----------------------------------------------------------------------------
	private int nextPixel() {
		if (remaining == 0)
			return EOF;

		--remaining;

		byte pix = pixels[curPixel++];

		return pix & 0xff;
	}

	void output(final int code, final OutputStream outs) throws IOException {
		curAccum &= masks[curBits];

		if (curBits > 0)
			curAccum |= (code << curBits);
		else
			curAccum = code;

		curBits += nBits;

		while (curBits >= 8) {
			charOut((byte) (curAccum & 0xff), outs);
			curAccum >>= 8;
			curBits -= 8;
		}

		// If the next entry is going to be too big for the code size,
		// then increase it, if possible.
		if (freeEnt > maxCode || clearFlag) {
			if (clearFlag) {
				nBits = gInitBits;
				maxCode = maxCode(nBits);
				clearFlag = false;
			} else {
				++nBits;
				if (nBits == maxBits)
					maxCode = maxMaxCode;
				else
					maxCode = maxCode(nBits);
			}
		}

		if (code == eofCode) {
			// At EOF, write the rest of the buffer.
			while (curBits > 0) {
				charOut((byte) (curAccum & 0xff), outs);
				curAccum >>= 8;
				curBits -= 8;
			}

			flushChar(outs);
		}
	}
}
