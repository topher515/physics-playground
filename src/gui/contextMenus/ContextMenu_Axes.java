package gui.contextMenus;

import gui.Desktop;
import gui.EditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import physics.BaseEntity;

public class ContextMenu_Axes extends JPopupMenu implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BaseEntity m_Entity;

	EditorPanel m_Panel;

	public ContextMenu_Axes()
	{
		this( null, null );
	}

	private ContextMenu_Axes( BaseEntity be, EditorPanel panel )
	{
		super( "Context Menu" );

		m_Entity = be;
		m_Panel = panel;

		JMenuItem j = new JCheckBoxMenuItem( "Set Visible" );
		j.setActionCommand( "setVisible" );
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
	}

	public void actionPerformed( ActionEvent e )
	{
		if ( m_Entity == null )
			return;
		
		if( m_Panel == null );
			m_Panel = Desktop.editFrame.getEditorContext();
		
		if( "setVisible".equals(e.getActionCommand()) )
		{
				m_Panel.setDrawAxes( false );
				m_Panel.repaint();
		}
    }
}
