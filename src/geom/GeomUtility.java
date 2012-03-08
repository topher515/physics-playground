package geom;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.vecmath.Vector2d;

/**
 * A class of commonly used, misc, helper methods related to Geometry
 * 
 * @author The UC Regents
 */
public final class GeomUtility
{
	public enum TurnType
	{
		Left, Right, Collinear
	}

	/**
	 * Checks to see if 2 vectors are <b>Approximately</b> parallel
	 * 
	 * @param firstVector
	 *            The first Vector2D
	 * @param secondVector
	 *            The second Vector2D
	 * @return Returns a boolean stating wether or not the Vectors are close to parallel
	 */
	public static boolean areParallel( Vector2d firstVector, Vector2d secondVector )
	{
		double angleBetween = firstVector.angle( secondVector );

		return angleBetween < .00001 || angleBetween > Math.PI - .00001;
	}

	/**
	 * Parses a Point2D from a string
	 * 
	 * @param stringToParse
	 *            The string to parse
	 * @return Returns the parsed Point2D, or, returns null if there was a parsing error
	 */
	public static Point2D parsePoint2D_Double( String stringToParse )
	{
		StringBuffer stringBuff = new StringBuffer( stringToParse );

		while ( true )
		{
			int indexToDelete = stringBuff.indexOf( " " );
			if ( indexToDelete == -1 )
			{
				break;
			}
			stringBuff.deleteCharAt( indexToDelete );
		}

		int commaIndex = stringBuff.indexOf( "," );

		if ( commaIndex == -1 )
		{
			return null;
		}

		String firstNum = stringBuff.substring( 0, commaIndex );
		String secNum = stringBuff.substring( commaIndex + 1 );

		double num1 = 0, num2 = 0;

		try
		{
			num1 = Double.parseDouble( firstNum );
			num2 = Double.parseDouble( secNum );
		}
		catch ( NumberFormatException nfe )
		{
			return null;
		}
		return new Point2D.Double( num1, num2 );
	}

	/**
	 * Gets the Unit vector in the X direction
	 * 
	 * @return Returns a Unit vector in the X direction
	 */
	public static Vector2d getUnitVectorX()
	{
		return new Vector2d( 1, 0 );
	}

	/**
	 * Gets the Unit vector in the Y direction
	 * 
	 * @return Returns a Unit vector in the Y direction
	 */
	public static Vector2d getUnitVectorY()
	{
		return new Vector2d( 0, 1 );
	}
	
	/**
	 * Gets the Unit vector in the negative X direction
	 * 
	 * @return Returns a Unit vector in the negative X direction
	 */
	public static Vector2d getUnitVectorNegX()
	{
		return new Vector2d( -1, 0 );
	}

	/**
	 * Gets the Unit vector in the negative Y direction
	 * 
	 * @return Returns a Unit vector in the negative Y direction
	 */
	public static Vector2d getUnitVectorNegY()
	{
		return new Vector2d( 0, -1 );
	}

	/**
	 * Gets the Zero vector
	 * 
	 * @return Returns a Zero Vector
	 */
	public static Vector2d getZeroVector()
	{
		return new Vector2d( 0, 0 );
	}

	/**
	 * Multiplies a vector by a scalar
	 * 
	 * @param v1
	 *            The vector
	 * @param scalar
	 *            The scalar
	 * @return The Multiplied vector
	 */
	public static Vector2d VectorMultiplication( Vector2d v1, double scalar )
	{
		return new Vector2d( v1.x * scalar, v1.y * scalar );
	}

	public static Vector2d NegateVector( Vector2d vec )
	{
		Vector2d newVec = (Vector2d)vec.clone();
		newVec.negate();
		return newVec;
	}
	
	/**
	 * Scales a vector by a scalar. Returns a new scalar.
	 * 
	 * @param v1
	 *            The vector
	 * @param scalar
	 *            The scalar
	 * @return A new vector scaled to the appropriate length
	 */
	public static Vector2d VectorScale( Vector2d v1, double scalar )
	{
		Vector2d tempVec = (Vector2d)v1.clone();
		tempVec.scale( scalar );
		return tempVec;
	}

	/**
	 * Divides a vector by a scalar
	 * 
	 * @param v1
	 *            The vector
	 * @param scalar
	 *            The scalar
	 * @return The Divided vector
	 */
	public static Vector2d VectorDivision( Vector2d v1, double scalar )
	{
		return new Vector2d( v1.x / scalar, v1.y / scalar );
	}

	/**
	 * Finds the vector from p1 to p2
	 * 
	 * @param p1
	 *            The vector
	 * @param p2
	 *            The scalar
	 * @return the vector from p1 to p2
	 */
	public static Vector2d VectorFromTo( Point2D p1, Point2D p2 )
	{
		return new Vector2d( p2.getX() - p1.getX(), p2.getY() - p1.getY() );
	}

	/**
	 * Creates a new vector of the same length as the passed vector which is orthogonal (perpindicular) to the given
	 * vector.
	 * 
	 * @param vec
	 *            The vector of which the orthogonal vector will be found
	 * @param direction
	 *            The choice of the direction which the orthogonal vector will point.
	 * @return Returns the orthogonal vector
	 */
	public static Vector2d OrthogonalVector( Vector2d vec, TurnType direction )
	{
		Vector2d newVec = null;
		
		switch ( direction )
		{
			case Left:
			{
				newVec = new Vector2d( -vec.y, vec.x );
				break;
			}
			case Right:
			{
				newVec = new Vector2d( vec.y, -vec.x );
			}
			default:
			case Collinear:
			{
				//Should this throw an Illegal Argument Exception instead?
				//after all, whats' the orthogonal vector in the direct that it's already going?
				newVec = vec;
			}
		}
		
		return newVec;
	}

	/**
	 * Creates a new unit vector which is orthogonal (perpindicular) to the given vector.
	 * 
	 * @param vec
	 *            The vector of which the orthogonal unit vector will be found
	 * @param direction
	 *            The choice of the direction which the orthogonal vector will point.
	 * @return Returns the Orthoginal Unit Vector
	 */
	public static Vector2d OrthogonalUnitVector( Vector2d vec, TurnType direction )
	{
		Vector2d newVec = (Vector2d)vec.clone();
		newVec.normalize();
		return OrthogonalVector( newVec, direction );
	}

	public static TurnType getTurnType( Point2D A, Point2D B, Point2D C )
	{
		double z = ( B.getX() - A.getX() ) * ( C.getY() - A.getY() ) - ( C.getX() - A.getX() ) * ( B.getY() - A.getY() );

		if ( z > 0 )
			return TurnType.Left;
		else if ( z < 0 )
			return TurnType.Right;

		return TurnType.Collinear; // On the same line
	}

	/**
	 * Normalizes a polygon represented as a List of points. Normalization gets the Convex-Hull of the poly as well as
	 * removing duplicate points and points which lie on the same line. O(n log(n)) due to sorting.
	 * 
	 * @param points
	 *            A list of the points for the polygon
	 * @return Returns a list representing the Hull of the Polygon in a clockwise order
	 */
	public static List<Point2D> Normalize( List<Point2D> points )
	{
		if ( points.size() < 3 )
			throw new IllegalArgumentException(
					"Trying to Normalize a set of points where set contains less than 3 points." );

		Point2D A = new Point2D.Double( Double.MAX_VALUE, Double.MAX_VALUE );

		// Ensures that the List is 'set-ified'
		HashSet<Point2D> set = new HashSet<Point2D>( points );
		points = new Vector<Point2D>( set );

		// Find the most upperleft point, with 'leftness' taking priority
		for ( Point2D p : points )
		{
			if ( p.getX() < A.getX() || ( p.getX() == A.getX() && p.getY() < A.getY() ) )
				A = p;
		}

		final Point2D finalA = A;

		Collections.sort( points, new Comparator<Point2D>()
		{
			public int compare( Point2D p, Point2D q )
			{
				// p < q if and only if orientation( a,p,q ) == left

				// Ensure that 'A' is at the top of the list
				if ( p == finalA )
					return -1;
				else if ( q == finalA )
					return 1;

				TurnType t = getTurnType( finalA, p, q );

				switch ( t )
				{
					// Why does eclipse/java not see that it'll alwyas return something if it's an enum and all the
					// possibilities are in the switch/case?
					default:
					case Left:
						return -1;
					case Collinear:
					{
						double pDist = finalA.distance( p );
						double qDist = finalA.distance( q );

						return Double.compare( pDist, qDist ); // Return the one closest to 'A'
						// return 0;
					}
					case Right:
						return 1;
				}
			}
		} );

		// What if some points are collinear to the first point? Maybe change Collinear sorting to also sort to make
		// sure they're in UL order?

		Stack<Point2D> newPoints = new Stack<Point2D>();

		newPoints.push( A );

		Point2D currentPoint = points.get( 1 ); // The second point
		int i = 2;

		while ( i < points.size() )
		{
			TurnType t = getTurnType( newPoints.peek(), currentPoint, points.get( i ) );

			if ( t == TurnType.Right )
			{
				currentPoint = newPoints.pop();
			}
			else
			{
				newPoints.push( currentPoint );
				currentPoint = points.get( i );
				i++;
			}
		}

		if ( getTurnType( newPoints.peek(), currentPoint, A ) == TurnType.Left )
			newPoints.push( currentPoint );

		return newPoints;
	}

}
