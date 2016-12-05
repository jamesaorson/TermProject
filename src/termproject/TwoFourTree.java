package termproject;

/**
  * 
  *
  * @author James Osborne
  * @author Spencer Graffunder
  * @version 1.0 
  * File: TwoFourTree.java
  * Created:  12 Dec 2016
  * ©Copyright James Osborne and Spencer Graffunder. All rights reserved.
  * Summary of Modifications:
  *     01 Dec 2016 - JAO - Added some initial exception throws for
  *     InvalidIntegerException cases.
  *     05 Dec 2016 - JAO - Added wcit()
  *		05 Dec 2016 - STG - Added ffgtet()
  *	                        Completed findElement()
  * 
  * Description: 
  */
public class TwoFourTree implements Dictionary {
    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    /**
      * Searches dictionary to determine if key is present
      * @param key to be searched for
      * @return object corresponding to key; null if not found
      */
    public Object findElement(Object key) {
        if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }
		
		// object that will get returned.
		Object result = null;
		
		// node for walking tree.
		TFNode currentNode = treeRoot;
		
		// while loop varible.
		boolean isFinished = false;
		
		// while we haven't found the item or gotten to bottom of tree,
		while(!isFinished){
			
			// index of element or child we will be using.
			int index = ffgtet(currentNode, key);
			Object currentKey = currentNode.getItem(index).key();
			
			// if the thing at index equals the incoming key
			if(treeComp.isEqual(currentKey, key)){
				// set result and break.
				result = currentNode.getItem(index);
				isFinished = true;
			}else{
				TFNode nextNode = currentNode.getChild(index);
				if(nextNode == null){
					// item isn't in tree so break resulting in a null return.
					isFinished = true;
				}else{
					// walk down tree.
					currentNode = nextNode;
				}
			}
		}
		return result;
    }

    /**
      * Inserts provided element into the Dictionary
      * @param key of object to be inserted
      * @param element to be inserted
      */
    public void insertElement(Object key, Object element) {
        if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }
        if (!treeComp.isComparable(element)) {
            throw new InvalidIntegerException("Element was not an integer");
        }
        
        Item insert = new Item(key, element);
        
        if (treeRoot == null) {
            treeRoot = new TFNode();
            
            treeRoot.addItem(0, insert);
        }
        else {
			int insertIndex = ffgtet(treeRoot, key);
			int tempKey = (int) key;
			int tempElement = (int) element;
			Item insertItem;
			
			//Shifts items in the node over
			for (int i = insertIndex; i < treeRoot.getNumItems(); ++i) {
				//Make item from tempKey and tempElement.
				insertItem = new Item(tempKey, tempElement);
				
				treeRoot.replaceItem(i, insertItem);				
				
				//Must keep key and element as ints to avoid
				//pointer confusion.
				tempKey = (int) (treeRoot.getItem(i).key());
				tempElement = (int) (treeRoot.getItem(i).element());
			}
			
			//Adds last item in for shift process.
			insertItem = new Item(tempKey, tempElement);
			treeRoot.addItem(treeRoot.getNumItems(), insertItem);
			
			if (treeRoot.getNumItems() > treeRoot.getMaxItems()) {
				//Do overflow handling.
			}
		}
    }

    /**
      * Searches dictionary to determine if key is present, then
      * removes and returns corresponding object
      * @param key of data to be removed
      * @return object corresponding to key
      * @exception ElementNotFoundException if the key is not in dictionary
      */
    public Object removeElement(Object key) throws ElementNotFoundException {
        if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }
        
        return null;
	}

    public void printAllElements() {
        int indent = 0;
       
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        
        printTFNode(start);
        indent += 4;
        
        int numChildren = start.getNumItems() + 1;
        
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        
        System.out.println();
    }

    /**
      * 
      * @param node 
      * @return index showing 
      */
    private int wcit(TFNode node) {
        if (node == null) {
            throw new TFNodeException("TFNode was null");
        }
        
        TFNode parent = node.getParent();
        
        if (parent == null) {
            //This may be an unnecessary method
            return -1;
        }
        else {
			//Runs through parent's child array comparing pointers to find out
			//what index of its child array node is in.
            for (int i = 0; i < parent.getNumItems() + 1; ++i) {
                if (node == parent.getChild(i)) {
                    return i;
                }
            }
            
            throw new ElementNotFoundException("Something is wrong in wcit()");            
        }
    }
    
    private int ffgtet(TFNode node, Object key){
        // go through the node item array and return insert point
        for (int i = 0; i < node.getNumItems(); i++){
            if (!treeComp.isLessThan(key, node.getItem(i).key())){
                return i;
            }
        }
		
        // if we haven't returned at this point, the insert point is at the
        // first unoccupied index.
        return node.getNumItems();
	}
    
    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
         
            int childIndex;
         
            for (childIndex = 0; childIndex <= parent.getNumItems(); 
                                 childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems();
                                     childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
        
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }
    }
	
	public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        Integer myInt1 = new Integer(47);
        myTree.insertElement(myInt1, myInt1);
        Integer myInt2 = new Integer(83);
        myTree.insertElement(myInt2, myInt2);
        Integer myInt3 = new Integer(22);
        myTree.insertElement(myInt3, myInt3);

        /*Integer myInt4 = new Integer(16);
        myTree.insertElement(myInt4, myInt4);

        Integer myInt5 = new Integer(49);
        myTree.insertElement(myInt5, myInt5);

        Integer myInt6 = new Integer(100);
        myTree.insertElement(myInt6, myInt6);

        Integer myInt7 = new Integer(38);
        myTree.insertElement(myInt7, myInt7);

        Integer myInt8 = new Integer(3);
        myTree.insertElement(myInt8, myInt8);

        Integer myInt9 = new Integer(53);
        myTree.insertElement(myInt9, myInt9);

        Integer myInt10 = new Integer(66);
        myTree.insertElement(myInt10, myInt10);

        Integer myInt11 = new Integer(19);
        myTree.insertElement(myInt11, myInt11);

        Integer myInt12 = new Integer(23);
        myTree.insertElement(myInt12, myInt12);

        Integer myInt13 = new Integer(24);
        myTree.insertElement(myInt13, myInt13);

        Integer myInt14 = new Integer(88);
        myTree.insertElement(myInt14, myInt14);

        Integer myInt15 = new Integer(1);
        myTree.insertElement(myInt15, myInt15);

        Integer myInt16 = new Integer(97);
        myTree.insertElement(myInt16, myInt16);

        Integer myInt17 = new Integer(94);
        myTree.insertElement(myInt17, myInt17);

        Integer myInt18 = new Integer(35);
        myTree.insertElement(myInt18, myInt18);

        Integer myInt19 = new Integer(51);
        myTree.insertElement(myInt19, myInt19);*/

        myTree.printAllElements();
        System.out.println("done");

        /*myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
            //myTree.printAllElements();
            //myTree.checkTree();
        }*
        
        System.out.println("removing");
        
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) myTree.removeElement(new Integer(i));
        
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > (TEST_SIZE - 15)) {
                myTree.printAllElements();
            }
        }*/
        
        System.out.println("done");
    }	
}
