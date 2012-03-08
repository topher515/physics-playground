package test;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all the JUnit tests
 */
public class AllTests
{
	public static void main( String[] args )
	{
		if ( args.length > 0 && args[0].startsWith( "-g" ) )
		{
			// Graphical
			String[] arguments = new String[] { AllTests.class.getName() };
			junit.swingui.TestRunner.main( arguments );
		}
		else
		{
			// Textual
			TestResult testResult = junit.textui.TestRunner.run( suite() );
			System.exit( testResult.wasSuccessful() ? 0 : 1 );
		}
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite( "Project Tests" );
		suite.addTestSuite( MovingEntityTest.class );
		suite.addTestSuite( UtilityTest.class );
		suite.addTestSuite( PairTest.class );
		suite.addTestSuite( BaseEntityTest.class );
		return suite;
	}
}