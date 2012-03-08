package util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * A custom class loader that gives access to URLClassLoader's protected methods.
 *
 */
public class myURLClassLoader extends URLClassLoader
{

	/**
	 * See URLClassLoader
	 * 
	 * @param arg0 URL[] Array
	 */
	public myURLClassLoader( URL[] arg0 )
	{
		super( arg0 );
		// super( arg0, ClassLoader.getSystemClassLoader() );
	}

	@Override
	public Class<?> findClass( String name ) throws ClassNotFoundException
	{
		return super.findClass( name );
	}

	/**
	 * Simplified defineClass accessor for the base class' defineClass method
	 * @param name The class name
	 * @param b Byte array of the class
	 * @return The Class object that becomes defined
	 */
	public Class defineClass( String name, byte[] b )
	{
		return super.defineClass( name, b, 0, b.length );
	}

	/**
	 * Simplified defineClass accessor for the base class' defineClass method
	 * @param name The class name
	 * @param classFile The class file
	 * @return The Class object that becomes defined
	 * @throws IOException 
	 */
	public Class defineClass( String name, File classFile ) throws IOException
	{
		byte buff[] = new byte[(int)( classFile.length() )];

		DataInputStream dis = new DataInputStream( new FileInputStream( classFile ) );

		dis.readFully( buff );
		dis.close();

		return this.defineClass( name, buff );
	}
}
