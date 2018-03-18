package io.korhner.asciimg.image.exporter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.matrix.GrayScaleMatrix;
import io.korhner.asciimg.utils.AnimatedGifEncoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
	private AsciiExporter<BufferedImage> frameExporter;

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
			final Map.Entry<Character, GrayScaleMatrix> characterEntry,
			final int[] sourceImagePixels,
			final int tileX,
			final int tileY,
			final int imageWidth) {
		frameExporter.addCharacter(characterEntry, sourceImagePixels, tileX, tileY, imageWidth);
	}

	/**
	 * Called at the beginning of a frame an empty buffered image.
	 */
	@Override
	public void init(final int imageWidth, final int imageHeight) {

		frameExporter = new ImageAsciiExporter();
		frameExporter.setCharacterCache(characterCache);
		frameExporter.init(imageWidth, imageHeight);
	}

	/**
	 * Called at the end of a frame.
	 */
	@Override
	public void finalize(final int[] sourceImagePixels, final int imageWidth, final int imageHeight) {

		frameExporter.finalize(sourceImagePixels, imageWidth, imageHeight);
		encoder.addFrame(frameExporter.getOutput());
		frameExporter = null;
	}

	/**
	 * Called at the beginning of the animation (before hte first frame.
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
