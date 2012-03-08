package gui.opf;

import util.Utility;

import gui.Desktop;
import gui.undo.ObjectOPFEdit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector2d;

/**
 * The OPF is the Object Properties Frame. This frame is launched when a game entity is clicked and allows the user to set properties of the object
 * before the arena panel is launched. The methods the user can interact with are determined by the coder, by setting a special annotation with the
 * method.
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * @author The UC Regents
 */

public class DesktopOPF extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	private static ConcurrentHashMap<Class, Collection<Method>> m_VisibleSortedMethodCache = new ConcurrentHashMap<Class, Collection<Method>>();

	private static ConcurrentHashMap<Method, Method> m_GetterCache = new ConcurrentHashMap<Method, Method>();

	private JPanel jp;

	private Border b;

	private Method curMethod;

	private Object focusedObject;
	
	KeyAdapter ka = new KeyAdapter()
	{
		@Override
		public void keyPressed( KeyEvent e )
		{
			if( e.getModifiers() == 2 && (e.getKeyCode() == KeyEvent.VK_Z || e.getKeyCode() == KeyEvent.VK_Y) )
			{
				Desktop.editFrame.getEditorContext().keyPressed( e );	//Don't handle it, just send it to the editor to undo and redo stuff.
			}
		}
	};

	public Object getObject()
	{
		return focusedObject;
	}

	public void setObject( Object be )
	{
		this.focusedObject = be;

		updateOPF();
	}

	/*
	 * Invoking a method using reflection requires 3 things: 1)the method you wish to invoke 2)the object containing that method 3)the parameters to
	 * send it (if any) In order for the actionlisteners to have access to the BaseEntity object, BaseEntity must be final
	 */
	public DesktopOPF( Object be )
	{
		super( ( be == null ? "Properties" : be.toString() ), true, false );
		this.setVisible( true );
		
		focusedObject = be;
		if ( be == null )
			setSize( 400, 650 );
		// this.setLayer( 10 );
		displayMethods( be );
		
		this.addKeyListener( ka );
	}

	/**
	 * Updates the Object Properties Frame and returns Focus to the Editor Frame
	 */
	public void updateOPF()
	{
		this.setTitle( null );

		this.displayMethods( focusedObject );

		//Wierdness with regards to title.  If titles are the same during the repaint... apparently it won't draw the rest of the contents?  Some change in 1.6?

		this.setTitle( ( focusedObject == null ? "Properties" : focusedObject.toString() ) );
	}

	/**
	 * Uses reflection to generate the swing components that allow the user to interact with the objects.
	 * 
	 * @param be The entity to retrieve the methods from
	 */
	public void displayMethods( Object be )
	{
		focusedObject = be;

		if ( jp != null )
		{
			this.remove( jp );
		}

		jp = new JPanel();

		jp.setBackground( Color.WHITE );
		// b = BorderFactory.createLineBorder( Color.LIGHT_GRAY, 1 );
		b = BorderFactory.createMatteBorder( 1, 1, 1, 0, Color.LIGHT_GRAY );
		jp.setVisible( true );

		if ( be == null )
			return;

		Class c = be.getClass();

		Collection<Method> methods = getSortedMethodsFor( c );

		for ( Method method : methods )
		{
			curMethod = method;

			String friendlyMethodName = method.getName();

			if ( friendlyMethodName.startsWith( "set" ) && Character.isUpperCase( friendlyMethodName.charAt( 3 ) ) )
				friendlyMethodName = friendlyMethodName.substring( 3 );

			friendlyMethodName = Utility.Capitalize( friendlyMethodName );

			final String finalFriendlyName = friendlyMethodName;

			JLabel nameLabel = new JLabel( friendlyMethodName );

			nameLabel.setBorder( b );
			jp.add( nameLabel );

			Class<?>[] parameters = curMethod.getParameterTypes();

			if ( parameters.length != 1 )
			{
				System.err.println( "The OPF can only support setters with a single parameter (Setter: " + curMethod.getName() + ")" );
				continue;
			}

			final Class<?> inputParameter = parameters[0];

			OPFComponentType componentType = method.getAnnotation( Setter.class ).componentType();

			if ( componentType == OPFComponentType.Auto )
			{
				// Auto find and set the ComponentType for the most common
				// component types if the 'Auto' Component Type was
				// specified

				if ( inputParameter == Color.class )
					componentType = OPFComponentType.ColorChooser;
				else if ( inputParameter == boolean.class )
					componentType = OPFComponentType.CheckBox;
				else if ( Vector2d.class.isAssignableFrom( inputParameter ) || Point2D.class.isAssignableFrom( inputParameter ) )
					componentType = OPFComponentType.XYTextField;
				else if ( Enum.class.isAssignableFrom( inputParameter ) )
					componentType = OPFComponentType.ComboBox;
				else
					componentType = OPFComponentType.TextField;	//Example: Numbers, strings, etc
				// Default to TextField for everything else.
				// Slider omission is intentional Should be explicitly set to use
			}

			switch( componentType )
			{
				default:
				case TextField:
				{
					final JTextField temp = new JTextField();

					final Object o = getProperty( be, method );

					if ( o != null )
						temp.setText( o.toString() );

					temp.setToolTipText( Utility.Capitalize( inputParameter.getSimpleName() ) );

					temp.addKeyListener( new KeyListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void keyReleased( KeyEvent e )
						{
							String contents = temp.getText();
							try
							{
								Object s = contents; // Default to String

								// Only allow blank/null if inputting to a string
								if ( inputParameter != String.class && ( contents == null || contents.length() <= 0 ) )
									return;

								//In the middle of typing something.
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

								changeProperty( focusedObject, thisMethod, s, o );

								Desktop.editFrame.getEditorContext().repaint();
								setTitle( focusedObject.toString() );

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
					break;
				}
				case CheckBox:
				{
					JPanel pan = new JPanel();
					JCheckBox temp = new JCheckBox();

					final Object o = getProperty( be, method );

					if ( o != null )
						temp.setSelected( (Boolean)o );

					temp.setBorder( null );

					temp.addItemListener( new ItemListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void itemStateChanged( ItemEvent e )
						{
							if ( inputParameter == boolean.class )
							{
								try
								{
									boolean bool = ( e.getStateChange() == ItemEvent.SELECTED );
									
									changeProperty( focusedObject, thisMethod, bool, o );

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
								System.err.println( "Error: Trying to set a non-boolean value with a checkbox." );
							}
						}
					} );

					pan.setLayout( new GridBagLayout() );
					pan.add( new JSeparator() );
					pan.add( temp );
					pan.add( new JSeparator() );
					pan.setBorder( BorderFactory.createLineBorder( Color.LIGHT_GRAY ) );

					jp.add( pan );

					break;
				}
				case ComboBox:
				{
					JComboBox temp;

					final Object o = getProperty( be, method );

					if ( inputParameter.isEnum() )
					{
						temp = new JComboBox( inputParameter.getEnumConstants() );
						temp.setEditable( false );
						temp.setSelectedItem( o );
					}
					else
					{
						temp = new JComboBox();
					}

					final JComboBox finalBox = temp;

					temp.addActionListener( new ActionListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void actionPerformed( ActionEvent e )
						{
							Object selected = finalBox.getSelectedItem();

							try
							{
								changeProperty( focusedObject, thisMethod, selected, o );

								Desktop.editFrame.getEditorContext().repaint();
							}
							catch ( Exception x )
							{
								x.printStackTrace();
								System.err.println( "Parse Error in swing component" );
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

					final Object o = getProperty( be, method );

					if ( o != null )
						preview.setBackground( (Color)o );
					else
						preview.setBackground( Color.black );

					final Color co = preview.getBackground();

					preview.setToolTipText( "R: " + co.getRed() + ", G: " + co.getGreen() + ", B: " + co.getBlue() );

					preview.setOpaque( true );
					preview.setPreferredSize( new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE ) );

					holder.setLayout( new BorderLayout() );
					holder.add( preview, BorderLayout.CENTER );

					final JButton button = new JButton( new ImageIcon( Utility.GetBufferedImage( "color_wheel_25x25.png" ) ) );
					button.setPreferredSize( new Dimension( 30, 30 ) );
					button.setBorder( null );

					button.addActionListener( new ActionListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void actionPerformed( ActionEvent e )
						{
							Color newColor = JColorChooser.showDialog( button, "Set the " + finalFriendlyName + " for " + focusedObject.toString(),
									preview.getBackground() );

							if ( newColor == null )
								return;

							if ( inputParameter == Color.class )
							{
								try
								{
									changeProperty( focusedObject, thisMethod, newColor, o );
									preview.setBackground( newColor );

									preview.setToolTipText( "R: " + newColor.getRed() + ", G: " + newColor.getGreen() + ", B: " + newColor.getBlue() );

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

					holder.add( button, BorderLayout.EAST );

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
						{
							temp = new JSlider( 0, 10000, 10000 );
							/*
							 * temp.setMajorTickSpacing( 5000 ); temp.setPaintTicks( true );
							 */
							break;
						}
						case SliderZeroToTwo:
						{
							temp = new JSlider( 0, 20000, 10000 );
							/*
							 * temp.setMajorTickSpacing( 10000 ); temp.setPaintTicks( true );
							 */
							break;
						}
					}

					final Object o = getProperty( be, method );

					if ( o != null )
						temp.setValue( (int)( ( (Number)getProperty( be, method ) ).doubleValue() * 10000 ) );

					temp.setToolTipText( String.valueOf( temp.getValue() / 10000D ) );

					final JSlider finalSlider = temp;

					temp.addChangeListener( new ChangeListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void stateChanged( ChangeEvent e )
						{
							if ( inputParameter == double.class || inputParameter == float.class )
							{
								try
								{
									int sliderVal = ( (JSlider)e.getSource() ).getValue();
									double dou = (double)sliderVal / 10000D;
									float flo = (float)sliderVal / 10000F;

									Object obj = null;

									if ( inputParameter == float.class )
									{
										obj = flo;
									}
									else if ( inputParameter == double.class )
									{
										obj = dou;
									}

									changeProperty( focusedObject, thisMethod, obj, o );
									finalSlider.setToolTipText( String.valueOf( finalSlider.getValue() / 10000D ) );

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
								System.err.println( "Error: Trying to set a non-Floating point value with a '0 to 1' Slider." );
							}
						}
					} );

					jp.add( temp );
					break;
				}
				case XYTextField:
				{
					//Get the type of the return of the method getX from the type of the input parameter
					
					final Class<?> xReturn, yReturn;
					
					try
					{
						xReturn = inputParameter.getMethod( "getX" ).getReturnType();
						yReturn = inputParameter.getMethod( "getY" ).getReturnType();
					}
					catch (NoSuchMethodException e) 
					{
						System.err.println( "Error: No getX or getY method on type " + inputParameter.getSimpleName() );
						break;
					}
					
					if( yReturn != xReturn )
						System.err.println( "Error: getX and getY methods on type " + inputParameter.getSimpleName() + " are different types");
					
					JPanel holder = new JPanel( new GridLayout() );
					
					final JTextField tempX = new JTextField();
					final JTextField tempY = new JTextField();

					final Object o = getProperty( be, method );
					
					final Object dX, dY;
					
					if ( o != null )
					{
						try
						{
							dX = o.getClass().getMethod( "getX" ).invoke( o );
							dY = o.getClass().getMethod( "getY" ).invoke( o );
							
							tempX.setText( dX.toString() );
							tempY.setText( dY.toString() );
						}
						catch (Exception e) 
						{
							System.out.println( "WTF BBQ!");
							break;
						}
					}
					
					//.Double or .Whatever it is
					tempX.setToolTipText( Utility.Capitalize( inputParameter.getSimpleName() + "." +  Utility.Capitalize( xReturn.getSimpleName() ) ) );
					tempY.setToolTipText( Utility.Capitalize( inputParameter.getSimpleName() + "." +  Utility.Capitalize( yReturn.getSimpleName() ) ) );

					KeyListener kl = new KeyListener()
					{
						// Needed due to scope issues
						Method thisMethod = curMethod;

						public void keyReleased( KeyEvent e )
						{
							String contentsX = tempX.getText();
							String contentsY = tempY.getText();
							
							try
							{
								Object sX = contentsX; // Default to String

								// Only allow blank/null if inputting to a string
								if ( xReturn != String.class && ( contentsX == null || contentsX.length() <= 0 ) )
									return;

								//In the middle of typing something.
								if( contentsX.equals("-") || contentsX.equals(".") || contentsX.equals("-.") )
									return;

								if ( xReturn == double.class )
									sX = Double.parseDouble( contentsX );
								else if ( xReturn == float.class )
									sX = Float.parseFloat( contentsX );
								else if ( xReturn == long.class )
									sX = Long.parseLong( contentsX );
								else if ( xReturn == int.class )
									sX = Integer.parseInt( contentsX );
								else if ( xReturn == byte.class )
									sX = Byte.parseByte( contentsX );
								
								
								
								Object sY = contentsY; // Default to String

								// Only allow blank/null if inputting to a string
								if ( yReturn != String.class && ( contentsY == null || contentsY.length() <= 0 ) )
									return;

								//In the middle of typing something.
								if( contentsY.equals("-") || contentsY.equals(".") || contentsY.equals("-.") )
									return;

								if ( yReturn == double.class )
									sY = Double.parseDouble( contentsY );
								else if ( yReturn == float.class )
									sY = Float.parseFloat( contentsY );
								else if ( yReturn == long.class )
									sY = Long.parseLong( contentsY );
								else if ( yReturn == int.class )
									sY = Integer.parseInt( contentsY );
								else if ( yReturn == byte.class )
									sY = Byte.parseByte( contentsY );
								
								Constructor<?> ctor;
								
								try
								{
									//Try for a Ctor in this format: Class( xType, yType )
									ctor = inputParameter.getConstructor( xReturn, yReturn );
								}
								catch (NoSuchMethodException nsme) 
								{
									//Try for a Ctor in this format: Class.xyType( xtype, yType ), eg, Point2D.Double()
									ctor = Class.forName( inputParameter.getCanonicalName() + "$" +  Utility.Capitalize( yReturn.getSimpleName() ) ).getConstructor( xReturn, yReturn );
								}
								
								Object s = ctor.newInstance( sX, sY );

								
								changeProperty( focusedObject, thisMethod, s, o );

								Desktop.editFrame.getEditorContext().repaint();
							}
							catch ( Exception x )
							{
								 x.printStackTrace();
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
					};
					
					tempX.addKeyListener( kl );
					tempY.addKeyListener( kl );
					
					holder.add( tempX );
					holder.add( tempY );

					jp.add( holder );
					break;
				}
			}
		}

		int methodCount = jp.getComponentCount() / 2;

		this.setSize( 200, ( methodCount + 1 ) * 26 );
		jp.setLayout( new GridLayout( methodCount, 2 ) );
		

		
		for( Component com : jp.getComponents() )
		{
			com.addKeyListener( ka );
			
			if( com instanceof JPanel )	//We'll go down one depth looking for containers.  after that, YOU'RE ON YOUR OWN
			{
				for( Component comDepth1 : ((JPanel)com).getComponents() )
				{
					comDepth1.addKeyListener( ka );
				}
			}
		}
		
		jp.addKeyListener( ka );

		this.add( jp );

		this.repaint();
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Sets the value of the given Method with the Object array as the method parameters.
	 * 
	 * @param obj The object on which to change the property on.
	 * @param m The method that's used to set/change the value on the Object.
	 * @param param The method parameter. Can be null for no argument methods.
	 * @param oldParam The method's old value, for Undo purposes
	 * @return Returns wether or not the property change was successful
	 */
	public static boolean changeProperty( Object obj, Method m, Object param, Object oldParam )
	{
		Desktop.editFrame.getEditorContext().getUndoManager().addEdit( new ObjectOPFEdit( obj, m, param, oldParam) );
		return changeProperty( obj, m, param );
	}
	
	/**
	 * Sets the value of the given Method with the Object array as the method parameters. Will NOT place things in undo queue
	 * 
	 * @param obj The object on which to change the property on.
	 * @param m The method that's used to set/change the value on the Object.
	 * @param param The method parameter. Can be null for no argument methods.
	 * @return Returns wether or not the property change was successful
	 */
	public static boolean changeProperty( Object obj, Method m, Object param )
	{
		try
		{
			m.invoke( obj, param );
			return true;
		}
		catch ( Exception e )
		{
			System.err.println( "Problem with method invocation. Method: " + m.getName() + " Object: " + obj + "Params: "
					+ ( ( param == null ) ? "null" : param.toString() ) );
			return false;
		}
	}
	

	/**
	 * Returns the value of a specified objected using the 'Getter' Annotation on the specified Method.
	 * 
	 * @param obj The Object of which to get the value. Use 'null' for static classes
	 * @param setter The specified setting method in which to find and get the value of it's Getter.
	 * @return The returning value of the method's getter. If the getter returns void, this returns null
	 */
	public static Object getProperty( Object obj, Method setter )
	{
		Class c = obj.getClass();

		try
		{
			Method mGetter = getGetterFor( setter, c );

			return mGetter.invoke( obj );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.err.println( c );
			System.err.println( setter.getDeclaringClass() );
			System.err.println( "Problem with method retrieval. Setter: " + setter.getName() + " Class: " + c );

			return null;
		}
	}

	private static Collection<Method> getSortedMethodsFor( Class c )
	{
		if ( m_VisibleSortedMethodCache.containsKey( c ) )
		{
			return m_VisibleSortedMethodCache.get( c );
		}

		List<Method> methods = new Vector<Method>();

		Method[] methodAry = c.getMethods();

		for ( int i = 0; i < methodAry.length; i++ )
		{
			Method method = methodAry[i];

			if ( method.isAnnotationPresent( Setter.class ) && method.getAnnotation( Setter.class ).showInOPF() )
				methods.add( method );
		}

		Collections.sort( methods, new Comparator<Method>()
				{
					public int compare( Method arg0, Method arg1 )
					{
						return arg0.getName().compareTo( arg1.getName() );
					}
				} );

		m_VisibleSortedMethodCache.put( c, methods );

		return methods;
	}

	private static Method getGetterFor( Method setter, Class<?> objectClass ) throws NoSuchMethodException
	{
		/*
		 * //There are issues with the caching. It (might?) even be a bug in Java. It's telling me that a class which is an instance of another
		 * class... isn't. 
		 * 
		 * Note in July 07, Java 1.6 - Uncommenting the code DOES work now.  it WAS a bug in Java.
		 * 
		 * Andre 1, Java 0.
		 * */

		if( m_GetterCache.containsKey( setter ) )
		{ 
			//System.err.println( setter.toGenericString() ); 
			//System.err.println(	m_GetterCache.keySet().toString() ); 
			//System.out.println("Cache Hit " + setter + " " + m_GetterCache.get( setter )); 
			return m_GetterCache.get( setter ); 
		}


		String getter = setter.getAnnotation( Setter.class ).getter();

		if ( getter == null || getter.length() <= 0 ) // If none specified, try to auto-determine the method
		{
			String friendlyMethodName = setter.getName();

			// Is Camel Case?
			if ( friendlyMethodName.startsWith( "set" ) && Character.isUpperCase( friendlyMethodName.charAt( 3 ) ) )
				friendlyMethodName = friendlyMethodName.substring( 3 );
			else
				throw new NoSuchMethodException( "Non-standard method name, unable to auto-determine getter." );

			Class<?>[] parameters = setter.getParameterTypes();

			if ( parameters.length != 1 )
				throw new NoSuchMethodException( "Trying to get a getter of a setter that doesn't have exactly one input parameter." );

			Class inputParameter = parameters[0];

			if ( inputParameter == boolean.class )
				friendlyMethodName = "is" + friendlyMethodName;
			else
				friendlyMethodName = "get" + friendlyMethodName;

			getter = friendlyMethodName;
		}

		Method m = objectClass.getMethod( getter );

		m_GetterCache.put( setter, m );

		return m;
	}
}
