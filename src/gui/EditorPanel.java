package gui;

import geom.GeomUtility;
import geom.PolygonOrCircle;
import gui.contextMenus.ContextMenu_Axes;
import gui.contextMenus.ContextMenu_Entity;
import gui.opf.Setter;
import gui.undo.EntityRegistrationEdit;
import gui.undo.SafeUndoManager;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.vecmath.Vector2d;

import physics.BaseDisplayPanel;
import physics.BaseEntity;
import physics.MovingEntity;
import sun.java2d.SunGraphics2D;
import util.Utility;

/**
 * The Editor Panel contains all of the entity data. 
 * When the arena panel is launched, these data are registered with the Arena.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public final class EditorPanel 
    extends BaseDisplayPanel 
 implements MouseListener, MouseMotionListener
{
	static final long serialVersionUID = 0xfee4beef;
	
	/* Editor panel current state */
	/* The EditorPanel can be in several states simultaneously... */
	private boolean drawingPolygon,
					drawingVelocity,
					resizing,
					movingAxesOrigin,
					dragging;
	protected boolean snapToGrid = true;

	private boolean displayGrid = true;
	
	/* The EditorPanel can be in one "mode" at a time..
	 * 1) select
	 * 2) drawSquare
	 * 3) drawCircle
	 * 4) draw
	 * 5) creation
	 */
	private String mode = "select"; // initialize mode to "select"
	
	/* Entity control / creation */
	private BaseEntity selectedEntity = null;
	private BaseEntity copiedEntity = null;

	/* EditorPanel UI Settings */
	private int gridWidth = 15;
	private Color gridColor = Color.LIGHT_GRAY;
	
	public static final int VEL_DRAW_SCALE = 2;
	public static final int VEL_DRAW_STROKE = 2;

	/* Current mouse position */
	// paintComponent() needs to know where mouse is
	private int mouseX, mouseY;
	// difference between mouse position and UL point of selected entity
	private int mouseDX, mouseDY;
	
	/* drawPolygon mode stuff */
	private List<Point2D> polyPoints = new Vector<Point2D>();
	private Shape s = null;
	
	/* right click menus */
	private ContextMenu_Entity rightClickInEntityMenu = new ContextMenu_Entity();
	private ContextMenu_Axes rightClickOnAxesMenu = new ContextMenu_Axes();

	protected Color wallColor = new Color(56,56,56);
	
	private SafeUndoManager undoManager = new SafeUndoManager();
	
	/**
	 * Constructs the Panel by Initializing its components (jbInit). 
	 * Uses default 660 by 500 size.
	 */
	public EditorPanel()
	{
		this( new Dimension( 660, 500 ) );
	}

	/**
	 * Constructs a Panel of the given size
	 * 
	 * @param d The size of the Arena to be created
	 */
	public EditorPanel( Dimension d )
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

	@Override
	protected void PanelDrawBefore( Graphics2D g2d )
	{
		super.PanelDrawBefore( g2d );

		if( displayGrid )
			drawGrid( g2d, gridColor );
	}

	@Override
	protected void PanelDrawAfter( Graphics2D g2d )
	{
		super.PanelDrawAfter( g2d );

		if ( selectedEntity != null )
		{
			DrawEntity( selectedEntity, g2d ); // Draw the selected entity atop everything else
		}

		if ( drawingPolygon )
		{
			if ( !polyPoints.isEmpty() )
			{
				g2d.setColor( Color.BLACK );
				s = new Rectangle2D.Double( polyPoints.get( 0 ).getX() - 5, polyPoints.get( 0 ).getY() - 5, 10, 10 );
				g2d.draw( s );
				// draw all the current poly points
				for ( int i = 0; i < polyPoints.size() - 1; i++ )
				{
					Point2D point1 = polyPoints.get( i );
					Point2D point2 = polyPoints.get( i + 1 );
					g2d.drawLine( (int)point1.getX(), (int)point1.getY(), (int)point2.getX(), (int)point2.getY() );
				}
				// draw line from last point to mouse
				g2d.drawLine( (int)polyPoints.get( polyPoints.size() - 1 ).getX(), (int)polyPoints.get( polyPoints.size() - 1 ).getY(), mouseX,
						mouseY );

				if ( polyPoints.size() > 1 )
				{
					List<Point2D> clone = new Vector<Point2D>( polyPoints );

					clone.add( new Point2D.Double( mouseX, mouseY ) );

					List<Point2D> normalized = GeomUtility.Normalize( clone );

					g2d.setColor( Utility.ApplyAlpha( Color.BLACK, 40 ) );
					g2d.fill( new PolygonOrCircle( normalized ).getShape() );

					g2d.setColor( Color.BLACK );
					for ( int i = 0; i < normalized.size(); i++ )
					{
						g2d.drawString( String.valueOf( i ), (float)normalized.get( i ).getX(), (float)normalized.get( i ).getY() );
					}
				}
			}

		}
		else if ( mode == "drawSquare" )
		{
			if ( !polyPoints.isEmpty() )
			{
				g2d.setColor( Color.BLACK );
				// polyPoints.add( new Point2D.Double( mouseX,mouseY));

				int originalX = (int)polyPoints.get( 0 ).getX();
				int originalY = (int)polyPoints.get( 0 ).getY();
				// draw the rectangle
				g2d.drawLine( originalX, originalY, mouseX, originalY );
				g2d.drawLine( mouseX, originalY, mouseX, mouseY );
				g2d.drawLine( mouseX, mouseY, originalX, mouseY );
				g2d.drawLine( originalX, mouseY, originalX, originalY );
			}
		}
		else if ( mode == "drawCircle" )
		{
			if ( !polyPoints.isEmpty() )
			{
				g2d.setColor( Color.BLACK );

				Point2D lowerRight = new Point2D.Double( mouseX, mouseY );
				Point2D upperLeft = polyPoints.get( 0 );
				int diameter1 = (int)Math.abs( lowerRight.getX() - upperLeft.getX() );
				int diameter2 = (int)Math.abs( lowerRight.getY() - upperLeft.getY() );
				int diameter;
				if ( diameter1 >= diameter2 )
					diameter = diameter1;
				else
					diameter = diameter2;
				g2d.drawOval( (int)upperLeft.getX(), (int)upperLeft.getY(), diameter, diameter );
			}
		}
		else if ( resizing && mode == "select" )
		{
			Point2D lowerRight = new Point2D.Double( mouseX, mouseY );
			Point2D upperLeft = new Point2D.Double( selectedEntity.getEffectiveBounds().getX(), selectedEntity.getEffectiveBounds().getY() );
			
			if( snapToGrid )
				lowerRight = nearestGridLoc( lowerRight );
			
			
			
			int diameter1 = (int)Math.abs( lowerRight.getX() - upperLeft.getX() );
			int diameter2 = (int)Math.abs( lowerRight.getY() - upperLeft.getY() );

			int diameter = Math.max( diameter1, diameter2 );

			double oldScale = selectedEntity.getScale();

			double larger = Math.max( selectedEntity.getEffectiveBounds().getWidth(), selectedEntity.getEffectiveBounds().getHeight() ) / oldScale;

			selectedEntity.setScale( diameter / larger );
		}
	}

	@Override
	protected void DrawEntity( BaseEntity entity, Graphics2D g2d )
	{
		super.DrawEntity( entity, g2d );

		/*
		 * Draw the selection box around the entity
		 */
		if ( entity == selectedEntity )
		{			
			g2d.setColor(Color.GRAY);
			
			g2d.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] {9}, 0 ));
			
			Rectangle2D rect = entity.getEffectiveBounds();
			
			if( rect == null )
				rect = entity.getActualBounds();
			
			Rectangle2D newRect = new Rectangle2D.Double(
					rect.getMinX()-5, rect.getMinY()-5,
					rect.getMaxX()-rect.getMinX()+10,
					rect.getMaxY()-rect.getMinY()+10
					);
			//g2d.draw( entity.getShape().getBounds2D() );
			g2d.draw(newRect);
			g2d.setStroke(new BasicStroke());
		}
	}

	/**
	 * Draw the editor grid
	 * 
	 * @param g Graphics context to use
	 * @param c Color to set the grid lines
	 */
	private void drawGrid( Graphics2D g, Color c )
	{
		// Start Grid
		g.setColor( c );

		int i = 0;
		int toDrawTo = (int)( Math.max( this.getWidth(), this.getHeight() ) / getZoomLevel() );
		while ( i < toDrawTo )
		{
			g.drawLine( 0, i, toDrawTo, i );
			g.drawLine( i, 0, i, toDrawTo );
			i += gridWidth;
		}

	}

	/**
	 * Adds initial items to the Panel, called first and mostly for testing
	 */
	@Override
	protected void Populate()
	{
		/*TEST ENTITIES
		 * Ball bally = new Ball_Beach( 600, 200, -100, 00 ); // bally.setMass(10); RegisterEntity( bally ); Ball bally2 = new Ball_Beach( 200, 200,
		 * 0, 0 ); RegisterEntity( bally2 );
		 */
		/*
		 * RegisterEntity( new Planet2( 200, 200, 25, -25 ) ); RegisterEntity( new Planet1( 100, 100, 25, -25 ) ); Planet3 p = new Planet3( 500, 350,
		 * -10, -10 ); p.setOrientation( 45 ); RegisterEntity( p ); RegisterEntity( new RangedForceEntity( 0, 0, 660, 500, 50 ) );
		 */
		/*
		 * MyPolygon anotherPolygon = new MyPolygon(); anotherPolygon.setULPoint(100,300); anotherPolygon.setCurrentTickVelocity(new Vector2d(0,0));
		 * RegisterEntity(anotherPolygon); MyPolygon anotherPolygon2 = new MyPolygon(); anotherPolygon2.setULPoint(300,100);
		 * anotherPolygon2.setCurrentTickVelocity(new Vector2d(-1,0)); //anotherPolygon2.setOrientation(50); RegisterEntity(anotherPolygon2);
		 * RegisterEntity(new RangedForceEntity(50,50,300,300,1000)); //RegisterEntity(new Ball_Soccer(100,100,70,-50));
		 */
		// RegisterEntity(new RangedForceEntity(0,0,600,500,5000));
		/*
		 * RegisterEntity(new FieldForceEntity(0,400,600,100,new Vector2d(0,-100)));
		 */
		
		// RegisterEntity(new samplegame.Ball_Beach(300,300,100,100));

	}

	private void jbInit() throws Exception
	{

		this.setBackground( Color.white );
		this.setLayout( new BorderLayout() );
		this.setDoubleBuffered( true );

		this.setFocusable( true );
		this.requestFocus();

		this.addMouseListener( this );
		this.addMouseMotionListener( this );
		this.addKeyListener( this );

		this.setVerbose( true );

		this.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyPressed( KeyEvent k )
			{
				if ( k.getKeyCode() == KeyEvent.VK_DELETE )
				{

					if ( selectedEntity != null )
					{
						// System.out.println( selectedEntity.toString() + " was deleted" );
						// InternalEntityUnregistration( selectedEntity );
						selectedEntity.QueueDeletion();
						ProcessQueues();

						rightClickInEntityMenu.setVisible( false );
						rightClickOnAxesMenu.setVisible( false );

						// ProcessQueues();
						repaint();
					}
				}
			}
		} );

		Populate();
		ProcessQueues();

		if ( getEntities().size() > 0 )
			selectEntity( getEntities().get( 0 ) );

		repaint();
	}


	/**
	 * Sets which entity should be selected on the editor. 
	 * 
	 * @param b Entity to select
	 */
	public void selectEntity( BaseEntity b )
	{
		if ( selectedEntity != null )
		{
			selectedEntity.setSelected( false );
		}
		
		if( b != null )
		{
			b.setSelected( true );
			Desktop.opf.setObject( b );
		}
		else
		{
			Desktop.opf.setObject( this );
		}


		selectedEntity = b;

		resizing = false;
	}
	
	public void selectLastEntity()
	{
		if ( !getEntities().isEmpty() )
		{
			selectEntity( getEntities().get( getEntities().size() - 1 ) );
		}
		else
		{
			selectEntity( null );
		}
	}

	/**
	 * Returns which entity was clicked into
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return BaseEntity object that was clicked into
	 */
	protected BaseEntity getClickedInEntity( int x, int y )
	{
		for ( BaseEntity b : getEntities() )
		{
			if ( b.getShape().contains( x, y ) )
			{
				return b;
			}
		}
		return null;
	}
	
	protected BaseEntity getClickedInCentroidOfEntity( int x, int y )
	{
		for ( BaseEntity b : getEntities() )
		{
			int x_pos = (int)b.getCentroid().getX();
			int y_pos = (int)b.getCentroid().getY();

			boolean boo = Math.abs( x_pos - x ) < 15 && Math.abs( y_pos - y ) < 15 ;

			if( boo )
				return b;
		}
		return null;
	}

	/**
	 * Determines if given coordinates constitute a click on the axes origin.
	 * 
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 * @return Whether or not the mouse clicked on origin
	 */
	protected boolean clickedOnAxesOrigin( int x, int y )
	{
		if ( !getDrawAxes() )
			return false;

		int x_pos = (int)getAxesOrigin().getX();
		int y_pos = (int)getAxesOrigin().getY();
		if ( Math.abs( x_pos - x ) < 15 && Math.abs( y_pos - y ) < 15 )
		{
			return true;
		}
		return false;
	}

	/**
	 * Determine whether a given set of coordinates correspond with the center of an entity.
	 * 
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 * @return Whether or not the coordinates are within the center of an entity
	 */

	protected boolean clickedOnCentroidOfSelectedEntity( int x, int y )
	{
		if ( selectedEntity == null )
			return false;

		int x_pos = (int)selectedEntity.getCentroid().getX();
		int y_pos = (int)selectedEntity.getCentroid().getY();

		return ( Math.abs( x_pos - x ) < 10 && Math.abs( y_pos - y ) < 10 );
	}

	@Override
	protected synchronized void InternalEntityUnregistration( BaseEntity removeEntity )
	{
		super.InternalEntityUnregistration( removeEntity );

		selectLastEntity();
	}

	
	
	private void beginDrawCircle( MouseEvent e )
	{
		if ( polyPoints.isEmpty() )
		{
			Point2D.Double pToAdd = new Point2D.Double( e.getX(), e.getY() );
			if ( snapToGrid )
				polyPoints.add( nearestGridLoc(pToAdd) );
			else
				polyPoints.add( pToAdd );
		}
	}
	
	private void endDrawCircle( MouseEvent e )
	{
		if ( !polyPoints.isEmpty() )
		{
			Point2D lowerRight;
			if (snapToGrid)
				lowerRight = nearestGridLoc(new Point2D.Double( e.getX(), e.getY() ));
			else
				lowerRight = new Point2D.Double( e.getX(), e.getY() );
			
			Point2D upperLeft = polyPoints.get( 0 );
			if ( lowerRight.distance( upperLeft ) > 10 )
			{
				int diameter1 = (int)Math.abs( lowerRight.getX() - upperLeft.getX() );
				int diameter2 = (int)Math.abs( lowerRight.getY() - upperLeft.getY() );
				int diameter;
				if ( diameter1 >= diameter2 )
					diameter = diameter1;
				else
					diameter = diameter2;
				MovingEntity newCircle = new MovingEntity( new PolygonOrCircle( upperLeft, diameter/2 ) );
				newCircle.setMass( newCircle.getRoundedShapeMass() );
				this.RegisterEntity( newCircle );
				this.selectEntity( newCircle );
				ProcessQueues();
				polyPoints.clear();
			}

			else
			// rectangle too small
			{
				polyPoints.clear();
			}
		}
	}
	
	private void beginDrawSquare( MouseEvent e )
	{
		if ( polyPoints.isEmpty() )
		{
			Point2D.Double pToAdd = new Point2D.Double( e.getX(), e.getY() );
			if ( snapToGrid )
			{
				polyPoints.add( nearestGridLoc(pToAdd));
			}
			else {
				polyPoints.add( pToAdd );
			}
		}	
	}
	
	private void endDrawSquare( MouseEvent e )
	{
		if ( !polyPoints.isEmpty() )
		{
			Point2D p = new Point2D.Double(e.getX(),e.getY());
			
			if ( snapToGrid ) 
				p = nearestGridLoc(p);
			
			if ( Math.abs( polyPoints.get( 0 ).getX() - p.getX() ) > 10 && Math.abs( polyPoints.get( 0 ).getY() - p.getY() ) > 10)
			{

				
				polyPoints.add( new Point2D.Double( polyPoints.get( 0 ).getX(), p.getY() ) );
				polyPoints.add( new Point2D.Double( p.getX(), p.getY() ) );
				polyPoints.add( new Point2D.Double( p.getX(), polyPoints.get( 0 ).getY() ) );
				MovingEntity newPoly = new MovingEntity( new PolygonOrCircle( polyPoints ) );
				newPoly.setMass( newPoly.getRoundedShapeMass() );
				this.RegisterEntity( newPoly );
				this.selectEntity( newPoly );
				ProcessQueues();
				polyPoints.clear();
			}
			else
			// rectangle too small
			{
				polyPoints.clear();
			}
		}	
	}
	
	private void beginHandDraw( MouseEvent e)
	{
		if ( polyPoints.isEmpty() && !drawingPolygon )
		{
			Point2D pToAdd = new Point2D.Double( e.getX(), e.getY() );
			
			if( snapToGrid )
				pToAdd = nearestGridLoc( pToAdd );
			

			drawingPolygon = true;
			s = new Rectangle2D.Double( e.getX() - 5, e.getY() - 5, 10, 10 );

		}
		else if ( s.contains( e.getX(), e.getY() ) )
		{
			drawingPolygon = false;
			for ( Point2D point : polyPoints )
			{
				if ( ( point.distance( polyPoints.get( 0 ) ) > 8 ) && ( polyPoints.size() > 2 ) )
				{
					// its big enough
					MovingEntity newPoly = new MovingEntity( new PolygonOrCircle( polyPoints ) );
					newPoly.setMass( newPoly.getRoundedShapeMass() );
					this.RegisterEntity( newPoly );
					this.selectEntity( newPoly );
					ProcessQueues();
					break;
				}
			}
			polyPoints.clear();
			Desktop.cFrame.setMode( "select" );
		}
		else
		{
			Point2D p2d = new Point2D.Double( e.getX(), e.getY() );
			
			if( snapToGrid )
				p2d = nearestGridLoc( p2d );
			
			polyPoints.add( p2d );
		}
	}
	
	/**
	 * Returns the closest point on the editor panel's
	 * grid to the point passed in.
	 * 
	 * @param point the point passed in
	 * @return the closest point to the point passed in
	 */
	private Point2D nearestGridLoc(Point2D point)
	{
		double xModGrid = point.getX()%getGridWidth();
		double xToAdd = 
			(xModGrid < getGridWidth()/2) 
			? -xModGrid : getGridWidth()-xModGrid;
		
		double yModGrid = point.getY()%getGridWidth();
		double yToAdd = 
			(yModGrid < getGridWidth()/2) 
			? -yModGrid : getGridWidth()-yModGrid;
		return new Point2D.Double(point.getX()+xToAdd, point.getY()+yToAdd);	
	}
	
	private void endHandDraw( MouseEvent e )
	{
		if ( !polyPoints.isEmpty() )
		{
			s = new Rectangle2D.Double( polyPoints.get( 0 ).getX() - 5, polyPoints.get( 0 ).getY() - 5, 10, 10 );
			if ( s.contains( e.getX(), e.getY() ) && ( polyPoints.size() > 2 ) )
			{
				drawingPolygon = false;
				for ( Point2D point : polyPoints )
				{
					if ( point.distance( polyPoints.get( 0 ) ) > 8 )
					{
						// its big enough
						MovingEntity newPoly = new MovingEntity( new PolygonOrCircle( GeomUtility.Normalize( polyPoints )) );
						newPoly.setMass( newPoly.getRoundedShapeMass() );
						this.RegisterEntity( newPoly );
						this.selectEntity( newPoly );
						ProcessQueues();
						break;
					}
				}

				polyPoints.clear();
				Desktop.cFrame.setMode( "select" );
			}
		}
	}

	private void createObject( MouseEvent e )
	{
		Class<? extends BaseEntity> c = Desktop.cFrame.getSelectedClass( BaseEntity.class );

		if ( c != null )// create an entity
		{
			BaseEntity temp = Utility.CreateInstanceOf( c );

			if ( temp == null )
				return;

			// temp.setShapeColor( Utility.getRandomColor() );
			temp.setCenterPoint( e.getX(), e.getY() );

			RegisterEntity( temp );
			this.selectEntity( temp );

			ProcessQueues();
			repaint();
		}
		else
		{
			System.out.println( "No object selected. Select an object from the Tree Menu on the left." );
		}
	}
	
	public void mousePressed( MouseEvent e )
	{

		adjustMouseEventForZoom(e);
		
		if( resizing )
		{
			Desktop.cFrame.setMode( "select" );
			return;
		}
		
		BaseEntity b = getClickedInEntity( e.getX(), e.getY() );
		
		selectEntity(b);
		
		/********* CLICK DESKTOP **********/
		if ( b == null )
		{
			/***** Left Click *****/
			if ( e.getButton() == MouseEvent.BUTTON1 )
			{
				/*selectEntity(null) does this.
				 * Desktop.opf.setObject(this);
				 * selectedEntity = null;
				*/
				dragging = false;

				if ( mode == "drawSquare" )
				{
					beginDrawSquare(e);
				}
				else if ( mode == "drawCircle" )
				{
					beginDrawCircle(e);
				}
				else if ( mode == "creation" )
				{
					createObject( e );
				}
				else if ( clickedOnAxesOrigin( e.getX(), e.getY() ) )
				{
					movingAxesOrigin = true;
				}
				
			}/***** Right Click *****/
			else if (e.getButton() == MouseEvent.BUTTON3 ) // Right Click
			{
				// Do Nothing
			}
			/***** Unsupported Click *****/
			else // Unsupported Click
			{
				// Do Nothing
			}
			
			setComponentPopupMenu( null );
		}
		else/********* CLICK ON ENTITY **********/
		{
			Desktop.cFrame.setMode( "select" );
			setComponentPopupMenu( null );
			
			/***** Left Click *****/
			if ( e.getButton() == MouseEvent.BUTTON1 )
			{
				dragging = true;
				
				/* Reset mouse offsets for correct dragging */
				Point mouseOffset = findOffset( selectedEntity, e.getX(), e.getY() );
				mouseDX = (int)mouseOffset.getX();
				mouseDY = (int)mouseOffset.getY();
				
				repaint();
			}
			
			/***** Right Click *****/
			else if (e.getButton() == MouseEvent.BUTTON3 ) // Right Click
			{		
				/* CTRL-RightClick */
				if ( e.getModifiers() == 6 )
				{

					MovingEntity t = (MovingEntity)selectedEntity;
					t.setCurrentVelocity( new Vector2d( 0, 0 ) );

				}
				
				/* RightClick in Centroid */
				else if (clickedOnCentroidOfSelectedEntity( e.getX(), e.getY())) 
				{
					drawingVelocity = true;
				}
				
				/* Right clicked anywhere in entity */
				else
				{
					//Desktop.opf.setObject( b ); SelectEntity sets the OPF
					
					rightClickInEntityMenu.setEntity( b );
					setComponentPopupMenu( rightClickInEntityMenu );

					
				}
				repaint();
			}
			
			/***** Unsupported Click *****/
			else // Unsupported Click
			{
				
				// Do Nothing

			}
		}
	}

	public void mouseReleased( MouseEvent e )
	{

		adjustMouseEventForZoom( e );

		/***** Left Click *****/
		if ( e.getButton() == MouseEvent.BUTTON1 )
		{
			
			
		}
		
		/***** Right Click *****/
		else if (e.getButton() == MouseEvent.BUTTON3 ) // Right Click
		{
				

		}
		
		/***** Unsupported Click *****/
		else // Unsupported Click
		{
			
			// Do Nothing

		}
		
		drawingVelocity = false;
		resizing = false;
		movingAxesOrigin = false;
		dragging = false;
		
		if ( mode == "draw" )
		{
			endHandDraw(e);
		}
		else if ( mode == "drawSquare" )
		{
			endDrawSquare(e);
		}
		else if ( mode == "drawCircle" )
		{
			endDrawCircle(e);
		}
		repaint();
	}

	public void mouseClicked( MouseEvent e )
	{
		adjustMouseEventForZoom( e );

		if ( mode == "draw" )
		{
			beginHandDraw(e);
		}
		repaint();
	}

	public void mouseEntered( MouseEvent e )
	{
		adjustMouseEventForZoom( e );
	}

	public void mouseExited( MouseEvent e )
	{
		adjustMouseEventForZoom( e );
	}

	public void mouseDragged( MouseEvent e )
	{
		adjustMouseEventForZoom( e );
		
		setGlobalMousePosition( e );

		setComponentPopupMenu( null );
		
		if ( mode.equals("select"))
		{
			if ( selectedEntity != null )
			{
				/* If we are dragging an entity */
				if ( dragging )
				{
					if ( IntersectsWithAnything( selectedEntity ) )
						selectedEntity.setFrameColor( Color.RED );
					else 
						selectedEntity.resetFrameColor();
					
					selectedEntity.setULPoint( e.getX() - mouseDX, e.getY() - mouseDY );
					
					if( snapToGrid )
					{
						double tx = selectedEntity.getULPoint().getX();
						double ty = selectedEntity.getULPoint().getY();
						selectedEntity.setULPoint( tx - tx % getGridWidth(), ty - ty % getGridWidth() );
					}
					
					repaint();
				}
				/* If we are drawing a velocity vector */
				else if ( drawingVelocity && selectedEntity instanceof MovingEntity )
				{
					MovingEntity t = (MovingEntity)selectedEntity;
					
					Point2D p = new Point2D.Double( e.getX(), e.getY() );
					
					if( snapToGrid )
						p = nearestGridLoc( p );
					
					Vector2d vec = new Vector2d( VEL_DRAW_SCALE * ( p.getX() - t.getCentroid().getX() ), VEL_DRAW_SCALE * ( p.getY() - t.getCentroid().getY() ) );
					
					if( getKeysDown().containsKey( KeyEvent.VK_CONTROL ) )
					{
						double angle = vec.angle( GeomUtility.getUnitVectorNegY() );	//12 o'clock = 0
											
						if( angle < Math.PI/8 || angle > Math.PI * 7/8 )
						{
							vec.x = 0;
						}
						else if( angle < Math.PI * 3/8)
						{
							int signage = (vec.x > 0) ? -1 : 1;
							if( Math.abs( vec.y ) > Math.abs( vec.x ) )
								vec.x = vec.y * signage;
							else
								vec.y = vec.x * signage;
						}
						else if( angle < Math.PI * 5/8)
						{
							vec.y = 0;
						}					
						else if( angle < Math.PI * 7/8)
						{
							int signage = (vec.x > 0) ? 1 : -1;
							
							if( Math.abs( vec.y ) > Math.abs( vec.x ) )
								vec.x = vec.y * signage;
							else
								vec.y = vec.x * signage;
						}
					}
						
	
					t.setCurrentVelocity( vec );
					repaint();
					Desktop.opf.updateOPF();
				}

			}
			else if ( movingAxesOrigin )
			{
				setAxesOrigin( new Point2D.Double( e.getX(), e.getY() ) );
				// Desktop.dtArena.arena.setAxesOrigin(new Point2D.Double(e.getX(),
				// e.getY()));
				Desktop.editFrame.getEditorContext().setAxesOrigin( new Point2D.Double( e.getX(), e.getY() ) );
			}					
		}
		if ( mode == "draw" )
		{
			Graphics g = this.getGraphics();
			SunGraphics2D g2d = (SunGraphics2D)g;
			if ( polyPoints.isEmpty() )
			{
				drawingPolygon = true;
				s = new Rectangle2D.Double( e.getX() - 5, e.getY() - 5, 10, 10 );
				g2d.draw( s );
				polyPoints.add( new Point2D.Double( e.getX(), e.getY() ) );
			}
			else
			{
				polyPoints.add( new Point2D.Double( e.getX(), e.getY() ) );
			}
		}
		else if ( mode == "drawSqaure" )
		{
			if ( polyPoints.isEmpty() )
			{
				polyPoints.add( new Point2D.Double( e.getX(), e.getY() ) );

			}
			else
			{
				Graphics g = this.getGraphics();
				SunGraphics2D g2d = (SunGraphics2D)g;
				int originalX = (int)polyPoints.get( 0 ).getX();
				int originalY = (int)polyPoints.get( 0 ).getY();
				// draw the rectangle
				g2d.drawLine( originalX, originalY, e.getX(), originalY );
				g2d.drawLine( e.getX(), originalY, e.getX(), e.getY() );
				g2d.drawLine( e.getX(), e.getY(), originalX, e.getY() );
				g2d.drawLine( originalX, e.getY(), originalX, originalY );
			}
		}
		else if ( mode == "drawCircle" )
		{
			if ( polyPoints.isEmpty() )
			{
				polyPoints.add( new Point2D.Double( e.getX(), e.getY() ) );
			}
		}
		repaint();

	}

	public void mouseMoved( MouseEvent e )
	{

		adjustMouseEventForZoom( e );

		setGlobalMousePosition( e );
		if ( drawingPolygon && !polyPoints.isEmpty() )
		{
			Graphics g = this.getGraphics();
			Graphics2D g2d = (Graphics2D)g;
			// draw line from last point to mouse
			// g2d.drawLine( (int)polyPoints.lastElement().getX(), (int)polyPoints.lastElement().getY(), mouseX, mouseY
			// );
			update( g2d );
		}

		resizing = this.mouseOnEntityCorners( e.getX(), e.getY() );
		/*
		 * if ( this.mouseOnEntityCorners( e.getX(), e.getY() ) ) { resizing = true; } else resizing = false;
		 */
	}



	public MouseEvent adjustMouseEventForZoom( MouseEvent e )
	{

		e.translatePoint( (int)( e.getX() / getZoomLevel() ) - e.getX(), (int)( e.getY() / getZoomLevel() ) - e.getY() );

		return e;
	}
	
	@Override
	public void keyPressed( KeyEvent k )
	{
		super.keyPressed( k );
		if( k.getModifiers() == 2 )	//CTRL Modifier
		{
			switch( k.getKeyCode() )
			{
				case KeyEvent.VK_C: 
				{
					if ( selectedEntity != null )
					{
						copiedEntity = (BaseEntity)selectedEntity.clone();
						System.out.println("Entity Copied");
					}
					
					break;
				}
				case  KeyEvent.VK_V:
				{
					if ( copiedEntity != null )
					{
						System.out.println("Entity Pasted");
						
						BaseEntity t = (BaseEntity)copiedEntity.clone();
						t.setULPoint( new Point2D.Double( mouseX, mouseY ) );
						this.RegisterEntity( t );
						this.selectEntity( t );
		
						this.ProcessQueues();
						this.repaint();
					}
					
					break;
				}
				case KeyEvent.VK_X:
				{
					if ( selectedEntity != null )
					{
						copiedEntity = (BaseEntity)selectedEntity.clone();
						System.out.println("Entity Cut");
						
						UnregisterEntity( selectedEntity );
						
						this.ProcessQueues();
						this.repaint();
					}
					
					break;
				}
				case KeyEvent.VK_Z:
				{
					//TODO: UNDO
					
					if( undoManager.canUndo() )
					{
						undoManager.undo();
					}
					else
					{
						System.out.println( "Cannot undo" );
					}
					break;
				}
				case KeyEvent.VK_Y:
				{
					//TODO: REDO
					
					if( undoManager.canRedo() )
					{
						undoManager.redo();
					}
					else
					{
						System.out.println( "Cannot redo" );
					}
					
					break;
				}
			}
		}
		
		if ( selectedEntity != null )
		{
			if (k.getKeyCode() == KeyEvent.VK_UP)
			{
				selectedEntity.setULPoint(selectedEntity.getULPoint().getX(),
						selectedEntity.getULPoint().getY()-1);
				repaint();
			}
			else if (k.getKeyCode() == KeyEvent.VK_RIGHT )
			{
				selectedEntity.setULPoint(selectedEntity.getULPoint().getX()+1,
						selectedEntity.getULPoint().getY());
				repaint();
			}
			else if (k.getKeyCode() == KeyEvent.VK_DOWN )
			{
				selectedEntity.setULPoint(selectedEntity.getULPoint().getX(),
						selectedEntity.getULPoint().getY()+1);
				repaint();
			}
			else if (k.getKeyCode() == KeyEvent.VK_LEFT )
			{
				selectedEntity.setULPoint(selectedEntity.getULPoint().getX()-1,
						selectedEntity.getULPoint().getY());
				repaint();
			}
		}
	}
	


	/**
	 * Sets the current mouse position. Called during mouse pressed, mouse move, and mouse drag events
	 * 
	 * @param e The mouse event from which to extract its coordinates
	 */
	public void setGlobalMousePosition( MouseEvent e )
	{
		mouseX = e.getX();
		mouseY = e.getY();
	}

	/**
	 * Retrieve the Graphics2d version of the graphics context, if applicable
	 * 
	 * @return The Graphics2d context
	 */
	public Graphics2D getGraphics2D()
	{
		Graphics g = this.getGraphics();

		if ( g instanceof Graphics2D )
		{
			return (Graphics2D)g;
		}

		return null;

	}

	/**
	 * Determine if a given set of coordinates are within a certain area that would correspond to the edges or corner of an object. If within the
	 * edges or corners, change the cursor to the correct type of cursor. For use with resize ability.
	 * 
	 * @param x x-Coordinate
	 * @param y y-Coordinate
	 * @return Whether or not the mouse is within the edges or corners
	 */
	public boolean mouseOnEntityCorners( int x, int y )
	{
		/*
		if ( mode != "select" )
			return false;
			*/

		// Shape N, NE, E, SE, S, SW, W, NW;
		Shape SE;
		if ( selectedEntity != null && selectedEntity.getEffectiveBounds() != null )
		{
			Point2D uLpt = new Point2D.Double( selectedEntity.getEffectiveBounds().getX(), selectedEntity.getEffectiveBounds().getY() );
			Point2D lRpt;// , uRpt,

			int width = (int)selectedEntity.getEffectiveBounds().getWidth();
			int height = (int)selectedEntity.getEffectiveBounds().getHeight();
			int offset = 10;

			lRpt = new Point2D.Double( uLpt.getX() + width, uLpt.getY() + height );
			SE = new Rectangle2D.Double( lRpt.getX(), lRpt.getY(), offset, offset );

			if ( SE.contains( x, y ) )
			{
				Cursor c = new Cursor( Cursor.SE_RESIZE_CURSOR );
				setCursor( c );
				return true;
			}
			Cursor c = new Cursor( Cursor.DEFAULT_CURSOR );
			setCursor( c );
			return false;
		}
		return false;

	}

	public String getMode()
	{
		return mode;
	}

	public void setMode( String m )
	{
		mode = m;
		polyPoints.clear();
		drawingPolygon = false;

		Utility.FocusOn( Desktop.editFrame );
	}

	public BaseEntity getSelectedEntity()
	{
		return selectedEntity;
	}
	
	/**
	 * Finds the difference in the x and y direction of where the mouse clicked in an entity and the entity's Upper-left corner. This way, when an
	 * object is dragged by the mouse, the object doesn't jump the Upper-left point underneath the cursor.
	 * 
	 * @param b Entity that is selected
	 * @param x x-Coordinate of the mouse
	 * @param y y-Coordinate of the mouse
	 * @return the delta X and delta Y from the mouse position and the UL-Point
	 */
	public Point findOffset( BaseEntity b, int x, int y )
	{
		if ( b != null )
		{
			int DX = (int)( x - b.getULPoint().getX() );
			int DY = (int)( y - b.getULPoint().getY() );

			return new Point( DX, DY );
		}

		return new Point( 0, 0 );
	}

	protected JPopupMenu getRightClickInEntityMenu()
	{
		return rightClickInEntityMenu;
	}

	@Override
	public String toString()
	{
		return "Editor Panel";
	}

	/**
	 * @return Returns the gridColor.
	 */
	public final Color getGridColor()
	{
		return gridColor;
	}

	/**
	 * @param gColor The gridColor to set.
	 */
	@Setter
	public final void setGridColor( Color gColor )
	{
		this.gridColor = gColor;
	}

	/**
	 * @return Returns the gridWidth.
	 */
	public final int getGridWidth()
	{
		return gridWidth;
	}

	/**
	 * @param gWidth The gridWidth to set.
	 */
	@Setter
	public final void setGridWidth( int gWidth )
	{
		if ( gWidth < 2 )
			gWidth = 2;

		this.gridWidth = gWidth;
	}
	

	@Override
	public void RegisterEntity( BaseEntity addEntity )
	{
		super.RegisterEntity( addEntity );
		
		undoManager.addEdit( new EntityRegistrationEdit( this, addEntity, true ) );
	}
	

	@Override
	public void UnregisterEntity( BaseEntity entity )
	{
		super.UnregisterEntity( entity );
		
		undoManager.addEdit( new EntityRegistrationEdit( this, entity, false ) );
	}

	public SafeUndoManager getUndoManager()
	{
		return undoManager;
	}
	
	

	public boolean isDisplayGrid()
	{
		return displayGrid;
	}
	
	@Setter
	public void setDisplayGrid( boolean displayGrid )
	{
		this.displayGrid = displayGrid;
	}

}
