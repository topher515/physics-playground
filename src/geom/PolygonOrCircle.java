package geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Vector;


public class PolygonOrCircle implements Cloneable
{
	private Shape m_Shape;

	private double m_Scale = 1;

	private Point2D m_centroid;

	public PolygonOrCircle()
	{
		this( 0, 0, 5 );
	}

	public PolygonOrCircle( double x, double y, double radius )
	{
		m_Shape = new Circle2d( x, y, radius );
		this.calculateCentroid();
	}

	public PolygonOrCircle( Point2D upperLeft, double radius )
	{
		m_Shape = new Circle2d( upperLeft.getX(), upperLeft.getY(), radius );
		this.calculateCentroid();
	}

	public PolygonOrCircle( double[] xCoords, double[] yCoords, int nPoints )
	{
		m_Shape = new Polygon2D.Double( xCoords, yCoords, nPoints );
		this.calculateCentroid();
	}

	public PolygonOrCircle( double[] xyCoords )
	{
		m_Shape = new Polygon2D.Double( xyCoords );
		this.calculateCentroid();
	}

	public PolygonOrCircle( Collection<? extends Point2D> points )
	{
		double[] xCoords = new double[points.size()];
		double[] yCoords = new double[points.size()];
		/*
		 * for ( int i = 0; i < points.size(); i++ ) { xCoords[i] = points.get( i ).getX(); yCoords[i] = points.get( i
		 * ).getY(); }
		 */
		int i = 0;

		for ( Point2D p : points )
		{
			xCoords[i] = p.getX();
			yCoords[i] = p.getY();

			i++;
		}

		m_Shape = new Polygon2D.Double( xCoords, yCoords, points.size() );
		this.calculateCentroid();
	}

	// Make a Rectangle. Boring.
	public PolygonOrCircle( double x, double y, double width, double height )
	{
		double[] coords = new double[]{ x, y, x + width, y, x + width, y + height, x, y + height };
		m_Shape = new Polygon2D.Double( coords );
		this.calculateCentroid();
	}

	public Shape getShape()
	{
		return m_Shape;
	}

	public double getCenterX()
	{
		return m_Shape.getBounds2D().getCenterX();
	}

	public double getCenterY()
	{
		return m_Shape.getBounds2D().getCenterY();
	}

	public double getX()
	{
		return m_Shape.getBounds2D().getX();
	}

	public double getY()
	{
		return m_Shape.getBounds2D().getY();
	}

	public void Rotate( double degrees )
	{
		RotateRadians( Math.toRadians( degrees ) );
	}

	public void RotateRadians( double radians )
	{
		if ( isCircle() )
			return; // rotating a circle is kinda silly, don't ya think?
		else if ( isPolygon() )
			( (Polygon2D)m_Shape ).transform( AffineTransform.getRotateInstance( radians, m_centroid.getX(), m_centroid.getY() ) );

	}

	public void Translate( double x, double y )
	{
		if ( isCircle() )
		{
			Circle2d c = (Circle2d)m_Shape;

			c.setFrame( c.getX() + x, c.getY() + y, c.getWidth(), c.getHeight() );
		}
		else if ( isPolygon() )
		{
			Polygon2D p = (Polygon2D)m_Shape;
			p.translate( x, y );
		}
		m_centroid.setLocation( m_centroid.getX() + x, m_centroid.getY() + y );
	}

	/**
	 * A fixed ratio scale of the PolygonOrCircle around the UL Point
	 * 
	 * @param s
	 *            The Scalar to scale it by
	 * @return Returns the new UL Point
	 */
	public Point2D Scale( double s )
	{
		if ( s <= 0 )
			return new Point2D.Double( this.getX(), this.getY() );

		double temp = s;
		s = s / m_Scale;

		m_Scale = temp;

		if ( s == 1 )
			return new Point2D.Double( this.getX(), this.getY() );

		if ( isCircle() )
		{
			Circle2d c = (Circle2d)m_Shape;
			c.setFrame( c.getX(), c.getY(), c.getWidth() * s, c.getHeight() * s );
			this.calculateCentroid();
		}
		else if ( isPolygon() )
		{
			Polygon2D p = (Polygon2D)m_Shape;

			double oldULX = this.getX();
			double oldULY = this.getY();

			p.transform( AffineTransform.getScaleInstance( s, s ) );
			this.calculateCentroid();
			return new Point2D.Double( oldULX, oldULY );
		}
		return new Point2D.Double( this.getX(), this.getY() );
	}

	public void ScaleAroundCenter( double s )
	{
		if ( s <= 0 )
			return;

		double temp = s;
		s = s / m_Scale;

		m_Scale = temp;

		if ( s == 1 )
			return;

		if ( isCircle() )
		{
			Circle2d c = (Circle2d)m_Shape;

			double deltaR = c.getRadius() * s;

			c.setFrameFromCenter( c.getCenterX(), c.getCenterY(), c.getCenterX() + deltaR, c.getCenterY() + deltaR );
		}
		else if ( isPolygon() )
		{
			Polygon2D p = (Polygon2D)m_Shape;

			double oldCenterX = this.getCenterX();
			double oldCenterY = this.getCenterY();

			p.transform( AffineTransform.getScaleInstance( s, s ) );

			this.Translate( oldCenterX - this.getCenterX(), oldCenterY - this.getCenterY() );
		}
		this.calculateCentroid();
	}

	/*
	 * public void SetCenterPoint( Point2D p ) { this.SetCenterPoint( p.getX(), p.getY() ); } public void
	 * SetCenterPoint( double x, double y ) { }
	 */

	public Rectangle2D GetBoundingBox()
	{
		return m_Shape.getBounds2D();
	}

	public boolean isCircle()
	{
		return ( m_Shape instanceof Circle2d );
	}

	public boolean isPolygon()
	{
		return ( m_Shape instanceof Polygon2D );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		PolygonOrCircle pc;
		try
		{
			pc = (PolygonOrCircle)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}

		if ( isCircle() )
		{
			Circle2d c = (Circle2d)m_Shape;

			pc.m_Shape = new Circle2d( c.getX(), c.getY(), c.getRadius() );
		}
		else if ( isPolygon() )
		{
			Polygon2D p = (Polygon2D)m_Shape;

			pc.m_Shape = new Polygon2D.Double( p.getCoords() );
		}
		pc.m_centroid = (Point2D)this.m_centroid.clone();
		return pc;
	}

	/**
	 * Calculates the centroid
	 */
	public void calculateCentroid()
	{
		if ( m_Shape instanceof Polygon2D )
		{
			// int pt;
			double second_factor;
			double polygon_area;

			int coordCount = ( (Polygon2D)m_Shape ).getCoordCount();
			List<Point2D> thePoints = new Vector<Point2D>();

			double[] points = ( (Polygon2D)m_Shape ).getCoords();
			double tempx = 0;
			for ( int i = 0; i < coordCount; i++ )
			{
				if ( i % 2 == 1 )
				{
					thePoints.add( new Point2D.Double( tempx, points[i] ) );
					// System.out.println("adding " + new Point2D.Double(tempx,
					// points[i]));
				}
				else
				{
					tempx = points[i];
				}
			}
			thePoints.add( thePoints.get( 0 ) );

			// Find the centroid.
			double xCoord = 0, yCoord = 0;
			Point2D currentPoint;
			Point2D nextPoint;
			for ( int i = 0; i < thePoints.size() - 1; i++ )
			{
				currentPoint = thePoints.get( i );
				nextPoint = thePoints.get( i + 1 );
				second_factor = currentPoint.getX() * nextPoint.getY() - nextPoint.getX() * currentPoint.getY();
				xCoord = xCoord + ( currentPoint.getX() + nextPoint.getX() ) * second_factor;
				yCoord = yCoord + ( currentPoint.getY() + nextPoint.getY() ) * second_factor;
			}

			// Divide by 6 times the polygon's area.
			polygon_area = this.getArea();
			xCoord = ( xCoord / 6 ) / polygon_area;
			yCoord = ( yCoord / 6 ) / polygon_area;

			// If the values are negative, the polygon is
			// oriented counterclockwise. Reverse the signs.
			if ( xCoord < 0 )
			{
				xCoord = -xCoord;
				yCoord = -yCoord;
			}
			// System.out.println("center of mass is " + new
			// Point2D.Double(xCoord, yCoord));
			m_centroid = new Point2D.Double( xCoord, yCoord );
		}
		else
		// it is a circle
		{
			Circle2d circle = (Circle2d)m_Shape;
			m_centroid = new Point2D.Double( circle.getCenterX(), circle.getCenterY() );
		}
	}

	/**
	 * Calculates the area of the polygon
	 * 
	 * @return The Area of the polygon
	 */
	public double getArea()
	{
		double area = 0;

		if ( isPolygon() )
		{
			// Add the first point to the end.
			int coordCount = ( (Polygon2D)m_Shape ).getCoordCount();
			List<Point2D> thePoints = new Vector<Point2D>();

			double[] points = ( (Polygon2D)m_Shape ).getCoords();
			double tempx = 0;
			for ( int i = 0; i < coordCount; i++ )
			{
				if ( i % 2 == 1 )
				{
					thePoints.add( new Point2D.Double( tempx, points[i] ) );

				}
				else
				{
					tempx = points[i];
				}
			}
			thePoints.add( thePoints.get( 0 ) );
			Point2D currentPoint;
			Point2D nextPoint;
			// Get the areas.
			for ( int i = 0; i < thePoints.size() - 1; i++ )
			{
				currentPoint = thePoints.get( i );
				nextPoint = thePoints.get( i + 1 );
				area = area + ( nextPoint.getX() - currentPoint.getX() ) * ( nextPoint.getY() + currentPoint.getY() ) / 2;
			}
			if ( area < 0 )
				area = area * -1;
		}
		else if ( isCircle() )
		{
			// its a circle so pi * r^2
			area = Math.pow( m_Shape.getBounds2D().getWidth() / 2, 2 ) * Math.PI;
		}

		return area;
	}

	/**
	 * Returns the centroid for the polygon
	 * 
	 * @return The centroid of the polygon
	 */
	public Point2D getCentroid()
	{
		return m_centroid;
	}

	public double getInternalScale()
	{
		return m_Scale;
	}

	public void setInternalScale( double scale )
	{
		m_Scale = scale;
	}
}
