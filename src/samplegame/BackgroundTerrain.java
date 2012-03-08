package samplegame;



public class BackgroundTerrain extends Terrain
{	
	private String m_Image = "stars_bg.png";
	
	public BackgroundTerrain()
	{
		super();
	}

	public BackgroundTerrain( double x, double y, double w, double h )
	{
		
		super( x, y, w, h );
	}

	@Override
	public String DefaultImageLocation() {
		return m_Image;
	}
	
	public void setDefaultImageLocation(String nameOfImage) {
		m_Image = nameOfImage;
	}

	@Override
	public boolean isCollidable()
	{
		return false;
	}
}
