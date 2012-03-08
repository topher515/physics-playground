/**
 * 
 */
package gui.contextMenus;

import gui.Desktop;
import gui.EditorPanel;
import gui.events.EventManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import physics.BaseEntity;
import physics.MovingEntity;

/**
 * The popup menu that appears when right clicking on
 * the Editor Panel, not on an entity.
 * 
 * @author CWilcox
 *
 */
public class ContextMenu_Panel extends JPopupMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BaseEntity m_Entity;

	EditorPanel m_Panel;

	JCheckBoxMenuItem m_Stationary;

	JCheckBoxMenuItem m_Visible;

	/**
	 * Default constructor
	 */
	public ContextMenu_Panel()
	{
		this( null, null );
	}

	/**
	 * Constructs a popup menu that applies to the BaseEntity, 
	 * with a reference to the panel from which it originated.
	 * 
	 * @param be The entity that the popup menu is modifying
	 * @param panel The panel on which the menu resides
	 */
	private ContextMenu_Panel( BaseEntity be, EditorPanel panel )
	{
		super( "Context Menu" );

		m_Entity = be;
		m_Panel = panel;

		JMenuItem j = new JMenuItem( "Delete" );
		j.setActionCommand( "delete" );
		j.addActionListener( this );
		// j.set
		add( j );

		setEntity( be );

		j = new JMenuItem( "Add Event" );
		j.setActionCommand( "addEvent" );
		j.addActionListener( this );
		add( j );

	}

	protected BaseEntity getEntity()
	{
		return m_Entity;
	}

	protected void setEntity( BaseEntity entity )
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
			m_Panel.UnregisterEntity( m_Entity );

			this.setVisible( false );

			if ( !m_Panel.getEntities().isEmpty() )
			{
				m_Panel.selectEntity( m_Panel.getEntities().get( m_Panel.getEntities().size() -1 ) );
			}
			else
				m_Panel.selectEntity( null );

			m_Panel.repaint();
		}

		if ( "setStationary".equals( e.getActionCommand() ) && m_Entity instanceof MovingEntity )
		{
			( (MovingEntity)m_Panel.getSelectedEntity() ).setStationary( !( (MovingEntity)m_Panel.getSelectedEntity() ).isStationary() );

			Desktop.opf.updateOPF();
		}

		if ( "setVisible".equals( e.getActionCommand() ) )
		{
			m_Panel.getSelectedEntity().setVisible( !m_Panel.getSelectedEntity().isVisible() );

			Desktop.opf.updateOPF();
		}

		if ( "addEvent".equals( e.getActionCommand() ) )
		{
			EventManagerFrame eFrame = new EventManagerFrame( m_Entity );
			eFrame.setSize( 800, 300 );
			eFrame.setLocation( ( Desktop.desktop.getWidth() - eFrame.getWidth() ) / 2, ( Desktop.desktop.getHeight() - eFrame.getHeight() ) / 2 );

			eFrame.setVisible( true );
			eFrame.setFocusable( true );
			// Desktop.desktop.add(eFrame);
			eFrame.toFront();
			// Utility.FocusOn(eFrame);
		}

	}
}
