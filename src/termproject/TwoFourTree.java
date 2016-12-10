package termproject;

import java.util.Random;

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
  *     05 Dec 2016 - JAO - Added wcit() and got insertElement() to shift.
  *                         Does not handle overflow.
  *                         Need to make major changes to insert still.
  *		05 Dec 2016 - STG - Added ffgtet()
  *	                        Completed findElement()
  *		06 Dec 2016 - JAO - InsertElement() is now fully functional. Overflow()
  *							also works for all cases. Made untested body of
  *							leftTransfer(). Made untested body of leftFusion().
  *							Fixed issue in removeElement and it worked with 
  *                         20 elements. There is a new issue in insertElement()
  *                         when we start inserting 23 elements.
  *     06 Dec 2016 - STG - Added rightTransfer() and rightFusion()
  *		07 Dec 2016 - JAO - Fixed insertElement() and two-four tree passes 10000
  *							element test provided by Dr. Gallagher.
  *     07 Dec 2016 - STG - Made right and left siblings so we don't have to
  *						    keep calling parent.getChild().
  *		08 Dec 2016 - JAO - Created a search() and moveDownRight() to simplify
  *							findElement() and removeElement(). Still has issue
  *							in remove when inserting random elements with seed
  *							of 10000 or greater.
  *		09 Dec 2016 - JAO - Started intensive testing for remove. Only have one
  *							known issue which exists where a node believes it
  *							has 4 items somehow. Problem is present with fusions
  *		10 Dec 2016 - JAO/STG - Fixed all issues. 2-4 tree performs correctly.
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

	@Override
    public int size() {
        return size;
    }

	@Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
      * Searches dictionary to determine if key is present
      * @param key to be searched for
      * @return object corresponding to key; null if not found
      */
	@Override
    public Object findElement(Object key) {
		TFNode currNode = null;
		int itemIndex = -1;
		
		//Finds the node element should be located.
		currNode = searchForKey(currNode, key, itemIndex);
		itemIndex = currNode.ffgtet(key, treeComp);
		
		if (currNode == null) {
			throw new ElementNotFoundException("Find couldn't find element");
		}
		
		//If the element has been found based upon the key, returns element.
		if (treeComp.isEqual(currNode.getItem(itemIndex).key(), key)) {
			return currNode.getItem(itemIndex).element();
		}
		else {
			return null;
		}
    }
	
	private TFNode searchForKey(TFNode currNode, Object key, int itemIndex) {
		// This will go if we need to run down the right child because the key
		// we're looking for it greater than all the keys in the node.
		if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }

		//Start search at root.
		currNode = treeRoot;
		itemIndex = currNode.ffgtet(key, treeComp);
		
		//Checks if we must move down the rightmost child for the key.
		currNode = moveDownRight(currNode, key, itemIndex);
		itemIndex = currNode.ffgtet(key, treeComp);
		
		//As long as we have not found the equivalent key and have not reached
		//an external node, continues the search.
		while (!treeComp.isEqual(currNode.getItem(itemIndex).key(), key) 
			   && !currNode.isExternal()) {
			//Looks down child of insertIndex to find exact insert
			//node. If ending condition is met, we will insert at currNode.
			currNode = currNode.getChild(itemIndex);
			
			itemIndex = currNode.ffgtet(key, treeComp);
			
			//Checks if we must move down the rightmost child for the key.
			currNode = moveDownRight(currNode, key, itemIndex);
			itemIndex = currNode.ffgtet(key, treeComp);
		}
		
		return currNode;
	}
	
	private TFNode moveDownRight(TFNode currNode, Object key, int itemIndex) {
		// This checks for the case where we must run down the right child due
		//to the key we're looking for being greater
		//than all current keys of the node.
		while (itemIndex == currNode.getNumItems()) {
			currNode = currNode.getChild(itemIndex);
			itemIndex = currNode.ffgtet(key, treeComp);
		}
		
		return currNode;
	}

    /**
      * Inserts provided element into the Dictionary
      * @param key of object to be inserted
      * @param element to be inserted
      */
	@Override
    public void insertElement(Object key, Object element) {
        if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }
        
        Item insertItem = new Item(key, element);

        //If there is no root yet, make the element into the root.
        if (treeRoot == null) {
			treeRoot = new TFNode();

			treeRoot.addItem(0, insertItem);
        }
        else {
			TFNode currNode = treeRoot;
			int insertIndex = currNode.ffgtet(key, treeComp);

			//Finds the node we will be inserting into. We should always insert
			//at a leaf.
			while (!currNode.isExternal()) {
				//Looks down child of insertIndex to find exact insert
				//node. We will end up inserting at currNode.
				currNode = currNode.getChild(insertIndex);

				if (currNode == null) {
					currNode = new TFNode();
				}
				
				insertIndex = currNode.ffgtet(key, treeComp);
			}
			
			//Inserts item and checks for overflow.
			currNode.insertItem(insertIndex, insertItem);		

			overflow(currNode);
        }
        
        ++size;
    }

	/**
	  * 
	  * @param currNode 
	  */
	private void overflow(TFNode currNode) {
		//AS long as currNode is overflowed, continue to propogate up the tree.
		while (currNode.getNumItems() > currNode.getMaxItems()) {
			Item tempItem = currNode.getItem(2);
			TFNode parent = currNode.getParent();
			TFNode newNode = new TFNode();

			newNode.addItem(0, currNode.getItem(3));

			//If there is no parent, we are at root and must make a new one.
			if (parent == null) {
				parent = new TFNode();
				
				parent.addItem(0, tempItem);
				//Hook up children.
				parent.setChild(0, currNode);
				parent.setChild(1, newNode);
				//Set parent of old root to the new root.
				currNode.setParent(parent);
				setRoot(parent);
			}
			//Otherwise, we will insert based upon our child position.
			else {
				parent.insertItem(currNode.wcit(), tempItem);
				//Hook up newNode
				parent.setChild(currNode.wcit() + 1, newNode);
			}

			newNode.setParent(parent);

			//Hook up child d and e with newNode as parents
			for(int i = 0; i < 2; ++i){
				if (currNode.getChild(i + 3) != null) {
					TFNode temp;
					temp = currNode.getChild(i + 3);
					temp.setParent(newNode);
					newNode.setChild(i, temp);
					currNode.setChild(i + 3, null);
				}
			}

			//Deletes items that are now in newNode,
			//which were previously part of currNode.
			currNode.deleteItem(3);
			currNode.deleteItem(2);
			
			//Set currNode to parent so we can check if we have 
			//overflowed the parent in fixing, propogating up the tree.
			currNode = parent;
		}
	}
	
    /**
      * Searches dictionary to determine if key is present, then
      * removes and returns corresponding object
      * @param key of data to be removed
      * @return object corresponding to key
      * @exception ElementNotFoundException if the key is not in dictionary
      */
	@Override
    public Object removeElement(Object key) throws ElementNotFoundException {
		TFNode currNode = null;
		int itemIndex = -1;
		
		//Finds the node we will be removing from.
		currNode = searchForKey(currNode, key, itemIndex);
		itemIndex = currNode.ffgtet(key, treeComp);
		
		if (currNode == null) {
			throw new ElementNotFoundException("Remove couldn't find element");
		}
		
		//If the element has been found based upon the key, must delete it
		//and return the element.
		if (treeComp.isEqual(currNode.getItem(itemIndex).key(), key)) {
			return deleteElement(currNode, itemIndex);
		}
		else {
			throw new ElementNotFoundException("Remove couldn't find element");
		}
    }
	
	/**
	  * 
	  * @param node
	  * @param index 
	  * @return 
	  */
    private Object deleteElement(TFNode node, int index){
		Item deletedItem = new Item();
		
		// if node is a leaf
        if(node.isExternal()){
            // null item at index
            deletedItem= node.removeItem(index);
			underflow(node);
        }else{
            // walk through tree to inorder successor
            TFNode successor = node.getChild(index + 1);
			
			while(successor.getChild(0) != null){
				successor = successor.getChild(0);
			}
				
			//Swap with inorder successor.
			deletedItem = node.replaceItem(index, successor.removeItem(0));
			
			//Must now check underflow of current node and then the successor.
			//The successor swap could potentially have made successor an
			//underflowed node.
			underflow(node);
			underflow(successor);
        }

        --size;
		
		return deletedItem.element();
    }
	
	/**
	  * 
	  * @param node 
	  */
	private void underflow(TFNode node) {
		int index = node.wcit();
		TFNode parent = node.getParent();
		
		//If there is an underflow and a parent, perform an underflow fix.
		if (node.getNumItems() == 0 && parent != null) {
			if (index > 0 && parent.getChild(index - 1).getNumItems() >= 2) {
				leftTransfer(node, parent, parent.getChild(index - 1));
			}
			else if (index < (parent.getNumItems())
					 && parent.getChild(index + 1).getNumItems() >= 2) {

				rightTransfer(node, parent, parent.getChild(index + 1));
			}
			else if (index > 0 && parent.getChild(index - 1) != null) {
				leftFusion(node, parent, parent.getChild(index - 1));
			}
			else {
				rightFusion(node, parent, parent.getChild(index + 1));
			}
		}
	}
	
	/**
	  *  
	  * @param node
	  * @param parent
	  * @param leftSib 
	  */
	private void leftTransfer(TFNode node, TFNode parent, TFNode leftSib) {
		Item rightmostItemOfSib = leftSib.getItem(leftSib.getNumItems() - 1);
		TFNode rightmostChildOfSib = leftSib.getChild(leftSib.getNumItems());
		Item parentItem;
		
		//Move rightmost item of sibling to be the parent item.
		parentItem = parent.replaceItem(leftSib.wcit(), rightmostItemOfSib);
		//Add parent item into the underflowed node.
		node.addItem(0, parentItem);
		
		//Doing a shifting insert, move rightmost child of sibling to this node.
		TFNode temp;
		
		for (int i = (node.getNumItems() + 1); i > 0; --i) {
			temp = node.getChild(i - 1);
			node.setChild(i, temp);
		}
		
		//Set node's left child to rightmostChildSib
		if (rightmostChildOfSib != null) {
			rightmostChildOfSib.setParent(node);
		}
		
		node.setChild(0, rightmostChildOfSib);
		
		//Remove rightmost item and child of leftSib, shifting children over.
		temp = leftSib.getChild(leftSib.getNumItems() - 1);
		leftSib.removeItem(leftSib.getNumItems() - 1);
		leftSib.setChild(leftSib.getNumItems(), temp);
	}
	
	/**
	  * 
	  * @param node
	  * @param parent
	  * @param rightSib 
	  */
	private void rightTransfer(TFNode node, TFNode parent, TFNode rightSib) {
		Item leftmostItemOfSib = rightSib.getItem(0);
        TFNode leftmostChildOfSib = rightSib.getChild(0);
		Item parentItem;
		
		//Replace parent item with leftmostItemOfSib.
		parentItem = parent.replaceItem(node.wcit(), leftmostItemOfSib);
		//Add parent item into underflowed node.
		node.addItem(0, parentItem);
		
		// set node's right child to leftmost child of right sib
        if (leftmostChildOfSib != null) {
			leftmostChildOfSib.setParent(node);
		}
		
		node.setChild(1, leftmostChildOfSib);
		
		// remove leftmost item which also shifts items and children
		rightSib.removeItem(0);
	}
	
	/**
	  * 
	  * @param node
	  * @param parent
	  * @param leftSib 
	  */
	private void leftFusion(TFNode node, TFNode parent, TFNode leftSib) {
		//Pull down (me - 1) parent item (shifting remove)
		//to right spot of left sibling.
		Item parentItem = parent.removeItem(node.wcit() - 1);
		TFNode nodeChild = node.getChild(0);
		
		//add parent item into leftChild at index 1.
		leftSib.addItem(1, parentItem);
		
		//Move child of underflowed node to the right child of leftSib.
		if (nodeChild != null) {
			nodeChild.setParent(leftSib);
		}
		
		leftSib.setChild(2, nodeChild);
		//removeItem() on parent shifts children over so leftSib leaves and node
		//is still a child. Must correct with this.
		parent.setChild(node.wcit(), leftSib);
		
		//Clean up pointers of node.
		node.setParent(null);
		node.setChild(0, null);
		
		//Check for underflow of parent.
		if (parent.getNumItems() == 0) {
			//Makes leftSib the root if root was the underflowed parent.
			if (parent == treeRoot) {
				replaceRoot(parent, leftSib);
			}
			else {
				underflow(parent);
			}
		}
	}
	
	/**
	  * 
	  * @param node
	  * @param parent
	  * @param rightSib 
	  */
	private void rightFusion(TFNode node, TFNode parent, TFNode rightSib) {
		//remove item from parent at node's index
		Item parentItem = parent.removeItem(node.wcit());
		TFNode nodeChild = node.getChild(0);
		
		//insert parent item into rightChild at index 0. This shifts everything.
		rightSib.insertItem(0, parentItem);
		
		//Move child of underflowed node to the left child of rightSib.
		if (nodeChild != null) {
			nodeChild.setParent(rightSib);
		}
		
		rightSib.setChild(0, nodeChild);
		
		//Clean up pointers of node.
		node.setParent(null);
		node.setChild(0, null);
		
		if (parent.getNumItems() == 0){
			//Makes rightSib the root if root was the underflowed parent.
			if (parent == treeRoot) {
				replaceRoot(parent, rightSib);
			}
			else {
				underflow(parent);
			}
		}
	}
	
	/**
	  * 
	  * @param parent
	  * @param sib 
	  */
	private void replaceRoot(TFNode parent, TFNode sib) {
		parent.setChild(sib.wcit(), null);
		sib.setParent(null);
		treeRoot = sib;
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

        myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;

		Random random = new Random(System.nanoTime());
		int[] randomNums = new int[TEST_SIZE];
		
        for (int i = 0; i < TEST_SIZE; i++) {

			randomNums[i] = random.nextInt(10000);
            myTree.insertElement(randomNums[i], randomNums[i]);
            myTree.checkTree();
        }
		/*
		System.out.print("searching");
		for (int i = 0; i < TEST_SIZE; i++) {
			int out = (Integer) myTree.findElement(randomNums[i]);
			myTree.checkTree();
			
            if (out != randomNums[i]) {
                throw new TwoFourTreeException("main: wrong element found");
            }
        }
		System.out.println(" - success!");
		*/
        System.out.print("removing");
        for (int i = 0; i < TEST_SIZE; i++) {

			int out = (Integer) myTree.removeElement(randomNums[i]);

			myTree.checkTree();
			
            if (out != randomNums[i]) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
        }
		
		System.out.println(" - success!");
    }
}