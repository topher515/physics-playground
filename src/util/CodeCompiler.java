package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;

/**
 * This class handles aspects of dynamically compiled code, including storing
 * and creating the dynamic classes themselves.
 * 
 * @author The UC Regents
 */
public final class CodeCompiler
{
	private static boolean m_Compiling = false;

	private static final com.sun.tools.javac.Main m_JavaCompiler = new com.sun.tools.javac.Main();

	private static Hashtable<String, Class> m_ConversionTable = new Hashtable<String, Class>();

	public static final boolean isCompiling()
	{
		return m_Compiling;
	}

	public static final void setCompiling( boolean compiling )
	{
		m_Compiling = compiling;
	}

	/**
	 * The Conversion table that converts from the given 'friendly' string to
	 * the internal class
	 */
	public static final Hashtable<String, Class> getConversionTable()
	{
		return m_ConversionTable;
	}

	/**
	 * @param s
	 *            The 'friendly' name of the class to find
	 * @return Returns the class object if found, otherwise it returns null
	 */
	public static Class GetClassFor( String s )
	{
		return m_ConversionTable.get( s );
	}

	private static String m_LastError;

	public static String getLastError()
	{
		return m_LastError;
	}

	/***************************************************************************
	 * In the contained code, use %ThisClass% to represent the 'self' class.
	 */
	/**
	 * Compiles a new class and adds it as a temporary file into the class/
	 * directory for use elsewhere Requires write access into the class/
	 * directory.
	 * 
	 * For referencing the true name of the class within the class itself, for
	 * example, in constructors, use %THISCLASS% and it'll be automagically
	 * replaced with the true classname.
	 * 
	 * @param targetClassName
	 *            The friendly & desired name for the class
	 * @param containedCode
	 *            A string containing the desired code WITHIN the initial
	 *            brackets of the class
	 * @param classDeclarationAppendix
	 *            A string containing the declatations of a class, specifically,
	 *            the Implements and Extends keywords
	 * @return Returns a boolean indicating the status of the compilation. True
	 *         means that the compile succeeded. If false, look inside
	 *         getLastError for a string containg an errorMessage
	 */
	@SuppressWarnings( "static-access" )
	public static final boolean Compile( String targetClassName, String containedCode, String classDeclarationAppendix )
	{

		File file = null;

		try
		{
			file = File.createTempFile( targetClassName, ".java", new File( System.getProperty( "user.dir" ) ) );
		}
		catch ( IOException exception )
		{
			HandleMessage( exception.toString() );
			return false;
		}

		// Set the file to be deleted on exit
		file.deleteOnExit();

		// Get the file name and extract a class name from it

		String filename = file.getName();
		String classname = filename.substring( 0, filename.length() - 5 ); // File
																			// name
																			// without
																			// the
																			// .java

		// Output the source

		PrintWriter out = null;
		try
		{
			out = new PrintWriter( new FileOutputStream( file ) );
		}
		catch ( IOException exception )
		{
			HandleMessage( exception.toString() );
			return false;
		}

		// ClassLoader.getSystemClassLoader().

		out.println( "package source.ucregents.playground;" );

		out.println( "import java.util.*;" );
		out.println( "import javax.vecmath.Vector2d;" );
		out.println( "import java.awt.*;" );
		out.println( "import *;" );
		out.println( "import gui.*;" );
		out.println( "import physics.*;" );
		out.println( "import samplegame.*;" );
		out.println( "import serialization.*;" );
		out.println( "import java.awt.geom.Point2D;" );
		out.println();
		out.println( "/**" );
		out.println( " * Source created on " + new Date() );
		out.println( " */" );
		out.println( "public class " + classname + " " + classDeclarationAppendix );
		out.println( "{" );

		out.println( containedCode.replaceAll( "%THISCLASS%", classname ) ); // Must
																				// Replace
																				// cause
																				// of
																				// the
																				// dynamic
																				// name/file
																				// structure

		out.println( "}" );

		// Flush and close the stream

		out.flush();
		out.close();

		// Compile

		String[] args = new String[] { "-d", "class/", filename };

		// System.out.println(args[1] + args[2]);

		m_Compiling = true;
		int status = m_JavaCompiler.compile( args );
		m_Compiling = false;

		// Run

		// System.out.println( status );

		switch( status )
		{
			case 0: // OK
				// Make the class file temporary as well

				File f = new File( "class/source/ucregents/playground/", classname + ".class" );

				f.deleteOnExit();

				// System.out.println( f.exists() );

				try
				{
					URL[] urlAry = new URL[] { f.toURI().toURL() };

					Class<?> clazz = null;

					try
					{
						myURLClassLoader cl = new myURLClassLoader( urlAry );

						clazz = cl.defineClass( "" + classname, f );

						// System.out.println( clazz.toString());

						// System.out.println( "!" +
						// cl.findClass(""+classname)
						// );
					}
					catch ( Exception e )
					{
						e.printStackTrace();
					}

					m_ConversionTable.put( targetClassName, clazz ); // Add to the hashtable for future lookups


					/*
					 * Method main = clazz.getMethod("main", new Class[] {
					 * String[].class }); main.invoke(null, new Object[] { new
					 * String[0] });
					 */
					try
					{
						Method init = clazz.getMethod( "Initialize", new Class[] {} );

						if ( init != null )
							init.invoke( null, new Object[] {} );
					}
					catch ( NoSuchMethodException e )
					{
						// Ignore no initialize method found.
					}

					return true;
				}
				/*
				 * catch (InvocationTargetException ex) { // Exception in the
				 * main method that we just tried to run
				 * 
				 * HandleMessage("Exception in main: " +
				 * ex.getTargetException()); HandleMessage(
				 * ex.getTargetException().toString() ); }
				 */
				catch ( Exception ex )
				{
					HandleMessage( ex.toString() );
				}
				break;
			case 1:
				HandleMessage( "Compile status: ERROR" );
				break;
			case 2:
				HandleMessage( "Compile status: CMDERR" );
				break;
			case 3:
				HandleMessage( "Compile status: SYSERR" );
				break;
			case 4:
				HandleMessage( "Compile status: ABNORMAL" );
				break;
			default:
				HandleMessage( "Compile status: Unknown exit status" );
		}

		return false;
	}

	private static void HandleMessage( String s )
	{
		System.out.println( s );

		m_LastError = s;

	}

}
