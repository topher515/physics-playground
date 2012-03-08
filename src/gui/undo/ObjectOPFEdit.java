package gui.undo;

import gui.Desktop;
import gui.opf.DesktopOPF;

import java.lang.reflect.Method;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class ObjectOPFEdit extends AbstractUndoableEdit
{
	private static final long serialVersionUID = 1L;
	
	//( Object obj, Method m, Object param, Object oldParam )
	
	private Object m_Object;
	private Method m_Method;
	private Object m_Param;
	private Object m_OldParam;
	
	@Override
	public String getPresentationName()
	{
		return "TODO: Some OPF Presention name";
	}
	
	public ObjectOPFEdit( Object object, Method method, Object param, Object oldParam )
	{
		m_Object = object;
		m_Method = method;
		m_Param = param;
		m_OldParam = oldParam;
	}
	
	@Override
	public void redo() throws CannotRedoException
	{
		super.redo();
		
		DesktopOPF.changeProperty( m_Object, m_Method, m_Param );
		
		Desktop.editFrame.getEditorContext().repaint();
		Desktop.opf.updateOPF();
	}

	@Override
	public void undo() throws CannotUndoException
	{
		super.undo();
		
		DesktopOPF.changeProperty( m_Object, m_Method, m_OldParam );
		
		Desktop.editFrame.getEditorContext().repaint();
		Desktop.opf.updateOPF();
	}
}
