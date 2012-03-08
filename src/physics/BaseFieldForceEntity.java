package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import geom.PolygonOrCircle;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * The ForceEntity class creates an entity which produces a "force" which can
 * attract or repel "MovingEntities" and can act uniformly on the playing field
 * or drop off over a distance. The ForceEntity does not move itself.
 * 
 * @author The UC Regents
 * 
 */
public abstract class BaseFieldForceEntity extends BaseEntity implements IForceEntity
{
	private PolygonOrCircle m_ForceShape;

	/**
	 * Constructs a default field force entity at 0,0 with 1 width and 1 height
	 * and 0 magnitude
	 * 
	 */
	public BaseFieldForceEntity()
	{
		this( new PolygonOrCircle( 0, 0, 300, 300 ), false );
	}
	
	@Override
	public void Deserialize( CustomXMLReader reader )
	{
		super.Deserialize( reader );
		
		m_ForceShape = reader.ReadPolygonOrCircle( "ForceShape" );
	}

	@Override
	public void Serialize( CustomXMLWriter writer )
	{
		super.Serialize( writer );
		
		writer.Write( "ForceShape", m_ForceShape );
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
	 */
	public BaseFieldForceEntity( int x_loc, int y_loc, int width, int height )
	{

		this( new PolygonOrCircle( x_loc, y_loc, width, height ), false );
	}

	/**
	 * Constructs a custom field force entity with the given parameters
	 * 
	 * @param forceShape
	 *            Bounding field rectangle
	 * @param isVisible
	 *            Field Visibility
	 */
	public BaseFieldForceEntity( PolygonOrCircle forceShape, boolean isVisible )
	{
		super( new PolygonOrCircle( forceShape.getX(), forceShape.getY(), 25, 25 ) );
		
		this.setVisible( isVisible );

		m_ForceShape = forceShape;
	}
	@Override
	protected String DefaultImageLocation()
	{
		return "placeholder.png";
	}

	/**
	 * Returns if any entity is in contact with the field
	 * 
	 * @param entity
	 *            The entity to check intersection with
	 * @return Returns true if an entity is approx. in contact with the field
	 */
	public boolean IntersectsWith( MovingEntity entity )
	{
		return ( m_ForceShape.getShape().intersects( entity.getShapeFrame() ) && entity.getShape().intersects(
				this.m_ForceShape.getShape().getBounds2D() ) );
		// Ensure that they intersect eachother. Due to the nature of
		// Rectangles, there can be times where one may 'intersect' the other.
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
			g.setColor( getFrameColor() );
			g.draw( m_ForceShape.getShape() );
			// g.draw(m_ForceShape.getShape().getBounds2D());

			g.drawOval( (int)( m_ForceShape.getCenterX() - 2 ), (int)( m_ForceShape.getCenterY() - 2 ), 4, 4 );
		}
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	@Override
	protected void OnLocationChange( double deltaX, double deltaY )
	{
		m_ForceShape.Translate( deltaX, deltaY );
	}

	@Override
	public Object clone()
	{
		BaseFieldForceEntity be;
		try
		{
			be = (BaseFieldForceEntity)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}

		be.m_ForceShape = (PolygonOrCircle)m_ForceShape.clone();
		return be;
	}
	
	@Override
	protected void OnULScale( double s )
	{
		this.m_ForceShape.Scale( s );
	}

	@Override
	protected void OnCenterScale( double s )
	{
		this.m_ForceShape.ScaleAroundCenter( s );
	}

	@Override
	public Rectangle2D getEffectiveBounds()
	{
		return m_ForceShape.GetBoundingBox();
	}
}
