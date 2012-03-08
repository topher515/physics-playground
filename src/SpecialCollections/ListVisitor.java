package SpecialCollections;

import java.util.List;
import java.util.Vector;

/**
 * @version 1.5
 */
public class ListVisitor<T> implements ItemVisitor<T>
{
	private List<T> items;

	public ListVisitor()
	{
		items = new Vector<T>();
	}
	
	public ListVisitor( List<T> list )
	{
		items = list;
	}

	public void visitItem( T item )
	{
		items.add( item );
	}

	public List<T> getItems()
	{
		return items;
	}

}
