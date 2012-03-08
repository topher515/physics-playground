package geom;

import java.awt.geom.Dimension2D;

/**
 * Dimension stores a pair of numbers as doubles and provides the means to
 * modify them.
 * 
 * @author The UC Regents
 */
public class DimensionDouble extends Dimension2D
{
	private double width;

	private double height;

	/**
	 * This constructs a copy of another DimensionDouble
	 * 
	 * @param dd
	 *            The dimension to copy
	 */
	public DimensionDouble( DimensionDouble dd )
	{
		width = dd.width;
		height = dd.height;
	}

	/**
	 * Constructs a new DimensionDouble with width w and height h.
	 * 
	 * @param w
	 *            The width
	 * @param h
	 *            The height
	 */
	public DimensionDouble( double w, double h )
	{
		width = w;
		height = h;
	}

	/**
	 * Accessor for the demsion's width
	 * 
	 * @return The dimension's width
	 */
	@Override
	public double getWidth()
	{
		return width;
	}

	/**
	 * Accessor for the demsion's height
	 * 
	 * @return The dimension's height
	 */
	@Override
	public double getHeight()
	{
		return height;
	}

	/**
	 * Mutator for both the width and the height of the dimension
	 * 
	 * @param w
	 *            The new width
	 * @param h
	 *            The new height
	 */
	@Override
	public void setSize( double w, double h )
	{
		width = w;
		height = h;
	}

}
