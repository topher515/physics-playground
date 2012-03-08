package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import util.Utility;
import geom.Arrow;
import geom.GeomUtility;
import geom.PolygonOrCircle;
import gui.EditorPanel;
import gui.opf.OPFComponentType;
import gui.opf.Setter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import javax.vecmath.Vector2d;

/**
 * This class extends base entity by added properties that allow the object to function in a 2d physics environment
 * 
 * @author The UC Regents
 */
public class MovingEntity extends BaseEntity
{

	private Vector2d m_currentVelocity = GeomUtility.getZeroVector();

	private double m_angularVelocity = 0;

	private double m_mass = 1;

	private boolean m_isStationary;

	private boolean m_isUnstoppableForce = false;

	/**
	 * The default constructor uses no parameters
	 */
	public MovingEntity()
	{
		super();
	}

	/**
	 * Constructor that accepts position and velocity vectors
	 * 
	 * @param p The UL point of the entity
	 * @param v The velocity vector
	 */
	public MovingEntity( Point2D p, Vector2d v )
	{
		this( p.getX(), p.getY(), 30, 30, v, 1 );
	}

	/**
	 * Constructs a moving entity with 30 width and 30 height, mass 1 and no friction
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param dX The initial X velocity
	 * @param dY The initial Y velocity
	 */
	public MovingEntity( double x, double y, double dX, double dY )
	{
		this( x, y, 30, 30, new Vector2d( dX, dY ), 1 );

	}

	public MovingEntity( double x, double y, double width, double height, double dX, double dY )
	{
		this( x, y, width, height, new Vector2d( dX, dY ), 1 );

	}

	/**
	 * The main constructor for Moving entity, gives the entity the following properties
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param width The width of the entity
	 * @param height The height of the entity
	 * @param initialVelocity The initial velocity of the entity
	 * @param inputMass The mass of the entity
	 */
	public MovingEntity( double x, double y, double width, double height, Vector2d initialVelocity, float inputMass )
	{
		this( new PolygonOrCircle( x, y, width, height ), initialVelocity, inputMass );
	}

	public MovingEntity( PolygonOrCircle shape, Color color )
	{
		this( shape, new Vector2d( 0, 0 ), 1, color );
	}
	
	public MovingEntity( PolygonOrCircle shape )
	{
		this( shape, new Vector2d( 0, 0 ), 1 );
	}

	/**
	 * The main constructor for Moving entity, gives the entity the following properties
	 * 
	 * @param shape The Shape of the entity
	 * @param initialVelocity The initial velocity of the entity
	 * @param inputMass The mass of the entity
	 */
	public MovingEntity( PolygonOrCircle shape, Vector2d initialVelocity, double inputMass )

	{
		super( shape );

		m_mass = inputMass;

		m_currentVelocity = initialVelocity;
	}
	
	public MovingEntity( PolygonOrCircle shape, Vector2d initialVelocity, double inputMass, Color color )

	{
		super( shape );

		m_mass = inputMass;

		m_currentVelocity = initialVelocity;
		
		setShapeColor(color);
		setGradientColor(color);
	}

	@Override
	protected String DefaultImageLocation()
	{
		return "placeHolder.png";
	}

	/**
	 * Draws the entity
	 */
	@Override
	public void Draw( Graphics2D g2d, boolean verbose )
	{
		super.Draw( g2d, verbose );

		if ( verbose )
		{
			int cX = (int)Math.rint(this.getCentroid().getX());
			int cY = (int)Math.rint(this.getCentroid().getY());
			// Entity's velocity intervals
			int vX = (int)Math.rint(getCurrentVelocity().x);
			int vY = (int)Math.rint(getCurrentVelocity().y);

			g2d.setColor( Color.ORANGE );
			
			g2d.setStroke( new BasicStroke( EditorPanel.VEL_DRAW_STROKE ) );
			if ( !m_isStationary )
			{
				Arrow.drawArrow( g2d, cX, cY, cX + vX / EditorPanel.VEL_DRAW_SCALE, cY + vY / EditorPanel.VEL_DRAW_SCALE );
			}
			
			g2d.setStroke( new BasicStroke( 1 ) );
			g2d.setColor( Color.BLACK );

			if ( this.isStationary() )
			{

				AffineTransformOp transformOp = null;
				transformOp = new AffineTransformOp( new AffineTransform(), null );
				g2d.drawImage( Utility.GetBufferedImage( "lock_20x25.png" ), transformOp, (int)getCenterPoint().getX(), (int)getCenterPoint().getY() );

			}
		}
	}

	/**
	 * Uses standard billard physics to change the velocities of the entities after the collision has occured.
	 * 
	 * @param obj2 The moving entity that the calling entity has collided with
	 */
	public void collide( MovingEntity obj2 )
	{
		if ( this.isStationary() || m_isUnstoppableForce == true )
			return;// leave if this is stationary or user controlled

		if ( obj2.isStationary() || obj2.m_isUnstoppableForce == true )
		{
			this.collide( (BaseEntity)obj2 );// treat it as a base entity if
			// its stationary
			return;
		}
		

		Vector2d n = this.isOverlapping( obj2 );
		n.normalize();
		// Find the length of the component of each of the movement
		// vectors along n.
		// a1 = v1 . n
		// a2 = v2 . n

		double a1 = getCurrentVelocity().dot( n );
		double a2 = obj2.getCurrentVelocity().dot( n );
		// Using the optimized version,
		// optimizedP = 2(a1 - a2)
		// -----------
		// m1 + m2
		double optimizedP = ( 2.0 * ( a1 - a2 ) ) / ( this.getMass() + obj2.getMass() );

		// Calculate v1', the new movement vector of circle1
		// v1' = v1 - optimizedP * m2 * n

		Vector2d v1Prime = this.getCurrentVelocity();
		v1Prime.sub( GeomUtility.VectorMultiplication( n, optimizedP * obj2.m_mass ) );

		// now v1' = v1 - optimizedP * m2 * n
		// Calculate v2', the new movement vector of circle2
		// v2' = v2 + optimizedP * m1 * n
		Vector2d v2Prime = obj2.getCurrentVelocity();
		v2Prime.add( GeomUtility.VectorMultiplication( n, optimizedP * this.m_mass ) );
		// Start with bounciness and friction
		Vector2d v1PrimeDir1 = GeomUtility.VectorMultiplication( n, v1Prime.dot( n ) );
		Vector2d v2PrimeDir1 = GeomUtility.VectorMultiplication( n, v2Prime.dot( n ) );
		double tempForSwap = n.x;// Make vector perpendicular
		n.x = -n.y;
		n.y = tempForSwap;
		Vector2d v1PrimeDir2 = GeomUtility.VectorMultiplication( n, v1Prime.dot( n ) );
		Vector2d v2PrimeDir2 = GeomUtility.VectorMultiplication( n, v2Prime.dot( n ) );
		
		double avgBounciness = ( this.getBounciness() + obj2.getBounciness() ) / 2;
		
		float greaterFriction;
		if ( this.getFrictionConstant() > obj2.getFrictionConstant() )
		{
			greaterFriction = this.getFrictionConstant();
		}
		else
		{
			greaterFriction = obj2.getFrictionConstant();
		}
		
		v1PrimeDir1.scale( avgBounciness );
		v2PrimeDir1.scale( avgBounciness );
		v1PrimeDir2.scale( 1 - greaterFriction );
		v2PrimeDir2.scale( 1 - greaterFriction );

		v1Prime = new Vector2d( v1PrimeDir1.x + v1PrimeDir2.x, v1PrimeDir1.y + v1PrimeDir2.y );
		v2Prime = new Vector2d( v2PrimeDir1.x + v2PrimeDir2.x, v2PrimeDir1.y + v2PrimeDir2.y );
		
		this.setCurrentVelocity( v1Prime );
		obj2.setCurrentVelocity( v2Prime );
		
		//System.out.println( this.toString() + " "+ this.getCurrentVelocity().toString() + " " + obj2.getCurrentVelocity());

		this.OnCollisionWith( obj2 );
		obj2.OnCollisionWith( this );
	}

	/**
	 * Uses standard billard physics to change the velocities of the moving after the collision has occured. The base entity will remain a base entity
	 * and not gain any momemtum.
	 * 
	 * @param obj2 The base entity that the calling entity has collided with
	 */
	public void collide( BaseEntity obj2 )
	{
		Vector2d n = this.isOverlapping( obj2 );
		n.normalize();
		// Find the length of the component of each of the movement
		// vectors along n.
		Vector2d dir1 = GeomUtility.VectorMultiplication( n, getCurrentVelocity().dot( n ) );
		double tempForSwap = n.x;// Make vector perpendicular
		n.x = -n.y;
		n.y = tempForSwap;
		Vector2d dir2 = GeomUtility.VectorMultiplication( n, getCurrentVelocity().dot( n ) );

		double avgBounciness = ( this.getBounciness() + obj2.getBounciness() ) / 2;
		dir1.scale( avgBounciness );
		dir1.scale( -1 );// reflect it back
		float greaterFriction;
		if ( this.getFrictionConstant() > obj2.getFrictionConstant() )
		{
			greaterFriction = this.getFrictionConstant();
		}
		else
		{
			greaterFriction = obj2.getFrictionConstant();
		}
		dir2.scale( 1 - greaterFriction );// scale it by the greater friction
		Vector2d v1Prime = new Vector2d( dir1.x + dir2.x, dir1.y + dir2.y );
		setCurrentVelocity( v1Prime );
		this.OnCollisionWith( obj2 );
		obj2.OnCollisionWith( this );
	}

	// Start Accessor/Mutators/
	/**
	 * Accessor for the current velocity of the entity
	 * 
	 * @return The entity's current velocity
	 */
	public Vector2d getCurrentVelocity()
	{
		if ( m_currentVelocity == null )
			return GeomUtility.getZeroVector();

		return (Vector2d)m_currentVelocity.clone();
	}

	/**
	 * Increases the velocity by the given value
	 * 
	 * @param velocity The vector to increase the velocity by
	 */
	@Setter(componentType = OPFComponentType.XYTextField, showInOPF = false, eventType="Movement")
	public void increaseVelocity( Vector2d velocity )
	{
		this.m_currentVelocity.x += velocity.x;
		this.m_currentVelocity.y += velocity.y;
	}

	/**
	 * Increases the velocity in the x direction
	 * 
	 * @param x The x component to increase by
	 */
	@Setter(showInOPF = false, eventType="Movement")
	public void increaseXVelocity( double x )
	{
		this.m_currentVelocity.x += x;
	}

	/**
	 * Increases the velocity in the y direction
	 * 
	 * @param y The y component to increase by
	 */
	@Setter(showInOPF = false, eventType="Movement")
	public void increaseYVelocity( double y )
	{
		this.m_currentVelocity.y += y;
	}

	/**
	 * Mutator for the current velocity of the entity
	 * 
	 * @param velocity The new velocity of the entity
	 */
	@Setter( componentType = OPFComponentType.XYTextField, eventType="Movement" )
	public void setCurrentVelocity( Vector2d velocity )
	{
		this.m_currentVelocity = velocity;
	}

	/**
	 * Returns the velocity as measured in current tick times, not seconds
	 * 
	 * @return The Velocity vector per tick
	 */
	public Vector2d getCurrentTickVelocity()
	{
		if ( m_currentVelocity == null )
			return GeomUtility.getZeroVector();

		return GeomUtility.VectorMultiplication( m_currentVelocity, ( Arena.getPeriodinNs() / 1000000000 ) );
	}

	/**
	 * Sets the current velocity by being given a tick velocity
	 * 
	 * @param input The Velocity vector per tick
	 */
	public void setCurrentTickVelocity( Vector2d input )
	{
		m_currentVelocity = GeomUtility.VectorMultiplication( input, ( 1000000000 / Arena.getPeriodinNs() ) );
	}

	/**
	 * Accessor method for the entity's mass
	 * 
	 * @return The entity's mass
	 */
	public double getMass()
	{
		if ( m_mass <= 0 )
		{
			System.err.println( "Trying to get a mass that's <= 0.  Returning 1." );
			return 1;
		}

		return m_mass;
	}

	/**
	 * Mutator for the entity's mass
	 * 
	 * @param newMass The entities new mass value
	 */
	@Setter
	public void setMass( double newMass )
	{
		this.m_mass = newMass;
	}

	/**
	 * This moves the entity a fraction of its velocity according to the current tick period length.
	 */
	public void moveCurrentVelocity()
	{
		Vector2d toMove;
		toMove = this.getCurrentTickVelocity();

		if ( ( toMove.length() == 0 ) || ( m_isStationary ) )
			return;

		this.setULPoint( new Point2D.Double( getULPoint().getX() + toMove.x, getULPoint().getY() + toMove.y ) );
	}

	/**
	 * Moves the entity a vector, there is no overlap checking with other entities
	 * 
	 * @param toMove the vector to move the entity
	 */
	@Setter( getter = "", componentType = OPFComponentType.XYTextField, showInOPF = false )
	public void moveVector( Vector2d toMove )
	{
		if ( ( toMove.length() == 0 ) || ( m_isStationary ) )
			return;

		this.setULPoint( new Point2D.Double( getULPoint().getX() + toMove.x, getULPoint().getY() + toMove.y ) );
	}
	/**
	 * Moves the entity in the direction of its orientation the distance of the parameter.
	 * 
	 * @param distance The length to move
	 */
	@Setter( eventType = "Movement", showInOPF = false )
	public void moveAheadOnce(double distance)
	{
		Vector2d moveVec = new Vector2d( Math.cos( Math.toRadians( this.getOrientation() ) ), Math.sin( Math.toRadians( this.getOrientation() ) ) );
		
		moveVec.normalize();
		moveVec.scale(distance);
		moveVector(moveVec);
	}
	
	/**
	 * Moves the entity in the direction of its orientation the distance of the parameter.
	 * 
	 * @param distance The length to move
	 */
	@Setter( eventType = "Movement", showInOPF = false )
	public void moveAhead(double distance)
	{
		Vector2d moveVec = new Vector2d( Math.cos( Math.toRadians( this.getOrientation() ) ), Math.sin( Math.toRadians( this.getOrientation() ) ) );
		
		moveVec.normalize();
		moveVec.scale(distance);
		setCurrentVelocity(moveVec);
	}
	/**
	 * Gets the angular Velocity
	 * 
	 * @return Theangular velocity
	 */
	public double getAngularVelocity()
	{
		return m_angularVelocity;
	}

	/**
	 * Sets the angular velocity
	 * 
	 * @param angularVel the new angular velocity
	 */
	@Setter( eventType = "Movement", getter = "getAngularVelocity" )
	public void setAngularVelocity( double angularVel )
	{
		this.m_angularVelocity = angularVel;
	}

	/**
	 * Gets whether the entity is stationary or not
	 * 
	 * @return If it is stationary
	 */
	public final boolean isStationary()
	{
		return m_isStationary;
	}
	
	

	/**
	 * Sets whether the entity is stationary or not
	 * 
	 * @param stationary If it is stationary
	 */
	@Setter
	public final void setStationary( boolean stationary )
	{
		this.m_isStationary = stationary;
	}

	@Override
	public void Serialize( CustomXMLWriter writer )
	{
		super.Serialize( writer );

		writer.Write( "Velocity", this.getCurrentVelocity() );
		writer.Write( "Mass", this.getMass() );
		writer.Write( "IsUnstoppableForce", this.isUnstoppableForce() );
		writer.Write( "IsStationary", this.isStationary() );

	}

	@Override
	public void Deserialize( CustomXMLReader reader )
	{
		super.Deserialize( reader );

		this.setCurrentVelocity( reader.ReadVector2d( "Velocity" ) );
		this.setMass( reader.ReadDouble( "Mass" ) );
		this.setUnstoppableForce( reader.ReadBoolean( "IsUnstoppableForce" ) );
		this.setStationary( reader.ReadBoolean( "IsStationary" ) );

	}

	/**
	 * Tells whether the entity is user an Unstoppable Force. The entity will act like an "unstoppable force" and acts as a moving stationary object
	 * would
	 * 
	 * @return If the entity is user controlled
	 */
	public boolean isUnstoppableForce()
	{
		return m_isUnstoppableForce;
	}

	/**
	 * Sets if the entity is an Unstoppable Force
	 * 
	 * @param uf Sets if the entity is an Unstoppable Force
	 */
	@Setter
	public void setUnstoppableForce( boolean uf )
	{
		m_isUnstoppableForce = uf;
	}
	

	@Override
	public Object clone()
	{
		MovingEntity me;
		try
		{
			me = (MovingEntity)super.clone();
		}
		catch ( Exception e ) // Should n'er happen
		{
			System.err.println( "Bad clone" );
			return null;
		}
		
		me.m_currentVelocity = (Vector2d)m_currentVelocity.clone();
		
		return me;
	}
}
