package physics;

import serialization.CustomXMLReader;
import serialization.CustomXMLWriter;
import geom.PolygonOrCircle;
import gui.opf.OPFComponentType;
import gui.opf.Setter;

import javax.vecmath.Vector2d;

public class ScaledMassFieldForce extends BaseFieldForceEntity
{

	private double m_ForceScalar = 1;
	
	@Override
	public void Deserialize( CustomXMLReader reader )
	{
		super.Deserialize( reader );
		
		m_ForceScalar = reader.ReadDouble( "ForceScalar" );
	}

	@Override
	public void Serialize( CustomXMLWriter writer )
	{
		super.Serialize( writer );
		
		writer.Write( "ForceScalar", m_ForceScalar );
	}

	public ScaledMassFieldForce()
	{
		this( new PolygonOrCircle( 0, 0, 200, 150 ), false );
	}

	public ScaledMassFieldForce( int x_loc, int y_loc, int width, int height )
	{
		this( new PolygonOrCircle( x_loc, y_loc, width, height ), false );
	}

	public ScaledMassFieldForce( PolygonOrCircle forceShape, boolean isVisible )
	{
		super( forceShape, isVisible );
	}

	public Vector2d getForceFor( MovingEntity be )
	{
		Vector2d currentVector = be.getCurrentVelocity();
		
		double force = (1-m_ForceScalar) / 25;
		
		if( m_ForceScalar > 1 )
			force /= 5;
		
		force /= be.getMass();
		
		currentVector.scale( force * -1 );
		
		
		return currentVector;
	}

	public double getForceScalar()
	{
		return m_ForceScalar;
	}

	@Setter( componentType = OPFComponentType.SliderZeroToTwo )
	public void setForceScalar( double forceScalar )
	{
		m_ForceScalar = forceScalar;
	}

}
