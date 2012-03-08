/**
 * 
 */
package serialization;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for XML and PXML files. Based off of tutorial on JavaWorld
 */
public class CustomXMLFileFilter extends FileFilter
{
	public CustomXMLFileFilter()
	{
		super();
	}

	@Override
	public boolean accept( File f )
	{

		if ( f.isDirectory() )
			return true;

		String extension = getExtension( f );
		if ( ( extension.equals( "xml" ) ) || ( extension.equals( "pxml" ) ) )
			return true;

		return false;
	}

	/**
	 * Method to get the extension of the file, in lowercase
	 */
	private String getExtension( File f )
	{
		String s = f.getName();
		int i = s.lastIndexOf( '.' );
		if ( i > 0 && i < s.length() - 1 )
			return s.substring( i + 1 ).toLowerCase();
		return "";
	}

	@Override
	public String getDescription()
	{
		return "Playground XML Files (*.xml, *.pxml)";
	}
}
