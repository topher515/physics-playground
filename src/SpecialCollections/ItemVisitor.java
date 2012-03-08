package SpecialCollections;

/**
 * A visitor for items in an index.
 * 
 * @version 1.5
 */

public interface ItemVisitor<T>
{
	void visitItem( T item );
}
