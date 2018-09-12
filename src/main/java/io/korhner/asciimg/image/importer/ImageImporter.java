package io.korhner.asciimg.image.importer;

import io.korhner.asciimg.image.matrix.ImageMatrix;
import java.io.IOException;

/**
 * Used to read (import) different image types to the internal format.
 *
 * @param <I>
 *            input type of the image (or similar) to be imported
 * @param <O>
 *            output data-point value type of the internal version of the imported image
 */
public interface ImageImporter<I, O> {

	/**
	 * Sets the source, an image (or similar) that is to be read, later on.
	 *
	 * @param source the source, non-ASCII image
	 * @throws IOException on any kind of input error
	 */
	void setSource(I source) throws IOException;

	/**
	 * Indicates the number of frames contained in the associated source image (or similar).
	 * This will usually be 1, but potentially more in the case of a GIF, for example.
	 * The {@link #read()} method may be called this many times to get all the frames.
	 *
	 * @return usually 1, but potentially more, for example in the case of an animated GIF
	 * @throws IOException if this info could not be read from the assigned source
	 */
	int getFrames() throws IOException;

	/**
	 * Reads an image (or similar) into the internal format.
	 *
	 * @return the contents of the imported source,
	 * which is usually either an image or a frame of an animation
	 * @throws IOException on any kind of input error
	 */
	ImageMatrix<O> read() throws IOException;
}
