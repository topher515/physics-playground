package gui.events;

import util.Utility;

import gui.Desktop;
import gui.opf.OPFComponentType;
import gui.opf.Setter;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import physics.BaseEntity;

/**
 * The MainFrame Class contains the menu and children panels display sidebar and arena.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */
public class EventParameterFrame extends JDialog implements ActionListener
{
	static final long serialVersionUID = 0001;

	private BaseEntity selectedEntity;

	private JPanel jp;

	private EventManagerFrame referrerFrame;

	Method method;

	Object[] params;

	UserEvent event;

	Vector2d xyVec = new Vector2d();

	Point2d xyPt = new Point2d();

	private JPanel contentPane; // Why is this still around?

	public EventParameterFrame()
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

	public EventParameterFrame( UserEvent e, Method m, EventManagerFrame f )
	{
		super( f, true );

		this.method = m;
		this.event = e;
		this.referrerFrame = f;

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
		if ( "set".equals( e.getActionCommand() ) )
		{
			// event.setMethod(m);
			referrerFrame.addActionToList( method, params );
			// event.setParameters(new Object[]{params});
			this.dispose();
		}
		if ( "cancel".equals( e.getActionCommand() ) )
		{
			this.dispose();
		}

	}

	/**
	 * Component initialization.
	 * 
	 * @throws java.lang.Exception
	 */
	private void jbInit() throws Exception
	{

		this.setTitle( "Event Parameter Frame" );

		contentPane = (JPanel)getContentPane(); // access Frame's display
		contentPane.setLayout( new GridBagLayout() ); // set Frame's Layout

		this.displayMethods( method );

		JButton cancel = new JButton( "Cancel" );
		cancel.setActionCommand( "cancel" );
		cancel.setVisible( true );
		cancel.addActionListener( this );
		contentPane.add( cancel );

		JButton set = new JButton( "Set" );
		set.setActionCommand( "set" );
		set.setVisible( true );
		set.addActionListener( this );
		contentPane.add( set );

	}

	/**
	 * Dynamically generate swing component according to the data type passed to the method
	 * 
	 * @param m Method that you want to invoke. Components are generated according to the parameter being sent as its parameters
	 */
	public void displayMethods( Method m )
	{
		Border b;
		final Method curMethod = m;

		if ( jp != null )
		{
			this.remove( jp );
		}

		jp = new JPanel();
		jp.setLayout( new GridLayout( 1, 3 ) );
		jp.setVisible( true );
		// jp.setBackground( Color.WHITE );
		b = BorderFactory.createEtchedBorder();
		jp.setVisible( true );

		String friendlyMethodName = method.getName();

		if ( friendlyMethodName.startsWith( "set" ) && Character.isUpperCase( friendlyMethodName.charAt( 3 ) ) )
			friendlyMethodName = friendlyMethodName.substring( 3 );

		friendlyMethodName = Utility.Capitalize( friendlyMethodName );

		final String finalFriendlyName = friendlyMethodName;

		JLabel label = new JLabel( friendlyMethodName );
		label.setBorder( b );
		jp.add( label );

		Class<?>[] parameters = curMethod.getParameterTypes();

		OPFComponentType componentType = method.getAnnotation( Setter.class ).componentType();

		if ( parameters.length != 1 )
		{
			System.err.println( "The OPF can only support setters with a single parameter (Setter: " + curMethod.getName() + ")" );
		}
		else
		{
			final Class inputParameter = parameters[0];

			if ( componentType == OPFComponentType.Auto )
			{
				// Auto find and set the ComponentType for the most common
				// component types if the 'Auto' Component Type was
				// specified

				if ( inputParameter == Color.class )
					componentType = OPFComponentType.ColorChooser;
				else if ( inputParameter == boolean.class )
					componentType = OPFComponentType.CheckBox;
				else
					componentType = OPFComponentType.TextField;
				// Default to TextField for everything else currently

				// Slider is missing on purpose. Should be explicitly set to
				// use.
			}

			switch( componentType )
			{
				default:
				case TextField:
				{
					final JTextField temp = new JTextField();

					temp.addKeyListener( new KeyListener()
					{
						@SuppressWarnings( "boxing" )
						public void keyReleased( KeyEvent e )
						{
							String contents = temp.getText();
							try
							{
								Object s = contents; // Default to String

								if ( inputParameter != String.class && ( contents == null || contents.length() <= 0 ) )
									return;
								
//								In the middle of typing something.
								if( contents.equals("-") || contents.equals(".") || contents.equals("-.") )
									return;

								if ( inputParameter == double.class )
									s = Double.parseDouble( contents );
								else if ( inputParameter == float.class )
									s = Float.parseFloat( contents );
								else if ( inputParameter == long.class )
									s = Long.parseLong( contents );
								else if ( inputParameter == int.class )
									s = Integer.parseInt( contents );
								else if ( inputParameter == byte.class )
									s = Byte.parseByte( contents );

								setEventParameters( new Object[] { s } );

							}
							catch ( Exception x )
							{
								// x.printStackTrace();
								System.err.println( "Parse Error in swing component" );
							}
						}

						// These two required for the Interface.
						public void keyTyped( KeyEvent arg0 )
						{
						}

						public void keyPressed( KeyEvent arg0 )
						{
						}
					} );

					jp.add( temp );
					break;
				}
				case CheckBox:
				{
					JCheckBox temp = new JCheckBox();
					temp.setSelected( true );
					setEventParameters( new Object[] { true } );
					temp.addItemListener( new ItemListener()
					{

						@SuppressWarnings( "boxing" )
						public void itemStateChanged( ItemEvent e )
						{
							if ( inputParameter == boolean.class )
							{

								try
								{
									boolean bool = ( e.getStateChange() == ItemEvent.SELECTED );
									setEventParameters( new Object[] { bool } );
								}
								catch ( Exception x )
								{
									// x.printStackTrace();
									System.err.println( "Parse Error in swing component" );
								}

							}
							else
							{
								System.err.println( "Error: Trying to set a non-boolean value with a checkbox." );
							}
						}
					} );
					jp.add( temp );
					break;
				}

				case ColorChooser:
				{
					JPanel holder = new JPanel( new GridLayout() );

					final JLabel preview = new JLabel();
					preview.setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED, Color.LIGHT_GRAY, Color.LIGHT_GRAY ) );

					preview.setOpaque( true );

					holder.add( preview );

					final JButton button = new JButton( new ImageIcon( Utility.GetBufferedImage( "color_wheel_25x25.png" ) ) );

					button.addActionListener( new ActionListener()
					{
						public void actionPerformed( ActionEvent e )
						{
							Color newColor = JColorChooser
									.showDialog( button, "Set the " + finalFriendlyName + " for event", preview.getBackground() );

							if ( newColor == null )
								return;

							if ( inputParameter == Color.class )
							{
								try
								{
									setEventParameters( new Object[] { newColor } );

									preview.setBackground( newColor );

									Desktop.editFrame.getEditorContext().repaint();
								}
								catch ( Exception x )
								{
									x.printStackTrace();
									System.err.println( "Parse Error in swing component" );
								}

							}
							else
							{
								System.err.println( "Error: Trying to set a non-Color value with a Color Chooser." );
							}
						}
					} );

					holder.add( button );

					jp.add( holder );
					break;
				}
				case SliderZeroToOne:
				case SliderZeroToTwo:
				{

					JSlider temp;

					switch( componentType )
					{
						default:
						case SliderZeroToOne:
							temp = new JSlider( 0, 1000, 1000 );
							break;
						case SliderZeroToTwo:
							temp = new JSlider( 0, 2000, 1000 );
							break;
					}

					temp.addChangeListener( new ChangeListener()
					{
						public void stateChanged( ChangeEvent e )
						{
							if ( inputParameter == double.class || inputParameter == float.class )
							{
								try
								{
									int sliderVal = ( (JSlider)e.getSource() ).getValue();
									double dou = (double)sliderVal / 1000;
									float flo = (float)sliderVal / 1000;

									Object[] objAry = null;

									if ( inputParameter == float.class )
										objAry = new Object[] { flo };
									else if ( inputParameter == double.class )
										objAry = new Object[] { dou };

									setEventParameters( objAry );

								}
								catch ( Exception x )
								{
									// x.printStackTrace();
									System.err.println( "Parse Error in swing component" );
								}

							}
							else
							{
								System.err.println( "Error: Trying to set a non-Floating point value with a '0 to 1' Slider." );
							}
						}
					} );

					jp.add( temp );
					break;
				}
				case XYTextField:
				{
					final JTextField temp = new JTextField();
					final JTextField temp2 = new JTextField();

					temp.addKeyListener( new KeyListener()
					{
						public void keyReleased( KeyEvent e )
						{
							String contents = temp.getText();
							try
							{
								Object s = contents; // Default to String
								
								//In the middle of typing something.
								if( contents.equals("-") || contents.equals(".") || contents.equals("-.") )
									return;

								if ( inputParameter != String.class && ( contents == null || contents.length() <= 0 ) )
									return;

								if ( inputParameter == Vector2d.class )
								{
									s = Double.parseDouble( contents );
									setEventParameters( (Double)s, null, Vector2d.class );
								}
								else if ( inputParameter == Point2d.class )
								{
									s = Double.parseDouble( contents );
									setEventParameters( (Double)s, null, Point2d.class );
								}

							}
							catch ( Exception x )
							{
								// x.printStackTrace();
								System.err.println( "Parse Error in swing component" );
							}
						}

						// These two required for the Interface.
						public void keyTyped( KeyEvent arg0 )
						{
						}

						public void keyPressed( KeyEvent arg0 )
						{
						}
					} );
					temp2.addKeyListener( new KeyListener()
					{
						public void keyReleased( KeyEvent e )
						{
							String contents = temp2.getText();
							try
							{
								Object s = contents; // Default to String

								//In the middle of typing something.
								if( contents.equals("-") || contents.equals(".") || contents.equals("-.") )
									return;
								
								if ( inputParameter != String.class && ( contents == null || contents.length() <= 0 ) )
									return;

								if ( inputParameter == Vector2d.class )
								{
									s = Double.parseDouble( contents );
									setEventParameters( null, (Double)s, Vector2d.class );
								}
								else if ( inputParameter == Point2d.class )
								{
									s = Double.parseDouble( contents );
									setEventParameters( null, (Double)s, Point2d.class );
								}

							}
							catch ( Exception x )
							{
								// x.printStackTrace();
								System.err.println( "Parse Error in swing component" );
							}
						}

						public void keyTyped( KeyEvent arg0 )
						{
							// These two required for the Interface.
						}

						public void keyPressed( KeyEvent arg0 )
						{
							// These two required for the Interface.
						}
					} );

					jp.add( temp );
					jp.add( temp2 );

					break;
				}

			}
		}

		// this.setSize( 300, 30 );

		GridBagConstraints threeLabelGBC = new GridBagConstraints();
		threeLabelGBC.gridwidth = GridBagConstraints.REMAINDER;
		contentPane.add( jp, threeLabelGBC );

		this.repaint();
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void setEventParameters( Object[] parameters )
	{
		this.params = parameters;
	}

	/**
	 * Used in constructing Vector2ds or Point2ds for methods that take those datatypes
	 * 
	 * @param x The x parameter of the Vector or Point
	 * @param y The y parameter of the Vector or Point
	 * @param c Either Vector2d.class or Point2d.class. Tells method which type to cast to
	 */
	public void setEventParameters( Double x, Double y, Class c )
	{

		if ( c == Vector2d.class )
		{
			if ( x != null )
			{
				xyVec.x = x;
			}
			if ( y != null )
			{
				xyVec.y = y;
			}
			Object[] p = { xyVec };
			this.params = p;
		}
		else if ( c == Point2d.class )
		{
			if ( x != null )
			{
				xyPt.x = x;
			}
			if ( y != null )
			{
				xyPt.y = y;
			}
			Object[] p = { xyPt };
			this.params = p;
		}

	}

	public BaseEntity getSelectedEntity()
	{
		return selectedEntity;
	}

	public void setSelectedEntity( BaseEntity be )
	{
		selectedEntity = be;
	}

}
