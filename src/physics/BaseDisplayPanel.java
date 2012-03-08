package physics;

import SpecialCollections.quadtree.Quadtree;
import gui.opf.Setter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

public class BaseDisplayPanel extends JPanel implements KeyListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<BaseEntity> m_Entities = new Vector<BaseEntity>();

	private DrawableAxes drawableAxes = new DrawableAxes( 20, 450 );

	private Queue<BaseEntity> m_NewEntityQueue = new ConcurrentLinkedQueue<BaseEntity>();

	private Queue<BaseEntity> m_EntityDeletionQueue = new ConcurrentLinkedQueue<BaseEntity>();

	private Quadtree<BaseEntity> m_QuadTree = new Quadtree<BaseEntity>();

	private boolean m_Drawing, m_DrawAxes, m_Verbose;

	private float zoomLevel = 1;

	private static boolean implementedPhys_Rotation = false;

	public static boolean isRotationPerformed()
	{
		return implementedPhys_Rotation;
	}

	public static void setRotationPerformed(boolean choice)
	{
		implementedPhys_Rotation = choice;
	}

	public BaseDisplayPanel( Dimension d )
	{
		super();

		this.setPreferredSize( d );
	}

	private boolean toPopulate = false;

	public synchronized void repopulate()
	{
		depopulate();
		toPopulate = true;

		// repaint();
	}

	public synchronized void depopulate()
	{

		for ( BaseEntity b : m_Entities )
		{
			// System.out.println("thing to delete: " + b);
			this.m_EntityDeletionQueue.add( b );
		}

	}

	protected void Populate()
	{
		// Intended for optional use in classes that extend from this
	}

	// Processes the add/delete queues while drawing isn't taking place
	public synchronized void ProcessQueues()
	{
		boolean changedStuff = false;

		while ( !m_EntityDeletionQueue.isEmpty() && !m_Drawing )
		{
			InternalEntityUnregistration( m_EntityDeletionQueue.poll() );
			changedStuff = true;
		}

		if ( toPopulate )
		{
			toPopulate = false;
			Populate();
		}

		while ( !m_NewEntityQueue.isEmpty() && !m_Drawing )
		{
			InternalEntityRegistration( m_NewEntityQueue.poll() );
			changedStuff = true;
		}

		if ( changedStuff )
		{
			repaint();
		}
	}

	public List<BaseEntity> GetEntitiesIn( Rectangle2D bounds )
	{
		return m_QuadTree.query( bounds );
	}

	/**
	 * Returns a list of entities within a the range of the parameter entity
	 * 
	 * @param entity
	 *            The entity to be checked
	 * @param range
	 *            Range to check othe entities for
	 * @return A list of base entities in the range given
	 */
	public List<BaseEntity> GetEntitiesInRange( BaseEntity entity, int range )
	{
		return GetEntitiesInRange( entity.getCenterPoint(), range );
	}

	/**
	 * Returns a vector of entities within a the range of the parameter point
	 * 
	 * @param p
	 *            The point to be used as the center
	 * @param range
	 *            Range to check othe entities for
	 * @return A list of base entities in the range given
	 */
	public List<BaseEntity> GetEntitiesInRange( Point2D p, int range )
	{
		List<BaseEntity> list = GetEntitiesIn( new Rectangle2D.Double( p.getX() - range, p.getY() - range, p.getX()
				+ range, p.getY() + range ) );

		List<BaseEntity> v = new Vector<BaseEntity>();

		for ( BaseEntity entity : list )
		{
			if ( entity.getCenterPoint().distance( p ) <= range )
				v.add( entity );
		}

		return v;
	}

	/**
	 * Returns all of the entities currently in the Panel
	 * 
	 * @return A vector of all of the base entities
	 */
	public List<BaseEntity> getEntities()
	{
		return new Vector<BaseEntity>( m_Entities );
		// return (List<BaseEntity>)m_Entities.clone();
		// return m_Entities;
	}

	/**
	 * Returns a copy of the entities currently in the Panel
	 * 
	 * @return A vector of all of the base entities
	 */
	public List<BaseEntity> getClonedEntities()
	{

		List<BaseEntity> temp = new Vector<BaseEntity>( m_Entities.size() );

		for ( BaseEntity entity : m_Entities )
		{
			temp.add( (BaseEntity)entity.clone() );
		}

		return temp;
	}

	public void setAxesOrigin( Point2D newOrigin )
	{
		drawableAxes.setAxesOrigin( newOrigin );
	}

	public Point2D getAxesOrigin()
	{
		return drawableAxes.getAxesOrigin();
	}

	/**
	 * Determines if any party of 1 entity intersects with any other
	 * 
	 * @param be
	 *            The entity to check if it intersects others with
	 * @return If the entity intersects with other entities
	 */
	protected boolean IntersectsWithAnything( BaseEntity be )
	{
		if ( !be.isCollidable() )
			return false;

		for ( BaseEntity entity : GetEntitiesIn(be.getActualBounds()) )
		{
			if ( !entity.isCollidable() || be == entity )
				continue;

			if ( be.isOverlapping( entity ) != null )
				return true;
		}

		return false;
	}

	/**
	 * Registers an entity with the Panel, the entity will be added after every other entity has been moved and drawn.
	 * 
	 * @param addEntity
	 *            The Entity to add to the Panel
	 */
	public void RegisterEntity( BaseEntity addEntity )
	{

		if ( IntersectsWithAnything( addEntity ) )// Checks if it will overlap a collidable entity
		{
			System.out.println( "Caution: Two entities are overlapping" );
			addEntity.setFrameColor( Color.red );
		}

		m_NewEntityQueue.add( addEntity );
	}

	public void UnregisterEntity( BaseEntity entity )
	{
		m_EntityDeletionQueue.add( entity );
	}

	public final void RegisterEntities( List<BaseEntity> ents )
	{
		for ( BaseEntity e1 : ents )
		{
			RegisterEntity( e1 );
		}
	}

	/**
	 * Unregisters an entity with the Panel, the entity will be removed after every other entity has been moved and
	 * drawn.
	 * 
	 * @param removeEntity
	 *            The Entity to be removed
	 */
	protected synchronized void InternalEntityUnregistration( BaseEntity removeEntity )
	{
		m_Entities.remove( removeEntity );

		m_QuadTree.remove( removeEntity.getActualBounds(), removeEntity );

		removeEntity.setDisplayer( null );
		removeEntity.OnDelete();
	}

	// Finaly adds the entity
	protected synchronized void InternalEntityRegistration( BaseEntity entity )
	{
		m_Entities.add( entity );

		m_QuadTree.add( entity.getActualBounds(), entity );

		entity.setDisplayer( this );
	}

	@Override
	public synchronized void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		m_Drawing = true;

		if ( g instanceof Graphics2D )
		{
			Graphics2D g2d = (Graphics2D)g;

			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2d.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );

			// ZOOM
			g2d.scale( zoomLevel, zoomLevel );

			PanelDrawBefore( g2d );

			if ( m_DrawAxes && m_Entities.size() != 0 )
			{
				drawableAxes.Draw( g2d, m_Entities );
			}

			for ( BaseEntity entity : m_Entities )
			{
				DrawEntity( entity, g2d );
			}

			PanelDrawAfter( g2d );

		}

		m_Drawing = false;
	}

	protected void DrawEntity( BaseEntity entity, Graphics2D g2d )
	{
		entity.Draw( g2d, m_Verbose );
	}

	protected void PanelDrawBefore( Graphics2D g2d )
	{
		// Intended for optional use in classes that extend from this
	}

	protected void PanelDrawAfter( Graphics2D g2d )
	{
		// Intended for optional use in classes that extend from this
	}

	public float getZoomLevel()
	{
		return zoomLevel;
	}

	public void setZoomLevel( float zoom )
	{
		if ( zoom <= 0 )
			return;

		this.zoomLevel = zoom;
	}

	public boolean getDrawAxes()
	{
		return m_DrawAxes;
	}

	// Getter needed here because it uses 'get' as opposed to 'is'
	@Setter( getter = "getDrawAxes" )
	public void setDrawAxes( boolean drawAxes )
	{
		m_DrawAxes = drawAxes;
	}

	public boolean isVerbose()
	{
		return m_Verbose;
	}

	public void setVerbose( boolean verbose )
	{
		m_Verbose = verbose;
	}

	public Color getBackgroundColor()
	{
		return super.getBackground();
	}

	@Setter
	public void setBackgroundColor( Color c )
	{
		super.setBackground( c );
	}

	public void EntityInvalidation( BaseEntity be, Rectangle2D oldBounds )
	{
		m_QuadTree.remove( oldBounds, be );

		m_QuadTree.add( be.getActualBounds(), be );
	}
	
	private ConcurrentHashMap<Integer, KeyEvent> keysDown = new ConcurrentHashMap<Integer, KeyEvent>();
	
	protected ConcurrentHashMap<Integer, KeyEvent> getKeysDown()
	{
		return keysDown;
	}

	public void keyPressed( KeyEvent k )
	{
		keysDown.putIfAbsent( k.getKeyCode(), k );
	}

	public void keyReleased( KeyEvent k )
	{
		if ( keysDown.containsKey( k.getKeyCode() ) )
			keysDown.remove( k.getKeyCode() );
	}

	public void keyTyped( KeyEvent k )
	{
		// Needed for the Listener
	}
}
