package io.korhner.asciimg.image.exporter;

/**
 * Exports multi-frame ASCII art to custom output formats.
 * These formats might typically be GIF or AVI.
 *
 * @param <O>
 *            output container type of the ASCII art
 */
public interface MultiFrameAsciiExporter<O> extends AsciiExporter<O> {


	/**
	 * Initializes the inner state of this exporter
	 * to be ready to start exporting the next frame.
	 *
	 * @param numFrame
	 *            number of frames of the complete animation
	 */
	void initFrames(int numFrame);


	/**
	 * Tells this exporter to finalize the current frame.
	 *            source image height
	 */
	void finalizeFrames();
}
