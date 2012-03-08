package samplegame;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

/**
 * A Bowling Ball is a MovingEntity with the image of a bowling ball and with a weight of 7kg and friction coefs of 1.
 * 
 * @author The UC Regents
 */
public class Ball_Bowling extends Ball
{

	@Override
	public String DefaultImageLocation()
	{
		return "bowlingball.png";
	}

	public Ball_Bowling()
	{
		this( 0, 0, 0, 0 );
	}

	public Ball_Bowling( Point2D p, Vector2d v )
	{
		super( p.getX(), p.getY(), 15, v, 7 ); // mass (in kg)
	}

	public Ball_Bowling( double x, double y, double dX, double dY )
	{
		this( new Point2D.Double( x, y ), new Vector2d( dX, dY ) );

	}
}
