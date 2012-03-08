package physics;

/**
 * Pair can contain any two objects. The main purpose is the equals method, which ignores the order of the objects in the pair. ie [1,2] == [2,1]
 * 
 * @author The UC Regents
 * @param <E>
 *            The Class of the objects to store
 */
public class Pair<E>
{
	private E element1;

	private E element2;

	/**
	 * The basic pair consructor
	 * 
	 * @param obj1
	 *            The first object
	 * @param obj2
	 *            The second object
	 */
	public Pair( E obj1, E obj2 )
	{
		element1 = obj1;
		element2 = obj2;
	}

	@Override
	/**
	 * Tests if 2 pairs are equal, ignoring the order of the objects in the pair. ie [1,2] == [2,1]
	 * 
	 * @param pair2
	 *            The other pair to compare to
	 * @return If the pairs are equal
	 */
	public boolean equals( Object pair2 )
	{
		if ( !( pair2 instanceof Pair ) )
			return false;

		Pair otherPair = (Pair)pair2;

		return ( ( this.element1.equals( otherPair.element1 ) && ( this.element2.equals( otherPair.element2 ) )
				|| ( this.element1.equals( otherPair.element2 ) ) && ( this.element2.equals( otherPair.element1 ) ) ) );
	}

	@Override
	/**
	 * Outputs the pair in string form
	 * 
	 * @return The combined strings of each object
	 */
	public String toString()
	{
		return "[" + element1.toString() + ", " + element2.toString() + "]";
	}
}
