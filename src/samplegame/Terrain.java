package samplegame;

import geom.PolygonOrCircle;
import gui.opf.OPFComponentType;
import gui.opf.Setter;
import physics.BaseEntity;

public abstract class Terrain extends BaseEntity
{
	private float alphaTransparency;

	public Terrain()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public Terrain( double x_loc, double y_loc, double width, double height )
	{
		super( new PolygonOrCircle( x_loc, y_loc, width, height ) );
	}

	public final float getAlphaTransparency()
	{
		return alphaTransparency;
	}

	@Setter( componentType = OPFComponentType.SliderZeroToOne )
	public final void setAlphaTransparency( float value )
	{
		this.alphaTransparency = value;
	}
}
