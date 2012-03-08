package samplegame;

public interface IThink	// Therefore I am.  Sam I am.  Would you like some green eggs & Ham?
{
	public int getMiniTicksThisTick();
	public void setMiniTicksThisTick( int value );
	
	public void OnBeginTick();
	public void OnEndTick();
	//Tick tock, Tick tock, Tick tock clock.
	
}
