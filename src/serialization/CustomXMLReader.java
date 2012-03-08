package serialization;

import geom.GeomUtility;
import geom.PolygonOrCircle;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;

import javax.vecmath.Vector2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a Reader for simplified reading from an XML document
 */
public class CustomXMLReader
{
	Document m_OutputDoc;

	Element m_Root;

	NodeList m_EntityProperties;

	public CustomXMLReader( Document outputDoc, Element root )
	{
		m_OutputDoc = outputDoc;
		m_Root = root;
		m_EntityProperties = m_Root.getChildNodes();
	}

	public Vector2d ReadVector2d( String name )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element curNode = (Element)m_Root.getElementsByTagName( name ).item( 0 );

		if ( curNode == null || !curNode.hasAttributes() )
		{
			return GeomUtility.getZeroVector();
		}

		double x, y;

		x = Double.parseDouble( curNode.getAttribute( "dX" ) );
		y = Double.parseDouble( curNode.getAttribute( "dY" ) );

		return new Vector2d( x, y );

	}

	public PolygonOrCircle ReadPolygonOrCircle( String name )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element curNode = (Element)m_Root.getElementsByTagName( name ).item( 0 );

		if ( curNode == null || !curNode.hasAttributes() )
		{
			// return new PolygonOrCircle();
			return null;
		}

		boolean isCircle, isPolygon;
		isCircle = Boolean.parseBoolean( curNode.getAttribute( "IsCircle" ) );
		isPolygon = Boolean.parseBoolean( curNode.getAttribute( "IsPolygon" ) );

		PolygonOrCircle poc = new PolygonOrCircle();

		if ( isCircle )
		{
			double x, y, radius;
			x = Double.parseDouble( curNode.getAttribute( "X" ) );
			y = Double.parseDouble( curNode.getAttribute( "Y" ) );
			radius = Double.parseDouble( curNode.getAttribute( "Radius" ) );

			poc = new PolygonOrCircle( x, y, radius );
		}
		else if ( isPolygon )
		{
			double[] xyCoords;

			StringTokenizer st = new StringTokenizer( curNode.getAttribute( "Points" ), "," );

			xyCoords = new double[st.countTokens()];

			int i = 0;
			while ( st.hasMoreTokens() )
			{
				xyCoords[i] = Double.parseDouble( st.nextToken() );

				i++;
			}

			poc = new PolygonOrCircle( xyCoords );
		}

		poc.setInternalScale( Double.parseDouble( curNode.getAttribute( "InternalScale" ) ) );

		return poc;
	}

	public Point2D ReadPoint2D( String name )
	{
		// Point2D temp = new Point2D.Double();

		if ( name == null )
			throw new IllegalArgumentException();

		Element curNode = (Element)m_Root.getElementsByTagName( name ).item( 0 );

		if ( curNode == null || !curNode.hasAttributes() )
		{
			return new Point2D.Double( 0, 0 );
		}

		double x = 0, y = 0;

		x = Double.parseDouble( curNode.getAttribute( "X" ) );
		y = Double.parseDouble( curNode.getAttribute( "Y" ) );
		// temp.x = x;
		// temp.y = y;

		// temp.set
		return new Point2D.Double( x, y );

	}

	public double ReadDouble( String name )
	{
		try
		{
			return Double.parseDouble( ReadString( name ) );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading 'double' value from '" + name + "'" );
			return 0;
		}
	}

	public int ReadInteger( String name )
	{
		try
		{
			return Integer.parseInt( ReadString( name ) );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading 'int' value from '" + name + "'" );
			return 0;
		}
	}

	public float ReadFloat( String name )
	{
		try
		{
			return Float.parseFloat( ReadString( name ) );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading 'float' value from '" + name + "'" );
			return 0;
		}
	}

	public Color ReadColor( String name )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Element curNode = (Element)m_Root.getElementsByTagName( name ).item( 0 );

		if ( curNode == null || !curNode.hasAttributes() )
		{
			System.err.println( "Error reading 'Color' value from '" + name + "'" );
			return new Color( 0, 0, 0 );
		}

		int R, G, B, A;

		R = Integer.parseInt( curNode.getAttribute( "R" ) );
		G = Integer.parseInt( curNode.getAttribute( "G" ) );
		B = Integer.parseInt( curNode.getAttribute( "B" ) );
		A = Integer.parseInt( curNode.getAttribute( "A" ) );

		return new Color( R, G, B, A );

	}

	public <T extends Enum<T>> T ReadEnum( Class<T> enumType, String name )
	{
		try
		{
			return Enum.valueOf( enumType, ReadString( name ) );
		}
		catch ( IllegalArgumentException e )
		{
			System.err.println( "Error reading 'Enum' value from '" + name + "' with Enum type of " + enumType );
			return null;
		}
	}

	public <T> Class<? extends T> ReadClass( Class<T> baseClass, String name )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		try
		{
			Class<?> c = Class.forName( m_Root.getAttribute( "Class" ) );

			Class<? extends T> typeClass = c.asSubclass( baseClass );

			return typeClass;
		}
		catch ( ClassNotFoundException e )
		{
			System.err.println( "Class '" + name + "' not found." );
		}
		catch ( ClassCastException e )
		{
			System.err.println( "Class '" + name + "' is not a subclass of '" + baseClass + "'" );
		}

		return null;
	}

	/*
	 * public Object ReadObject( String name ) { if ( name == null ) throw new IllegalArgumentException(); Node curNode = m_Root.getElementsByTagName(
	 * name ).item( 0 ); if ( curNode == null || curNode.getNodeValue() == null ) { return new Object(); } return curNode.getNodeValue(); }
	 */
	public String ReadString( String name )
	{
		if ( name == null )
			throw new IllegalArgumentException();

		Node curNode = m_Root.getElementsByTagName( name ).item( 0 );

		if ( curNode == null )
		{
			System.err.println( "Node not found: " + name );
			return null;
		}

		return curNode.getTextContent();
	}

	public boolean ReadBoolean( String name )
	{
		return Boolean.parseBoolean( ReadString( name ) );
	}
	
	public byte ReadByte( String name )
	{
		return Byte.parseByte( ReadString( name ) );
	}

	public char ReadChar( String name )
	{
		String s = ReadString( name );

		if ( s == null || s.length() <= 0 )
			return '\u0000'; // Null character, ie, default char value

		return s.charAt( 0 );

	}

	public long ReadLong( String name )
	{
		try
		{
			return Long.parseLong( ReadString( name ) );
		}
		catch ( Exception e )
		{
			System.err.println( "Error reading 'long' value from '" + name + "'" );
			return 0;
		}
	}
}
