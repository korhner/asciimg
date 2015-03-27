package io.korhner.asciimg.image.character_fit_strategy;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.GrayscaleMatrix;

import java.util.Map.Entry;

public interface BestCharacterFitStrategy {

	Entry<Character, GrayscaleMatrix> findBestFit (AsciiImgCache characterCache, GrayscaleMatrix tile);
}
