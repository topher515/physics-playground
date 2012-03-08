package gui.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class SafeUndoManager extends UndoManager
{
	private static final long serialVersionUID = 1L;
	
	private boolean m_NoAdd = false;	//So that undoing an action doesn't trigger the undone action to be added back

	public SafeUndoManager()
	{
		super();
	}

	@Override
	public synchronized boolean addEdit( UndoableEdit arg0 )
	{
		if( !m_NoAdd )
		{
			return super.addEdit( arg0 );
		}
		
		return false;
	}

	@Override
	public synchronized void redo() throws CannotRedoException
	{
		m_NoAdd = true;
		super.redo();
		m_NoAdd = false;
	}

	@Override
	public synchronized void undo() throws CannotUndoException
	{
		m_NoAdd = true;
		super.undo();
		m_NoAdd = false;
	}
}
