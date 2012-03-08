package samplegame.planets;


/**
 * A Planet1 is a MovingEntity with a Planet1 image and with a mass of 50000kg,
 * and friction coefficients of 1
 * 
 * @author The UC Regents
 * 
 */
public class Planet1 extends BasePlanet
{

	@Override
	public String DefaultImageLocation()
	{
		return "planet1_45x45.png";
	}

	public Planet1()
	{
		super( 45/2, 50000 );
	}
}
