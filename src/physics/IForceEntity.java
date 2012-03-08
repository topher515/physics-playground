package physics;

import javax.vecmath.Vector2d;

/**
 * Any entity that implements this interface is considered a force entity that
 * will act forces upon other entities upon certian conditions such as ther
 * distance from the entity or the location of the other entity.
 */
public interface IForceEntity
{

	/**
	 * Calculates the force to act on another entity
	 * 
	 * @param be
	 *            The force is calculated for this entity
	 * @return A vector2d of the force to act on the parameter entity
	 */
	public Vector2d getForceFor( MovingEntity be );

	public boolean IntersectsWith( MovingEntity be );
}
