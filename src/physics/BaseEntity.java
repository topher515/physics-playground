package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import util.Utility;
import geom.DimensionDouble;
import geom.GeomUtility;
import geom.OdinsCross;
import geom.Polygon2D;
import geom.PolygonOrCircle;
import gui.DrawType;
import gui.events.UserEvent;
import gui.opf.OPFComponentType;
import gui.opf.Setter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Vector2d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The most basic type of Entity. BaseEntity contains the shape, 
 * orientation, image, as well as whether it is visible, 
 * whether it can be collided with, and what its bounciness is.
 */
public abstract class BaseEntity implements Cloneable
{
	private PolygonOrCircle m_Shape;
	
	private double m_Orientation;
	
	private boolean m_isVisible = true;

	private float m_bounciness = 1;
	private boolean m_collidable = true;
	private float m_frictionConstant = 0;
	
	private Point2D m_centerPoint = new Point2D.Double( 0, 0 );

	private List<Vector2d> m_currentAxes;

	private double m_Scale = 1.0;
	private BufferedImage m_Image;
	
	private boolean m_isSelected;
	private Color m_frameColor = Color.BLACK;

	private Color m_shapeColor = Utility.getRandomColor();
	private Color m_GradientColor = Utility.getColorSimilarTo(m_shapeColor);
	private DrawType m_DrawType = DrawType.GradientTopDown;

	private String m_Name;

	private List<UserEvent> m_eventList = new Vector<UserEvent>();

	private Point2D m_ImageOffset = new Point2D.Double( 0, 0 );

	// testing
	public Point2D POC = null;

	public Line2D testLine = null;

	/**
	 * Constructs a default base entity with position 0,0 and 0 height and width
	 */
	public BaseEntity()
	{
		this( new PolygonOrCircle( 0, 0, 0 ) );
	}

	/**
	 * Constructs a BaseEntity with the input shape properties
	 * 
	 * @param shape
	 *            The shape
	 */
	public BaseEntity( PolygonOrCircle shape )
	{
		m_Image = Utility.GetBufferedImage( DefaultImageLocation() );

		m_centerPoint = new Point2D.Double( shape.getCenterX(), shape.getCenterY() );

		// System.out.println("center " + centerPoint);
		m_Shape = shape;
		// this.setOrientation( orientation );
		this.calculateAxes();
	}

	/**
	 * Provides the location of the default image
	 * 
	 * @return Returns the location of the image
	 */
	protected String DefaultImageLocation()
	{
		return Utility.getDefaultImageName();
	}

	/**
	 * Determines the number of vertices in the current polygon
	 * 
	 * @return number of vertices
	 */
	public final int getVertexCount()
	{
		if ( m_Shape.isPolygon() )
		{
			return ( (Polygon2D)m_Shape.getShape() ).getVertexCount();
		}

		PathIterator thePath = m_Shape.getShape().getPathIterator( null );

		int countedVertices = 0;
		double coords[] = new double[6];

		while ( !thePath.isDone() )
		{
			int segmentType = thePath.currentSegment( coords );
			if ( segmentType == PathIterator.SEG_CLOSE )
			{
				break;
			}
			countedVertices++;
			thePath.next();
		}

		return countedVertices;
	}

	/**
	 * Determines the axis (in the form of a unit vector) which is formed by the specified side of the polygon.<br>
	 * Note: this method DOES NOT know if there are other axes parallel to the one it is returning
	 * 
	 * @param whichPolygonSide
	 *            integer referring to the side of the polygon from which the axis is extrapolated
	 * @return the unit vector parallel to the axis
	 */
	public final Vector2d getAxis( int whichPolygonSide )
	{
		List<Vector2d> thisShapesSideVectors = getSideVectors();

		Vector2d tempSideVector = (Vector2d)thisShapesSideVectors.get( whichPolygonSide ).clone();
		tempSideVector.normalize();

		return tempSideVector;
	}

	/**
	 * Recalculates all the axes of the given shape. <br>
	 * Note: this method knows only stores those axes not parallel to one another. (Axes from two parallel sides are the
	 * same value!)
	 */
	public final void calculateAxes()
	{
		List<Vector2d> axesVectors = new Vector<Vector2d>();
		if ( m_Shape.isPolygon() )
		{
			List<Vector2d> thisShapesSideVectors = getSideVectors();
			Vector2d tempSideVector;
			for ( int i = 0; i < thisShapesSideVectors.size(); i++ )
			{
				tempSideVector = (Vector2d)thisShapesSideVectors.get( i ).clone();
				tempSideVector.normalize();

				if ( i > 1 ) // check for parallel sides
				{
					double tempForSwap = tempSideVector.x;// Make vector
					// perpendicular
					tempSideVector.x = -tempSideVector.y;
					tempSideVector.y = tempForSwap;
					for ( int j = 0; j < axesVectors.size(); j++ )
					{
						// don't add it if it's parallel to any other side
						if ( GeomUtility.areParallel( tempSideVector, axesVectors.get( j ) ) )
							break;

						else if ( j == axesVectors.size() - 1 )
						{
							// add if its not parallel to all other axesVecrors
							axesVectors.add( tempSideVector );
							break;
						}
					}
				}
				else
				{
					double tempForSwap = tempSideVector.x;// Make vector
					// perpendicular
					tempSideVector.x = -tempSideVector.y;
					tempSideVector.y = tempForSwap;
					if ( tempSideVector.y < 0 )// if vector is below x axis,
					// mirror across y=-x
					{
						tempSideVector.x = -tempSideVector.x;
						tempSideVector.y = -tempSideVector.y;
					}
					axesVectors.add( tempSideVector );
				}
			}
			m_currentAxes = axesVectors;
			return;
		}

		m_currentAxes = axesVectors;
	}

	/**
	 * Returns a clone of the vector of current axes <br>
	 * 
	 * @return A series of vector values representing all the axes of the polygon of this entity.
	 */
	public final List<Vector2d> getAxesClone()
	{
		if ( m_currentAxes == null )
			calculateAxes();
		List<Vector2d> axesClone = new Vector<Vector2d>();
		for ( Vector2d axis : m_currentAxes )
		{
			axesClone.add( (Vector2d)axis.clone() );
		}
		return axesClone;
	}

	/**
	 * Returns the vector of current axes <br>
	 * 
	 * @return A series of vector values representing all the axes of the polygon of this entity.
	 */
	public final List<Vector2d> getAxes()
	{
		if ( m_currentAxes == null )
			calculateAxes();
		return m_currentAxes;
	}

	/**
	 * Scale the shape by it's upper left point
	 * 
	 * @param s
	 *            The ratio to scale it by
	 */
	public final void Scale( double s )
	{
		// This will invalidate the bounds when it sets the point.
		OnULScale( s );
		this.setULPoint( m_Shape.Scale( s ) );
	}

	/**
	 * Scale the shape around it's center point
	 * 
	 * @param s
	 *            The ratio to scale it by
	 */
	public final void ScaleAroundCenter( double s )
	{
		Rectangle2D oldBounds = this.getActualBounds();

		OnCenterScale( s );
		m_Shape.ScaleAroundCenter( s );

		InvalidateBoundingBox( oldBounds );
	}

	protected void OnULScale( double s )
	{
		// Intended for optional use in classes that extend from this
	}

	protected void OnCenterScale( double s )
	{
		// Intended for optional use in classes that extend from this
	}

	/**
	 * Determines whether another polygon is overlapping another using the seperate axis theorem.
	 * 
	 * @param ent2
	 *            The entity to check this one against
	 * @return True if it is overlapping the other polygon at its current position
	 */
	public Vector2d isOverlapping( BaseEntity ent2 )
	{
		if ( this == ent2 )
			return null;// same object

		// Early Escape. Fast but only narrows it down so much cause it relies on Rectangles.
		if ( !m_Shape.getShape().intersects( ent2.m_Shape.GetBoundingBox() )
				|| !ent2.m_Shape.getShape().intersects( this.m_Shape.GetBoundingBox() ) )
			return null;

		if ( m_Shape.isCircle() && ent2.m_Shape.isCircle() )
			return isOverlappingBothCircles( ent2 );
		if ( m_Shape.isCircle() || ent2.m_Shape.isCircle() )
			return isOverlappingCircle( ent2 );

		double min0 = 0, max0 = 0; // The min and max values that the
		double min1 = 0, max1 = 0; // points had projected onto
		Vector2d vAxis; // this line
		double t; // Counting & temp variables

		// Get vertices
		List<Point2D> ent1Points = this.getVerticesCoordinates();
		List<Point2D> ent2Points = ent2.getVerticesCoordinates();
		// make them vectors
		List<Vector2d> ent1Vectors = new Vector<Vector2d>();
		List<Vector2d> ent2Vectors = new Vector<Vector2d>();

		for ( Point2D currentPoint : ent1Points )
		{
			ent1Vectors.add( new Vector2d( currentPoint.getX(), currentPoint.getY() ) );
		}
		for ( Point2D currentPoint : ent2Points )
		{
			ent2Vectors.add( new Vector2d( currentPoint.getX(), currentPoint.getY() ) );
		}

		List<Vector2d> allAxes = this.getAxesClone();// Add axes from polygon a
		List<Vector2d> ent2Axes = ent2.getAxesClone();// Get axes from polygon b

		int originalSize = allAxes.size();

		for ( int i = 0; i < ent2Axes.size(); i++ )// Add the non-parrallel sides to allVectors
		{
			for ( int j = 0; j < originalSize; j++ )
			{
				if ( GeomUtility.areParallel( ent2Axes.get( i ), allAxes.get( j ) ) )
					break;
				else if ( j == originalSize - 1 )
					allAxes.add( ent2Axes.get( i ) );
			}
		}
		for ( int i = 0; i < allAxes.size(); i++ )
		{
			vAxis = allAxes.get( i ); // The axis that the points will be
			// projected onto

			// Project polygon A
			min0 = vAxis.dot( ent1Vectors.get( 0 ) );
			max0 = min0;
			for ( int j = 1; j < ent1Vectors.size(); j++ )
			{
				t = vAxis.dot( ent1Vectors.get( j ) );
				if ( t < min0 )
					min0 = t;
				if ( t > max0 )
					max0 = t;
			}

			// Project polygon B
			min1 = vAxis.dot( ent2Vectors.get( 0 ) );
			max1 = min1;
			for ( int j = 1; j < ent2Vectors.size(); j++ )
			{
				t = vAxis.dot( ent2Vectors.get( j ) );
				if ( t < min1 )
					min1 = t;
				if ( t > max1 )
					max1 = t;
			}

			// Test for intersections
			double d0 = min0 - max1;
			double d1 = min1 - max0;
			if ( ( d0 >= 0 ) || ( d1 >= 0 ) )// then they cant be touching
			{
				return null;
			}
			if ( d1 < d0 )
			{
				vAxis.scale( -d0 );
			}
			else
			{
				vAxis.scale( d1 );
			}
		}

		// None of the axis's seperated the two polygons, they are indeed colliding
		// This find the least overlapping direction and beems the object back to edge
		// HACK
		if ( allAxes.size() == 0 )
		{
			return null;
		}
		Vector2d toMove = allAxes.get( 0 );
		for ( int i = 1; i < allAxes.size(); i++ )
		{
			if ( allAxes.get( i ).length() < toMove.length() )
				toMove = allAxes.get( i );
		}

		return toMove;
	}

	private Vector2d isOverlappingCircle( BaseEntity ent2 )
	{
		BaseEntity thePolygon;
		BaseEntity theCircle;

		if ( m_Shape.isCircle() )
		{
			theCircle = this;
			thePolygon = ent2;
		}
		else if ( ent2.m_Shape.isCircle() )
		{
			theCircle = ent2;
			thePolygon = this;
		}
		else
		{
			return null; // more than one is a ball
		}

		double min0 = 0, max0 = 0; // The min and max values that the
		double min1 = 0, max1 = 0; // points had projected onto
		Vector2d vAxis; // this line
		Vector2d circleOffset; // Vector representation of sOffset
		double t; // Counting & temp variables

		circleOffset = new Vector2d( theCircle.m_centerPoint.getX(), theCircle.m_centerPoint.getY() );
		// Get vertices
		List<Point2D> polyPoints = thePolygon.getVerticesCoordinates();
		// make points vectors and find closest point to circle
		List<Vector2d> polyVectors = new Vector<Vector2d>();
		Point2D closestPointToCircle = polyPoints.get( 0 );
		double shortestCircleDistance = Math.hypot( closestPointToCircle.getX() - theCircle.m_centerPoint.getX(),
				closestPointToCircle.getY() - theCircle.m_centerPoint.getY() );
		for ( Point2D currentPoint : polyPoints )
		{
			polyVectors.add( new Vector2d( currentPoint.getX(), currentPoint.getY() ) );
			double currentCircleDistance = Math.hypot( currentPoint.getX() - theCircle.m_centerPoint.getX(),
					currentPoint.getY() - theCircle.m_centerPoint.getY() );
			if ( shortestCircleDistance > currentCircleDistance )
			{
				shortestCircleDistance = currentCircleDistance;
				closestPointToCircle = currentPoint;
			}
		}
		double radius = theCircle.getShapeFrame().getWidth() / 2;
		// Get Axes from polygon
		List<Vector2d> allAxes = thePolygon.getAxesClone();
		// Add axis from circle to closest point on polygon
		allAxes.add( new Vector2d( closestPointToCircle.getX() - theCircle.m_centerPoint.getX(), closestPointToCircle
				.getY()
				- theCircle.m_centerPoint.getY() ) );
		allAxes.get( allAxes.size() - 1 ).normalize();// normalize it to make it an axis
		for ( int i = 0; i < allAxes.size(); i++ )
		{
			vAxis = allAxes.get( i ); // The axis that the points will be
			// projected onto
			// Project the circle, always the diameter
			max0 = vAxis.dot( circleOffset ) + radius;
			min0 = max0 - 2 * radius;

			// Project the polygon
			min1 = vAxis.dot( polyVectors.get( 0 ) );
			max1 = min1;
			for ( int j = 1; j < polyVectors.size(); j++ )
			{
				t = vAxis.dot( polyVectors.get( j ) );
				if ( t < min1 )
					min1 = t;
				if ( t > max1 )
					max1 = t;
			}

			// Test for intersections
			double d0 = min0 - max1;
			double d1 = min1 - max0;
			if ( ( d0 >= 0 ) || ( d1 >= 0 ) )
			{
				return null;// then they cant be touching
			}
			if ( d1 < d0 )
			{
				vAxis.scale( -d0 );
			}
			else
			{
				vAxis.scale( d1 );
			}
		}

		// None of the axis's seperated the two polygons, they are indeed
		// colliding
		// This find the least overlapping direction and beems the object back
		// to edge
		// HACK
		if ( allAxes.size() == 0 )
		{
			return null;
		}
		Vector2d toMove = allAxes.get( 0 );
		for ( int i = 1; i < allAxes.size(); i++ )
		{
			if ( allAxes.get( i ).length() < toMove.length() )
				toMove = allAxes.get( i );
		}
		if ( this == thePolygon )
			toMove.scale( -1 );// flip the toMove if this is the circle
		return toMove;

	}

	private Vector2d isOverlappingBothCircles( BaseEntity ent2 )
	{
		double radius1 = this.m_Shape.GetBoundingBox().getWidth() / 2;
		double radius2 = ent2.m_Shape.GetBoundingBox().getWidth() / 2;
		Vector2d vOffset = new Vector2d( this.m_centerPoint.getX() - ent2.m_centerPoint.getX(), this.m_centerPoint
				.getY()
				- ent2.m_centerPoint.getY() );// vector
		// from/to
		// centers
		// of
		// objects
		double distance = vOffset.length();

		if ( distance < radius1 + radius2 )
		{
			vOffset.normalize();
			vOffset.scale( radius1 + radius2 - distance );
			return vOffset;
		}

		return null;
	}
	/**
	 * Gets the center of mass of the shape
	 * 
	 * @return The absolute position center of mass
	 */
	public Point2D getCentroid()
	{
		return m_Shape.getCentroid();
	}
	/**
	 * Draws the entity to the screen
	 * 
	 * @param g
	 *            The Graphics2D object
	 * @param verbose
	 *            a boolean saying the level of Verbosity
	 * 
	 */
	public void Draw( Graphics2D g, boolean verbose )
	{
		if ( !m_isVisible && verbose )
		{
			g.setPaint( Utility.ApplyAlpha( m_shapeColor, 75 ) );

			g.fill( m_Shape.getShape() );

			if ( verbose )
			{
				g.setColor( m_frameColor );
				g.draw( m_Shape.getShape() );
			}
		}
		else if ( m_isVisible )
		{
			AffineTransformOp transformOp = null;
			AffineTransform form = AffineTransform.getRotateInstance( Math.toRadians( m_Orientation ),
					getRelativeCenterPoint().getX() - m_ImageOffset.getX(), getRelativeCenterPoint().getY()
							- m_ImageOffset.getY() );

			form.scale( m_Scale, m_Scale );

			transformOp = new AffineTransformOp( form, null );

			float floatX = (float)this.getULPoint().getX();
			float floatY = (float)this.getULPoint().getY();
			float floatW = (float)this.getActualFrameDimensions().getWidth();
			float floatH = (float)this.getActualFrameDimensions().getHeight();
			switch ( m_DrawType )
			{
				case GradientDiagNWtoSE:
				{
					g.setPaint( new GradientPaint( floatX, floatY, m_shapeColor, floatX + floatW, floatY + floatH,
							m_GradientColor ) );
					break;
				}
				case GradientDiagNEtoSW:
				{
					g.setPaint( new GradientPaint( floatX + floatW, floatY, m_shapeColor, floatX, floatY + floatH,
							m_GradientColor ) );
					break;
				}
				case GradientLeftRight:
				{
					g.setPaint( new GradientPaint( floatX, floatY, m_shapeColor, floatX + floatW, floatY,
							m_GradientColor ) );
					break;
				}
				case GradientTopDown:
				{
					g.setPaint( new GradientPaint( floatX, floatY, m_shapeColor, floatX, floatY + floatH,
							m_GradientColor ) );
					break;
				}
				/*
				 * case GradientRadial: { g.setPaint( new GradientPaint( floatX + floatW/2, floatY + floatH/2,
				 * m_shapeColor, floatX + floatW, floatY + floatH/2, m_GradientColor, true ) ); break; }
				 */
				default:
				case SolidColor:
				{
					g.setPaint( m_shapeColor );
				}
			}

			g.fill( m_Shape.getShape() );

			if ( !DefaultImageLocation().equalsIgnoreCase( "placeholder.png" ) )
			{
				int relX = (int)Math.rint( getULPoint().getX() + m_ImageOffset.getX() * m_Scale );
				int relY = (int)Math.rint( getULPoint().getY() + m_ImageOffset.getY() * m_Scale );
				g.drawImage( m_Image, transformOp, relX, relY );
			}
			if ( verbose )
			{
				g.setPaint( m_frameColor );

				// g.setStroke(new BasicStroke(2));
				g.draw( m_Shape.getShape() );
				
				// draw the centroid
				Point2D centroid = m_Shape.getCentroid();
				OdinsCross.DrawOdinsCross( g, m_Orientation, centroid );
			}
		}

		/*
		// testing 
		if ( POC != null )
			{
			g.drawOval( (int)POC.getX() - 5, (int)POC.getY() - 5, 10, 10 );
			}

		if ( testLine != null )
		{
			g.setStroke( new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL ) );
			g.drawLine( (int)testLine.getX1(), (int)testLine.getY1(), (int)testLine.getX2(),
					(int)testLine.getY2() );
			g.setStroke( new BasicStroke( 1 ) );
		}
		*/
	}

	/**
	 * Physics collisions are already handled, this method is called immediately after each collision to determine any
	 * special behavior of this entity. The other object is given only to determine what special event will happen to
	 * this entity and in most cases should not be modified. OnCollisionWith is called twice with reference to both
	 * objects in opposite order.
	 * 
	 * @param ent2
	 *            The entity that collision has occured with
	 */
	public void OnCollisionWith( BaseEntity ent2 )
	{
		Utility.HandleEvent( UserEvent.OBJECT_COLLIDED, 0, this );
	}

	/**
	 * Gets the dimensions of the Bounding Box
	 * 
	 * @return Returns the Dimensions of the Bounding Box
	 */
	public final Dimension2D getActualFrameDimensions()
	{
		Rectangle2D frame = m_Shape.GetBoundingBox();
		return new DimensionDouble( frame.getWidth(), frame.getHeight() );
	}

	/**
	 * Sets the upperleft point of the entity to the given double accuracy Point2D
	 * 
	 * @param p
	 *            the point to set the upperleft corner of the entity
	 */
	//@Setter
	public final void setULPoint( Point2D p )
	{
		setULPoint( p.getX(), p.getY() );
	}

	/**
	 * Sets the upperleft point of the entity to the given x and y coordinates
	 * 
	 * @param x
	 *            the x coordinate of the point
	 * @param y
	 *            the y coordinate of the point
	 */
	public final void setULPoint( double x, double y )
	{
		Dimension2D dim = getActualFrameDimensions();

		setCenterPoint( x + dim.getWidth() / 2, y + dim.getHeight() / 2 );

	}

	protected void OnLocationChange( double deltaX, double deltaY )
	{
		// Intended for optional use in classes that extend from this
	}

	/**
	 * Sets the center point of the entity to the given point
	 * 
	 * @param p
	 *            the point to set to
	 */
	public final void setCenterPoint( Point2D p )
	{
		setCenterPoint( p.getX(), p.getY() );
	}

	/**
	 * Sets the center point of the entity to the given x and y coordinates
	 * 
	 * @param x
	 *            the x coordinate of the point
	 * @param y
	 *            the y coordinate of the point
	 */
	public final void setCenterPoint( double x, double y )
	{
		Rectangle2D oldBounds = this.getActualBounds();

		m_centerPoint.setLocation( x, y );

		double deltaX = x - m_Shape.getCenterX();
		double deltaY = y - m_Shape.getCenterY();
		m_Shape.Translate( deltaX, deltaY );

		OnLocationChange( deltaX, deltaY );

		InvalidateBoundingBox( oldBounds );
	}

	/**
	 * Accessor for the current upper left point
	 * 
	 * @return Returns the upper left position of the entity
	 */
	public final Point2D getULPoint()
	{
		Dimension2D dim = getActualFrameDimensions();
		double width = dim.getWidth();
		double height = dim.getHeight();

		return new Point2D.Double( m_centerPoint.getX() - width / 2, m_centerPoint.getY() - height / 2 );
	}

	/**
	 * Accessor for the current center point
	 * 
	 * @return Returns the center of the entity
	 */
	public final Point2D getCenterPoint()
	{
		return m_centerPoint;
	}

	// Returns the center points position relative to the upper left point
	public final Point2D getRelativeCenterPoint()
	{
		return new Point2D.Double( ( m_centerPoint.getX() - this.getULPoint().getX() ), ( m_centerPoint.getY() - this
				.getULPoint().getY() ) );
	}

	/**
	 * Determines the coordinates of the Vertices of the shape of this entity. (With respect to the grid coordinates the
	 * entity was created in)
	 * 
	 * @return a list of Points corresponding to the location of the vertices of the shape. returns a 0 length List if
	 *         it's a circle.
	 */
	public final List<Point2D> getVerticesCoordinates()
	{

		List<Point2D> cList = new Vector<Point2D>();

		if ( m_Shape.isPolygon() )
		{
			Polygon2D polygon2D = (Polygon2D)m_Shape.getShape();
			double[] polyCoords = polygon2D.getCoords();

			for ( int i = 0; i < polyCoords.length; i += 2 )
			{
				cList.add( new Point2D.Double( polyCoords[i], polyCoords[i + 1] ) );
			}

			return cList;
		}

		return new Vector<Point2D>();
	}

	/**
	 * Determines the vectors which connect the vertices of the entity's shape (the sides of the shape).
	 * 
	 * @return a series of Vectors corresponding to the sides of the shape
	 */
	public final List<Vector2d> getSideVectors()
	{
		List<Vector2d> sideVecs = new Vector<Vector2d>();
		List<Point2D> vertices = getVerticesCoordinates();

		for ( int i = 0; i < vertices.size(); i++ )
		{
			if ( vertices.get( i ) == vertices.get( vertices.size() - 1 ) )
			{
				sideVecs.add( new Vector2d( vertices.get( 0 ).getX() - vertices.get( i ).getX(), vertices.get( 0 )
						.getY()
						- vertices.get( i ).getY() ) );
			}
			else
			{
				sideVecs.add( new Vector2d( vertices.get( i + 1 ).getX() - vertices.get( i ).getX(), vertices.get(
						i + 1 ).getY()
						- vertices.get( i ).getY() ) );
			}
		}

		return sideVecs;
	}

	/**
	 * Determines whether or not this entity can be collided with by other entities.
	 * 
	 * @return true if can be collided with
	 */
	public boolean isCollidable()
	{
		return m_collidable;
	}

	/**
	 * Sets whether the entity can collide with other entities
	 * 
	 * @param collide
	 *            True if it can collide with other entities
	 */
	@Setter
	public void setCollidable( boolean collide )
	{
		m_collidable = collide;
	}

	/**
	 * Determines whether or not this entity can be seen by the user.
	 * 
	 * @return true is can be seen with
	 */
	public final boolean isVisible()
	{
		return m_isVisible;
	}

	/**
	 * Mutator for the entity's visibility
	 * 
	 * @param isVisible
	 *            Returns if the entity is currently visible or not
	 */

	@Setter
	public final void setVisible( boolean isVisible )
	{
		this.m_isVisible = isVisible;
	}

	/**
	 * Gets the name set by the user for the entity
	 * 
	 * @return The name of the entity
	 */
	public final String getName()
	{
		return m_Name;
	}

	/**
	 * Sets the name of the entity
	 * 
	 * @param name
	 *            name of the entity
	 */
	@Setter
	public final void setName( String name )
	{
		m_Name = name;
	}

	/**
	 * Accessor for the entity's current orientation
	 * 
	 * @return The entity's current orientation.
	 */
	public final double getOrientation()
	{
		return m_Orientation;
	}

	/**
	 * Mutator for the entity's orientation
	 * 
	 * @param orientation
	 *            The orientation to be set
	 */
	@Setter
	public final void setOrientation( double orientation )
	{
		Rectangle2D oldBounds = this.getActualBounds();
		double delta = orientation - m_Orientation;
		m_Shape.Rotate( delta );
		OnOrientationChange( delta );

		if ( delta != 0 )
			this.calculateAxes();

		this.m_Orientation = orientation;

		InvalidateBoundingBox( oldBounds );
	}

	protected void OnOrientationChange( double deltaTheta )
	{
		// Intended for optional use in classes that extend from this
	}

	/**
	 * Accessor for the entity's shape
	 * 
	 * @return Returns the entity's shape
	 */
	public final Shape getShape()
	{
		return m_Shape.getShape();
	}

	/**
	 * Returns the outer bounding box of the entity
	 * 
	 * @return A rectangle2d that encloses the object
	 */
	public final Rectangle2D getShapeFrame()
	{
		return m_Shape.GetBoundingBox();
	}

	/**
	 * Accessor for the entity's bounciness
	 * 
	 * @return The entity's current bounciness
	 */
	public final float getBounciness()
	{
		if ( m_bounciness < 0 || m_bounciness > 1 )
			return 1;
		return m_bounciness;
	}

	/**
	 * Mutator for the entitys current bounciness
	 * 
	 * @param bounciness
	 *            The entity's new bounciness from 0-1
	 */
	@Setter( componentType = OPFComponentType.SliderZeroToOne )
	public final void setBounciness( float bounciness )
	{
		this.m_bounciness = bounciness;
	}

	/**
	 * Set if this entity has been selected by the user
	 * 
	 * @param b
	 *            the isSelected value
	 */
	public final void setSelected( boolean b )
	{
		m_isSelected = b;
	}

	/**
	 * Gets if this entity is selected
	 * 
	 * @return if this is selected
	 */
	public final boolean isSelected()
	{
		return m_isSelected;
	}

	/**
	 * Sets the shape's current fill color
	 * 
	 * @param c
	 *            The new fill color of the shape
	 */
	@Setter
	public final void setShapeColor( Color c )
	{
		this.m_shapeColor = c;
	}

	/**
	 * Gets the shape's current fill color
	 * 
	 * @return Returns the shape's primary color
	 */
	public final Color getShapeColor()
	{
		return m_shapeColor;
	}
	

	/**
	 * Sets the entity's current gradient color
	 * 
	 * @param c
	 *            The new gradient color
	 */
	@Setter
	public final void setGradientColor( Color c )
	{
		this.m_GradientColor = c;
	}

	/**
	 * Gets the entity's current graient color
	 * 
	 * @return Returns the sharpe's gradient color
	 */
	public final Color getGradientColor()
	{
		return m_GradientColor;
	}

	/**
	 * Sets the shape's current frame color
	 * 
	 * @param c
	 *            The new frame color
	 */
	public final void setFrameColor( Color c )
	{
		this.m_frameColor = c;
	}

	/**
	 * Gets the shape's current frame color
	 * 
	 * @return The frame color
	 */
	public final Color getFrameColor()
	{
		return m_frameColor;
	}

	/**
	 * Resets the frame color to black
	 */
	public final void resetFrameColor()
	{
		m_frameColor = Color.BLACK;
	}

	@Override
	/**
	 * Returns the name of the entity, or the class name if it's null
	 * 
	 * @return the name of the entity, or the class name if it's null.
	 */
	public String toString()
	{
		String className = this.getClass().getSimpleName();

		if ( m_Name == null || m_Name.length() <= 0 )
			return className;

		return m_Name + " <" + className + ">";
	}

	/**
	 * Tells whether the shape is a circle
	 * 
	 * @return If the shape is a circle
	 */
	public final boolean isCircle()
	{
		return m_Shape.isCircle();
	}

	/**
	 * Tells whether the shape is a polygon
	 * 
	 * @return If the shape is a polygon
	 */
	public final boolean isPolygon()
	{
		return m_Shape.isPolygon();
	}

	public final Node toXMLElement()
	{
		Document outputDoc;
		Element root;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			outputDoc = db.newDocument();

			root = outputDoc.createElement( "Entity" );

			CustomXMLWriter writer = new CustomXMLWriter( outputDoc, root );

			writer.Write( "Class", this.getClass() );
			
			Serialize( writer );

			return root;
		}
		catch ( Exception e )
		{
			return null;
		}

	}

	public void Serialize( CustomXMLWriter writer )
	{
		writer.Write( "Position", m_centerPoint );
		writer.Write( "Shape", m_Shape );
		writer.Write( "Visible", this.isVisible() );
		writer.Write( "Collidable", this.isCollidable() );
		writer.Write( "Orientation", this.getOrientation() );
		writer.Write( "Bounciness", this.getBounciness() );
		writer.Write( "Name", this.getName() );
		writer.Write( "FrictionConstant", this.getFrictionConstant() );

		writer.Write( "ShapeColor", this.getShapeColor() );
		writer.Write( "GradientColor", this.getGradientColor() );
		
		writer.Write( "ImageOffset", this.getImageOffset() );

		writer.Write( "Scale", this.getScale() );

		writer.Write( "DrawType", this.getDrawType() );
	}

	public void Deserialize( CustomXMLReader reader )
	{
		m_centerPoint = reader.ReadPoint2D( "Position" );
		m_Shape = reader.ReadPolygonOrCircle( "Shape" );
		setVisible( reader.ReadBoolean( "Visible" ) );
		setCollidable( reader.ReadBoolean( "Collidable" ) );
		setOrientation( reader.ReadDouble( "Orientation" ) );
		setBounciness( reader.ReadFloat( "Bounciness" ) );
		setName( reader.ReadString( "Name" ) );
		setFrictionConstant( reader.ReadFloat( "FrictionConstant" ) );

		setShapeColor( reader.ReadColor( "ShapeColor" ) );
		setGradientColor( reader.ReadColor( "GradientColor" ) );
		
		setImageOffset( reader.ReadPoint2D( "ImageOffset" ) );

		m_Scale = reader.ReadDouble( "Scale" );

		setDrawType( reader.ReadEnum( DrawType.class, "DrawType" ) );
		
		this.calculateAxes();
	}

	@Override
	public Object clone()
	{
		BaseEntity be;
		try
		{
			be = (BaseEntity)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}
		be.m_Shape = (PolygonOrCircle)m_Shape.clone();
		be.m_centerPoint = (Point2D)m_centerPoint.clone();
		be.m_eventList = new Vector<UserEvent>();
		be.m_currentAxes = new Vector<Vector2d>();
		for ( Vector2d axis : this.m_currentAxes )
		{
			be.m_currentAxes.add( (Vector2d)axis.clone() );
		}
		for ( UserEvent ue : this.m_eventList )
		{
			be.m_eventList.add( ue.cloneTo( be ) );
		}

		be.setSelected( false );
		be.m_Displayer = null; // Don't save the displayer when cloning;
		return be;
	}

	/**
	 * Gets the image offset
	 * 
	 * @return the image offset
	 */
	protected final Point2D getImageOffset()
	{
		return m_ImageOffset;
	}

	/**
	 * Sets the image offset
	 * 
	 * @param imageOffset
	 */
	protected final void setImageOffset( Point2D imageOffset )
	{
		m_ImageOffset = imageOffset;
	}

	/**
	 * Gets the user events
	 * 
	 * @return the vector of user events
	 */
	public List<UserEvent> getEvents()
	{
		return m_eventList;
	}

	/**
	 * Gets the friction constant
	 * 
	 * @return the friction constant
	 */
	public final float getFrictionConstant()
	{
		return m_frictionConstant;
	}

	/**
	 * Sets the friction constant
	 * 
	 * @param friction
	 *            the new friction constant
	 */
	@Setter( getter = "getFrictionConstant", componentType = OPFComponentType.SliderZeroToOne )
	public final void setFrictionConstant( float friction )
	{
		this.m_frictionConstant = friction;
	}

	/**
	 * Gets the current scale
	 * 
	 * @return the current scale of the entity
	 */
	public final double getScale()
	{
		return m_Scale;
	}

	/**
	 * Sets the scale of the entity
	 * 
	 * @param scale
	 *            the new scale of the entity
	 */
	public final void setScale( double scale )
	{
		if ( scale <= 0 )
			return;

		this.Scale( scale );
		m_Scale = scale;
	}

	/**
	 * Gets the effective bounds of the object for Quadtree purposes and resizing purposes.
	 * 
	 * @return Gets the effective bounds. For most entities, this is usually just the bounding box of the shape
	 */
	public Rectangle2D getEffectiveBounds()
	{
		return this.getActualBounds();
	}

	public final Rectangle2D getActualBounds()
	{
		return m_Shape.GetBoundingBox();
	}

	public void RegisterEvent( int eventType, int eventSubType, Method m, Object[] params )
	{
		m_eventList.add( new UserEvent( eventType, eventSubType, m, params, this ) );
	}

	public void RegisterEvent( UserEvent u )
	{
		m_eventList.add( u );
	}

	public void RegisterEvents( List<UserEvent> ue )
	{
		List<UserEvent> newUE = new Vector<UserEvent>( ue );

		m_eventList = newUE;
	}

	public void RemoveEvent( UserEvent u )
	{
		m_eventList.remove( u );
	}

	public void RemoveEvents( List<UserEvent> ue )
	{
		ue.clear();
	}

	public final List<UserEvent> getUserEvents()
	{
		return m_eventList;
	}


	public final DrawType getDrawType()
	{
		return m_DrawType;
	}

	@Setter
	public final void setDrawType( DrawType drawType )
	{
		m_DrawType = drawType;
	}

	/**
	 * Gets the Shape's (PolygonOrCircle) Area and adjusts it for use in Mass.
	 * 
	 * @return Returns the Area of the PolygonOrCircle / 100
	 */
	public double getShapeMass()
	{
		return m_Shape.getArea() / 100;
	}

	/**
	 * Gets the Shape's (PolygonOrCircle) Rounded Area and adjusts it for the mass
	 * 
	 * @return Returns the The Rounded Area of the PolygonOrCircle / 100
	 */
	public double getRoundedShapeMass()
	{
		return ( (int)Math.rint( getShapeMass() * 100 ) ) / 100D;
	}
	
	private BaseDisplayPanel m_Displayer;

	public final void setDisplayer( BaseDisplayPanel p )
	{
		if ( m_Displayer != null && p != null )
	{
			System.err
					.println( "Error: trying to set the displayer for an Entity that already has a displayer.  Displayer not set. ("
							+ this.toString() + ")" );
			return;
		}

		m_Displayer = p;
	}

	public final BaseDisplayPanel getDisplayer()
	{
		return m_Displayer;
	}

	public void QueueDeletion()
	{
		if ( m_Displayer != null )
		{
			m_Displayer.UnregisterEntity( this );
		}
	}

	public void OnDelete()
	{
		//Utility.HandleEvent( UserEvent.OBJECT_DESTROYED, 0, this );
		// Intended for overriding. Unless, do we need something happening for events & Deletion?
	}
	
	public final void InvalidateBoundingBox( Rectangle2D oldBounds )
	{
		if ( m_Displayer != null )
		{
			m_Displayer.EntityInvalidation( this, oldBounds );
			// First remove 'this' from the old tree within the old bounds, then, add in the new bounds.
		}
	}
}
