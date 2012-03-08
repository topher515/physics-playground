package samplegame;

import geom.PolygonOrCircle;
import physics.MovingEntity;


public class Triangle extends MovingEntity {
	
	public Triangle() 
	{
		super( new PolygonOrCircle( new double[]{0,75,0}, new double[]{0,100,100}, 3 ) );
		this.setMass(this.getShapeMass());
	}
	
}