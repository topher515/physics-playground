package samplegame;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

/**
 * A Soccer ball is a MovingEntity with a soccerball image and with a mass of 1kg, and friction coefficients of 1
 * 
 * @author The UC Regents
 */
public class Ball_Soccer extends Ball
{

	// ObjectPropertiesFrame pFrame = new ObjectPropertiesFrame(this.getClass());
	@Override
	public String DefaultImageLocation()
	{
		return "soccerball.png";
	}

	public Ball_Soccer()
	{
		this( 0, 0, 0, 0 );
	}

	public Ball_Soccer( Point2D p, Vector2d v )
	{
		super( p.getX(), p.getY(), 15, v, 1 );// mass (in kg)
	}

	public Ball_Soccer( double x, double y, double dX, double dY )
	{

		this( new Point2D.Double( x, y ), new Vector2d( dX, dY ) );
	}
}
