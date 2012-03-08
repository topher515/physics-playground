package samplegame;

import util.Utility;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import physics.BaseEntity;

public class Ball_Smile extends Ball
{

	public Ball_Smile()
	{
		this( 0, 0, 0, 0 );
	}

	public Ball_Smile( Point2D p, Vector2d v )
	{
		super( p.getX(), p.getY(), 50, v, 2 ); // mass (in kg)
	}

	public Ball_Smile( double x, double y, double dX, double dY )
	{
		this( new Point2D.Double( x, y ), new Vector2d( dX, dY ) );
	}

	static
	{
		Utility.cacheSound( "uhoh.wav" );
	}

	@Override
	public String DefaultImageLocation()
	{
		return "smile_100x100.png";
	}

	@Override
	public void OnCollisionWith( BaseEntity ent2 )
	{
		Utility.PlaySound( "uhoh.wav" );
	}
}
