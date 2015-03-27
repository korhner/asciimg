package io.korhner.asciimg.image;

import java.awt.image.BufferedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GrayscaleMatrix {

	private final float data[];
	private final int width;
	private final int height;

	public GrayscaleMatrix(int width, int height) {
		this.data = new float[width * height];
		this.width = width;
		this.height = height;
	}

	public float[] getData() {
		return data;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public GrayscaleMatrix(int[] pixels, int width, int height) {
		this(width, height);

		if (width * height != pixels.length) {
			throw new IllegalArgumentException(
					"Pixels array does not match specified width and height!");
		}

		for (int i = 0; i < this.data.length; i++) {

			int color = pixels[i];
			int red = FloatColor.extractRed(color);
			int green = FloatColor.extractGreen(color);
			int blue = FloatColor.extractBlue(color);

			data[i] = 0.3f * red + 0.59f * green + 0.11f * blue;

		}
	}

	public static GrayscaleMatrix createFromRegion(
			final GrayscaleMatrix source, final int width, final int height,
			final int startPixelX, final int startPixelY) {
		GrayscaleMatrix output = new GrayscaleMatrix(width, height);

		for (int i = 0; i < output.data.length; i++) {
			int xOffset = i % width;
			int yOffset = i / width;

			int index = ImageUtils.convert2DTo1D(startPixelX + xOffset,
					startPixelY + yOffset, source.width);
			output.data[i] = source.data[index];
		}

		return output;
	}

	public float getTotal() {
		float total = 0;
		for (int i = 0; i < this.data.length; i++) {
			total += data[i];
		}

		return total;
	}

	public static GrayscaleMatrix createFromGaussian(final int size,
			final float sigma) {
		GrayscaleMatrix filter = new GrayscaleMatrix(size, size);
		float s2 = sigma * sigma;
		float c = (size - 1) / 2.0f;

		for (int i = 0; i < filter.data.length; i++) {

			float dx = ImageUtils.convert1DtoX(i, size) - c;
			float dy = ImageUtils.convert1DtoY(i, size) - c;
			filter.data[i] = (float) Math.exp(-(dx * dx + dy * dy) / (2 * s2));
		}

		float scale = 1.0f / filter.getTotal();
		filter.scale(scale);

		return filter;
	}

	public static GrayscaleMatrix createFromSubsample(
			final GrayscaleMatrix img, int skip) {
		int w = img.width;
		int h = img.height;
		float scale = 1.0f / (skip * skip);
		GrayscaleMatrix ans = new GrayscaleMatrix(w / skip, h / skip);
		for (int i = 0; i < w - skip; i += skip)
			for (int j = 0; j < h - skip; j += skip) {
				float sum = 0;
				for (int x = i; x < i + skip; ++x)
					for (int y = j; y < j + skip; ++y)
						sum += img.data[ImageUtils.convert2DTo1D(x, y,
								img.width)];
				ans.data[ImageUtils
						.convert2DTo1D(i / skip, j / skip, ans.width)] = sum
						* scale;
			}
		return ans;
	}

	public void scale(final float scale) {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] *= scale;
		}
	}

	public static GrayscaleMatrix createFromFilter(final GrayscaleMatrix a,
			final GrayscaleMatrix b) {
		int ax = a.width, ay = a.height;
		int bx = b.width, by = b.height;
		int bcx = (bx + 1) / 2, bcy = (by + 1) / 2; // center position
		GrayscaleMatrix c = new GrayscaleMatrix(ax - bx + 1, ay - by + 1);
		for (int i = bx - bcx + 1; i < ax - bx; ++i) {
			for (int j = by - bcy + 1; j < ay - by; ++j) {
				float sum = 0;
				for (int x = bcx - bx + 1 + i; x < 1 + i + bcx; ++x) {
					for (int y = bcy - by + 1 + j; y < 1 + j + bcy; ++y) {
						sum += a.data[ImageUtils.convert2DTo1D(x, y, a.width)]
								* b.data[ImageUtils.convert2DTo1D(bx - bcx - 1
										- i + x, by - bcy - 1 - j + y, b.width)];
					}
					c.data[ImageUtils.convert2DTo1D(i - bcx, j - bcy, c.width)] = sum;
				}
			}
		}
		return c;
	}

	public static GrayscaleMatrix multiply(final GrayscaleMatrix a,
			final GrayscaleMatrix b) {
		GrayscaleMatrix c = new GrayscaleMatrix(a.width, a.height);
		for (int i = 0; i < c.data.length; i++) {
			c.data[i] = a.data[i] * b.data[i];
		}

		return c;
	}

	public static GrayscaleMatrix subtract(final GrayscaleMatrix a,
			final GrayscaleMatrix b) {
		GrayscaleMatrix c = new GrayscaleMatrix(a.width, a.height);
		for (int i = 0; i < c.data.length; i++) {
			c.data[i] = a.data[i] - b.data[i];
		}

		return c;
	}

	public static GrayscaleMatrix add(final GrayscaleMatrix a,
			final GrayscaleMatrix b) {
		GrayscaleMatrix c = new GrayscaleMatrix(a.width, a.height);
		for (int i = 0; i < c.data.length; i++) {
			c.data[i] = a.data[i] + b.data[i];
		}

		return c;
	}

	public static GrayscaleMatrix createFromLinear(float s, GrayscaleMatrix a,
			float c) {
		GrayscaleMatrix grid = new GrayscaleMatrix(a.width, a.height);
		for (int i = 0; i < grid.data.length; i++) {
			grid.data[i] = s * a.data[i] + c;
		}

		return grid;

	}

	public void debugSave(final String filename) {
		BufferedImage output = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_ARGB);
		int[] pixels = new int[width * height];

		for (int i = 0; i < data.length; i++) {
			int color = (int) data[i];
			pixels[i] = FloatColor.getColorFromComponents(255, color, color,
					color);
		}

		output.setRGB(0, 0, this.width, this.height, pixels, 0, this.width);
		File outputfile = new File("grid/" + filename + ".png");

		try {
			ImageIO.write(output, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public float calculateMeanSquareError(GrayscaleMatrix target) {
		if (this.width != target.width || this.height != target.height) {
			throw new IllegalArgumentException(
					"Images must be of same dimensions!");
		}

		float error = 0;
		for (int i = 0; i < data.length; i++) {
			error += (data[i] - target.getData()[i])
					* (data[i] - target.getData()[i]);
		}

		return error / data.length;
	}
}
