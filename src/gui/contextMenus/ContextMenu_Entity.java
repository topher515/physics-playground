package gui.contextMenus;

import gui.Desktop;
import gui.EditorPanel;
import gui.events.EventManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import physics.BaseEntity;
import physics.MovingEntity;

/**
 * The popup menu that appears when right-clicking an entity.
 * 
 * @author The UC Regents
 */
public class ContextMenu_Entity extends JPopupMenu implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BaseEntity m_Entity;
	EditorPanel m_Panel;

	JLabel m_Label = new JLabel("default");
	JCheckBoxMenuItem m_Stationary;
	JCheckBoxMenuItem m_Visible;


	/**
	 * Default constructor
	 */
	public ContextMenu_Entity()
	{
		this( null, null );
	}

	/**
	 * Constructs a popup menu that applies to the BaseEntity, with a reference to the panel from which it originated.
	 * 
	 * @param be The entity that the popup menu is modifying
	 * @param panel The panel on which the menu resides
	 */
	private ContextMenu_Entity( BaseEntity be, EditorPanel panel )
	{
		super( "Context Menu" );

		setPopupSize(170,130);
		
		m_Entity = be;
		m_Panel = panel;
		
		add( m_Label );
		
		add( new JSeparator());
			
		JMenuItem j = new JMenuItem( "Remove Object" );
		j.setActionCommand( "delete" );
		j.addActionListener( this );
		add( j );

		add( new JSeparator());
		
		j = new JMenuItem( "Add Event" );
		j.setActionCommand( "addEvent" );
		j.addActionListener( this );
		add( j );
		
		add( new JSeparator());
		
		//setEntity( be );

	}

	protected BaseEntity getEntity()
	{
		return m_Entity;
	}

	public void setEntity( BaseEntity entity )
	{
		m_Entity = entity;
		
		if ( m_Stationary != null )
			this.remove( m_Stationary );

		if ( m_Visible != null )
			this.remove( m_Visible );
		
		if ( entity == null )
			return;

		if ( m_Entity instanceof MovingEntity )
		{
			m_Stationary = new JCheckBoxMenuItem( "Set Stationary" );
			m_Stationary.setActionCommand( "setStationary" );
			m_Stationary.addActionListener( this );

			m_Stationary.setSelected( ( (MovingEntity)m_Entity ).isStationary() );

			add( m_Stationary );
		}

		m_Visible = new JCheckBoxMenuItem( "Set Visible" );
		m_Visible.setActionCommand( "setVisible" );
		m_Visible.addActionListener( this );

		m_Visible.setSelected( m_Entity.isVisible() );

		add( m_Visible );
		
		String label;
		if (entity.getName() == null)
		{
			label = entity.getClass().toString();
			label = label.substring(6,label.length());
			label = label.trim();
		}
		else {
			label = entity.getName();
		}
		m_Label.setText(label);
		
		this.repaint();
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( m_Entity == null )
			return;
		
		if ( m_Panel == null )
		{
			m_Panel = Desktop.editFrame.getEditorContext();
		}

		if ( "delete".equals( e.getActionCommand() ) )
		{
			// m_Panel.InternalEntityUnregistration( m_Entity );

			m_Entity.QueueDeletion();

			this.setVisible( false );

			m_Panel.ProcessQueues();

			m_Panel.repaint();
		}

		if ( "setStationary".equals( e.getActionCommand() ) && m_Entity instanceof MovingEntity )
		{
			( (MovingEntity)m_Panel.getSelectedEntity() ).setStationary( !( (MovingEntity)m_Panel.getSelectedEntity() ).isStationary() );

			Desktop.opf.updateOPF();
			m_Panel.repaint();
		}

		if ( "setVisible".equals( e.getActionCommand() ) )
		{
			m_Panel.getSelectedEntity().setVisible( !m_Panel.getSelectedEntity().isVisible() );

			Desktop.opf.updateOPF();
			m_Panel.repaint();
		}

		if ( "addEvent".equals( e.getActionCommand() ) )
		{
			EventManagerFrame eFrame = new EventManagerFrame( m_Entity );
			eFrame.setSize( 800, 300 );
			eFrame.setLocation( ( Desktop.desktop.getWidth() - eFrame.getWidth() ) / 2,
					( Desktop.desktop.getHeight() - eFrame.getHeight() ) / 2 );

			eFrame.setVisible( true );
			eFrame.setFocusable( true );
			// Desktop.desktop.add(eFrame);
			eFrame.toFront();
			// Utility.FocusOn(eFrame);
		}
		
		if ( "setVector".equals( e.getActionCommand() ) ) 
		{
			m_Panel.setMode("drawing");
		}

	}
	
	public void setMenuLabel(String label)
	{
		m_Label.setText(label);
	}
}
