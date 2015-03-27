import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.AsciiToImageConverter;
import io.korhner.asciimg.image.AsciiToStringConverter;
import io.korhner.asciimg.image.character_fit_strategy.BestCharacterFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.ColorSquareErrorFitStrategy;
import io.korhner.asciimg.image.character_fit_strategy.StructuralSimilarityFitStrategy;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) throws IOException {
		
		long startTime = 0;
		long endTime = 0;
		
		//Scanner scanInput = new Scanner(System.in);
		//scanInput.nextLine();
		
		//System.out.println("starting");
		startTime = System.currentTimeMillis();
		File inputFile = new File("d.bmp");
		BufferedImage input = ImageIO.read(inputFile);
		Font font = new Font("Courier", Font.PLAIN, 10);
		AsciiImgCache cache = AsciiImgCache.create(font);
		BestCharacterFitStrategy squareErrorStrategy = new ColorSquareErrorFitStrategy();
		BestCharacterFitStrategy ssimStrategy = new StructuralSimilarityFitStrategy();
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("Creating cache took " + endTime);
		
		startTime = System.currentTimeMillis();
		AsciiToImageConverter imgConverter = new AsciiToImageConverter(cache, squareErrorStrategy);
		BufferedImage output = imgConverter.convertImage(input);
		File outputfile = new File("square_error.png");
		ImageIO.write(output, "png", outputfile);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("Square error took " + endTime);
		
		startTime = System.currentTimeMillis();
		imgConverter.setCharacterFitStrategy(ssimStrategy);
		output = imgConverter.convertImage(input);
		outputfile = new File("ssim.png");
		ImageIO.write(output, "png", outputfile);
		endTime = System.currentTimeMillis() - startTime;
		System.out.println("SSIM took " + endTime);
//		AsciiToStringConverter strConverter = new AsciiToStringConverter(cache, ssimStrategy);
//		String strOutput = strConverter.convertImage(input);
//		System.out.println(strOutput);
		//System.out.println("done");
		//scanInput.nextLine();
		
	}
}
