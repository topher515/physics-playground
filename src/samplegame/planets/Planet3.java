package samplegame.planets;

import java.awt.geom.Point2D;

/**
 * A Planet3 is a BasePlanet with a Planet3 image and with a mass of 50000kg,
 * 
 * @author The UC Regents
 * 
 */
public class Planet3 extends BasePlanet
{

	@Override
	public String DefaultImageLocation()
	{
		return "planet3_177x70.png";
	}

	public Planet3()
	{
		super( 70/2, 50000 );
		
		this.setImageOffset( new Point2D.Double( -52, 0 ) );
	}
}
