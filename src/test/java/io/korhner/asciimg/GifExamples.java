package io.korhner.asciimg;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;
import io.korhner.asciimg.image.converter.AsciiToImageConverter;
import io.korhner.asciimg.image.converter.GifToAsciiConvert;
import io.korhner.asciimg.utils.AnimatedGifEncoder;
import io.korhner.asciimg.utils.GifDecoder;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class GifExamples {

	public static void main(String[] args) throws IOException {

		// initialize caches
		AsciiImgCache smallFontCache = AsciiImgCache.create(new Font("Courier",Font.BOLD, 6));
		// initialize ssimStrategy
		BestCharacterFitStrategy ssimStrategy = new StructuralSimilarityFitStrategy();
		
		String srcFilePath = "examples/test.gif";
		String disFilePath = "examples/test-ascii.gif";
		int delay = 100;//ms
		
		GifToAsciiConvert asciiConvert = new GifToAsciiConvert(smallFontCache, ssimStrategy);
		
		asciiConvert.convertGitToAscii(srcFilePath, disFilePath, delay,0);
	}
}
