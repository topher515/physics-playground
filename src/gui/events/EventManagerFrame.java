package gui.events;

import gui.Desktop;
import gui.opf.Setter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import physics.BaseEntity;

/**
 * The Event Manager Frame is an interface for constructing a User Event object. The Left tree contains the types of
 * events supported. The Right tree contains the types of resulting actions supported.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public class EventManagerFrame extends JDialog implements ActionListener, TreeSelectionListener, ListDataListener,
		ListSelectionListener
{
	static final long serialVersionUID = 0001;

	private BaseEntity selectedEntity;

	private List<UserEvent> events;// = new Vector<UserEvent>();

	private JScrollPane eventTreeView;

	private JScrollPane actionTreeView;

	private JTree eventTree;

	private JTree actionTree;

	private JPanel contentPane; // Why is this still around?

	private JPanel eventButtonPane;

	private JPanel actionButtonPane;

	private DefaultListModel eventListModel;

	private DefaultListModel actionListModel;

	private JScrollPane eventListPane;

	private JScrollPane actionListPane;

	private JList addedEvents;

	private JList addedActions;

	/**
	 * Default constructor
	 */
	public EventManagerFrame()
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

	/**
	 * Main constructor. Send the Manager a BaseEntity and it will populate the JLists with its UserEvent information,
	 * and associate a UserEvent with the entity.
	 * 
	 * @param b
	 *            The entity to read User Events from and write User Events to
	 */
	public EventManagerFrame( BaseEntity b )
	{
		super( Desktop.MainDesktop, true );

		selectedEntity = b;
		events = selectedEntity.getUserEvents();

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

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)eventTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode node2 = (DefaultMutableTreeNode)actionTree.getLastSelectedPathComponent();
		Object nodeInfo;

		if ( "addEvent".equals( e.getActionCommand() ) )
		{

			if ( node == null )
			{
				System.out.println( "Node is null" );
				return;
			}

			nodeInfo = node.getUserObject();
			if ( node.isLeaf() && nodeInfo instanceof UserEvent )
			{
				UserEvent temp = (UserEvent)nodeInfo;

				events.add( temp );
				populateEventList( events );
				addedEvents.setSelectedIndex( addedEvents.getLastVisibleIndex() );
			}
		}
		else if ( "removeEvent".equals( e.getActionCommand() ) )
		{
			int curIndex = addedEvents.getSelectedIndex();

			if ( curIndex >= 0 )
			{
				events.remove( curIndex );
				populateEventList( events );
				
				if ( !events.isEmpty() )
				{
					if( curIndex >= events.size() )
						curIndex = events.size()-1;
					
					populateActionList( events.get( curIndex ) );
					addedEvents.setSelectedIndex( curIndex );
				}
				else
				{

				}

			}
			else
			{
				System.out.println( "No selected value" );
			}
		}
		else if ( "addAction".equals( e.getActionCommand() ) )
		{
			Object nodeInfo2 = node2.getUserObject();

			if ( node2.isLeaf() && nodeInfo2 instanceof Object[] )
			{
				if ( node2 == null )
				{
					return;
				}

				int evtIndex = addedEvents.getSelectedIndex();
				if ( evtIndex >= 0 )
				{
					UserEvent temp = (UserEvent)addedEvents.getSelectedValue();
					Object[] methodAndParams = (Object[])node2.getUserObject();

					Method m = (Method)methodAndParams[0];

					EventParameterFrame epf = new EventParameterFrame( temp, m, this );
					epf.setSize( new Dimension( 300, 100 ) );
					epf.setLocation( ( Desktop.desktop.getWidth() - epf.getWidth() ) / 2,
							( Desktop.desktop.getHeight() - epf.getHeight() ) / 2 );
					epf.setVisible( true );
					// Desktop.desktop.add( epf );
					epf.toFront();
					// Utility.FocusOn( epf );

				}
			}
		}
		else if ( "removeAction".equals( e.getActionCommand() ) )
		{
			int curIndex = addedActions.getSelectedIndex();
			if ( curIndex >= 0 )
			{

				events.get( addedEvents.getSelectedIndex() ).removeMethod( curIndex );

				actionListModel.removeElementAt( addedActions.getSelectedIndex() );
				addedActions.setSelectedIndex( curIndex );

			}
			else
			{
				System.out.println( "No selected event" );
			}

		}
		else if ( "finish".equals( e.getActionCommand() ) )
		{

			if ( !events.isEmpty() )
			{
				selectedEntity.RegisterEvents( getSolidEventList( events ) );
			}
			this.dispose();

		}
	}

	/**
	 * Often a user will forget or not want to add actions to an added event type. Since events must have a method to
	 * invoke, these gaps in the event list need to be removed. getSolidEventList will provide the list of complete,
	 * acceptable events
	 * 
	 * @param evs
	 *            Vector of events to check from which to remove gaps
	 * @return Vector of gap-free events
	 */
	public List<UserEvent> getSolidEventList( List<UserEvent> evs )
	{
		List<UserEvent> temp = new Vector<UserEvent>();
		for ( UserEvent e : evs )
		{
			if ( !e.getMethods().isEmpty() )
			{
				temp.add( e );
			}
		}
		return temp;
	}

	/**
	 * Take a method and its parameters and insert them appropriately into the object and the action display list
	 * 
	 * @param m
	 *            Method to insert
	 * @param params
	 *            parameters sent to the method
	 */
	public void addActionToList( Method m, Object[] params )
	{

		UserEvent temp = (UserEvent)addedEvents.getSelectedValue();
		temp.addMethodAndParams( m, params );

		this.populateActionList( temp );

	}

	/**
	 * Component initialization.
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception
	{

		this.setTitle( "Event Manager Frame" );

		contentPane = (JPanel)getContentPane(); // access Frame's display
		contentPane.setLayout( new GridBagLayout() ); // set Frame's Layout
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		eventTree = new JTree( createEventNodes() );
		eventTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		eventTree.addTreeSelectionListener( this );

		eventTreeView = new JScrollPane( eventTree );
		eventTreeView.setPreferredSize( new Dimension( 200, 100 ) );
		eventTreeView.setVisible( true );

		JButton btnAddEvent = new JButton( "->" );
		btnAddEvent.setActionCommand( "addEvent" );
		btnAddEvent.addActionListener( this );

		JButton btnRemoveEvent = new JButton( "<-" );
		btnRemoveEvent.setActionCommand( "removeEvent" );
		btnRemoveEvent.addActionListener( this );

		eventButtonPane = new JPanel();
		// eventButtonPane.setSize(new Dimension(100, 100));
		eventButtonPane.setPreferredSize( new Dimension( 70, 100 ) );
		eventButtonPane.setVisible( true );
		eventButtonPane.add( btnAddEvent );
		eventButtonPane.add( btnRemoveEvent );

		eventListModel = new DefaultListModel();
		eventListModel = convertEventsToList( eventListModel, events );
		eventListModel.addListDataListener( this );

		addedEvents = new JList( eventListModel );
		// addedEvents = new JList( events );
		addedEvents.addListSelectionListener( this );
		addedEvents.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		addedEvents.setVisibleRowCount( -1 );
		addedEvents.setVisible( true );

		eventListPane = new JScrollPane( addedEvents );
		eventListPane.setPreferredSize( new Dimension( 100, 150 ) );

		actionListModel = new DefaultListModel();
		actionListModel.addListDataListener( this );

		addedActions = new JList( actionListModel );
		addedActions.setSelectionMode( ListSelectionModel.SINGLE_INTERVAL_SELECTION );
		addedActions.setVisibleRowCount( -1 );
		addedActions.setVisible( true );

		actionListPane = new JScrollPane( addedActions );
		actionListPane.setPreferredSize( new Dimension( 100, 150 ) );

		JButton btnAddAction = new JButton( "<-" );
		btnAddAction.setActionCommand( "addAction" );
		btnAddAction.addActionListener( this );

		JButton btnRemoveAction = new JButton( "->" );
		btnRemoveAction.setActionCommand( "removeAction" );
		btnRemoveAction.addActionListener( this );

		actionButtonPane = new JPanel();
		actionButtonPane.setSize( new Dimension( 100, 100 ) );
		actionButtonPane.setPreferredSize( new Dimension( 100, 100 ) );
		actionButtonPane.setVisible( true );
		actionButtonPane.add( btnAddAction );
		actionButtonPane.add( btnRemoveAction );

		actionTree = new JTree( createActionNodes() );
		actionTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
		actionTree.addTreeSelectionListener( this );

		actionTreeView = new JScrollPane( actionTree );
		actionTreeView.setPreferredSize( new Dimension( 200, 100 ) );
		actionTreeView.setVisible( true );

		contentPane.add( eventTreeView );
		contentPane.add( eventButtonPane );
		contentPane.add( eventListPane );
		contentPane.add( actionListPane );
		contentPane.add( actionButtonPane );

		GridBagConstraints threeLabelGBC = new GridBagConstraints();
		threeLabelGBC.gridwidth = GridBagConstraints.REMAINDER;
		contentPane.add( actionTreeView, threeLabelGBC );

		JButton btnFinish = new JButton( "Finish" );
		btnFinish.setActionCommand( "finish" );
		btnFinish.addActionListener( this );

		GridBagConstraints FinishGBC = new GridBagConstraints();
		FinishGBC.gridwidth = GridBagConstraints.CENTER;
		FinishGBC.weightx = 2;
		contentPane.add( btnFinish, FinishGBC );

		this.populateEventList( events );
		if ( addedEvents.getSelectedValue() instanceof UserEvent )
		{
			this.populateActionList( (UserEvent)addedEvents.getSelectedValue() );
		}

	}

	public void addUserEventToList( String s )
	{

	}

	public BaseEntity getSelectedEntity()
	{
		return selectedEntity;
	}

	public void setSelectedEntity( BaseEntity be )
	{
		selectedEntity = be;
	}

	/**
	 * Populate the event list with information corresponding to the vector of User Events. Also serves as a list
	 * refresh.
	 * 
	 * @param e
	 *            Vector of events from which to extract event information
	 */
	public void populateEventList( List<UserEvent> e )
	{
		eventListModel.removeAllElements();
		eventListModel = convertEventsToList( eventListModel, e );

		addedEvents.setSelectedIndex( 0 );
		addedEvents.ensureIndexIsVisible( 0 );
	}

	/**
	 * Populate the action list based on the Vector of events Also serves as a refresh.
	 * 
	 * @param e
	 *            Vector of events from which to extract action information
	 */
	public void populateActionList( UserEvent e )
	{
		actionListModel.removeAllElements();
		if ( e != null )
		{

			List<Method> m = e.getMethods();
			List<Object[]> params = e.getParameters();

			String showParams = "(";

			int i = 0;
			for ( Method meth : m )
			{
				Object[] p = params.get( i );
				if ( p.length == 1 )
				{
					showParams += p[0] + ")";
				}
				else
				{
					for ( Object o : params )
					{
						showParams += o + ", ";

					}
					showParams += ")";
				}
				actionListModel.insertElementAt( meth.getName() + showParams, actionListModel.getSize() );
				addedActions.setSelectedIndex( actionListModel.getSize() );
				addedActions.ensureIndexIsVisible( 0 );
			}
		}
		else
		{

		}
		/*
		 * actionListModel.removeAllElements(); //actionListModel = convertEventsToList(eventListModel, e); if (e !=
		 * null && e.getMethod() != null) { actionListModel.add(0, e.getMethod()); } addedActions.setSelectedIndex(0);
		 * addedActions.ensureIndexIsVisible(0);
		 */
	}

	/**
	 * Take a vector of events and convert them into a DefaultListModel so that we can dynamically add and remove from
	 * the list.
	 * 
	 * @param l
	 *            The ListModel to add events to
	 * @param e
	 *            The vector of events to add to the listmodel
	 * @return The completed DefaultListModel populated with event information
	 */
	public DefaultListModel convertEventsToList( DefaultListModel l, List<UserEvent> e )
	{
		for ( UserEvent evt : e )
		{
			l.add( l.getSize(), evt );
		}
		return l;
	}

	/**
	 * Create the nodes to construct the event tree
	 * 
	 * @return The top node of the tree, with all sub-folders and leaves
	 */
	public DefaultMutableTreeNode createEventNodes()
	{
		// Top tree node
		DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Events" );
		// tmEvents = new DefaultTreeModel( top );

		// Categories
		DefaultMutableTreeNode mouseEvents = null;
		DefaultMutableTreeNode keyEvents = null;
		DefaultMutableTreeNode objectEvents = null;

		// Create category for movable entities
		mouseEvents = new DefaultMutableTreeNode( "Mouse" );
		top.add( mouseEvents );

		int pressed = MouseEvent.MOUSE_PRESSED;
		// Create sub-categories of movable entities
		mouseEvents.add( new EventTreeNode( new UserEvent( pressed, MouseEvent.BUTTON1, selectedEntity ), "Left" ) );
		mouseEvents.add( new EventTreeNode( new UserEvent( pressed, MouseEvent.BUTTON2, selectedEntity ), "Middle" ) );
		mouseEvents.add( new EventTreeNode( new UserEvent( pressed, MouseEvent.BUTTON3, selectedEntity ), "Right" ) );
		top.add( mouseEvents );

		pressed = KeyEvent.KEY_PRESSED;
		keyEvents = new DefaultMutableTreeNode( "Keyboard" );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_UP, selectedEntity ), "Up" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_DOWN, selectedEntity ), "Down" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_LEFT, selectedEntity ), "Left" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_RIGHT, selectedEntity ), "Right" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_W, selectedEntity ), "W" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_A, selectedEntity ), "A" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_S, selectedEntity ), "S" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_D, selectedEntity ), "D" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_SPACE, selectedEntity ), "Space" ) );
		keyEvents.add( new EventTreeNode( new UserEvent( pressed, KeyEvent.VK_ENTER, selectedEntity ), "Enter" ) );
		top.add( keyEvents );

		pressed = UserEvent.OBJECT_COLLIDED;
		objectEvents = new DefaultMutableTreeNode( "Object Events" );
		objectEvents.add( new EventTreeNode( new UserEvent( UserEvent.OBJECT_COLLIDED, 0, selectedEntity ), "Impact" ) );
		objectEvents.add( new EventTreeNode( new UserEvent( UserEvent.OBJECT_DESTROYED, 0, selectedEntity ),
				"Destroyed" ) );
		top.add( objectEvents );

		return top;
	}

	/**
	 * Construct the tree of action nodes.
	 * 
	 * @return The top node of the action tree with all the associate folders and leaves
	 */
	public DefaultMutableTreeNode createActionNodes()
	{
		// Top tree node
		DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Actions" );
		// tmActions = new DefaultTreeModel( top );
		// Categories
		DefaultMutableTreeNode moveAction = null;
		DefaultMutableTreeNode propertyAction = null;

		// Create category for movable entities
		moveAction = new DefaultMutableTreeNode( "Movement" );
		propertyAction = new DefaultMutableTreeNode( "Properties" );
		// Create sub-categories of movable entities
		try
		{

			Method[] methods = selectedEntity.getClass().getMethods();
			for ( Method m : methods )
			{
				if ( m.isAnnotationPresent( Setter.class ) )
				{

					if ( m.getAnnotation( Setter.class ).eventType().equals( "Movement" ) )
					{
						moveAction.add( new ActionTreeNode( m, new Object[]{ new Object() }, m.getName() ) );
					}
					else if ( m.getAnnotation( Setter.class ).eventType().equals( "Properties" ) )
					{
						propertyAction.add( new ActionTreeNode( m, new Object[]{ new Object() }, m.getName() ) );
					}
				}

			}
			top.add( moveAction );
			top.add( propertyAction );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		/*
		 * top.add(keyEvents); pressed = UserEvent.OBJECT_COLLIDED; objectEvents = new DefaultMutableTreeNode("Object
		 * Events"); objectEvents.add(new EventTreeNode(new UserEvent(UserEvent.OBJECT_COLLIDED, 0, null, null,
		 * selectedEntity), "Impact")); objectEvents.add(new EventTreeNode(new UserEvent(UserEvent.OBJECT_DESTROYED, 0,
		 * null, null, selectedEntity), "Destroyed")); top.add(objectEvents);
		 */

		return top;
	}

	public void valueChanged( TreeSelectionEvent e )
	{

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)eventTree.getLastSelectedPathComponent();

		if ( node == null )
			return;

		// Retrieve Object associate with node
		Object nodeInfo = node.getUserObject();

		// Make sure it's not a folder and that it's a class
		if ( node.isLeaf() && nodeInfo instanceof String )
		{

			// String eventName = nodeInfo.toString();

			// this.setEventId( 1 );
		}
	}

	public void contentsChanged( ListDataEvent l )
	{

	}

	public void intervalAdded( ListDataEvent l )
	{
		if ( addedEvents.getSelectedIndex() < 0 )
		{
			addedEvents.setSelectedIndex( 0 );
		}
		// eventTree.remove(eventTree.getLastSelectedPathComponent());
	}

	public void intervalRemoved( ListDataEvent l )
	{

		if ( addedEvents.getSelectedIndex() < 0 )
		{
			addedEvents.setSelectedIndex( addedEvents.getLastVisibleIndex() );
		}
		else
		{
			// addedEvents.setSelectedIndex(addedEvents.getSelectedIndex() - 1
			// );
		}

		if ( addedActions.getSelectedIndex() < 0 )
		{
			addedActions.setSelectedIndex( addedActions.getLastVisibleIndex() );
		}
		else
		{
			// addedEvents.setSelectedIndex(addedEvents.getSelectedIndex() - 1
			// );
		}

	}

	public void valueChanged( ListSelectionEvent e )
	{
		if ( addedEvents.getSelectedValue() != null )
		{
			UserEvent evt = (UserEvent)addedEvents.getSelectedValue();
			this.populateActionList( evt );
		}
	}

	public void listDataEvent( ListDataEvent l )
	{

	}

	/**
	 * A special type of class designed for association with a tree node. Is essentially a UserEvent, but with the
	 * ability to associate a specific string with the event. Possibly redundant due to the UserEvent's inherant
	 * toString()
	 * 
	 * @author The UC Regents
	 */
	private class EventTreeNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String m_DisplayedName;

		private UserEvent ue;

		public EventTreeNode( UserEvent e, String displayedName )
		{ // TODO: Use Generic and the <> to restrict it to classes inheriting
			// from BaseEntity
			super( e );
			// 

			m_DisplayedName = displayedName;
		}

		public UserEvent getUserEvent()
		{
			return ue;
		}

		@Override
		public String toString()
		{
			return m_DisplayedName;
		}

	}

	/**
	 * A special type of class designed for association with a tree node. Is essentially a set of Method and Parameters,
	 * but with the ability to associate a specific string with the event. Possibly redundant due to the addActionToList
	 * method
	 * 
	 * @author The UC Regents
	 */
	private class ActionTreeNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String m_DisplayedName;

		private Object[] methodAndParams;

		public ActionTreeNode( Method m, Object[] p, String displayedName )
		{ // TODO: Use Generic and the <> to restrict it to classes inheriting
			// from BaseEntity
			this( new Object[]{ m, p }, displayedName );
			//
		}

		public ActionTreeNode( Object[] mAndP, String displayedName )
		{ // TODO: Use Generic and the <> to restrict it to classes inheriting
			// from BaseEntity
			super( mAndP );
			// 

			m_DisplayedName = displayedName;
		}

		public Object[] getMethodAndParams()
		{
			return methodAndParams;
		}

		@Override
		public String toString()
		{
			return m_DisplayedName;
		}

	}

}
