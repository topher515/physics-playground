package samplegame.planets;


/**
 * A Planet2 is a BasePlanet with a Planet2 image and with a mass of 80000kg,
 * 
 * @author The UC Regents
 */
public class Planet2 extends BasePlanet
{

	@Override
	public String DefaultImageLocation()
	{
		return "planet2_70x70.png";
	}

	public Planet2()
	{
		super( 70/2, 80000 );
	}
}
