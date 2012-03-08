package samplegame;

public interface IDestroyable
{
	public void Destroy();
	public boolean isDestroyed();
	
	public int getCurrentHP();
	public int getMaxHP();
	
	public void setCurrentHP( int value );
	public void setMaxHP( int value );
}
