/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 korhner <korhner@gmail.com>
 * Copyright (c) 2018 hoijui <hoijui.quaero@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.ImageMatrix;
import io.korhner.asciimg.image.matrix.ImageMatrixDimensions;
import io.korhner.asciimg.utils.AnimatedGifEncoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class AnimatedGifMultiFrameAsciiExporter implements MultiFrameAsciiExporter<byte[]> {

	private AsciiImgCache characterCache;
	private AnimatedGifEncoder encoder;
	private ByteArrayOutputStream gifBufferStream;
	/**
	 * The delay time(ms) between each frame.
	 */
	private int delay;
	/**
	 * The number of times the set of GIF frames should be played; 0 means play indefinitely.
	 */
	private int repeat;
	private MultiFrameAsciiExporter<List<BufferedImage>> frameExporter;

	public AnimatedGifMultiFrameAsciiExporter() {}

	public void setDelay(final int delay) {
		this.delay = delay;
	}

	public void setRepeat(final int repeat) {
		this.repeat = repeat;
	}

	@Override
	public void setCharacterCache(final AsciiImgCache characterCache) {
		this.characterCache = characterCache;
	}

	/**
	 * Copy image data over the source pixels image.
	 */
	@Override
	public void addCharacter(
			final Map.Entry<Character, ImageMatrix<Short>> characterEntry,
			final int tileX,
			final int tileY)
	{
		frameExporter.addCharacter(characterEntry, tileX, tileY);
	}

	/**
	 * Called at the beginning of a frame.
	 */
	@Override
	public void init(final ImageMatrixDimensions targetDimensions) {

		frameExporter = new ImageAsciiExporter();
		frameExporter.setCharacterCache(characterCache);
		frameExporter.initFrames(1);
		frameExporter.init(targetDimensions);
	}

	/**
	 * Called at the end of a frame.
	 */
	@Override
	public void imageEnd() {

		frameExporter.imageEnd();
		frameExporter.finalizeFrames();
		encoder.addFrame(frameExporter.getOutput().get(0));
		frameExporter = null;
	}

	/**
	 * Called at the beginning of the animation (before the first frame).
	 */
	@Override
	public void initFrames(final int numFrames) {

		encoder = new AnimatedGifEncoder();
		gifBufferStream = new ByteArrayOutputStream();
		final boolean openStatus = encoder.start(gifBufferStream);
		if (openStatus) {
			encoder.setDelay(delay);   // 1 frame per delay(ms)
			encoder.setRepeat(repeat);
		}
	}

	/**
	 * Called at the end of the animation (after tha last frame).
	 */
	@Override
	public void finalizeFrames() {

		encoder.finish();
	}

	@Override
	public byte[] getOutput() {
		return gifBufferStream.toByteArray();
	}
}
