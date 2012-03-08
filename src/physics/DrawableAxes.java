package physics;

import util.Utility;
import geom.GeomUtility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Vector2d;

/**
 * This class is used for demonstration purposes to draw the axis and project all the polygons in the arena on to them. While drawing, efficiency is
 * highly decreased
 */
public class DrawableAxes
{
	Point2D m_Origin;

	List<Vector2d> allAxes = new Vector<Vector2d>();

	public DrawableAxes( Point2D origin )
	{
		m_Origin = origin;
		// Random gen= new Random();
	}

	public DrawableAxes( int x_origin, int y_origin )
	{
		this( new Point2D.Double( x_origin, y_origin ) );
	}

	public void Draw( Graphics2D g, List<BaseEntity> entitiesToDrawAxes )
	{
		allAxes.clear();

		for ( BaseEntity b : entitiesToDrawAxes )
		{
			if ( !b.isPolygon() || !b.isCollidable() )
				continue;
			
			List<Vector2d> shapeAxes = b.getAxes();
			boolean isParallel = false;
			for ( Vector2d Axis1 : shapeAxes )// Add the non-parrallel sides to allVectors
			{
				isParallel = false;
				for ( Vector2d Axis2 : allAxes )
				{
					if ( GeomUtility.areParallel( Axis1, Axis2 ) )
					{
						isParallel = true;
						break;
					}
				}
				if ( !isParallel )
					allAxes.add( Axis1 );
			}
		}

		for ( int k = 0; k < entitiesToDrawAxes.size(); k++ )
		{
			BaseEntity b = entitiesToDrawAxes.get( k );
			if ( !b.isPolygon() || !b.isCollidable() )
				continue;

			g.setColor( Color.BLACK );

			Vector2d tempVec;
			int AXES_X = (int)m_Origin.getX();
			int AXES_Y = (int)m_Origin.getY();
			// Point2D.Double AXES_POINT = new Point2D.Double(AXES_X, AXES_Y);
			List<Point2D> vertices = b.getVerticesCoordinates();
			int closestVertex = 0;
			double closestVertexDist = 200000;
			// System.out.println("Startcalcing distances:");
			for ( int i = 0; i < vertices.size(); i++ )
			{
				double distance = vertices.get( i ).distance( new Point2D.Double( AXES_X, AXES_Y ) );
				// System.out.println( "Previous closest: " + closestVertexDist +
				// " this distance: " + distance);
				if ( closestVertexDist > distance )
				{
					closestVertex = i;
					closestVertexDist = distance;
				}
			}

			for ( int i = 0; i < allAxes.size(); i++ )
			{

				tempVec = GeomUtility.VectorScale( allAxes.get( i ), 600 );

				g.drawLine( AXES_X, AXES_Y, AXES_X + (int)tempVec.x, AXES_Y + (int)tempVec.y );
				g.drawLine( AXES_X, AXES_Y, AXES_X - (int)tempVec.x, AXES_Y - (int)tempVec.y );

			}

			Vector2d vecDist = new Vector2d( vertices.get( closestVertex ).getX() - AXES_X, vertices.get( closestVertex ).getY() - AXES_Y );
			for ( int i = 0; i < allAxes.size(); i++ )
			{

				// I think this is an estimate
				List<Vector2d> sideVecs = b.getSideVectors();
				double projectionLength = 0;
				for ( int j = 0; j < sideVecs.size(); j++ )
				{
					projectionLength += Math.abs( sideVecs.get( j ).dot( allAxes.get( i ) ) );
					// System.out.println(projectionLength);
				}
				projectionLength /= 2;

				double lengthToStartProjection = vecDist.dot( allAxes.get( i ) );
				if ( lengthToStartProjection < 0 )
					lengthToStartProjection -= projectionLength;
				Color shapeColor = b.getShapeColor();
				g.setColor( Utility.ApplyAlpha( shapeColor, 175 ) );

				g.setStroke( new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL ) );
				
				Vector2d positioningVec = GeomUtility.VectorScale( allAxes.get( i ), lengthToStartProjection );
				Vector2d projectionVec = GeomUtility.VectorScale( allAxes.get( i ), projectionLength );

				g.drawLine( (int)( positioningVec.x + AXES_X ), (int)( positioningVec.y + AXES_Y ),
						(int)( positioningVec.x + AXES_X + projectionVec.x ), (int)( positioningVec.y + AXES_Y + projectionVec.y ) );

			}

			g.setStroke( new BasicStroke( 1 ) );
		}
	}

	/**
	 * Moves the center of all of the axes around in the editor and arena
	 * 
	 * @param x_origin The new x coordinate for the origin of the axes
	 * @param y_origin The new y coordinate for the origin of the axes
	 */
	public void setAxesOrigin( int x_origin, int y_origin )
	{
		m_Origin = new Point2D.Double( x_origin, y_origin );
	}

	/**
	 * Moves the center of all of the axes around in the editor and arena
	 * 
	 * @param newOrigin The new origin for the axes
	 */
	public void setAxesOrigin( Point2D newOrigin )
	{
		m_Origin = newOrigin;
	}

	/**
	 * Gets the current origin of the axes
	 * 
	 * @return The current origin of the axes
	 */
	public Point2D getAxesOrigin()
	{
		return (Point2D)m_Origin.clone();
	}

}
