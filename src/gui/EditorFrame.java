package gui;

import java.awt.Dimension;

import javax.swing.JInternalFrame;

/**
 * The Editor Frame is where the user constructs the game environment. When the objects are added, and properties set,
 * the user may click "Run" to launch the arena which plays out the game.
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public final class EditorFrame extends JInternalFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EditorPanel editPanel;

	public EditorFrame()
	{
		// Make editor minimizable, maximizable, closable
		super( null, true, false, true, true );

		editPanel = new EditorPanel();
		editPanel.setSize( new Dimension( 660, 500 ) );
		editPanel.setVisible( true );
		this.setTitle( "Editor Panel" );

		this.add( editPanel );
	}

	public EditorPanel getEditorContext()
	{
		return editPanel;
	}
}
