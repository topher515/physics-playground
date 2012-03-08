package test;

import java.awt.geom.Point2D;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import physics.MovingEntity;

public class MovingEntityTest extends TestCase
{
	public MovingEntityTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite( MovingEntityTest.class );
		return suite;
	}

	public static void main( String[] args )
	{
		if ( args.length > 0 && args[0].startsWith( "-g" ) )
		{
			// Graphical
			junit.swingui.TestRunner.run( MovingEntityTest.class );
		}
		else
		{
			// Textual
			junit.textui.TestRunner runner = new junit.textui.TestRunner();

			// get all the tests associated with this class
			Test test = runner.getTest( MovingEntityTest.class.getName() );

			// run the tests
			TestResult testResult = junit.textui.TestRunner.run( test );

			// exit according to whether there were any failures
			System.exit( testResult.wasSuccessful() ? 0 : 1 );
		}
	}

	/** Test of add method, of class teamname.projectname.physics.BasicEntity */
	public void testgetULPoint()
	{
		System.out.println( "testgetULPoint" );
		MovingEntity q = new MovingEntity();
		assertTrue( q.getULPoint().equals( new Point2D.Double( 0, 0 ) ) );
	}

	public void testgetCurrentVelocity()
	{
		System.out.println( "testgetCurrentVelocity" );
		MovingEntity q = new MovingEntity();
		assertTrue( q.getCurrentVelocity().equals( new Vector2d( 0, 0 ) ) );
	}

	public void testMoveVector()
	{
		System.out.println( "testMoveVector" );
		MovingEntity q = new MovingEntity();
		q.moveVector( new Vector2d( 10, 10 ) );
		assertTrue( q.getULPoint().equals( new Point2D.Double( 10, 10 ) ) );
	}

	public void testMoveCurrentVelocity()
	{
		System.out.println( "testMoveVector" );
		MovingEntity q = new MovingEntity();
		q.setCurrentVelocity( new Vector2d( 1000, 1000 ) );
		q.moveCurrentVelocity();
		// This assumes the period in arena is 1ms
		assertTrue( q.getULPoint().equals( new Point2D.Double( 10, 10 ) ) );
	}
}
