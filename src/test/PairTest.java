package test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import physics.Pair;

public class PairTest extends TestCase
{
	public PairTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite( PairTest.class );
		return suite;
	}

	public static void main( String[] args )
	{
		if ( args.length > 0 && args[0].startsWith( "-g" ) )
		{
			// Graphical
			junit.swingui.TestRunner.run( PairTest.class );
		}
		else
		{
			// Textual
			junit.textui.TestRunner runner = new junit.textui.TestRunner();

			// get all the tests associated with this class
			Test test = runner.getTest( PairTest.class.getName() );

			// run the tests
			TestResult testResult = junit.textui.TestRunner.run( test );

			// exit according to whether there were any failures
			System.exit( testResult.wasSuccessful() ? 0 : 1 );
		}
	}

	public void testPair()
	{
		System.out.println( "testPair" );
		Pair<Integer> p1 = new Pair<Integer>( new Integer( 5 ), new Integer( 10 ) );
		Pair<Integer> p2 = new Pair<Integer>( new Integer( 10 ), new Integer( 5 ) );
		assertTrue( p1.equals( p2 ) );
	}
}
