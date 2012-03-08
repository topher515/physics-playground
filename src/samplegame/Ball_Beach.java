package samplegame;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

/**
 * A Soccer ball is a MovingEntity with a soccerball image and with a mass of .2kg, and friction coefficients of 1
 * 
 * @author The UC Regents
 */
public class Ball_Beach extends Ball
{

	@Override
	public String DefaultImageLocation()
	{
		return "beachball.png";
	}

	public Ball_Beach()
	{
		this( 0, 0, 0, 0 );
	}

	public Ball_Beach( Point2D p, Vector2d v )
	{
		super( p.getX(), p.getY(), 22.5, v, .2 ); // mass (in kg)

	}

	public Ball_Beach( double x, double y, double dX, double dY )
	{
		this( new Point2D.Double( x, y ), new Vector2d( dX, dY ) );
		// System.out.println(this.getULPoint().x);

	}
}