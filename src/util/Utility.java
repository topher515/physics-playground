package util;

import gui.events.UserEvent;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JInternalFrame;

import physics.BaseEntity;

/**
 * A class of commonly used, misc, helper methods.
 * 
 * @author The UC Regents
 */
public final class Utility
{
	private static Random random = new Random();

	public static final String ResourcePath = "resources//";

	public static final String SoundPath = "sounds//";

	private static Hashtable<String, BufferedImage> imageCache = new Hashtable<String, BufferedImage>();

	private static Hashtable<String, AudioClip> soundCache = new Hashtable<String, AudioClip>();

	/**
	 * @param filename The filename of the image
	 * @return The BufferedImage from the image. If the file wasn't found, returns the DefaultImage. If any other errors, returns null
	 */
	public static BufferedImage GetBufferedImage( String filename )
	{

		if ( imageCache.containsKey( filename ) )
			return imageCache.get( filename );

		try
		{
			File f = new File( ResourcePath + filename );

			BufferedImage b;

			if ( f.exists() )
			{
				b = ImageIO.read( f );
			}
			else
			{
				b = ImageIO.read( new File( ResourcePath + getDefaultImageName() ) );
			}

			imageCache.put( filename, b );

			return b;
		}
		catch ( Exception e )
		{
		}

		return null;
	}

	/**
	 * Return the filename of the default image. Usually an error image.
	 * 
	 * @return Returns a default or 'error.png'
	 */
	public static String getDefaultImageName()
	{
		return "error.png";
	}

	/**
	 * Gets a Random Boolean value
	 * 
	 * @return Returns either true or false with equal probability
	 */
	public static boolean RandomBoolean()
	{
		return random.nextBoolean();
	}

	/**
	 * @return Returns a random double between 0 & 1
	 */
	public static double RandomDouble()
	{
		return random.nextDouble();
	}

	/**
	 * @param max The maximum int value to return. The value is the maximum and excluded from the result
	 * @return Returns a value from 0 Inclusive to max Exclusive
	 */
	public static int RandomInt( int max )
	{
		return random.nextInt( max );
	}

	// min Inclusive, max exclusive
	/**
	 * @param min The minimum int value to return. The value is the minimum and included in the result
	 * @param max The maximum int value to return. The value is the maximum and excluded from the result
	 * @return Returns an integer from min Inclusive to max Exclusive
	 */
	public static int RandomInt( int min, int max )
	{
		if ( min > max )
		{
			int copy = min;
			min = max;
			max = copy;
		}
		else if ( min == max )
		{
			return min;
		}

		return min + random.nextInt( ( max - min ) + 1 );
	}

	// min Inclusive, max exclusive
	/**
	 * @param min The minimum double value to return. The value is the minimum and included in the result
	 * @param max The maximum double value to return. The value is the maximum and excluded from the result
	 * @return Returns a double from min Inclusive to max Exclusive
	 */
	public static double RandomDouble( double min, double max )
	{
		if ( min > max )
		{
			double copy = min;
			min = max;
			max = copy;
		}
		else if ( min == max )
		{
			return min;
		}

		return min + random.nextDouble() * max;
	}

	
	/**
	 * Focuses on a JInternalFrame
	 * 
	 * @param comp The frame to focus on
	 */
	public static void FocusOn( JInternalFrame comp )
	{
		try
		{
			comp.setSelected( true );
		}
		catch ( Exception e )
		{
			// stupidness of stupid java
		}
	}

	/**
	 * Capitalizes the beginning of a string.
	 * 
	 * @param s The string to capitalize
	 * @return Returns the Capitalized string
	 */
	public static String Capitalize( String s )
	{
		if ( s == null || s.length() == 0 )
			return s;

		return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
	}

	/**
	 * Gets a Random color with values between 25 and 225 for each RGB Value
	 * 
	 * @return A random color
	 */
	public static Color getRandomColor()
	{
		return new Color( Utility.RandomInt( 25, 225 ), Utility.RandomInt( 25, 225 ), Utility.RandomInt( 25, 225 ), 255 );
	}
	
	/**
	 * Returns a random color which is similar looking to the color
	 * passed in.
	 * 
	 * @param c The color which the new color should resemble
	 * @return The color similar to the passed color
	 */
	public static Color getColorSimilarTo(Color c)
	{
		int red = c.getRed()+Utility.RandomInt(-50,50);
		int green = c.getGreen()+Utility.RandomInt(-50,50);
		int blue = c.getBlue()+Utility.RandomInt(-50,50);

		if( red > 255 || red < 0 )
			red = c.getRed() - (red % 255 );
		
		if( green > 255 || green < 0 )
			green = c.getGreen() - (green % 255 );
		
		if( blue > 255 || blue < 0 )
			blue = c.getBlue() - (blue % 255 );
		

		return new Color(  red, green, blue, c.getAlpha() );
	}


	/**
	 * Gets a Sound and caches it if not yet cached.
	 * 
	 * @param filename The sound to cache
	 * @return The sound
	 */
	public static AudioClip cacheSound( final String filename )
	{
		if ( !soundCache.containsKey( filename ) )
		{
			try
			{
				File f = new File( ResourcePath + SoundPath + filename );

				AudioClip ac;

				if ( f.exists() )
				{
					ac = Applet.newAudioClip( f.toURI().toURL() );
					soundCache.put( filename, ac );

					return ac;
				}
			}
			catch ( Exception e )
			{
				return null;
			}
		}

		return soundCache.get( filename );
	}

	/**
	 * Plays a sound in a new thread. Gets the sound if it exists in the cache, or, caches it if needbe.
	 * 
	 * @param filename The filename of the sound.
	 */
	public static void PlaySound( final String filename )
	{
		Thread thread = new Thread()
		{
			@Override
			public void run()
			{
				AudioClip ac = cacheSound( filename );

				if ( ac != null )
					ac.play();
			}
		};

		thread.start();
	}

	/**
	 * Execute all UserEvents associated with a given Base Entity.
	 * 
	 * @param type Corresponds to the event id (e.g. MouseEvent.BUTTON_PRESSED)
	 * @param subtype Corresponds to the specific key or button associated with the event (e.g. MouseEvent.BUTTON1)
	 * @param b The object on which to execute the events
	 */
	public static void HandleEvent( int type, int subtype, BaseEntity b )
	{
		for ( UserEvent evt : b.getUserEvents() )
		{
			if ( evt.getEventType() == type && evt.getEventSubType() == subtype )
			{
				evt.execute();
			}
		}
	}

	/**
	 * Loop through all entities and execute any events bound to a certain type and subtype.
	 * 
	 * @param type Corresponds to the event id (e.g. MouseEvent.BUTTON_PRESSED)
	 * @param subtype Corresponds to the specific key or button associated with the event (e.g. MouseEvent.BUTTON1)
	 * @param ents Execute appropriate events with each entity in this vector
	 */
	public static void HandleEvents( int type, int subtype, List<BaseEntity> ents )
	{
		for ( BaseEntity b : ents )
		{
			HandleEvent( type, subtype, b );
		}
	}

	/**
	 * Creates and returns a new Color identical in all ways except that it's alpha value is set to the one specified
	 * 
	 * @param c The color to apply the alpha to.
	 * @param alpha The alpha value to apply to the Color. Normal values from 0 - 255
	 * @return Returns the new color with the aplha applied to it
	 */
	public static Color ApplyAlpha( Color c, int alpha )
	{
		return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
	}

	/**
	 * Creates an instance of the specified class.
	 * @param <T> The Type from the Class
	 * 
	 * @param c The class to instantiate
	 * @return Returns the instance of the specified class if there are no errors. Otherwise, it returns null.
	 */
	public static <T> T CreateInstanceOf( Class<T> c )
	{
		if ( c == null )
			return null;

		try
		{
			return c.newInstance();
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
			System.err.println( exception.toString() );
			return null;
		}
	}
}
