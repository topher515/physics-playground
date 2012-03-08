package gui;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import physics.Arena;

/**
 * The Arena Class contains all of the entity data, and paints itself
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public final class DesktopArena extends JInternalFrame implements InternalFrameListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Arena arena;

	/**
	 * Constructs the DesktopArena by Initializing its components (jbInit)
	 * 
	 */
	public DesktopArena( JDesktopPane d )
	{
		arena = new Arena();
		arena.setVisible( true );
		arena.setSize( new Dimension( 600, 400 ) );

		this.setSize( arena.getPreferredSize() );
		this.setVisible( true );
		this.setTitle( "Arena Frame" );
		this.setMaximizable( true );
		this.add( arena );

		addInternalFrameListener( this );
	}

	public Arena getArenaContext()
	{
		return arena;
	}

	@Override
	public void setSize( int w, int h )
	{
		super.setSize( w, h );

		arena.setSize( w, h );
	}

	public void internalFrameClosed( InternalFrameEvent e )
	{
		Desktop.dtArena.getArenaContext().depopulate();
		Desktop.dtArena.getArenaContext().ProcessQueues();
		Desktop.dtArena.getArenaContext().stop();
		Desktop.dtArena = null;

		Desktop.editFrame.getEditorContext().requestFocus();
		Desktop.editFrame.getEditorContext().repaint();

	}

	public void internalFrameIconified( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}

	public void internalFrameDeiconified( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}

	public void internalFrameDeactivated( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}

	public void internalFrameActivated( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}

	public void internalFrameOpened( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}

	public void internalFrameClosing( InternalFrameEvent e )
	{
		//Required by the listner Interface
	}
}
