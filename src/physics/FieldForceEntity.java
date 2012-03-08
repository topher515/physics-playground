package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import geom.PolygonOrCircle;
import gui.opf.Setter;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.vecmath.Vector2d;

/**
 * The ForceEntity class creates an entity which produces a "force" which can
 * attract or repel "MovingEntities" and can act uniformly on the playing field
 * or drop off over a distance. The ForceEntity does not move itself.
 * 
 * @author The UC Regents
 * 
 */
public class FieldForceEntity extends BaseFieldForceEntity
{
	
	@Override
	public void Deserialize( CustomXMLReader reader )
	{
		super.Deserialize( reader );
		
		m_Force = reader.ReadVector2d( "Force" );
	}

	@Override
	public void Serialize( CustomXMLWriter writer )
	{
		super.Serialize( writer );
		
		writer.Write( "Force", m_Force );
	}

	private Vector2d m_Force; // negative values for repulsion

	/**
	 * Constructs a default field force entity at 0,0 with 1 width and 1 height
	 * and 0 magnitude
	 * 
	 */
	public FieldForceEntity()
	{
		this( new PolygonOrCircle( 0, 0, 300, 300 ), false, new Vector2d( 0, 3 ) );
	}

	/**
	 * Constructs a custom field force entity with the given parameters
	 * 
	 * @param x_loc
	 *            X location
	 * @param y_loc
	 *            Y location
	 * @param width
	 *            Field width
	 * @param height
	 *            Field height
	 * @param force
	 *            A vector2d of the force
	 */
	public FieldForceEntity( int x_loc, int y_loc, int width, int height, Vector2d force )
	{

		this( new PolygonOrCircle( x_loc, y_loc, width, height ), false, force );
	}

	/**
	 * Constructs a custom field force entity with the given parameters
	 * 
	 * @param forceShape
	 *            Bounding field rectangle
	 * @param isVisible
	 *            Field Visibility
	 * @param force
	 *            Vector2d of the force
	 */
	public FieldForceEntity( PolygonOrCircle forceShape, boolean isVisible, Vector2d force  )
	{
		super( forceShape, isVisible );
		this.m_Force = force;
	}

	/**
	 * Draws the field force
	 * 
	 * @param g
	 *            The graphics2d to be drawn
	 */
	@Override
	public void Draw( Graphics2D g, boolean verbose )
	{
		super.Draw( g, verbose );

		if ( verbose )
		{
			g.setColor( Color.BLACK );
			g.drawString( "Field Force (DirectionVector: " + (int)m_Force.x + "," + (int)m_Force.y + ")", (int)getULPoint().getX() + 75,
					(int)getULPoint().getY() + 15 );

		}
	}
	
	/**
	 * Calculats the force to act on another entity
	 * 
	 * @param be
	 *            The base entity to get a force for
	 */
	public Vector2d getForceFor( MovingEntity be )
	{
		return m_Force;// Fields return a constant force for all entities
	}

	@Override
	/**
	 * Makes a clone of the entity in its current state
	 * @return A clone of the entity
	 */
	public Object clone()
	{
		FieldForceEntity be;
		try
		{
			be = (FieldForceEntity)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}

		be.m_Force = (Vector2d)m_Force.clone();
		return be;
	}

	/**
	 * Sets the x component of the force field
	 * @param f The new x component of the force field
	 */
	@Setter
	public void setXComponent( double f )
	{
		this.m_Force.x = f;
	}
	/**
	 * Gets the x component of the force field
	 * @return The x component of the force field
	 */
	public double getXComponent()
	{
		return this.m_Force.x;
	}
	/**
	 * Sets the y component of the force field
	 * @param f The new y component of the force field
	 */
	@Setter
	public void setYComponent( double f )
	{
		this.m_Force.y = f;
	}
	/**
	 * Gets the y component of the force field
	 * @return The y component of the force field
	 */
	public double getYComponent()
	{
		return this.m_Force.y;
	}
}
