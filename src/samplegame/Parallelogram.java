package samplegame;

import geom.PolygonOrCircle;
import physics.MovingEntity;

public class Parallelogram  extends MovingEntity
{
	public Parallelogram()
	{
		super( new PolygonOrCircle( new double[]{0,50,75,25}, new double[]{0,0,50,50}, 4 ) );
		this.setMass(this.getShapeMass());
	}
}