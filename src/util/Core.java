package util;

import samplegame.IThink;

import java.lang.reflect.Method;
import java.util.PriorityQueue;

import physics.Arena;


/**
 * Class planned for future use but not yet used.
 * 
 * Maintains a queue of methods for exectution with each method having a usage time.
 * Planned for game mechanics.
 * 
 * @author The UC Regents
 *
 */
public class Core implements Runnable{
	
	Arena m_Arena;
	
	public Core( Arena arena )
	{
		m_Arena = arena;
	}
	
	protected class QueueInfo implements Comparable<QueueInfo>
	{
		//BaseEntity entity;
		IThink entity;
		Method method;
		Object[] parameters;
		int  miniTicks;	//Doesn't corrspond to an actual unit in time, just so that methods can have RELATIVE time amounts.
		//Get MiniTicks from Annotation around Methods.  This annotation (and lack thereof) will signal wether or not said METHOD
		//		can be used within the Queue.  The queue then calls then Invokes that method with whatever parmaters passed in.
		
		public QueueInfo( IThink e, Method m, int ticks, Object[] params )
		{
			// TODO Auto-generated constructor stub
			this.entity = e;
			this.method = m;
			this.parameters = params;
			this.miniTicks = ticks;
		}

		public Object Invoke()
		{
			try
			{
				return method.invoke( entity, parameters );
			}
			catch ( Exception e )
			{
				// TODO: handle exception
				System.out.println("Exception: " + e.toString());
			}
			return null;
		}

		public int compareTo( QueueInfo otherInfo )
		{
			return Integer.valueOf( entity.getMiniTicksThisTick() ).compareTo( Integer.valueOf( otherInfo.entity.getMiniTicksThisTick() ));
			//return Integer.valueOf( miniTicks ).compareTo( Integer.valueOf( otherInfo.miniTicks ));
		}
	}
	
	public static final int MAX_MINITICKS_PER_TICK = 500;
	public static final int TICK_BUFFER_TIME = 100;
	
	private PriorityQueue<QueueInfo> m_Queue = new PriorityQueue<QueueInfo>();
	
	public void AddToQueue( IThink thinker, Method method, Object... objects)
	{		
		TickCosting tickCost = method.getAnnotation( TickCosting.class );
		
		if( tickCost != null )
		{
			TickIntervalCost intervalCost = method.getAnnotation( TickIntervalCost.class );
			
			if( intervalCost != null )	//Recurring thing, split it up.
			{
				if( objects.length >= 1 && (objects[0] instanceof Integer))
				{
					//TODO:Split it up.  Possibbly new class overriding QueueInfo & Invoke, having it before and after create a new Queinfo and add it back into the Queue.
					
					return;
				}
				//else
				System.out.println( "Interval Cost with undefined 1st parameter integer ");
				//Treat it as if it WASN'T an Interval.
			}
			
			m_Queue.add( new QueueInfo( thinker, method, tickCost.StartCost(), objects));
		}
	}
	
	
	public void run()
	{
		while( m_Thread != null )
		{
			long now = System.currentTimeMillis();
			
			//m_Arena.BeginTick();
			
			if( !m_Queue.isEmpty() )
				m_Queue.poll().Invoke();
			
			//m_Arena.EndTick();
			
			long toSleep = System.currentTimeMillis() - now;
			
			if( toSleep > MAX_MINITICKS_PER_TICK )
				toSleep = 0;	//A sleep for 0, atleast in C#, is a 'let other threads have their turn, but don't actually sleep
			
			try
			{
				Thread.sleep( toSleep );
			}
			catch ( Exception e )
			{
				// TODO: handle exception
			}
		}
	}
	private Thread m_Thread;
	
	public void Start()
	{
		if( m_Thread == null )
			m_Thread = new Thread( this );
		
		m_Thread.start();
	}
	
	public void Stop()
	{
		m_Thread = null;
	}
	
}
