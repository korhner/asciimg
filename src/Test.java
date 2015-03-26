import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.AsciiToImageConverter;
import io.korhner.asciimg.image.AsciiToStringConverter;
import io.korhner.asciimg.image.BestCharacterFitStrategy;
import io.korhner.asciimg.image.ColorSquareErrorFitStrategy;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("ascii-3.jpg");
		BufferedImage input = ImageIO.read(inputFile);
		Font font = new Font("Courier", Font.PLAIN, 6);
		AsciiImgCache cache = AsciiImgCache.create(font);
		BestCharacterFitStrategy fitStrategy = new ColorSquareErrorFitStrategy();
		
		AsciiToImageConverter imgConverter = new AsciiToImageConverter(cache, fitStrategy);
		BufferedImage output = imgConverter.convertImage(input);
		File outputfile = new File("output3.png");
		ImageIO.write(output, "png", outputfile);
		
		AsciiToStringConverter strConverter = new AsciiToStringConverter(cache, fitStrategy);
		String strOutput = strConverter.convertImage(input);
		System.out.println(strOutput);
	}
}
