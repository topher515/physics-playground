package samplegame;

import gui.opf.Setter;

public class Obstacle extends Terrain implements IDestroyable
{
	private int m_CurrentHP;

	private int m_MaxHP;

	public Obstacle()
	{
		super();
	}

	public Obstacle( double x, double y, double w, double h, int maxHP, int startHP )
	{
		super( x, y, w, h );

		m_MaxHP = maxHP;
		if ( startHP > maxHP )
		{
			m_CurrentHP = maxHP;
		}
		else
		{
			m_CurrentHP = startHP;
		}

	}

	@Override
	protected String DefaultImageLocation()
	{
		return "obstacle.png";
	}

	@Override
	public boolean isCollidable()
	{
		return !isDestroyed();
	}

	public void Destroy()
	{
		m_CurrentHP = -1;
	}

	public int getCurrentHP()
	{
		return m_CurrentHP;
	}

	public int getMaxHP()
	{
		return m_MaxHP;
	}

	public boolean isDestroyed()
	{
		return m_CurrentHP <= 0;
	}

	@Setter
	public final void setCurrentHP( int value )
	{
		m_CurrentHP = value;
	}

	@Setter
	public final void setMaxHP( int value )
	{
		m_MaxHP = value;
	}
}
