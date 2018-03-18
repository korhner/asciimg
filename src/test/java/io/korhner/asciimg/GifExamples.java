package io.korhner.asciimg;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.converter.GifToAsciiConvert;
import java.awt.Font;

public class GifExamples {

	public static void main(String[] args) {

		// initialize caches
		AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));
		// initialize ssimStrategy
		BestCharacterFitStrategy ssimStrategy = new StructuralSimilarityFitStrategy();
		
		String srcFilePath = "examples/animation/orig.gif";
		String disFilePath = "examples/animation/converted.gif";
		int delay = 100; // ms
		
		GifToAsciiConvert asciiConvert = new GifToAsciiConvert(smallFontCache, ssimStrategy);
		
		asciiConvert.convertGitToAscii(srcFilePath, disFilePath, delay,0);
	}
}
