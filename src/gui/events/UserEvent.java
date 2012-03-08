package gui.events;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import physics.BaseEntity;

/**
 * The UserEvent object stores all the components necessary to invoke any number
 * of methods using reflection. This way you can dynamically add player events
 * to an object to make them controllable by mouse and keyboard input.
 * 
 * @author The UC Regents
 * 
 */
public class UserEvent
{
	private Object o;

	private List<Method> methods = new Vector<Method>();

	private List<Object[]> params = new Vector<Object[]>();

	private int eventType;

	private int eventSubType;

	private String ActionCommand = "";

	public static final int OBJECT_COLLIDED = 1;

	public static final int OBJECT_DESTROYED = 2;

	public UserEvent()
	{
		super();
	}

	/**
	 * Create a cloned version of this event to a given object
	 * 
	 * @param obj
	 * @return Returns a copy of this object but with the parameters as needed by the new Object 
	 */
	public UserEvent cloneTo( Object obj )
	{

		return new UserEvent( eventType, eventSubType, methods, params, obj );
	}

	/**
	 * Main constructor. Defines event type, method to invoke, and parameters
	 * 
	 * @param e
	 *            event type which corresponds to the event ids (e.g.
	 *            MouseEvent.MOUSE_PRESSED)
	 * @param es
	 *            event sub-type which corresponds to the exact event (e.g
	 *            MouseEvent.BUTTON1)
	 * @param meth
	 *            Vector of Methods to invoke on the given object
	 * @param p
	 *            Vector of parameters that correspond on a one-to-one basis
	 *            with the method vector
	 * @param obj
	 *            Object on which to invoke the given methods
	 */
	public UserEvent( int e, int es, List<Method> meth, List<Object[]> p, Object obj )
	{
		this.eventType = e;
		this.eventSubType = es;
		this.methods = meth;
		this.params = p;
		this.o = (BaseEntity)obj;
	}

	/**
	 * Constructor that accepts a single method and set of parameters rather
	 * than a vector of methods. The main constructor is called and the method
	 * and parameters are added to the class's appropriate vectors
	 * 
	 * @param e
	 *            event type which corresponds to the event ids (e.g.
	 *            MouseEvent.MOUSE_PRESSED)
	 * @param es
	 *            event sub-type which corresponds to the exact event (e.g
	 *            MouseEvent.BUTTON1)
	 * @param meth
	 *            Method to add to the method container for the object
	 * @param p
	 *            parameters that correspond to the given method
	 * @param obj
	 *            Object on which to invoke the given method
	 */
	public UserEvent( int e, int es, Method meth, Object[] p, Object obj )
	{
		this.eventType = e;
		this.eventSubType = es;
		this.methods.add( meth );
		this.params.add( p );
		this.o = (BaseEntity)obj;

	}

	/**
	 * Simple constructor that accepts no methods or parameters. Designed for
	 * easy object-association with the event tree nodes in EventManagerFrame:
	 * these nodes should represent UserEvent objects, but don't yet have any
	 * methods or parameters associated.
	 * 
	 * @param e
	 *            event type which corresponds to the event ids (e.g.
	 *            MouseEvent.MOUSE_PRESSED)
	 * @param es
	 *            event sub-type which corresponds to the exact event (e.g
	 *            MouseEvent.BUTTON1)
	 * @param obj
	 *            Object on which to invoke the specified method
	 */
	public UserEvent( int e, int es, Object obj )
	{
		this.eventType = e;
		this.eventSubType = es;
		this.o = (BaseEntity)obj;
	}

	/**
	 * Attempt to invoke all methods with the given parameters.
	 * 
	 */
	public void execute()
	{
		try
		{
			int i = 0;
			for ( Method meth : methods )
			{
				meth.invoke( o, params.get( i ) );
				i++;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( "Error executing event: " + e );
		}
	}

	public Object getObject()
	{
		return o;
	}

	public List<Method> getMethods()
	{
		return methods;
	}

	public List<Object[]> getParameters()
	{
		return params;
	}

	public int getEventType()
	{
		return eventType;
	}

	public int getEventSubType()
	{
		return eventSubType;
	}

	public String getActionCommand()
	{
		return ActionCommand;
	}

	public void setObject( Object obj )
	{
		o = (BaseEntity)obj;
	}

	public void setMethod( List<Method> meth )
	{
		methods = meth;
	}

	public void setParameters( List<Object[]> p )
	{
		params = p;
	}

	public void setEventType( int e )
	{
		eventType = e;
	}

	public void setEventSubType( int es )
	{

		eventSubType = es;
	}

	/**
	 * The set action command allows you to associate the user event with
	 * whatever text you like. For example, you might want to add a User Event
	 * to a list and would like the list to display "Up Arrow" rather than the
	 * object's default tostring()
	 * 
	 * @param s
	 */
	public void setActionCommand( String s )
	{
		ActionCommand = s;
	}

	public void addMethod( Method m )
	{

	}
	
	public void removeMethod( int index ) {
		methods.remove(index);
		params.remove(index);
	}

	public void addParams( Method m )
	{

	}

	/**
	 * Add a set of corresponding methods and parameters to the UserEvent
	 * vectors
	 * 
	 * @param m
	 *            Method to add
	 * @param p
	 *            Parameters to send to the method
	 */
	public void addMethodAndParams( Method m, Object[] p )
	{
		this.methods.add( m );
		this.params.add( p );
	}

	/**
	 * Associates this UserEvent with a string. This string may be defined by
	 * the user through the setActionCommand method, or the class will attempt
	 * to determine the appropriate text to display. Using the auto-detect, it
	 * will just display the key or button that was pressed.
	 */
	@Override
	public String toString()
	{

		if ( ActionCommand != "" )
		{
			return ActionCommand;
		}

		switch( this.eventType )
		{
			case MouseEvent.MOUSE_PRESSED:
			{
				switch( this.eventSubType )
				{
					case MouseEvent.BUTTON1:
						return "Mouse Left";
					case MouseEvent.BUTTON2:
						return "Mouse Middle";
					case MouseEvent.BUTTON3:
						return "Mouse Right";
					default:
						return "Mouse Other";
				}
			}
			case KeyEvent.KEY_PRESSED:
			{
				switch( this.eventSubType )
				{
					case KeyEvent.VK_UP:
						return "Key Up";
					case KeyEvent.VK_DOWN:
						return "Key Down";
					case KeyEvent.VK_LEFT:
						return "Key Left";
					case KeyEvent.VK_RIGHT:
						return "Key Right";
					case KeyEvent.VK_W:
						return "Key W";
					case KeyEvent.VK_A:
						return "Key A";
					case KeyEvent.VK_S:
						return "Key S";
					case KeyEvent.VK_D:
						return "Key D";
					case KeyEvent.VK_SPACE:
						return "Key Space";
					case KeyEvent.VK_ENTER:
						return "Key Enter";
					default:
						return "Key Other";
				}

			}
			case UserEvent.OBJECT_COLLIDED:
			{
				return "Collision";
			}
			case UserEvent.OBJECT_DESTROYED:
			{
				return "Destruction";
			}
		}
		return "No name";

		// return super.toString();
	}


	public void Serialize( CustomXMLWriter writer )
	{
		writer.Write( "EventType", this.getEventType());
		writer.Write( "EventSubType", this.getEventSubType());
		for (Method m : this.getMethods() ){
			writer.Write( "Method", m.getName() );	
		}
		writer.Write( "ActionCommand", this.getActionCommand());
		//writer.Write( "Object", this.getObject());
		
	}


	public void DeSerialize( CustomXMLReader reader)
	{
		this.setEventType(reader.ReadInteger("EventType"));
		this.setEventSubType(reader.ReadInteger("EventSubType"));
		//this.setMethod(reader.ReadMethod("Method"));
		//this.setParameters(reader.ReadObjectArray("Parameters"));
		//this.setObject(reader.ReadObject("Object"));
	}
	
}
