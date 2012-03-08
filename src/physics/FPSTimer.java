package physics;

import sun.misc.Perf;

public class FPSTimer
{
	private Perf perf;

	private long startTime, endTime;

	private long FPS;

	private int c = 0, refreshRate = 20;

	public FPSTimer()
	{
		perf = Perf.getPerf();
		startTime = perf.highResCounter();

	}

	public void startTimer()
	{
		startTime = perf.highResCounter();
	}

	public long stopTimer()
	{
		endTime = perf.highResCounter() - startTime;
		
		return endTime;
	}

	public long getFPS( long diffTime )
	{

		if ( c >= refreshRate && diffTime > 0 )
		{
			c = 0;
			FPS = ( FPS + 1000000 / ( diffTime ) ) / 2;

		}
		else
		{
			c++;
		}

		return FPS;

	}

}
