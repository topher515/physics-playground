package gui;

import serialization.CustomXMLFileFilter;
import serialization.Loader;
import serialization.Saver;
import util.Utility;
import edu.stanford.ejalbert.BrowserLauncher;
import gui.opf.DesktopOPF;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import physics.Arena;

/**
 * This is the main Desktop Application. It launches the appropriate Internal Frames that allow interaction with the editor
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public class Desktop extends JFrame implements ActionListener, ChangeListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int m_LastLayer;

	private Point m_LastComponentPoint;

	// Internal Frames are made public so that any
	// internal frame can interact with any other
	public static JDesktopPane desktop;

	public static Desktop MainDesktop;

	public static DesktopArena dtArena;

	public static CreationFrame cFrame;

	public static OutputFrame oFrame;

	public static DesktopOPF opf;

	public static EditorFrame editFrame;

	private JSlider zoom;

	private JCheckBox displayArenaAxes;
	


	private final int SLIDER_MAX = 200;

	private final int SLIDER_MIN = 10;

	private File workingFile;

	private String softwareTitle = "Playground";
	
	private JCheckBox snapToGrid;

	public Desktop()
	{

		super( "Playground (UC Regents)" );

		assert MainDesktop != null : "Should not create more than one Desktop during the life of the program.";

		MainDesktop = this;

		m_LastComponentPoint = new Point( 10, 10 );

		// Make the big window be indented 50 pixels from each edge
		// of the screen.
		int inset = 5;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds( inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2 );

		// Set up the GUI.
		desktop = new JDesktopPane(); // a specialized layered pane

		// Create file menu
		setJMenuBar( createMenuBar() );

		// Create Internal Frames
		createFrames();

		// Make dragging a little faster but perhaps uglier.
		// desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		// Use getContentPane.add to get toolbar to dock properly
		getContentPane().add( desktop, BorderLayout.CENTER );
		getContentPane().add( createManagerToolbar(), BorderLayout.NORTH );
		//getContentPane().add( createSceneToolbar(), BorderLayout.SOUTH );
	}

	protected JDesktopPane getDesktopPane()
	{
		return desktop;
	}

	public static void main( String[] args )
	{
		 /*
		  * Schedule a job for the event-dispatching thread:
		  * creating and showing this application's GUI. 
		  */
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				try
				{
					createAndShowGUI();
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}

			}
		} );
	}

	/**
	 * Manager toolbar handles important functions like run, pause, and reset
	 * 
	 * @return JToolBar
	 */
	protected JToolBar createManagerToolbar()
	{

		JToolBar jt = new JToolBar();

		// jt.setLayout(new java.awt.GridBagLayout());

		jt.setSize( new Dimension( 50, 30 ) );
		jt.setPreferredSize( new Dimension( 50, 30 ) );
		jt.setVisible( true );
		jt.setLocation( 100, 100 );

		JButton startGame = new JButton( new ImageIcon( Utility.GetBufferedImage( "run.png" ) ) );
		JButton pauseGame = new JButton( new ImageIcon( Utility.GetBufferedImage( "pause.png" ) ) );
		JButton resetGame = new JButton( new ImageIcon( Utility.GetBufferedImage( "reset.png" ) ) );

		JToolBar.Separator separator1 = new JToolBar.Separator( new Dimension( 22, 1 ) );

		JLabel axesText = new JLabel( "Collision Axes:" );
		// displayEditorAxes = new JCheckBox( "Editor", Desktop.editFrame.getEditorContext().getDrawAxes() );
		displayArenaAxes = new JCheckBox( "Arena", false );
		
		JToolBar.Separator separator2 = new JToolBar.Separator( new Dimension( 22, 1 ) );

		snapToGrid = new JCheckBox( "Snap To Grid", editFrame.getEditorContext().snapToGrid );
	
		JToolBar.Separator separator3 = new JToolBar.Separator( new Dimension( 22, 1 ) );

		zoom = new JSlider( SLIDER_MIN, SLIDER_MAX, 100 - 32 );
		zoom.setMaximumSize( new Dimension( 150, 20 ) );
		JButton resetZoom = new JButton( "Reset" );

		JLabel zoomText = new JLabel( "Zoom:" );

		startGame.setActionCommand( "Run" );
		pauseGame.setActionCommand( "Pause" );
		resetGame.setActionCommand( "Reset" );

		// displayEditorAxes.setActionCommand( "displayEditorAxes" );
		displayArenaAxes.setActionCommand( "displayArenaAxes" );
		snapToGrid.setActionCommand("snapToGrid");
		resetZoom.setActionCommand( "resetZoom" );

		startGame.setToolTipText( "Run game" );
		startGame.setToolTipText( "Pause game" );
		startGame.setToolTipText( "Reset game" );

		startGame.setBorder( BorderFactory.createRaisedBevelBorder() );
		pauseGame.setBorder( BorderFactory.createRaisedBevelBorder() );
		resetGame.setBorder( BorderFactory.createRaisedBevelBorder() );

		startGame.addActionListener( this );
		pauseGame.addActionListener( this );
		resetGame.addActionListener( this );
		// displayEditorAxes.addActionListener( this );
		displayArenaAxes.addActionListener( this );
		snapToGrid.addActionListener( this );
		
		zoom.addChangeListener( this );
		resetZoom.addActionListener( this );

		jt.add( startGame );
		jt.add( pauseGame );
		jt.add( resetGame );

		jt.add( separator1 );

		jt.add( axesText );
		// jt.add( displayEditorAxes );
		jt.add( displayArenaAxes );

		jt.add( separator2 );
		
		jt.add( snapToGrid );
		
		jt.add( separator3 );
		
		jt.add( zoomText );
		jt.add( zoom );
		jt.add( resetZoom );

		return jt;
	}
	
	/**
	 * Manager toolbar handles Scene creation and management
	 * 
	 * @return JToolBar
	 */
	protected JPanel createSceneToolbar()
	{
		JPanel jp = new JPanel();

		// jt.setLayout(new java.awt.GridBagLayout());
		
		jp.setSize( new Dimension( 50, 30 ) );
		jp.setPreferredSize( new Dimension( 50, 30 ) );
		jp.setVisible( true );
		jp.setLocation( 100, 100 );
		
		
		
		return jp;
	}

	@Override
	protected void addImpl( Component arg0, Object arg1, int arg2 )
	{
		if ( arg0 instanceof JInternalFrame )
		{
			JInternalFrame jFrame = (JInternalFrame)arg0;

			jFrame.setLayer( ++m_LastLayer );
		}

		Point loc = arg0.getLocation();
		if ( loc.x == 0 && loc.y == 0 ) // Creating it without a forced/specified location
		{

			if ( m_LastComponentPoint != null )
			{

				if ( ( m_LastComponentPoint.x += 30 ) >= this.getWidth() )
					m_LastComponentPoint.x = 30;

				if ( ( m_LastComponentPoint.y += 30 ) >= this.getHeight() )
					m_LastComponentPoint.y = 30;

				arg0.setLocation( m_LastComponentPoint );
			}
		}

		super.addImpl( arg0, arg1, arg2 );
	}

	/**
	 * Create the File Menu Bar
	 */
	protected JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		// Set up the lone menu.
		JMenu menu = new JMenu( "File" );
		menu.setMnemonic( KeyEvent.VK_F );
		menuBar.add( menu );

		// Set up the first menu item.
		JMenuItem menuItem = new JMenuItem( "New" );
		menuItem.setMnemonic( KeyEvent.VK_N );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );
		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{

				switch( JOptionPane.showConfirmDialog( Desktop.MainDesktop, "Would you like to save the current editor state?", "Save",
						JOptionPane.YES_NO_CANCEL_OPTION ) )
				{
					case 0: // Yes
					{
						if ( !TrySave( false ) ) // If canceled, cancel the new
							break;
						// Else, fall through to case 1

					}
					case 1: // No
					{
						// Get rid of all the objects!
						if ( Desktop.dtArena != null )
						{
							Desktop.dtArena.getArenaContext().depopulate();
							Desktop.dtArena.getArenaContext().ProcessQueues();
						}

						Desktop.editFrame.getEditorContext().getUndoManager().discardAllEdits();
						Desktop.editFrame.getEditorContext().depopulate();
						Desktop.editFrame.getEditorContext().ProcessQueues();

						workingFile = null;

						MainDesktop.setTitle( MainDesktop.softwareTitle );

						break;
					}
					case 2: // Cancel
					{
						break; // Do Nothing
					}
				}
			}
		} );
		menu.add( menuItem );

		// Set up the first menu item.
		menuItem = new JMenuItem( "Open..." );
		menuItem.setMnemonic( KeyEvent.VK_O );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );

		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{

				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter( new CustomXMLFileFilter() );

				int returnVal = chooser.showOpenDialog( Desktop.MainDesktop );

				if ( returnVal == JFileChooser.APPROVE_OPTION )
				{
					System.out.println( "You chose to open this file: " + chooser.getSelectedFile().getName() );

					workingFile = chooser.getSelectedFile();
					Desktop.MainDesktop.setTitle( softwareTitle + " - " + workingFile.getName() );

					Loader load = new Loader( workingFile );
					try
					{
						editFrame.getEditorContext().depopulate();
						editFrame.getEditorContext().ProcessQueues();
						editFrame.getEditorContext().RegisterEntities( load.Load() );
						editFrame.getEditorContext().ProcessQueues();
						editFrame.getEditorContext().repaint();

						opf.setObject( editFrame.getEditorContext() );
					}
					catch ( Exception f )
					{
						f.printStackTrace();
					}
				}
			}
		} );
		menu.add( menuItem );

		menu.add( new JSeparator() );

		// Set up the first menu item.
		menuItem = new JMenuItem( "Save" );
		menuItem.setMnemonic( KeyEvent.VK_S );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );

		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{
				TrySave( false );
			}
		} );
		menu.add( menuItem );

		// Set up menu item.
		menuItem = new JMenuItem( "Save As..." );
		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{
				TrySave( true );
			}
		} );
		menu.add( menuItem );
		
		menu.add( new JSeparator() );

		menuItem = new JMenuItem( "Quit" );
		menuItem.setMnemonic( KeyEvent.VK_Q );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.ALT_MASK ) );

		menuItem.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent arg0 )
			{
				switch( JOptionPane.showConfirmDialog( Desktop.MainDesktop, "Would you like to save the current editor state before quitting?",
						"Save", JOptionPane.YES_NO_CANCEL_OPTION ) )
				{
					case 0: // Yes
					{
						if ( !TrySave( false ) ) // If canceled, cancel the Quit
							break;
						// Else, fall through to case 1
					}
					case 1: // No
					{
						quit();
						break;
					}
					case 2: // Cancel
					{
						break; // Do Nothing
					}
				}
			}
		} );
		menu.add( menuItem );

		JMenu menu2 = new JMenu( "Objects" );
		menu.setMnemonic( KeyEvent.VK_D );
		menuBar.add( menu2 );

		// Starts a new compiler window
		// Todo: Make this an expanded menu item that
		// allows to choose whether the new object will
		// extend MovingEntity, BaseEntity, etc
		menuItem = new JMenuItem( "Code New Object" );
		menuItem.addActionListener( new ActionListener()
		{

			public void actionPerformed( ActionEvent arg0 )
			{

				JInternalFrame j = new CompilerFrame( 800, 400 );
				desktop.add( j );

				// Utility method to bring frame to front
				Utility.FocusOn( j );
			}
		} );
		menu2.add( menuItem );

		JMenu help = new JMenu( "Help" );
		menu.setMnemonic( KeyEvent.VK_H );
		menuBar.add( help );

		menuItem = new JMenuItem( "Help Index" );
		menuItem.addActionListener( new ActionListener()
		{

			public void actionPerformed( ActionEvent arg0 )
			{
				try
				{
					new BrowserLauncher( null ).openURLinBrowser( new File( "documents/index.html" ).toURI().toURL().toString() );
				}
				catch ( Exception ex )
				{
				}

			}
		} );

		help.add( menuItem );
		
		menuItem = new JMenuItem( "Java Docs" );
		menuItem.addActionListener( new ActionListener()
		{

			public void actionPerformed( ActionEvent arg0 )
			{
				try
				{
					new BrowserLauncher( null ).openURLinBrowser( new File( "documents/JavaDocs/index.html" ).toURI().toURL().toString() );
				}
				catch ( Exception ex )
				{
				}

			}
		} );

		help.add( menuItem );

		return menuBar;
	}

	// React to menu selections.
	public void actionPerformed( ActionEvent e )
	{
		if ( "Pause".equals( e.getActionCommand() ) )
		{
			if ( dtArena != null )
			{
				dtArena.getArenaContext().pause();
			}
		}
		else if ( "Run".equals( e.getActionCommand() ) )
		{

			// editFrame.getEditorContext().getJpop().setVisible( false );
			// If no arena has been launched, launch one
			// System.out.println("Arena Already Open");
			if ( dtArena == null )
			{
				// Create Arena
				dtArena = new DesktopArena( desktop );

				Arena arena = dtArena.getArenaContext();
				// Register clones of the Editor's entities with the main arena
				arena.RegisterEntities( editFrame.getEditorContext().getClonedEntities() );

				dtArena.setSize( new Dimension( editFrame.getWidth(), editFrame.getHeight() ) );
				dtArena.setLocation( desktop.getWidth() / 2 - dtArena.getWidth() / 2, 50 );
				dtArena.setVisible( true );
				dtArena.setClosable( true );
				arena.setDrawAxes( displayArenaAxes.isSelected() );
				if ( zoom.getValue() != 68 )
				arena.setZoomLevel( 3 * (float)( zoom.getValue() ) / 200 );
				arena.setAxesOrigin( (Point2D)editFrame.getEditorContext().getAxesOrigin().clone() );

				arena.setBackgroundColor( editFrame.getEditorContext().getBackgroundColor() );

				desktop.add( dtArena );

				Utility.FocusOn( dtArena );

				arena.start();

			}

			dtArena.getArenaContext().unpause();
		}

		else if ( "Reset".equals( e.getActionCommand() ) )
		{

			if ( dtArena != null )
			{
				dtArena.getArenaContext().repopulate();
			}

		}
		else if ( "displayEditorAxes".equals( e.getActionCommand() ) )
		{
			editFrame.getEditorContext().setDrawAxes( ( (JCheckBox)e.getSource() ).isSelected() );
			editFrame.getEditorContext().repaint();
		}
		else if ( "displayArenaAxes".equals( e.getActionCommand() ) )
		{
			if ( dtArena != null )
			{
				dtArena.getArenaContext().setDrawAxes( ( (JCheckBox)e.getSource() ).isSelected() );
				dtArena.getArenaContext().repaint();
			}
		}
		else if ( "snapToGrid".equals( e.getActionCommand()) )
		{
			editFrame.getEditorContext().snapToGrid = !editFrame.getEditorContext().snapToGrid;
		}
		else if ( "resetZoom".equals( e.getActionCommand() ) )
		{

			zoom.setValue( 100 - 32 );

			editFrame.getEditorContext().setZoomLevel( 1 );
			editFrame.getEditorContext().repaint();
			if ( dtArena != null )
			{
				dtArena.getArenaContext().setZoomLevel( 1 );
				dtArena.getArenaContext().repaint();
			}
		}
	}

	private boolean TrySave( boolean forceFileChoice )
	{

		if ( forceFileChoice || workingFile == null || workingFile.length() <= 0 )
		{
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter( new CustomXMLFileFilter() );

			int returnVal = chooser.showSaveDialog( this );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				System.out.println( "You chose to save this file: " + chooser.getSelectedFile().getName() );

				File f = chooser.getSelectedFile();

				if ( f.getName().indexOf( '.' ) == -1 )
					f = new File( f.getAbsolutePath() + ".pxml" );

				workingFile = f;
				this.setTitle( softwareTitle + " - " + workingFile.getName() );
				// actionPerformed( new ActionEvent( e.getSource(), e.getID(), "save" ) );
			}
			else
			{
				return false;
			}
		}

		Saver saver = new Saver( editFrame.getEditorContext().getEntities(), workingFile );
		
		saver.Save();

		return true;
	}

	/** Listen to the slider. */
	public void stateChanged( ChangeEvent e )
	{
		JSlider source = (JSlider)e.getSource();

		// System.out.println( "zoom: " + 3 * (float)( source.getValue() ) / 200 );
		editFrame.getEditorContext().setZoomLevel( 3 * (float)( source.getValue() ) / 200 );
		editFrame.getEditorContext().repaint();

		if ( dtArena != null )
		{
			dtArena.getArenaContext().setZoomLevel( 3 * (float)( source.getValue() ) / 200 );
			dtArena.getArenaContext().repaint();
		}
	}

	/**
	 * Creates the Internal Frames
	 */
	protected void createFrames()
	{

		// Editor Frame
		editFrame = new EditorFrame();
		editFrame.setSize( new Dimension( 660, 500 ) );
		editFrame.setVisible( true );
		editFrame.setLocation( 215, 0 );

		// Creation Frame -- Must be after Editor
		// because it references editor during construction
		cFrame = new CreationFrame();
		cFrame.setSize( new Dimension( 215, 500 ) );
		cFrame.setVisible( true );
		cFrame.setLocation( 0, 0 );

		// Console Output Frame
		oFrame = new OutputFrame( true, 700, 150 );
		oFrame.setSize( new Dimension( 875, 150 ) );
		oFrame.setLocation( 0, 500 );
		oFrame.setVisible( true );

		// Object Properties Frame
		opf = new DesktopOPF( editFrame.getEditorContext() );
		opf.setLocation( 875, 0 );
		opf.setVisible( true );

		// Add frames to desktop pane
		desktop.add( cFrame );
		desktop.add( editFrame );
		desktop.add( oFrame );
		desktop.add( opf );

		Utility.FocusOn( editFrame );

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{

		// Set Look&Feel of Desktop
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated( true );

		// Create and set up the window.
		Desktop frame = new Desktop();
		frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		// Display the window.
		frame.setVisible( true );
	}

	// Quit the application.
	protected void quit()
	{
		System.exit( 0 );
	}

}
