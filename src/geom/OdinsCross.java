package geom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;

/**
 * Draws Odin's Cross.  (Odin's cross is a type of Sun Wheel, 
 * and also the astronomical sign for the Earth) 
 * ( http://en.wikipedia.org/wiki/Sunwheel#Design )
 * 
 * Commonly used to draw the Center of Mass Symbol 
 * commonly used to calibrate high-speed cameras as they 
 * watch and record an object's movement
 */
public final class OdinsCross
{
	private static final int Radius = 5;

	/**
	 *  Draws Odin's cross
	 * @param g2d The Graphics
	 * @param orientation The Rotation of Odin's Cross in degrees
	 * @param x The Center X location
	 * @param y The Center Y location
	 * @param primaryColor The primary color - Quadrants 1, 3, and the outline
	 * @param SecondaryColor The secondary color - Quadrants 2 and 4
	 */
	public static void DrawOdinsCross( Graphics2D g2d, double orientation, double x, double y, Color primaryColor,
			Color SecondaryColor )
	{
		double xUL = x - Radius;
		double yUL = y - Radius;

		int diameter = Radius * 2;

		g2d.setColor( SecondaryColor );
		g2d.fill( new Arc2D.Double( xUL, yUL, diameter, diameter, 90 - orientation, 90, Arc2D.PIE ) );
		g2d.fill( new Arc2D.Double( xUL, yUL, diameter, diameter, 270 - orientation, 90, Arc2D.PIE ) );

		g2d.setColor( primaryColor );

		g2d.fill( new Arc2D.Double( xUL, yUL, diameter, diameter, 0 - orientation, 90, Arc2D.PIE ) );
		g2d.fill( new Arc2D.Double( xUL, yUL, diameter, diameter, 180 - orientation, 90, Arc2D.PIE ) );

		g2d.drawOval( (int)xUL, (int)yUL, diameter, diameter ); // Outline
	}

	/**
	 * Draws Odin's Cross, Defaults to Black & White as Primary and secondary colors respectively
	 * 
	 * @param g2d The graphics
	 * @param p The Center point to draw at
	 */
	public static void DrawOdinsCross( Graphics2D g2d, Point2D p )
	{
		DrawOdinsCross( g2d, 0, p.getX(), p.getY() );
	}
	
	/**
	 * Draws Odin's Cross, Defaults to Black & White as Primary and secondary colors respectively
	 * 
	 * @param g2d The graphics
	 * @param orientation The Rotation of Odin's Cross in degrees
	 * @param p The Center point to draw at
	 */
	public static void DrawOdinsCross( Graphics2D g2d, double orientation, Point2D p )
	{
		DrawOdinsCross( g2d, orientation, p.getX(), p.getY() );
	}
	
	/**
	 *  Draws Odin's Cross, Defaults to Black & White as Primary and secondary colors respectively
	 *  
	 * @param g2d The Graphics
	 * @param x The Center X location
	 * @param y The Center Y location
	 */
	public static void DrawOdinsCross( Graphics2D g2d, double x, double y )
	{
		DrawOdinsCross( g2d, 0, x, y, Color.BLACK, Color.WHITE );
	}

	/**
	 *  Draws Odin's Cross, Defaults to Black & White as Primary and secondary colors respectively
	 *  
	 * @param g2d The Graphics
	 * @param orientation The Rotation of Odin's Cross in degrees
	 * @param x The Center X location
	 * @param y The Center Y location
	 */
	public static void DrawOdinsCross( Graphics2D g2d, double orientation, double x, double y )
	{
		DrawOdinsCross( g2d, orientation, x, y, Color.BLACK, Color.WHITE );
	}
}
