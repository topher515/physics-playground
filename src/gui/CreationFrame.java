package gui;

import samplegame.Ball_Beach;
import samplegame.Ball_Bowling;
import samplegame.Ball_Smile;
import samplegame.Ball_Soccer;
import samplegame.Bot;
import samplegame.Parallelogram;
import samplegame.Square;
import samplegame.Triangle;
import samplegame.planets.Planet1;
import samplegame.planets.Planet2;
import samplegame.planets.Planet3;
import util.CodeCompiler;
import util.Utility;
import geom.PolygonOrCircle;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import physics.BaseEntity;
import physics.FieldForceEntity;
import physics.MovingEntity;
import physics.RangedForceEntity;
import physics.ScaledMassFieldForce;

/**
 * The creation frame gives access to all types of entities available to you. It contains drawing tools as well as a tree of pre-defined shapes and
 * entities. The drawing tools include rectangular, and circular drawing modes as well as a free-draw tool. The free-draw tool allows for line by line
 * polygon creations as well as "pencil" drawing on a mouse drag.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public class CreationFrame extends JInternalFrame implements ActionListener, TreeSelectionListener
{
	static final long serialVersionUID = 0001;

	private Class<?> selectedClass;

	private JPanel contentPane;

	// Add the entity selection tree to a scrollable pane
	private JScrollPane treeView;

	private JTree entityTree;

	private ButtonGroup grpDrawMode;

	/**
	 * Default constructor. Initializes components associated with the frame.
	 */
	public CreationFrame()
	{
		try
		{
			setDefaultCloseOperation( DISPOSE_ON_CLOSE );
			jbInit();
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( e.getActionCommand() == "draw" )
		{
			this.setMode( "draw" );
		}
		else if ( e.getActionCommand() == "select" )
		{
			Desktop.editFrame.getEditorContext().setMode( "select" );
		}
		else if ( e.getActionCommand() == "creation" )
		{
			Desktop.editFrame.getEditorContext().setMode( "creation" );
		}
		else if ( e.getActionCommand() == "drawSquare" )
		{
			Desktop.editFrame.getEditorContext().setMode( "drawSquare" );
		}
		else if ( e.getActionCommand() == "drawCircle" )
		{
			Desktop.editFrame.getEditorContext().setMode( "drawCircle" );
		}
		else if ( e.getActionCommand().startsWith( "addWall" ) )
		{

			int WALL_THICKNESS = 10;
			int SCREEN_HEIGHT = (int)( Desktop.editFrame.getEditorContext().getHeight() / Desktop.editFrame.getEditorContext().getZoomLevel() );
			int SCREEN_WIDTH = (int)( Desktop.editFrame.getEditorContext().getWidth() / Desktop.editFrame.getEditorContext().getZoomLevel() );

			String commandString = e.getActionCommand();
			commandString = commandString.substring( commandString.indexOf( "-" ) + 1 );

			MovingEntity tempEnt;
			double[] vertices = new double[8];
			// int ulX = 0, ulY = 0, shapeWidth = 0, shapeHeight = 0;

			if ( commandString.equals( "up" ) )
			{

				vertices[0] = 0;
				vertices[1] = 0;
				vertices[2] = SCREEN_WIDTH;
				vertices[3] = 0;
				vertices[4] = SCREEN_WIDTH - WALL_THICKNESS;
				vertices[5] = WALL_THICKNESS;
				vertices[6] = 0 + WALL_THICKNESS;
				vertices[7] = WALL_THICKNESS;

				/*
				 * ulX=ulY=0; shapeWidth = SCREEN_WIDTH; shapeHeight = WALL_THICKNESS;
				 */
			}
			if ( commandString.equals( "right" ) )
			{

				vertices[0] = SCREEN_WIDTH;
				vertices[1] = 0;
				vertices[2] = SCREEN_WIDTH;
				vertices[3] = SCREEN_HEIGHT;
				vertices[4] = SCREEN_WIDTH - WALL_THICKNESS;
				vertices[5] = SCREEN_HEIGHT - WALL_THICKNESS;
				vertices[6] = SCREEN_WIDTH - WALL_THICKNESS;
				vertices[7] = WALL_THICKNESS;

				/*
				 * ulX=SCREEN_WIDTH-WALL_THICKNESS; ulY=0; shapeWidth = WALL_THICKNESS; shapeHeight = SCREEN_HEIGHT;
				 */
			}
			if ( commandString.equals( "left" ) )
			{

				vertices[0] = 0;
				vertices[1] = SCREEN_HEIGHT;
				vertices[2] = 0;
				vertices[3] = 0;
				vertices[4] = WALL_THICKNESS;
				vertices[5] = WALL_THICKNESS;
				vertices[6] = WALL_THICKNESS;
				vertices[7] = SCREEN_HEIGHT - WALL_THICKNESS;

				/*
				 * ulX=0; ulY=0; shapeWidth = WALL_THICKNESS; shapeHeight = SCREEN_HEIGHT;
				 */
			}
			if ( commandString.equals( "down" ) )
			{

				vertices[0] = SCREEN_WIDTH;
				vertices[1] = SCREEN_HEIGHT;
				vertices[2] = 0;
				vertices[3] = SCREEN_HEIGHT;
				vertices[4] = WALL_THICKNESS;
				vertices[5] = SCREEN_HEIGHT - WALL_THICKNESS;
				vertices[6] = SCREEN_WIDTH - WALL_THICKNESS;
				vertices[7] = SCREEN_HEIGHT - WALL_THICKNESS;

				/*
				 * ulX=0; ulY=SCREEN_HEIGHT-WALL_THICKNESS; shapeWidth = SCREEN_WIDTH; shapeHeight = WALL_THICKNESS;
				 */
			}
			else if ( commandString.equals( "all" ) )
			{
				actionPerformed( new ActionEvent( e.getSource(), e.getID(), "addWall-up" ) );
				actionPerformed( new ActionEvent( e.getSource(), e.getID(), "addWall-down" ) );
				actionPerformed( new ActionEvent( e.getSource(), e.getID(), "addWall-left" ) );
				actionPerformed( new ActionEvent( e.getSource(), e.getID(), "addWall-right" ) );
			}

			if ( !commandString.equals( "all" ) )
			{
				// tempEnt = new MovingEntity( new PolygonOrCircle( ulX, ulY, shapeWidth, shapeHeight ) );
				tempEnt = new MovingEntity( new PolygonOrCircle( vertices ), 
						Desktop.editFrame.getEditorContext().wallColor );
				tempEnt.setStationary( true );

				List<BaseEntity> ents = Desktop.editFrame.getEditorContext().getEntities();

				boolean isAnOverlap = false;

				for ( BaseEntity theEnt : ents )
				{
					if ( null != tempEnt.isOverlapping( theEnt ) )
					{
						System.out.println( "Didn't draw a " + commandString + " wall because somethings in the way." );
						isAnOverlap = true;
						break;
					}
				}
				if ( !isAnOverlap )
				{
					Desktop.editFrame.getEditorContext().RegisterEntity( tempEnt );
					Desktop.editFrame.getEditorContext().ProcessQueues();
				}
			}
		}
	}

	/**
	 * Component initialization.
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception
	{

		this.setTitle( "Creation Frame" );

		contentPane = (JPanel)getContentPane(); // access Frame's display
		contentPane.setLayout( new FlowLayout() ); // set Frame's Layout

		grpDrawMode = new ButtonGroup();

		JToggleButton btnSelect = new JToggleButton( new ImageIcon( Utility.GetBufferedImage( "mouseSelect.png" ) ) );
		btnSelect.setActionCommand( "select" );
		//btnSelect.setBorder( new EmptyBorder( 5, 9, 5, 9 ) );
		btnSelect.addActionListener( this );
		btnSelect.setSelected( true );
		btnSelect.setToolTipText( "Selection Mode" );
		grpDrawMode.add( btnSelect );

		JToggleButton btnDraw = new JToggleButton( new ImageIcon( Utility.GetBufferedImage( "drawPoly.png" ) ) );
		btnDraw.setActionCommand( "draw" );
		//btnDraw.setBorder( new EmptyBorder( 5, 9, 5, 9 ) );
		btnDraw.addActionListener( this );
		btnDraw.setToolTipText( "Draw Polygon" );
		grpDrawMode.add( btnDraw );

		JToggleButton btnCreation = new JToggleButton( new ImageIcon( Utility.GetBufferedImage( "create_22x25.png" ) ) );
		btnCreation.setActionCommand( "creation" );
		//btnCreation.setBorder( new EmptyBorder( 5, 11, 5, 11 ) );
		btnCreation.addActionListener( this );
		btnCreation.setToolTipText( "Creation Mode" );
		grpDrawMode.add( btnCreation );

		JToggleButton btnDrawSquare = new JToggleButton( new ImageIcon( Utility.GetBufferedImage( "box_26x25.png" ) ) );
		btnDrawSquare.setActionCommand( "drawSquare" );
		//btnDrawSquare.setBorder( new EmptyBorder( 5, 7, 5, 7 ) );
		btnDrawSquare.addActionListener( this );
		btnDrawSquare.setToolTipText( "Draw Rectangle" );
		grpDrawMode.add( btnDrawSquare );

		JToggleButton btnDrawCircle = new JToggleButton( new ImageIcon( Utility.GetBufferedImage( "circle_21x25.png" ) ) );
		btnDrawCircle.setActionCommand( "drawCircle" );
		//btnDrawCircle.setBorder( new EmptyBorder( 5, 11, 5, 11 ) );
		btnDrawCircle.addActionListener( this );
		btnDrawCircle.setToolTipText( "Draw Circle" );
		grpDrawMode.add( btnDrawCircle );

		contentPane.add( btnSelect );
		contentPane.add( btnDraw );
		contentPane.add( btnCreation );
		contentPane.add( btnDrawSquare );
		contentPane.add( btnDrawCircle );

		RecreateNodes();

		Box positioner = Box.createHorizontalBox();

		Box wallHolder = Box.createVerticalBox();
		JLabel walltext = ( new JLabel( "Wall Creator  " ) );
		Box row1 = Box.createHorizontalBox();
		Box row2 = Box.createHorizontalBox();
		Box row3 = Box.createHorizontalBox();

		JButton wallAddBtn1 = new JButton( new ImageIcon( Utility.GetBufferedImage( "arrow_up.png" ) ) );
		//wallAddBtn1.setBorder( new EmptyBorder( 2, 0, 2, 0 ) );
		wallAddBtn1.setActionCommand( "addWall-up" );
		wallAddBtn1.addActionListener( this );
		row1.add( wallAddBtn1 );
		JButton wallAddBtn2 = new JButton( new ImageIcon( Utility.GetBufferedImage( "arrow_left.png" ) ) );
		//wallAddBtn2.setBorder( new EmptyBorder( 0, 2, 0, 2 ) );
		wallAddBtn2.setActionCommand( "addWall-left" );
		wallAddBtn2.addActionListener( this );
		row2.add( wallAddBtn2 );
		JButton wallAddBtn3 = new JButton( new ImageIcon( Utility.GetBufferedImage( "obstacle.png" ) ) );
		//wallAddBtn3.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
		wallAddBtn3.setActionCommand( "addWall-all" );
		wallAddBtn3.addActionListener( this );
		row2.add( wallAddBtn3 );
		JButton wallAddBtn4 = new JButton( new ImageIcon( Utility.GetBufferedImage( "arrow_right.png" ) ) );
		//wallAddBtn4.setBorder( new EmptyBorder( 0, 2, 0, 2 ) );
		wallAddBtn4.setActionCommand( "addWall-right" );
		wallAddBtn4.addActionListener( this );
		row2.add( wallAddBtn4 );
		JButton wallAddBtn5 = new JButton( new ImageIcon( Utility.GetBufferedImage( "arrow_down.png" ) ) );
		//wallAddBtn5.setBorder( new EmptyBorder( 2, 0, 2, 0 ) );
		wallAddBtn5.setActionCommand( "addWall-down" );
		wallAddBtn5.addActionListener( this );
		row3.add( wallAddBtn5 );

		positioner.add( walltext );

		wallHolder.add( row1 );
		wallHolder.add( row2 );
		wallHolder.add( row3 );

		positioner.add( wallHolder );

		contentPane.add( positioner );

	}

	// When an entity is selected from the tree
	public void valueChanged( TreeSelectionEvent e )
	{
		this.setMode( "creation" );
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)entityTree.getLastSelectedPathComponent();

		if ( node == null )
			return;

		// Retrieve Object associate with node
		Object nodeInfo = node.getUserObject();

		// Make sure it's not a folder and that it's a class
		if ( node.isLeaf() && nodeInfo instanceof Class )
		{
			Class c = (Class)nodeInfo;

			selectedClass = c;

			/*
			 * // If class is not associable with a BaseEntity object if ( !BaseEntity.class.isAssignableFrom( c ) ) { System.err.println( "Trying to
			 * instantiate something that's not a BaseEntity" ); return; } BaseEntity be; try { // Create entity object from class be =
			 * (BaseEntity)c.newInstance(); } catch ( Exception exception ) { System.err.println( exception.toString() ); return; }
			 * this.setSelectedEntity( be );
			 */
		}
	}

	/**
	 * Refresh node tree (e.g. after using integrated compiler)
	 */
	public void RecreateNodes()
	{
		// if there is already a tree, remove listener
		if ( entityTree != null )
			entityTree.removeTreeSelectionListener( this );// Removes old thing
		// from listening

		// Create tree
		entityTree = new JTree( createNodes() );
		entityTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		entityTree.addTreeSelectionListener( this );
		entityTree.expandPath( entityTree.getLeadSelectionPath() );

		for ( int i = 0; i < entityTree.getRowCount(); i++ )
		{
			entityTree.expandRow( i );
		}

		// If scroll pane to contain tree exists, remove it
		if ( treeView != null )
			contentPane.remove( treeView );

		// Create pane again
		treeView = new JScrollPane( entityTree );
		treeView.setPreferredSize( new Dimension( 170, 285 ) );

		// Add to main pane and redraw
		contentPane.add( treeView );
		contentPane.repaint();
	}

	/**
	 * Create addable-object tree structure. Each node's associated object is a BaseEntity. Currently all nodes are hard-coded.
	 * 
	 * @return Returns the top node
	 */
	public DefaultMutableTreeNode createNodes()
	{
		// Top tree node
		DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Entities" );
		// Categories
		DefaultMutableTreeNode moving = null;
		DefaultMutableTreeNode stationary = null;
		// DefaultMutableTreeNode background = null;
		DefaultMutableTreeNode custom = null;
		// Leafs
		DefaultMutableTreeNode characters = null;
		DefaultMutableTreeNode balls = null;
		DefaultMutableTreeNode planets = null;
		DefaultMutableTreeNode polygons = null;
		DefaultMutableTreeNode forces = null;

		// Create category for movable entities
		moving = new DefaultMutableTreeNode( "Moving Entities" );
		top.add( moving );

		// Create sub-categories of movable entities
		characters = new DefaultMutableTreeNode( "Characters" );
		characters.add( new ClassTreeNode( Bot.class ) );

		balls = new DefaultMutableTreeNode( "Balls" );
		balls.add( new ClassTreeNode( Ball_Soccer.class ) );
		balls.add( new ClassTreeNode( Ball_Bowling.class ) );
		balls.add( new ClassTreeNode( Ball_Beach.class ) );
		balls.add( new ClassTreeNode( Ball_Smile.class ) );

		planets = new DefaultMutableTreeNode( "Planets" );
		planets.add( new ClassTreeNode( Planet1.class ) );
		planets.add( new ClassTreeNode( Planet2.class ) );
		planets.add( new ClassTreeNode( Planet3.class ) );

		polygons = new DefaultMutableTreeNode( "Polygons" );
		polygons.add( new ClassTreeNode( Square.class ) );
		polygons.add( new ClassTreeNode( Triangle.class ) );
		polygons.add( new ClassTreeNode( Parallelogram.class ) );
		// Add to moving category
		moving.add( characters );
		moving.add( balls );
		moving.add( planets );
		moving.add( polygons );

		// Create category for stationary entities
		stationary = new DefaultMutableTreeNode( "Stationary Entities" );
		top.add( stationary );

		// Create sub-categories of stationary entities
		forces = new DefaultMutableTreeNode( "Forces" );
		forces.add( new ClassTreeNode( RangedForceEntity.class ) );
		forces.add( new ClassTreeNode( FieldForceEntity.class ) );
		forces.add( new ClassTreeNode( ScaledMassFieldForce.class ) );

		stationary.add( forces );

		// Create category for user-made entities
		custom = new DefaultMutableTreeNode( "Custom Classes" );
		top.add( custom );

		for ( String s : CodeCompiler.getConversionTable().keySet() )
		{
			custom.add( new ClassTreeNode( CodeCompiler.GetClassFor( s ), s ) );
		}

		return top;
	}

	public Class<?> getSelectedClass()
	{
		return selectedClass;
	}

	public <T> Class<? extends T> getSelectedClass( Class<T> baseClass )
	{
		if ( baseClass == null )
			throw new IllegalArgumentException();

		if ( selectedClass == null )
			return null;

		try
		{
			Class<? extends T> typeClass = selectedClass.asSubclass( baseClass );

			return typeClass;
		}
		catch ( ClassCastException e )
		{
			return null;
		}

	}

	/**
	 * Sets the current drawing mode, and depresses the appropriate JToggleButton
	 * 
	 * @param s The string representation of the current mode. <br>
	 *        select - sets mouse clicks to be able to move and resize entities. draw - a series of mouse clicks will draw out a polygon, or a mouse
	 *        drag will free-draw any shape. drawSquare - a mouse drag creates rectangles drawCircle - a mouse drag creates circles creation - mouse
	 *        clicks will add the object type selected in the tree
	 */

	public void setMode( String s )
	{
		Desktop.editFrame.getEditorContext().setMode( s );

		JToggleButton b;

		for ( Enumeration e = grpDrawMode.getElements(); e.hasMoreElements(); )
		{
			b = (JToggleButton)e.nextElement();
			if ( b.getActionCommand() == s )
			{
				b.setSelected( true );
			}
		}
	}

	/**
	 * Specific type of object stored in each leaf node of the creation tree. This allows the node to contain an Entity object, but will display a
	 * simple name in the node's label
	 * 
	 * @author The UC Regents
	 */
	private class ClassTreeNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String m_DisplayedName;

		public ClassTreeNode( Class c )
		{
			this( c, c.getSimpleName().replace( "_", " " ) );
		}

		public ClassTreeNode( Class c, String displayedName )
		{
			super( c );

			m_DisplayedName = displayedName;
		}

		@Override
		public String toString()
		{
			return m_DisplayedName;
		}

	}

}
