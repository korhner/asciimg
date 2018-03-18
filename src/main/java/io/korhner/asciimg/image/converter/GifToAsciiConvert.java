package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.utils.AnimatedGifEncoder;
import io.korhner.asciimg.utils.GifDecoder;

public class GifToAsciiConvert extends AsciiToImageConverter {

	public GifToAsciiConvert(final AsciiImgCache characterCacher, final BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
	}

	/**
	 * @param delay the delay time(ms) between each frame
	 * @param repeat the number of times the set of GIF frames should be played; 0 means play indefinitely.
	 * @return
	 *   1:  done
	 *   0:  opening disFile failed
	 *   -1: srcFile does not exist or opening failed
	 */
	public int convertGifToAscii(final String srcFilePath, final String disFilePath, final int delay, final int repeat) {
		final GifDecoder decoder = new GifDecoder();
		final int status = decoder.read(srcFilePath);
		final int ret;
		if (status == 0) {
			final AnimatedGifEncoder encoder = new AnimatedGifEncoder();
			final boolean openStatus = encoder.start(disFilePath);
			if (openStatus) {
				encoder.setDelay(delay);   // 1 frame per delay(ms)
				encoder.setRepeat(repeat);
				// initialize converters
				final int frameCount = decoder.getFrameCount();
				for (int i = 0; i < frameCount; i++) {
					// convert per frame
					encoder.addFrame(this.convertImage(decoder.getFrame(i)));
				}
				encoder.finish();
				ret = 1; // done
			} else {
				ret = 0; // opening disFile failed
			}
		} else {
			ret = -1; // srcFile does not exist or opening failed
		}

		return ret;
	}
}
