import io.korhner.asciimg.image.AsciiImageConverter;
import io.korhner.asciimg.image.AsciiImgCache;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;


public class Test {

	public static void main(String[] args) throws IOException {
		File inputFile = new File("a.jpg");
		BufferedImage input = ImageIO.read(inputFile);
		Font font = new Font("Courier", Font.PLAIN, 6);

		AsciiImgCache cache = AsciiImgCache.create(font);
		AsciiImageConverter imgConverter = new AsciiImageConverter(cache);
		BufferedImage output = imgConverter.convertImage(input);
		File outputfile = new File("output3.png");
		ImageIO.write(output, "png", outputfile);

	}
}
