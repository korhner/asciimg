package io.korhner.asciimg.image.matrix;

/**
 * Contains basic image meta data.
 */
public class BasicImageMatrixInfo implements ImageMatrixInfo {

	private final int valuesPerDataPoint;
	private final Class dataPointClass;
	private final int bitsPerValue;

	public BasicImageMatrixInfo(
			final int valuesPerDataPoint,
			final Class dataPointClass,
			final int bitsPerValue)
	{
		this.valuesPerDataPoint = valuesPerDataPoint;
		this.dataPointClass = dataPointClass;
		this.bitsPerValue = bitsPerValue;
	}

	@Override
	public boolean isGrayScale() {
		return !isColored() && !isBlackAndWhite();
	}

	@Override
	public boolean isBlackAndWhite() {
		return !isColored() && bitsPerValue == 1;
	}

	@Override
	public boolean isColored() {
		return valuesPerDataPoint > 2;
	}

	@Override
	public boolean isWithAlpha() {
		return valuesPerDataPoint == 2 || valuesPerDataPoint == 4;
	}

	@Override
	public int getValuesPerDataPoint() {
		return valuesPerDataPoint;
	}

	@Override
	public Class getDataPointClass() {
		return dataPointClass;
	}

	@Override
	public int getBitsPerValue() {
		return bitsPerValue;
	}
}
