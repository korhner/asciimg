import io.korhner.asciimg.image.AsciiImgCache;
import io.korhner.asciimg.image.AsciiToImageConverter;
import io.korhner.asciimg.image.AsciiToStringConverter;

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
		
		AsciiToImageConverter imgConverter = new AsciiToImageConverter(cache);
		BufferedImage output = imgConverter.convertImage(input);
		File outputfile = new File("output3.png");
		ImageIO.write(output, "png", outputfile);
		
		AsciiToStringConverter strConverter = new AsciiToStringConverter(cache);
		String strOutput = strConverter.convertImage(input);
		System.out.println(strOutput);
	}
}
