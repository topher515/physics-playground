/**
 * 
 */
package gui.undo;

import gui.EditorPanel;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import physics.BaseEntity;

/**
 * @author Andre
 *
 */
public class EntityRegistrationEdit extends AbstractUndoableEdit
{
	private static final long serialVersionUID = 1L;

	public EntityRegistrationEdit( EditorPanel panel, BaseEntity entity, boolean toRegister )
	{
		m_Panel = panel;
		m_Entity = entity;
		m_ToRegister = toRegister;
	}
	
	private EditorPanel m_Panel;
	private BaseEntity m_Entity;
	private boolean m_ToRegister;
	
	@Override
	public String getPresentationName()
	{
		return (m_ToRegister ? "Register" : "Unregister") + " " + m_Entity.toString();
	}

	@Override
	public void redo() throws CannotRedoException
	{
		super.redo();
		
		if( m_ToRegister )
		{
			m_Panel.RegisterEntity( m_Entity );
			m_Panel.selectEntity( m_Entity );
		}
		else
		{
			m_Panel.UnregisterEntity( m_Entity );
		}
		
		m_Panel.ProcessQueues();
		m_Panel.repaint();
	}

	@Override
	public void undo() throws CannotUndoException
	{
		super.undo();
		
		if( m_ToRegister )
		{
			m_Panel.UnregisterEntity( m_Entity );
		}
		else
		{
			m_Panel.RegisterEntity( m_Entity );
			m_Panel.selectEntity( m_Entity );
		}
		
		m_Panel.ProcessQueues();
		m_Panel.repaint();
	}
}
