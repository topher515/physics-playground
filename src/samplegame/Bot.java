package samplegame;

import gui.opf.Setter;

import java.awt.Color;

import physics.MovingEntity;

public class Bot extends MovingEntity implements IDestroyable
{
	private int m_CurrentHP, m_MaxHP;

	public void Destroy()
	{
		m_CurrentHP = -1;
		// TODO Auto-generated method stub

	}

	public final boolean isDestroyed()
	{
		return m_CurrentHP <= 0;
	}

	public final int getCurrentHP()
	{
		return m_CurrentHP;
	}

	public final int getMaxHP()
	{
		return m_MaxHP;
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

	public Bot()
	{
		super( 0, 0, 70, 70, 0, 0 );

		this.setShapeColor( new Color( 0, 0, 0, 0 ) );
	}

	/*
	 * public Bot( Point p, double orientation ) { super( p, orientation ); //
	 * TODO Auto-generated constructor stub }
	 */

	private int energy;

	private int shieldEnergy;

	public final int getEnergy()
	{
		return energy;
	}

	@Setter( getter = "getEnergy" )
	public final void setEnergy( int value )
	{
		this.energy = value;
	}

	public final int getShieldEnergy()
	{
		return shieldEnergy;
	}

	@Setter( getter = "getShieldEnergy" )
	public final void setShieldEnergy( int value )
	{
		this.shieldEnergy = value;
	}

	@Override
	protected String DefaultImageLocation()
	{
		return "sketchbot.png";
	}

	protected void fireBullet()
	{
		//PolygonOrCircle bullet = new PolygonOrCircle(this.getCenterPoint().getX(),this,0);
	}
}
