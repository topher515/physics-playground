package serialization;

import geom.Circle2d;
import geom.Polygon2D;
import geom.PolygonOrCircle;

import java.awt.Color;
import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CustomXMLWriter
{
	Document m_OutputDoc;

	Element m_Root;

	public CustomXMLWriter( Document outputDoc, Element root )
	{
		m_OutputDoc = outputDoc;
		m_Root = root;
	}

	public void Write( String name, Vector2d v2d )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element tempElement = m_OutputDoc.createElement( name );

		tempElement.setAttribute( "dX", String.valueOf( v2d.x ) );
		tempElement.setAttribute( "dY", String.valueOf( v2d.y ) );

		m_Root.appendChild( tempElement );
	}

	public void Write( String name, Point2D p2d )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element tempElement = m_OutputDoc.createElement( name );

		tempElement.setAttribute( "X", String.valueOf( p2d.getX() ) );
		tempElement.setAttribute( "Y", String.valueOf( p2d.getY() ) );

		m_Root.appendChild( tempElement );
	}

	public void Write( String name, PolygonOrCircle shape )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element tempElement = m_OutputDoc.createElement( name );

		if ( shape != null )
		{

			tempElement.setAttribute( "IsCircle", String.valueOf( shape.isCircle() ) );
			tempElement.setAttribute( "IsPolygon", String.valueOf( shape.isPolygon() ) );
			tempElement.setAttribute( "InternalScale", String.valueOf( shape.getInternalScale() ) );

			if ( shape.isCircle() )
			{
				tempElement.setAttribute( "Radius", String.valueOf( ( (Circle2d)shape.getShape() ).getRadius() ) );

				tempElement.setAttribute( "X", String.valueOf( shape.getX() ) );
				tempElement.setAttribute( "Y", String.valueOf( shape.getY() ) );
			}
			else
			{
				double[] points = ( (Polygon2D)shape.getShape() ).getCoords();

				StringBuilder sb = new StringBuilder();

				sb.append( points[0] );
				for ( int i = 1; i < points.length; i++ )
				{
					double d = points[i];

					sb.append( "," );
					sb.append( d );
				}

				tempElement.setAttribute( "Points", String.valueOf( sb.toString() ) );
			}
		}

		m_Root.appendChild( tempElement );
	}

	public void Write( String name, Color c )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element tempElement = m_OutputDoc.createElement( name );

		tempElement.setAttribute( "R", String.valueOf( c.getRed() ) );
		tempElement.setAttribute( "G", String.valueOf( c.getGreen() ) );
		tempElement.setAttribute( "B", String.valueOf( c.getBlue() ) );
		tempElement.setAttribute( "A", String.valueOf( c.getAlpha() ) );

		m_Root.appendChild( tempElement );
	}

	public void Write( String name, Enum e )
	{
		Write( name, String.valueOf( e ) );
	}

	public void Write( String name, Class c )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		m_Root.setAttribute( "Class", c.getCanonicalName() );

	}

	/*
	 * public void Write( String name, Object o ) { Write( name, String.valueOf( o ) ); }
	 */
	public void Write( String name, String s )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		// m_Root.setAttribute( name, String.valueOf( s ) );

		Element tempElement = m_OutputDoc.createElement( name );

		tempElement.setTextContent( s );

		m_Root.appendChild( tempElement );

		/*
		 * Element tempElement = outputDoc.createElement( name ); tempElement.setTextContent( String.valueOf( s ) ); //Node value =
		 * outputDoc.createTextNode( String.valueOf( s ) ); //tempElement.appendChild( value ); root.appendChild( tempElement );
		 */
	}

	public void Write( String name, double d )
	{
		Write( name, String.valueOf( d ) );
	}

	public void Write( String name, boolean b )
	{
		Write( name, String.valueOf( b ) );
	}
	
	public void Write( String name, byte b )
	{
		Write( name, String.valueOf( b ) );
	}

	public void Write( String name, char c )
	{
		Write( name, String.valueOf( c ) );
	}

	public void Write( String name, float f )
	{
		Write( name, String.valueOf( f ) );
	}

	public void Write( String name, int i )
	{
		Write( name, String.valueOf( i ) );
	}

	public void Write( String name, long l )
	{
		Write( name, String.valueOf( l ) );
	}
}
