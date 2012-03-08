package samplegame.planets;

import geom.PolygonOrCircle;

import java.awt.geom.Rectangle2D;

import physics.MovingEntity;
import physics.RangedForceEntity;

public abstract class BasePlanet extends RangedForceEntity
{

	/*
	 * public BasePlanet( double fieldRadius, double planetRadius, double planetMass ) { super( new PolygonOrCircle( (
	 * fieldRadius - planetRadius ) / -2, ( fieldRadius - planetRadius ) / -2, fieldRadius ), new PolygonOrCircle( 0, 0,
	 * planetRadius ), planetMass / 10 );
	 * 
	 * this.setMass( planetMass ); this.setVisible( true );
	 * 
	 * //System.out.println( this.getResizingObject() ); }
	 * 
	 * public BasePlanet( double planetRadius, double planetMass ) { this( planetMass / 100, planetRadius, planetMass ); }
	 */

	public BasePlanet( double planetRadius, double planetMass )
	{
		super( null, new PolygonOrCircle( 0, 0, planetRadius ), planetMass / 10 );

		this.setMass( planetMass );
		this.setVisible( true );

		// System.out.println( this.getResizingObject() );
	}

	@Override
	public boolean isCollidable()
	{
		return true;
	}

	@Override
	public boolean IntersectsWith( MovingEntity entity )
	{
		if ( entity == this )
			return false;

		return true; // They're fsking planets, they're gonna attract from virutally anywhere. :D

		/*
		 * 
		 * if ( entity instanceof BasePlanet ) { Shape thisShape = getForceShape().getShape(); Shape otherShape =
		 * ((BasePlanet)entity).getForceShape().getShape();
		 * 
		 * return ( thisShape.intersects( otherShape.getBounds2D() ) && otherShape.intersects( thisShape.getBounds2D() ) ); }
		 * 
		 * return super.IntersectsWith( entity );
		 */
	}

	@Override
	public Rectangle2D getEffectiveBounds()
	{
		return null;
	}
}
