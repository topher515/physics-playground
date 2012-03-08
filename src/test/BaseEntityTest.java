package test;

import geom.PolygonOrCircle;

import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import physics.MovingEntity;

public class BaseEntityTest extends TestCase {
	 public BaseEntityTest( String testName ) { super(testName); }
	    
	    public static Test suite() 
	    {
	        TestSuite suite = new TestSuite( BaseEntityTest.class );
	        return suite;
	    }
	public static void main( String[] args ) 
	{       
     if (args.length > 0 && args[0].startsWith( "-g") ) 
     {
         // Graphical
         junit.swingui.TestRunner.run( BaseEntityTest.class );
     }      
     else 
     {
         // Textual
         junit.textui.TestRunner runner = new junit.textui.TestRunner();
         
         // get all the tests associated with this class
         Test test = runner.getTest( BaseEntityTest.class.getName() );
         
         // run the tests
         TestResult testResult = junit.textui.TestRunner.run( test );
         
         // exit according to whether there were any failures
         System.exit( testResult.wasSuccessful() ? 0 : 1 );
     }
 	}
	//Base Entity is abstract, so it uses moving entity to test its methods
	public void testisOverlappingCircles()
	{
		System.out.println("testisOverlappingCircles");
		MovingEntity ent1 = new MovingEntity(new PolygonOrCircle(0,0,10));
		ent1.setULPoint(100,100);
		MovingEntity ent2 = new MovingEntity(new PolygonOrCircle(0,0,10));
		ent2.setULPoint(100,109);
		assertTrue(ent1.isOverlapping(ent2).length() == (ent2.isOverlapping(ent1)).length());
		assertTrue(ent1.isOverlapping(ent2).equals(new Vector2d(0,-1)));
	}
	public void testisOverlappingPolygons()
	{
		System.out.println("testisOverlappingPolygons");
		MovingEntity ent1 = new MovingEntity(new PolygonOrCircle(new double[]{0,10,10},
																new double[]{0,10,0},3));
		ent1.setULPoint(100,100);
		MovingEntity ent2 = new MovingEntity(new PolygonOrCircle(new double[]{0,10,0},
															new double[]{0,10,10},3));
		ent2.setULPoint(109,100);
		assertTrue(ent1.isOverlapping(ent2).length() == (ent2.isOverlapping(ent1)).length());
		assertTrue(ent1.isOverlapping(ent2).equals(new Vector2d(-1,0)));
	}
	public void testisOverlappingCircleandPolygon()
	{
		System.out.println("testisOverlappingCircleandPolygon");
		MovingEntity ent1 = new MovingEntity(new PolygonOrCircle(new double[]{0,10,10},
																new double[]{0,10,0},3));
		ent1.setULPoint(100,100);
		MovingEntity ent2 = new MovingEntity(new PolygonOrCircle(0,0,10));
		ent2.setULPoint(100,109);
		//System.out.println(ent1.isOverlapping(ent2));
		//assertTrue(ent1.isOverlapping(ent2).equals(ent2.isOverlapping(ent1)));
		assertTrue(ent1.isOverlapping(ent2)== null);
	}
}
