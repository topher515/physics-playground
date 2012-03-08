package samplegame;

import geom.PolygonOrCircle;
import physics.MovingEntity;


public class Square extends MovingEntity {
	
	public Square() 
	{
		super( new PolygonOrCircle( new double[]{0,75,75,0}, new double[]{0,0,75,75}, 4 ) );
		this.setMass(this.getShapeMass());
	}
	
}
