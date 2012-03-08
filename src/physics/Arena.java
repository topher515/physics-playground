package physics;

import util.Utility;
import gui.Desktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.vecmath.Vector2d;

/**
 * The Arena Class contains all of the entity data, paints itself, and calls the movements on each of the entities.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public final class Arena extends BaseDisplayPanel implements MouseListener, MouseMotionListener, KeyListener, Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<MovingEntity> movingEntities = new Vector<MovingEntity>();

	private List<IForceEntity> forceEntities = new Vector<IForceEntity>();

	private Set<Pair<BaseEntity>> recentCollisions = new HashSet<Pair<BaseEntity>>();

	private static List<List<Object>> eventList = new Vector<List<Object>>();

	private Thread th;

	// Timer object to calculate frames-per-second
	// ~20 fps minimum for smooth animation
	FPSTimer fpsTimer = new FPSTimer();

	private static double nanoPeriod = 10000000;

	private double m_SleepTime;

	private boolean gamePaused;

	/**
	 * Constructs the Arena by Initializing its components (jbInit). Uses default 660 by 500 size.
	 */
	public Arena()
	{
		this( new Dimension( 660, 500 ) );
		
		//this.setVerbose( true );
	}

	/**
	 * Constructs an Arena of the given size
	 * 
	 * @param d The size of the Arena to be created
	 */
	public Arena( Dimension d )
	{
		super( d );

		try
		{
			jbInit();
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
	}

	// Initializes this JPanel object
	private void jbInit() throws Exception
	{
		this.setBackground( Color.white );
		this.setLayout( new GridBagLayout() );
		this.setDoubleBuffered( true );
		this.setFocusable( true );

		// addMouseMotionListener(this);
		addMouseListener( this );
		addKeyListener( this );

		repaint();
	}

	/**
	 * This handles the movement of all the entities, calls the painting and changes the period based upon the time of the last period
	 */
	public void run()
	{
		Thread thisThread = Thread.currentThread();

		try
		{
			// double sleepTime;
			thisThread.setPriority( Thread.MIN_PRIORITY );

			fpsTimer.startTimer();

			while ( true && th == thisThread )
			{
				fpsTimer.startTimer();
				if ( !gamePaused )
				{
					synchronized ( movingEntities )
					{
						for ( MovingEntity ent1 : movingEntities )
						{
							if ( ( ent1.getCenterPoint().getX() < -500 || ent1.getCenterPoint().getX() > this.getWidth() / this.getZoomLevel() + 500 )
									|| ( ent1.getCenterPoint().getY() < -500 || ent1.getCenterPoint().getY() > this.getHeight() / this.getZoomLevel()
											+ 500 ) )
							{
								ent1.QueueDeletion();
								System.out.println( ent1.toString() + " was queued for deletion for going off the screen" );
							}

							if ( !ent1.isStationary() )
							{
								// Act all forces on this entity
								for ( IForceEntity force : forceEntities )
								{
									if ( force.IntersectsWith( ent1 ) )
									{
										Vector2d newVelocity = ent1.getCurrentVelocity();
										newVelocity.add( force.getForceFor( ent1 ) );
										ent1.setCurrentVelocity( newVelocity );
									}
								}

								if ( ent1.getAngularVelocity() != 0 )
								{
									// rotate the shape as much as needed
									double angVel = ent1.getAngularVelocity() / 10;

									angVel *= ( getPeriodinNs() / 10000000 ); // comment out for the 'old' #s

									ent1.setOrientation( angVel + ent1.getOrientation() );
								}

								ent1.moveCurrentVelocity();// move this entity
							}

							handleCollisions( ent1 );
						}

						synchronized ( getKeysDown() )
						{
							for ( KeyEvent ke : getKeysDown().values() )
							{
								Utility.HandleEvents( ke.getID(), ke.getKeyCode(), this.getEntities() );
							}
						}
					}
				}

				this.repaint();// repaint after each tick
				recentCollisions.clear();

				ProcessQueues();

				// System.out.println(" current frames per second:" +
				// 1000000000/nanoPeriod);

				double diffTime = fpsTimer.stopTimer();

				m_SleepTime = ( nanoPeriod - diffTime ) / 1000000;

				if ( m_SleepTime <= 0 || m_SleepTime >= 100 ) // Sanity
				{
					m_SleepTime = 5;// 5ms
				}

				try
				{
					Thread.sleep( (long)m_SleepTime );// change nano's to miliseconds
					// System.out.println(sleepTime);
				}
				catch ( InterruptedException e )
				{
				}

				Thread.currentThread().setPriority( Thread.NORM_PRIORITY );
				fpsTimer.startTimer();
			}
		}
		catch ( Exception e )
		{
			System.err.println( "Hey, there was a crash!" );
			e.printStackTrace();
		}
	}

	/**
	 * Computes collision information such as when the collisions occur and how to respond
	 * 
	 * @param ent1 Entity on which to check for collisions
	 */
	private void handleCollisions( MovingEntity ent1 )
	{
		if ( ent1.isCollidable() )// skip collision if not applicable
		{
			// List<BaseEntity> listToUse = getEntities();

			List<BaseEntity> listToUse = GetEntitiesIn( ent1.getActualBounds() );

			for ( BaseEntity ent2 : listToUse )
			{
				if ( ent1 == ent2 )
					continue;// same object, don't collide with self

				if ( !ent2.isCollidable() )
					continue;

				boolean ent2isStationary = ent2 instanceof MovingEntity && ( (MovingEntity)ent2 ).isStationary();

				if ( ent1.isStationary() && ent2isStationary )
					continue;// both stationary.  Who cares if they overlap?

				Vector2d overlapVector = ent1.isOverlapping( ent2 );

				if ( overlapVector != null )
				{
					Pair<BaseEntity> p = new Pair<BaseEntity>( ent1, ent2 );

					if ( !recentCollisions.contains( p ) )
					{
//						Point2D collisionPoint = ent1.determinePointOfCollision( ent2, overlapVector );

						if ( ent2 instanceof MovingEntity )
						{
							ent1.collide( (MovingEntity)ent2 );//, collisionPoint );
						}
						else
						{
							ent1.collide( ent2 );//, collisionPoint );
						}

						recentCollisions.add( p );
					}

					ent1.moveVector( overlapVector );
				}
			}
		}
	}

	/**
	 * Starts the action
	 */
	public void start()
	{
		if ( th == null )
		{
			ProcessQueues();
			th = new Thread( this );
			th.start();
		}

		gamePaused = false;
	}

	/**
	 * Pauses the movement of all of the entities
	 */
	public void pause()
	{
		gamePaused = true;
	}

	/**
	 * Unpauses the movement of all of the entities
	 */
	public void unpause()
	{
		gamePaused = false;
	}

	/**
	 * Reverses the pause state
	 */
	public void switchPause()
	{
		gamePaused = !gamePaused;
	}

	/**
	 * Stops the thread
	 */
	public void stop()
	{
		th = null;
	}

	@Override
	public synchronized void InternalEntityUnregistration( BaseEntity removeEntity )
	{
		super.InternalEntityUnregistration( removeEntity );

		if ( removeEntity instanceof MovingEntity )
			movingEntities.remove( removeEntity );

		if ( removeEntity instanceof IForceEntity )
			forceEntities.remove( removeEntity );
	}

	@Override
	protected void Populate()
	{
		RegisterEntities( Desktop.editFrame.getEditorContext().getClonedEntities() );
	};

	@Override
	protected synchronized void InternalEntityRegistration( BaseEntity entity )
	{
		super.InternalEntityRegistration( entity );

		if ( entity instanceof MovingEntity )
			movingEntities.add( (MovingEntity)entity );

		if ( entity instanceof IForceEntity )
			forceEntities.add( (IForceEntity)entity );
	}

	/**
	 * Gets the current time it takes to move all of the entites and draw them.
	 * 
	 * @return The period in nano-seconds
	 */
	public static double getPeriodinNs()
	{
		return nanoPeriod;
	}

	/**
	 * Checks if a given UserEvent type exists in an entity.
	 * 
	 * @param e The event type integer. Determines main type of event like a button or key press
	 * @return The index of the UserEvent that corresponds with this type of event
	 */
	public int isEventRegistered( Integer e )
	{
		int j = 0;
		for ( List<Object> o : eventList )
		{
			if ( o.get( 0 ) == e )
			{
				return j;
			}
			j++;
		}
		return -1;
	}

	public void mousePressed( MouseEvent e )
	{
		Utility.HandleEvents( e.getID(), e.getButton(), this.getEntities() );
	}

	public void mouseReleased( MouseEvent e )
	{

	}

	public void mouseClicked( MouseEvent e )
	{
	}

	public void mouseEntered( MouseEvent e )
	{

	}

	public void mouseExited( MouseEvent e )
	{

	}

	public void mouseDragged( MouseEvent e )
	{

	}

	public void mouseMoved( MouseEvent e )
	{

	}

	@Override
	protected void PanelDrawAfter( Graphics2D g2d )
	{
		super.PanelDrawAfter( g2d );

		g2d.drawString( Double.toString( m_SleepTime ), 0, 10 );
	}
}
