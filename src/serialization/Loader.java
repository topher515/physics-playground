package serialization;

import util.Utility;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import physics.BaseEntity;

/**
 * Designed to handle loading the game from XML files
 * 
 * @author The UC Regents
 */
public class Loader
{
	private File xmlFromPath;



	/**
	 * CTor to Load Entities from a file
	 * @param loadFrom The XML File to load from
	 */
	public Loader( File loadFrom )
	{
		this.xmlFromPath = loadFrom;

		if ( this.xmlFromPath == null )
		{
			System.out.println( "No filename given" );
		}
	}
	
	/**
	 * Processes and parses the XML file given in the CTOR
	 * @return Returns a Vector of BaseEntities containing the results of the load
	 * @throws Exception
	 */
	public List<BaseEntity> Load() throws Exception
	{
		System.out.println( "Processing URL " + this.xmlFromPath );
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		// XML Dom Document
		Document doc = db.parse( this.xmlFromPath );
		// Element elem = doc.getNodeName();
		// System.out.println(doc.getLocalName());
		NodeList nl = doc.getElementsByTagName( "Entity" );

		List<BaseEntity> entVector = new Vector<BaseEntity>();

		for ( int i = 0; i < nl.getLength(); i++ )
		{
			// Current entity root
			Node curNode = nl.item( i );
			Element e = (Element)curNode;
			CustomXMLReader reader = new CustomXMLReader( doc, e );
			Class<? extends BaseEntity> c = reader.ReadClass( BaseEntity.class, "Class" );
			BaseEntity temp = Utility.CreateInstanceOf( c );
			temp.Deserialize( reader );
			entVector.add( temp );
		}

		return entVector;
	}
}
