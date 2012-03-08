package serialization;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import physics.BaseEntity;

/**
 * Designed to handle Saving the game.
 * 
 * @author The UC Regents
 */
public class Saver
{
	private Document outputDoc;

	private Element root;

	private File xmlToPath;

	private static List<BaseEntity> m_Entities = new Vector<BaseEntity>();

	/**
	 * Constructor for serializing entities to an XML file. Takes a vector of entities and parses them into xml using their toXMLElement() method
	 * 
	 * @param ents Entities to serialize
	 * @param saveTo XML File to save to
	 */
	public Saver( List<BaseEntity> ents, File saveTo )
	{
		m_Entities = ents;
		xmlToPath = saveTo;

		if ( saveTo == null )
		{
			throw new IllegalArgumentException( "No file given to the Saver" );
		}
	}

	public void Save()
	{
		try
		{
			this.createXMLDoc( m_Entities );
			String htmlOut = this.toString();

			System.out.println( "Saving result to " + xmlToPath );
			FileWriter fw = new FileWriter( xmlToPath );
			fw.write( htmlOut, 0, htmlOut.length() );
			fw.flush();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Construct the XML document representing the Vector of entities in the editor
	 * 
	 * @param ents Entities to serialize
	 * @throws ParserConfigurationException
	 */
	public void createXMLDoc( List<BaseEntity> ents ) throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		outputDoc = db.newDocument();
		root = outputDoc.createElement( "Game" );

		for ( BaseEntity b : m_Entities )
		{

			Node n = outputDoc.importNode( b.toXMLElement(), true );
			root.appendChild( n );

			// System.out.println( "Trying to dynamagically get the value of the
			// 'Class' Attribute from a XML Node "
			// + n.getAttributes().getNamedItem( "Class" ).getNodeValue() );
		}

		outputDoc.appendChild( root );
	}

	/**
	 * Return XML
	 */
	@Override
	public String toString()
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			// initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult( new StringWriter() );
			DOMSource source = new DOMSource( outputDoc );
			transformer.transform( source, result );
			return result.getWriter().toString();

		}
		catch ( Exception e )
		{
			return super.toString();
		}
	}
}
