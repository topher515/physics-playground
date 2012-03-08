package test;

import util.Utility;
import geom.GeomUtility;

import java.awt.Color;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class UtilityTest extends TestCase
{
	public UtilityTest( String testName )
	{
		super( testName );
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite( UtilityTest.class );
		return suite;
	}

	public static void main( String[] args )
	{
		if ( args.length > 0 && args[0].startsWith( "-g" ) )
		{
			// Graphical
			junit.swingui.TestRunner.run( UtilityTest.class );
		}
		else
		{
			// Textual
			junit.textui.TestRunner runner = new junit.textui.TestRunner();

			// get all the tests associated with this class
			Test test = runner.getTest( UtilityTest.class.getName() );

			// run the tests
			TestResult testResult = junit.textui.TestRunner.run( test );

			// exit according to whether there were any failures
			System.exit( testResult.wasSuccessful() ? 0 : 1 );
		}
	}

	public void testApplyAlpha()
	{
		System.out.println( "testApplyAlpha" );
		Color testColor = new Color( 255, 255, 255, 0 );
		testColor = Utility.ApplyAlpha( testColor, 254 );
		// System.out.println(testColor.getAlpha());
		assertTrue( testColor.getAlpha() == 254 );

	}

	public void testRandomInt()
	{
		System.out.println( "testRandomInt" );
		for ( int i = 0; i < 25; i++ )
		{
			int n = Utility.RandomInt( 0, 10 );
			assertTrue( ( n <= 10 ) && ( 0 <= n ) );
		}
	}

	public void testRandomDouble()
	{
		System.out.println( "testRandomDouble" );
		for ( int i = 0; i < 25; i++ )
		{
			double n = Utility.RandomDouble( 0, 10 );
			assertTrue( ( n <= 10 ) && ( 0 <= n ) );
		}
	}

	public void testVectorMath()
	{
		System.out.println( "testVectorMath" );
		Vector2d testVec = GeomUtility.getUnitVectorX();
		testVec = GeomUtility.VectorMultiplication( testVec, 55 );
		testVec.scale( 10 );
		assertFalse( GeomUtility.getUnitVectorX().equals( testVec ) );
		assertTrue( testVec.length() == 550 );
	}

	public void testAreParallel()
	{
		System.out.println( "testAreParallel" );
		assertFalse( GeomUtility.areParallel( GeomUtility.getUnitVectorX(), GeomUtility.getUnitVectorY() ) );
		assertTrue( GeomUtility.areParallel( GeomUtility.getUnitVectorX(), GeomUtility.getUnitVectorX() ) );
	}

}
