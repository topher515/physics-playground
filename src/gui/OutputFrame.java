package gui;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JInternalFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OutputFrame extends JInternalFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextArea aTextArea = new JTextArea();

	JScrollPane jp;
	
	/**
	 * Creates a new RedirectFrame. From the moment it is created, all
	 * System.out messages and error messages (if requested) are diverted to
	 * this frame
	 * 
	 * //Code adapted and heavily modified from code by "Real Gagnon"
	 * 
	 * @param catchErrors
	 *            set this to true if you want the errors to also be caught
	 * @param width
	 *            the width of the frame
	 * @param height
	 *            the height of the frame
	 * 
	 */
	public OutputFrame( boolean catchErrors, int width, int height )
	{
		// Make output frame minimizable, maximizable, closable
		super( "Output Frame", true, true, true, true );

		setSize( width, height );
		aTextArea.setSize( width, height );

		aTextArea.setEditable( false );
		aTextArea.setAutoscrolls( true );

		this.setVisible( true );
		jp = new JScrollPane( aTextArea );
		jp.setAutoscrolls( true );
		this.add( jp );
		this.setLocation( 50, 450 );

		System.setOut( new PrintStream( new FilteredOutStream()) ); // catches System.out messages
		if ( catchErrors )
			System.setErr( new PrintStream( new FilteredErrStream()) ); // catches error messages

	}

	private final static PrintStream oldOut = System.out;
	private final static PrintStream oldErr = System.err;

	
	private abstract class FilteredStream extends FilterOutputStream
	{
		public FilteredStream( OutputStream aStream )
		{
			super( aStream );
		}

		@Override
		public void write( byte b[], int off, int len )
		{
			String aString = new String( b, off, len );

			JScrollBar vbar = jp.getVerticalScrollBar();
			boolean autoScroll = ( ( vbar.getValue() + vbar.getVisibleAmount() ) == vbar.getMaximum() );

			aTextArea.append( aString );

			if ( autoScroll )
				aTextArea.setCaretPosition( aTextArea.getDocument().getLength() );
		}
	}
	
	private class FilteredOutStream extends FilteredStream
	{
		public FilteredOutStream()
		{
			super( new ByteArrayOutputStream() );
		}
		
		@Override
		public void write( byte b[], int off, int len )
		{
			super.write( b, off, len );
			
			oldOut.write( b, off, len );
		}
	}
	
	private class FilteredErrStream extends FilteredStream
	{
		public FilteredErrStream()
		{
			super( new ByteArrayOutputStream() );
		}
		
		@Override
		public void write( byte b[], int off, int len )
		{
			super.write( b, off, len );
			
			oldErr.write( b, off, len );
		}
	}
}
