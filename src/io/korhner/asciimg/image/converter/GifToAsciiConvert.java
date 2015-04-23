package io.korhner.asciimg.image.converter;

import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.utils.AnimatedGifEncoder;
import io.korhner.asciimg.utils.GifDecoder;

public class GifToAsciiConvert extends AsciiToImageConverter{

	public GifToAsciiConvert(AsciiImgCache characterCacher,
			BestCharacterFitStrategy characterFitStrategy) {
		super(characterCacher, characterFitStrategy);
	}

	public int  convertGitToAscii(String srcFilePath,String disFilePath,int delay){
		GifDecoder decoder = new GifDecoder();
		int status = decoder.read(srcFilePath);
		if(status!=0){
			return -1;//srcfile not exist or open failed!
		}
		AnimatedGifEncoder e = new AnimatedGifEncoder();
		boolean openStatus = e.start(disFilePath);
		if(openStatus){
			e.setDelay(delay);   // 1 frame per delay(ms)
			// initialize converters
			int frameCount = decoder.getFrameCount();
			for(int i=0;i<frameCount;i++){
				//convert per frame
				e.addFrame(this.convertImage(decoder.getFrame(i)));
			}
			e.finish();
			return 1;//done!
		}
		return 0;//open disfile failed!
	};
	
}
