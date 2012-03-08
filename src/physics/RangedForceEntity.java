package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import geom.GeomUtility;
import geom.PolygonOrCircle;
import gui.opf.Setter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Vector2d;

/**
 * Ranged Force Entity is a force entity that has a force that pulls toward a point in space.
 * 
 * @author The UC Regents
 */
public class RangedForceEntity extends MovingEntity implements IForceEntity
{

	private double m_attractionStrength;

	private PolygonOrCircle m_ForceShape;

	@Override
	public void Deserialize( CustomXMLReader reader )
	{
		super.Deserialize( reader );

		m_attractionStrength = reader.ReadDouble( "AttractionStrength" );
		m_ForceShape = reader.ReadPolygonOrCircle( "ForceShape" );
	}

	@Override
	public void Serialize( CustomXMLWriter writer )
	{
		super.Serialize( writer );

		writer.Write( "AttractionStrength", m_attractionStrength );
		writer.Write( "ForceShape", m_ForceShape );
	}

	/**
	 * The ranged force entity is a moving entity with the additional attribute of attraction strength
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param width
	 *            The width of the entity
	 * @param height
	 *            The height of the entity
	 * @param attractionStrength
	 *            The attraction strength of the force
	 */
	public RangedForceEntity( double x, double y, double width, double height, double attractionStrength )
	{
		this( x, y, width, height, new Vector2d( 0, 0 ), 1, attractionStrength );
	}

	/**
	 * The ranged force entity is a moving entity with the additional attribute of attraction strength
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param width
	 *            The width of the entity
	 * @param height
	 *            The height of the entity
	 * @param inputVelocity
	 *            The initial velocity of the entity
	 * @param inputMass
	 *            The mass of the entity
	 * @param attractionStrength
	 *            The attraction strenght of the entity
	 */
	public RangedForceEntity( double x, double y, double width, double height, Vector2d inputVelocity, int inputMass,
			double attractionStrength )
	{

		super( x, y, 25, 25, inputVelocity, inputMass );

		m_attractionStrength = attractionStrength;

		m_ForceShape = new PolygonOrCircle( x, y, width, height );
		this.setVisible( false );
		// m_ForceShape = new Ellipse2D.Double(x, y, width, height); //Default
		// to Ellipse
	}

	/**
	 * Constructs a new ranged force entity
	 * 
	 * @param forceShape
	 *            The shape of the force
	 * @param attractionStrength
	 *            The attration strength of the force
	 */
	public RangedForceEntity( PolygonOrCircle forceShape, double attractionStrength )
	{
		super( forceShape.getX(), forceShape.getY(), 25, 25, 0, 0 );

		m_attractionStrength = attractionStrength;

		m_ForceShape = forceShape;
		this.setVisible( false );
	}

	/**
	 * Constructs a new ranged force entity
	 * 
	 * @param forceShape
	 *            The shape of the force
	 * @param entityShape
	 *            The shape of the entity, ie a planet
	 * @param attractionStrength
	 *            The attration strength of the force
	 */
	public RangedForceEntity( PolygonOrCircle forceShape, PolygonOrCircle entityShape, double attractionStrength )
	{
		super( entityShape );

		m_attractionStrength = attractionStrength;

		m_ForceShape = forceShape;
		this.setVisible( false );
	}

	public RangedForceEntity()
	{
		this( 0, 0, 600, 400, 1000 );
	}

	/**
	 * Determines if it intersects with another entity
	 * 
	 * @param entity
	 *            Uses the bounding boxes to determine if another entity is intersecting it
	 */
	public boolean IntersectsWith( MovingEntity entity )
	{
		if ( entity == this )
			return false;

		if ( m_ForceShape != null )
		{

			return ( m_ForceShape.getShape().intersects( entity.getShapeFrame() ) && entity.getShape().intersects(
					this.m_ForceShape.getShape().getBounds2D() ) );
		}
		else
		{
			return ( this.getShape().intersects( entity.getShapeFrame() ) && entity.getShape().intersects(
					this.getShape().getBounds2D() ) );
		}
		// Ensure that they intersect eachother. Due to the nature of
		// Rectangles, there can be times where one may 'intersect' the other.
	}

	@Override
	public String DefaultImageLocation()
	{
		return "placeholder.png";
	}

	@Override
	public void Draw( Graphics2D g, boolean verbose )
	{
		super.Draw( g, verbose );

		if ( verbose )
		{

			g.setColor( Color.BLACK );
			g.drawString( "Ranged Force (Strength: " + (int)m_attractionStrength + ")", (int)getULPoint().getX() + 75,
					(int)getULPoint().getY() + 15 );

			g.setColor( getFrameColor() );

			if ( m_ForceShape != null )
			{
				g.drawOval( (int)( m_ForceShape.getCenterX() - 2 ), (int)( m_ForceShape.getCenterY() - 2 ), 4, 4 );
				g.draw( m_ForceShape.getShape() );

				if ( this.isSelected() )
					g.draw( m_ForceShape.getShape().getBounds2D() );
			}
		}
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}

	/**
	 * Gets the force vector to act on another entity
	 * 
	 * @param be
	 *            The entity to get a force for
	 * @return The vector to apply on an entity
	 */
	public Vector2d getForceFor( MovingEntity be )
	{
		Vector2d v, v2;

		Point2D bePoint = be.getCenterPoint();

		Point2D thisPoint;

		if ( m_ForceShape != null )
			thisPoint = new Point2D.Double( m_ForceShape.getCenterX(), m_ForceShape.getCenterY() );
		else
			thisPoint = this.getCenterPoint();

		v2 = new Vector2d( thisPoint.getX() - bePoint.getX(), thisPoint.getY() - bePoint.getY() );
		v2.normalize();

		double distSquared = thisPoint.distanceSq( bePoint );

		v = GeomUtility.VectorDivision( v2, distSquared / m_attractionStrength );

		return v;
	}

	@Override
	protected void OnLocationChange( double deltaX, double deltaY )
	{
		if ( m_ForceShape != null )
			m_ForceShape.Translate( deltaX, deltaY );
	}

	/**
	 * Sets the attraction force
	 * 
	 * @param f
	 *            The new attraction force
	 */
	@Setter
	public void setAttraction( double f )
	{
		this.m_attractionStrength = f;
	}

	/**
	 * Gets the attraction force
	 * 
	 * @return The current attraction force
	 */
	public double getAttraction()
	{
		return this.m_attractionStrength;
	}

	/**
	 * Clones the entity in its current state
	 * 
	 * @return A copy of the entity
	 */
	@Override
	public Object clone()
	{
		RangedForceEntity be;

		try
		{
			be = (RangedForceEntity)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}

		if ( m_ForceShape != null )
			be.m_ForceShape = (PolygonOrCircle)m_ForceShape.clone();
		else
			be.m_ForceShape = null;

		return be;
	}

	protected PolygonOrCircle getForceShape()
	{
		return m_ForceShape;
	}

	protected void setForceShape( PolygonOrCircle forceShape )
	{
		m_ForceShape = forceShape;
	}

	@Override
	protected void OnULScale( double s )
	{
		if ( m_ForceShape != null )
			this.m_ForceShape.Scale( s );
	}

	@Override
	protected void OnCenterScale( double s )
	{
		if ( m_ForceShape != null )
			this.m_ForceShape.ScaleAroundCenter( s );
	}

	@Override
	public Rectangle2D getEffectiveBounds()
	{
		if ( m_ForceShape != null )
			return m_ForceShape.GetBoundingBox();
		else
			return super.getEffectiveBounds();
	}
}
