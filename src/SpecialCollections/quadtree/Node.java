/*
 * The JTS Topology Suite is a collection of Java classes that implement the fundamental operations required to validate a given geo-spatial data set
 * to a known topological specification. Copyright (C) 2001 Vivid Solutions This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA For more information, contact: Vivid Solutions Suite #1A 2328 Government Street Victoria BC V8T 5G5
 * Canada (250)385-6040 www.vividsolutions.com
 */
package SpecialCollections.quadtree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Represents a node of a {@link Quadtree}. Nodes contain items which have a spatial extent corresponding to the node's position in the quadtree.
 * 
 * @version 1.5
 */
public class Node<T> extends NodeBase<T>
{
	public static <T> Node<T> createNode( Rectangle2D env )
	{
		Key key = new Key( env );
		Node<T> node = new Node<T>( key.getEnvelope(), key.getLevel() );
		return node;
	}

	public static <T> Node<T> createExpanded( Node<T> node, Rectangle2D addEnv )
	{
		//Envelope expandEnv = new Envelope( addEnv );
		Rectangle2D expandEnv = new Rectangle2D.Double();
		expandEnv.setFrame( addEnv );
		
		if ( node != null )
		{
			//expandEnv.expandToInclude( node.env );
			expandEnv = expandEnv.createUnion( node.env );
		}

		Node<T> largerNode = createNode( expandEnv );
		if ( node != null )
			largerNode.insertNode( node );
		return largerNode;
	}

	private Rectangle2D env;

	private Point2D center;

	private int level;

	public Node( Rectangle2D env, int level )
	{
		// this.parent = parent;
		this.env = env;
		this.level = level;
		center = new Point2D.Double( ( env.getMinX() + env.getMaxX() ) / 2, ( env.getMinY() + env.getMaxY() ) / 2 );
/*		center.x = ( env.getMinX() + env.getMaxX() ) / 2;
		center.y = ( env.getMinY() + env.getMaxY() ) / 2;*/
	}

	public Rectangle2D getEnvelope()
	{
		return env;
	}

	@Override
	protected boolean isSearchMatch( Rectangle2D searchEnv )
	{
		return env.intersects( searchEnv );
	}

	/**
	 * Returns the subquad containing the envelope. Creates the subquad if it does not already exist.
	 */
	public Node<T> getNode( Rectangle2D searchEnv )
	{
		int subnodeIndex = getSubnodeIndex( searchEnv, center );
		// if subquadIndex is -1 searchEnv is not contained in a subquad
		if ( subnodeIndex != -1 )
		{
			// create the quad if it does not exist
			Node<T> node = getSubnode( subnodeIndex );
			// recursively search the found/created quad
			return node.getNode( searchEnv );
		}
		else
		{
			return this;
		}
	}

	/**
	 * Returns the smallest <i>existing</i> node containing the envelope.
	 */
	public NodeBase<T> find( Rectangle2D searchEnv )
	{
		int subnodeIndex = getSubnodeIndex( searchEnv, center );
		if ( subnodeIndex == -1 )
			return this;
		if ( subnode[subnodeIndex] != null )
		{
			// query lies in subquad, so search it
			Node<T> node = subnode[subnodeIndex];
			return node.find( searchEnv );
		}
		// no existing subquad, so return this one anyway
		return this;
	}

	void insertNode( Node<T> node )
	{
		// Assert.isTrue(env == null || env.contains(node.env));
		assert ( env == null || env.contains( node.env ) );
		// System.out.println(env);
		// System.out.println(quad.env);
		int index = getSubnodeIndex( node.env, center );
		// System.out.println(index);
		if ( node.level == level - 1 )
		{
			subnode[index] = node;
			// System.out.println("inserted");
		}
		else
		{
			// the quad is not a direct child, so make a new child quad to contain it
			// and recursively insert the quad
			Node<T> childNode = createSubnode( index );
			childNode.insertNode( node );
			subnode[index] = childNode;
		}
	}

	/**
	 * get the subquad for the index. If it doesn't exist, create it
	 */
	private Node<T> getSubnode( int index )
	{
		if ( subnode[index] == null )
		{
			subnode[index] = createSubnode( index );
		}
		return subnode[index];
	}

	private Node<T> createSubnode( int index )
	{
		// create a new subquad in the appropriate quadrant

		double minx = 0.0;
		double maxx = 0.0;
		double miny = 0.0;
		double maxy = 0.0;

		switch( index )
		{
			case 0:
				minx = env.getMinX();
				maxx = center.getX();
				miny = env.getMinY();
				maxy = center.getY();
				break;
			case 1:
				minx = center.getX();
				maxx = env.getMaxX();
				miny = env.getMinY();
				maxy = center.getY();
				break;
			case 2:
				minx = env.getMinX();
				maxx = center.getX();
				miny = center.getY();
				maxy = env.getMaxY();
				break;
			case 3:
				minx = center.getX();
				maxx = env.getMaxX();
				miny = center.getY();
				maxy = env.getMaxY();
				break;
		}
		//Envelope sqEnv = new Envelope( minx, maxx, miny, maxy );
		Rectangle2D sqEnv = new Rectangle2D.Double();
		sqEnv.setFrameFromDiagonal( minx, miny, maxx, maxy );
		Node<T> node = new Node<T>( sqEnv, level - 1 );
		return node;
	}

}
