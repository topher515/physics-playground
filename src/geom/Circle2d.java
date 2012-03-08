package geom;

import java.awt.geom.Ellipse2D;

/**
 * A Circle 2D is an Ellipse2D that accepts doubles and a radius as it's ctor
 * parameters
 * 
 * @author The UC Regents
 * 
 */
public class Circle2d extends Ellipse2D.Double
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constructs a circle
	 * @param xUL The upper left x coordinate
	 * @param yUL The upper left y coordinate
	 * @param radius The radius of the circle
	 */
	public Circle2d( double xUL, double yUL, double radius )
	{
		super( xUL, yUL, radius*2, radius*2 );
	}
	/**
	 * Gets the radius of the circle
	 * @return The radius of the circle
	 */
	public double getRadius()
	{
		//return (( this.getWidth() + this.getHeight() ) / 2);
		return this.getWidth()/2;
	}
}
