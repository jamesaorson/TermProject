package termproject;

/**
 * Basic storage element for the 2-4 Tree
 *
 * @author Dr. Gallagher
 * @version 1.0
 * Created 2 Mar 2001
 * Summary of Modifications
 *      3 Dec 2009 - DMG - changed type for data stored in TFNode to Item
 *          and changed necessary methods to deal with Item instead of Object
 *		6 Dec 2016 - JAO - Moved wcit() and ffgtet() into this. Left off access
 *			modifier because we wanted it visible within the package. Also added
 *			isExternal() method.
 *		10-Dec-2016 - STG - added javadoc
 *		
 * Description: The basic node for a 2-4 tree.  Contains an array of Items,
 * an array of references to children TFNodes, a pointer to a parent TFNode,
 * and a count of how many Items are stored in the node.
 */

public class TFNode {

    private static final int MAX_ITEMS = 3;

    private int numItems = 0;
    private TFNode nodeParent;
    private TFNode[] nodeChildren;
    // DMG 3 Dec 09 - changed type to Item
    private Item[] nodeItems;

    public TFNode() {
            // make them one bigger than needed, so can handle oversize nodes
            // during inserts
        nodeChildren = new TFNode[MAX_ITEMS+2];
        nodeItems = new Item[MAX_ITEMS+1];
    }

	/**
	 * @return number of items in a TFNode
	 */
    public int getNumItems () {
        return numItems;
    }

	/**
	 * @return max number of items allowed in a TFNode
	 */
    public int getMaxItems() {
        return MAX_ITEMS;
    }

	/**
	 * @return parent of node
	 */
    public TFNode getParent() {
        return nodeParent;
    }
	
	/**
	 * @param parent: node to set specified node's parent to
	 */
    public void setParent (TFNode parent) {
        nodeParent = parent;
    }
	
	/**
	 * @param index: index in node of desired item [0 - maxItems]
	 * @return item at desired location
	 */
    public Item getItem(int index) {
        if ( (index < 0) || (index > (numItems-1) ) )
            throw new TFNodeException();
        return nodeItems[index];
    }
	
	/**
	 * @param index: index of place in node for item to be insert
	 * @param data: item to be insert into node
	 */
    // adds, but does not extend array; so it overwrites anything there
    public void addItem (int index, Item data) {
            // always add at end+1; check that you are within array
        if ( (index < 0) || (index > numItems) || (index > MAX_ITEMS) )
            throw new TFNodeException();
        nodeItems[index] = data;
        numItems++;
    }
	
	/**
	 * @param index: index of place in node for item to be insert
	 * @param data: item to be insert into node
	 */
    // this function inserts an item into the node, and adjusts into child
    // pointers to add the proper corresponding pointer
    public void insertItem (int index, Item data) {
        if ( (index < 0) || (index > numItems) || (index > MAX_ITEMS) )
            throw new TFNodeException();
            // adjust Items
        for (int ind=numItems; ind > index; ind--) {
            nodeItems[ind] = nodeItems[ind-1];
        }
            // insert new data into hole made
        nodeItems[index] = data;
            // adjust children pointers; if inserting into index=1, we make
            // pointers 1 and 2 to point to 1; this is because whoever called
            // this function will fix one of them later; index 0 doesn't change;
            // pointer 3 becomes pointer 2; pointer 4 becomes 3, etc.
        for (int ind=numItems+1; ind > index; ind--) {
            nodeChildren[ind] = nodeChildren[ind-1];
        }
        numItems++;
    }

	/**
	 * @param index: index of item to be removed
	 * @return item that is removed
	 */
    // this method removes item, and shrinks array
    public Item removeItem (int index) {
        if ( (index < 0) || (index > (numItems-1) ) ) {
            if (index > (numItems - 1)) {
				throw new TFNodeException("Too high");
			}
			else {
				throw new TFNodeException("Too low");
			}
		}
        Item removedItem = nodeItems[index];

        for (int ind=index; ind < numItems-1; ind++) {
            nodeItems[ind] = nodeItems[ind+1];
        }
        nodeItems[numItems-1] = null;
            // fix children pointers also
            // typically, you wouldn't expect to do a removeItem unless
            // children are null, because removal of an item will mess up the
            // pointers; however, here we will simply delete the child to the
            // left of the removed item; i.e., the child with same index
        for (int ind=index; ind < numItems; ind++) {
            nodeChildren[ind] = nodeChildren[ind+1];
        }
        nodeChildren[numItems] = null;
        numItems--;
        return removedItem;
    }

	/**
	 * @param index: index of item to be deleted
	 * @return item that is deleted
	 */
    // this method removes item, but does not shrink array
    public Item deleteItem (int index) {
        if ( (index < 0) || (index > (numItems-1) ) ) {
			if (index > (numItems - 1)) {
				throw new TFNodeException("Too high");
			}
			else {
				throw new TFNodeException("Too low");
			}
		}
        Item removedItem = nodeItems[index];
        nodeItems[index] = null;

        numItems--;
        return removedItem;
    }
	
	/**
	 * @param index: index of item to be replaced
	 * @param newItem: item to be insert into node
	 * @return item that was removed to make room for new item
	 */
    // replaces Item at index with newItem, returning the old Item
    public Item replaceItem (int index, Item newItem) {
        if ( (index < 0) || (index > (numItems-1) ) )
            throw new TFNodeException();
        Item returnItem = nodeItems[index];

        nodeItems[index] = newItem;
        return returnItem;
    }

	/**
	 * @param index: index of child to be returned
	 * @return child at specified index
	 */
    public TFNode getChild (int index) {
        if ( (index < 0) || (index > (MAX_ITEMS+1)) )
            throw new TFNodeException();
        return nodeChildren[index];
    }
	
	/**
	 * @param index: index of child to be set
	 * @param child: node of child
	 */
    public void setChild (int index, TFNode child) {
        if ( (index < 0) || (index > (MAX_ITEMS+1)) )
            throw new TFNodeException();
        nodeChildren[index] = child;
    }
	
	/**
      * @return index of location of specified node in its parent's child array
	  *			or -1 if node is a root
	  * @throws ElementNotFoundException when the parent does not know it has a
	  *			the specified node as its child
      */
    int wcit() {
        if (nodeParent == null) {
            return -1;
        }
        else {
			//Runs through parent's child array comparing pointers to find out
			//what index of its child array node is in.
            for (int i = 0; i < (nodeParent.getNumItems() + 1); ++i) {
                if (this == nodeParent.getChild(i)) {
                    return i;
                }
            }
            throw new ElementNotFoundException("The parent is unaware that it"
					+ " has this child.");            
        }
    }
    
	/**
	  * @param key: key to be compared to
	  * @param comp: comparator to use for searching. only isLessThan is used
	  * @return the index of the insert point
	  */
    int ffgtet(Object key, Comparator comp){
        // go through the node item array and return insert point
        for (int i = 0; i < numItems; i++){
            if (!comp.isLessThan(nodeItems[i].key(), key)){
                return i;
            }
        }
		
        // if we haven't returned at this point, the insert point is at the
        // first unoccupied index.
        return numItems;
	}
	
	/**
	  * @return true if node is external
	  */
	public boolean isExternal() {
		return nodeChildren[0] == null;
	}
}