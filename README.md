# asciimg

Asciimg is an extensible ASCII art generator written in Java.
For more info refer to [this blog post](http://korhner.github.io/java/image-processing/ascii-art-generator-part-2/)

## Example usage

	// initialize cache
	AsciiImgCache cache = AsciiImgCache.create(new Font("Courier", Font.BOLD, 6));

	// load image
	BufferedImage portraitImage = ImageIO.read(new File("input_image.png"));

	// initialize converters
	AsciiToImageConverter imageConverter =
		new AsciiToImageConverter(cache, new ColorSquareErrorFitStrategy());
	AsciiToStringConverter stringConverter =
		new AsciiToStringConverter(cache, new StructuralSimilarityFitStrategy());

	// image output
	ImageIO.write(imageConverter.convertImage(portraitImage), "png",
		new File("ascii_art.png"));
	// string converter, output to console
	System.out.println(stringConverter.convertImage(portraitImage));

## Example output

Here are some sample images generated with various parameters:

![Original picture](http://korhner.github.io//assets/img/asciimg/orig.png "Original picture")

---------------------------------------

![16 pts font, MSE](http://korhner.github.io/assets/img/asciimg/large_square_error.png "16 pts font, MSE")

- - -

![16 pts font, SSIM](http://korhner.github.io/assets/img/asciimg/large_ssim.png "16 pts font, SSIM")

- - -

![10 pts font with 3 characters, MSE](http://korhner.github.io/assets/img/asciimg/medium_square_error.png "10 pts font with 3 characters, MSE")

- - -

![10 pts font with 3 characters, SSIM](http://korhner.github.io/assets/img/asciimg/medium_ssim.png "10 pts font with 3 characters, SSIM")

- - -

![6 pts font, MSE](http://korhner.github.io/assets/img/asciimg/small_square_error.png "6 pts font, MSE")

- - -

![6 pts font, SSIM](http://korhner.github.io/assets/img/asciimg/small_ssim.png "6 pts font, SSIM")

## Architecture

![Architecture](http://korhner.github.io/assets/img/asciimg/asciimg_cls_diagram.png)

## AsciiImgCache

Before any ASCII art rendering takes place, it is necessary to create an instance of this class.
It takes a font and a list of characters to use as parameters and it creates a map of images for every character.
There is also a default list of characters if you don't want to bother comming up with your own.

### BestCharacterFitStrategy

This is the abstraction of the algorithm used for determining how similar a part of the source image with each character is.
The implementation should compare two images and return a float error. Each character will be compared and the one that returns the lowest error will be selected.
Currently there two implementations available: `ColorSquareErrorFitStrategy` and `StructuralSimilarityFitStrategy`.

#### ColorSquareErrorFitStrategy

Very simple to understand, it compares every pixel and calculates Mean squared error.

#### StructuralSimilarityFitStrategy

The structural similarity (SSIM) index algorithm claims to reproduce human perception and its aim is to improve on traditional methods like MSE.
You can read more on [Wikipedia](http://en.wikipedia.org/wiki/Structural_similarity) if you want to know more.
I experimented a bit with it and implemented a version that seemed to produce the best results for this case.

### AsciiConverter<T>

This is the hearth of the process and it contains all the logic for tiling source image and utilizing concrete implementations for calculating character best fit.
However, it doesn't know how to create the concrete ASCII art - it needs to be subclassed.
There are currently two implementations: `AsciiToImageConverter` and `AsciiToStringConverter` - which, as you probably guessed, produce image and string output.

