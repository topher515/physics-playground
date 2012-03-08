package samplegame;

import geom.PolygonOrCircle;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import physics.MovingEntity;

public abstract class Ball extends MovingEntity
{

	/*
	 * public Ball() { this( 0, 0, 0, 0); } public Ball(Double p, Vector2d v) { super(p, v); // TODO Auto-generated constructor stub }
	 */
	public Ball( Point2D p, Vector2d v, double radius, int mass )
	{
		this( p.getX(), p.getY(), radius, v, mass );
	}

	public Ball( double x, double y, double radius, double dX, double dY )
	{
		this( x, y, radius, new Vector2d( dX, dY ), 1 );
	}

	public Ball( double x, double y, double radius, Vector2d inputVelocity, double inputMass )
	{
		super( new PolygonOrCircle( x, y, radius ), inputVelocity, inputMass );
	}
}
