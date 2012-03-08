/*
 * Copyright (C) 2005, University of Massachusetts, Multi-Agent Systems Lab See LICENSE for license information
 */

/**
 * Arrow class
 * 
 * @author Bryan Horling (bhorling@cs.umass.edu)
 * @version 1.0
 */

package geom;

/* Global imports */
import java.awt.Graphics;
import java.awt.Point;

/**
 * Draws an arrow
 */
public class Arrow
{
	/**
	 * These two control the arrow head angle and side length, respectively
	 */
	private static double AANG = Math.PI / 8;

	private static int ALEN = 10;

	/**
	 * Draws line with arrow heads at either end. Location of the arrowheads along the line is controlled by the place parameter (for example, if
	 * place = 0.45, arrowheads would be placed at spots 45% away from their respective ends)
	 * 
	 * @param g The graphics world to draw in
	 * @param x1 Coords of the line
	 * @param x2 Coords of the line
	 * @param y1 Coords of the line
	 * @param y2 Coords of the line
	 * @param start Arrowhead at start
	 * @param end Arrowhead at end
	 * @param place A double [0-1] indicating the line length percentage (from start) at which the arrow tip will be placed.
	 */
	public static void drawArrow( Graphics g, int x1, int y1, int x2, int y2, boolean start, boolean end, double place )
	{
		double theta, len, side = 1;
		Point tip, side1, side2;

		// Figure out the angle and length
		theta = Math.atan( (double)( y2 - y1 ) / (double)( x2 - x1 ) );
		len = Math.sqrt( Math.pow( ( x2 - x1 ), 2 ) + Math.pow( ( y2 - y1 ), 2 ) ) * place;

		// Hack to get it to display correctly to the left of center
		if ( x2 < x1 )
			side = -1;

		// Arrow at end
		if ( end )
		{
			tip = new Point( (int)( x1 + side * len * Math.cos( theta ) ), (int)( y1 + side * len * Math.sin( theta ) ) );
			side1 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta + AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta + AANG ) ) );
			side2 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta - AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta - AANG ) ) );

			g.drawLine( x1, y1, x2, y2 );
			g.fillPolygon( new int[] { tip.x, side1.x, side2.x }, new int[] { tip.y, side1.y, side2.y }, 3 );
		}

		// Arrow at start
		if ( start )
		{
			side *= -1;
			tip = new Point( (int)( x2 + side * len * Math.cos( theta ) ), (int)( y2 + side * len * Math.sin( theta ) ) );
			side1 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta + AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta + AANG ) ) );
			side2 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta - AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta - AANG ) ) );

			g.drawLine( x1, y1, x2, y2 );

			g.fillPolygon( new int[] { tip.x, side1.x, side2.x }, new int[] { tip.y, side1.y, side2.y }, 3 );
		}
	}

	/**
	 * Just draws an arrowhead at the end
	 * 
	 * @param g The graphics world to draw in
	 * @param x1 Coords of the line
	 * @param x2 Coords of the line
	 * @param y1 Coords of the line
	 * @param y2 Coords of the line
	 */
	public static void drawArrowhead( Graphics g, int x1, int y1, int x2, int y2 )
	{
		double theta, len, side = 1;
		Point tip, side1, side2;

		// Figure out the angle and length
		theta = Math.atan( (double)( y2 - y1 ) / (double)( x2 - x1 ) );
		len = Math.sqrt( Math.pow( ( x2 - x1 ), 2 ) + Math.pow( ( y2 - y1 ), 2 ) );

		// Hack to get it to display correctly to the left of center
		if ( x2 < x1 )
			side = -1;

		// Arrow at end
		tip = new Point( (int)( x1 + side * len * Math.cos( theta ) ), (int)( y1 + side * len * Math.sin( theta ) ) );
		side1 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta + AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta + AANG ) ) );
		side2 = new Point( (int)( tip.x - side * ALEN * Math.cos( theta - AANG ) ), (int)( tip.y - side * ALEN * Math.sin( theta - AANG ) ) );

		g.fillPolygon( new int[] { tip.x, side1.x, side2.x }, new int[] { tip.y, side1.y, side2.y }, 3 );
	}

	/**
	 * Draws a line with an arrowhead at its end (x2,y2) adjusted by place
	 * 
	 * @param g The graphics world to draw in
	 * @param x1 Coords of the line
	 * @param x2 Coords of the line
	 * @param y1 Coords of the line
	 * @param y2 Coords of the line
	 * @param place A double [0-1] indicating the line length percentage (from start) at which the arrow tip will be placed.
	 */
	public static void drawArrow( Graphics g, int x1, int y1, int x2, int y2, double place )
	{
		drawArrow( g, x1, y1, x2, y2, false, true, place );
	}

	/**
	 * Draws a line with an arrowhead at its terminal end (x2,y2)
	 * 
	 * @param g The graphics world to draw in
	 * @param x1 Coords of the line
	 * @param x2 Coords of the line
	 * @param y1 Coords of the line
	 * @param y2 Coords of the line
	 */
	public static void drawArrow( Graphics g, int x1, int y1, int x2, int y2 )
	{
		drawArrow( g, x1, y1, x2, y2, false, true, 1 );
	}
}
