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
		
		//Finds the node we will be inserting into.
		currNode = search(currNode, key, itemIndex);
		itemIndex = TFNode.ffgtet(currNode, key, treeComp);
		
		if (currNode == null) {
			throw new ElementNotFoundException("Find couldn't find element");
		}
		
		if (treeComp.isEqual(currNode.getItem(itemIndex).key(), key)) {
			Object result = currNode.getItem(itemIndex).element();			
			return result;
		}
		else {
			return null;
		}
    }
	
	private TFNode search(TFNode currNode, Object key, int itemIndex) {
		// This will go if we need to run down the right child because the key
		// we're looking for it greater than all the keys in the node.
		if (!treeComp.isComparable(key)) {
            throw new InvalidIntegerException("Key was not an integer");
        }

		currNode = treeRoot;
		itemIndex = TFNode.ffgtet(currNode, key, treeComp);
		
		currNode = moveDownRight(currNode, key, itemIndex);
		itemIndex = TFNode.ffgtet(currNode, key, treeComp);
		
		while (!treeComp.isEqual(currNode.getItem(itemIndex).key(), key) 
			   && !currNode.isExternal()) {
			//Looks down child of insertIndex to find exact insert
			//node. If ending condition is met, we will insert at currNode.
			currNode = currNode.getChild(itemIndex);
			
			itemIndex = TFNode.ffgtet(currNode, key, treeComp);
			
			currNode = moveDownRight(currNode, key, itemIndex);
			itemIndex = TFNode.ffgtet(currNode, key, treeComp);
		}
		
		return currNode;
	}
	
	private TFNode moveDownRight(TFNode currNode, Object key, int itemIndex) {
		// This will go if we need to run down the right child because the key
		// we're looking for it greater than all the keys in the node.
		while (itemIndex == currNode.getNumItems()) {
			currNode = currNode.getChild(itemIndex);
			itemIndex = TFNode.ffgtet(currNode, key, treeComp);
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
			int insertIndex = TFNode.ffgtet(currNode, key, treeComp);

			//Finds the node we will be inserting into.
			while (!currNode.isExternal()) {
				//Looks down child of insertIndex to find exact insert
				//node. If ending condition is met, we will insert at currNode.
				currNode = currNode.getChild(insertIndex);

				if (currNode == null) {
					currNode = new TFNode();
				}
				
				insertIndex = TFNode.ffgtet(currNode, key, treeComp);
			}
			
			currNode.insertItem(insertIndex, insertItem);		

			overflow(currNode);
        }
        
        ++size;
    }

	private void overflow(TFNode currNode) {
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
				//Set parent of old root to new root.
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
			for(int i = 0; i < 2; i++){
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
			//overflowed the tree again in fixing.
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
		
		//Finds the node we will be inserting into.
		currNode = search(currNode, key, itemIndex);
		itemIndex = TFNode.ffgtet(currNode, key, treeComp);
		
		if (currNode == null) {
			throw new ElementNotFoundException("Remove couldn't find element");
		}
		
		if (treeComp.isEqual(currNode.getItem(itemIndex).key(), key)) {
			Object result = currNode.getItem(itemIndex).element();
			
			deleteElement(currNode, itemIndex);
			
			return result;
		}
		else {
			throw new ElementNotFoundException("Remove couldn't find element");
		}
    }
	
    private void deleteElement(TFNode node, int index){
        // if node is a leaf
        if(node.isExternal()){
            // null item at index
            node.removeItem(index);
			underflow(node);
        }else{ // node is not a leaf
                // swap with inorder successor

            // walk through tree to inorder successor
            TFNode successor = node.getChild(index + 1);
			
			while(successor.getChild(0) != null){
				successor = successor.getChild(0);
			}
				
			node.replaceItem(index, successor.removeItem(0));

				//If successor is now too small, either replaces with its own child,
				//or nulls successor if no child exists.
				/*if (successor.getNumItems() == 0) {
					TFNode successorParent = successor.getParent();

					if (successor.getNumChildren() != 0) {	
						int wcit = successor.wcit();
						successor.setParent(null);
						successor = successor.getChild(0);
						successor.setParent(successorParent);
						successorParent.setChild(wcit, successor);
					}
					else {
						successorParent.setChild(successor.wcit(), null);
						successor.setParent(null);
					}
				}*/
			System.out.println("node");
			underflow(node);
			System.out.println("successor");
			underflow(successor);
        }

        --size;
    }
	
	private void underflow(TFNode node) {
		int index = node.wcit();
		TFNode parent = node.getParent();

		/*if(node.getNumItems() == 0 && node == treeRoot) {
			TFNode nodeChild = treeRoot.getChild(0);
			nodeChild.setParent(null);
			treeRoot = nodeChild;
			node.setChild(0, null);
			rightSib.setParent(null);
		}*/
		
		if (node.getNumItems() == 0 && parent != null) {
			// 7 Dec 2016 - STG - Made right and left siblings so we don't have
			// to keep calling parent.getChild().
			//Can't do this cause of accessing an index outside of the array.
			//TFNode rightSibling = parent.getChild(index+1);
			//TFNode leftSibling = parent.getChild(index-1);
			if (index > 0 && parent.getChild(index-1).getNumItems() >= 2) {
				leftTransfer(node, parent, parent.getChild(index-1));
			}
			else if (index < (parent.getNumItems() - 1)
					 && parent.getChild(index+1).getNumItems() >= 2) {

				rightTransfer(node, parent, parent.getChild(index+1));
			}
			else if (index > 0 && parent.getChild(node.wcit() - 1) != null) {
				leftFusion(node, parent, parent.getChild(index-1));
			}
			else {
				rightFusion(node, parent, parent.getChild(index+1));
			}
		}
		else {
			System.out.println("No underflow");
			//this.printAllElements();
		}
	}
	
	private void leftTransfer(TFNode node, TFNode parent, TFNode leftSib) {
		System.out.println("Left transfer");
		Item rightmostItemOfSib = leftSib.getItem(leftSib.getNumItems() - 1);
		Item parentItem;
		TFNode rightmostChildOfSib = leftSib.getChild(leftSib.getNumItems());
		
		//Move rightmost item of sibling to be the parent item.
		parentItem = parent.replaceItem(leftSib.wcit(), rightmostItemOfSib);
		//Move parent item into the underflowed node.
		node.addItem(0, parentItem);
		//Doing a shifting insert, move rightmost child of sibling to this node.
		TFNode temp;
		
		for (int i = (node.getNumItems() + 1); i > 0; --i) {
			temp = node.getChild(i - 1);
			node.setChild(i, temp);
		}
		
		if (rightmostChildOfSib != null) {
			rightmostChildOfSib.setParent(node);
		}
		
		node.setChild(0, rightmostChildOfSib);
		
		//Remove rightmost item and child of leftSib.
		temp = leftSib.getChild(leftSib.getNumItems() - 1);
		leftSib.removeItem(leftSib.getNumItems() - 1);
		leftSib.setChild(leftSib.getNumItems(), temp);
		
		//this.printAllElements();
	}
	
	private void rightTransfer(TFNode node, TFNode parent, TFNode rightSib) {		
		System.out.println("Right transfer");
		// move items
		Item leftmostItemOfSib = rightSib.getItem(0);
		Item parentItem = parent.getItem(node.wcit());
        TFNode leftmostChildOfSib = rightSib.getChild(0);
		
		parent.replaceItem(node.wcit(), leftmostItemOfSib);
		node.addItem(0, parentItem);
		
		// set node's right child to leftmost child of right sib
        //JAO - rightSib.getChild(0).setParent(node);
        if (leftmostChildOfSib != null) {
			leftmostChildOfSib.setParent(node);
		}
		
		node.setChild(1, leftmostChildOfSib);
		
		// remove leftmost item which also shifts items and children
		rightSib.removeItem(0);
		
		//this.printAllElements();
	}
	
	private void leftFusion(TFNode node, TFNode parent, TFNode leftSib) {
		System.out.println("Left fusion");
		//Pull down (me - 1) parent item (shifting remove)
		//to right spot of left sibling.
		Item parentItem = parent.removeItem(node.wcit() - 1);
		TFNode nodeChild = node.getChild(0);
		
		leftSib.addItem(1, parentItem);
		
		if (nodeChild != null) {
			nodeChild.setParent(leftSib);
		}
		
		//Move child of underflowed node to the right child of the left sibling.
		leftSib.setChild(2, nodeChild);
		//removeItem() on parent shifts children over so leftSib leaves and node
		//is still a child. Must correct with this.
		parent.setChild(node.wcit(), leftSib);
		
		//Clean up pointers of node for garbage collection.
		node.setParent(null);
		node.setChild(0, null);
		
		//this.printAllElements();
		
		//Check for underflow of parent.
		if (parent.getNumItems() == 0) {
			if (parent == treeRoot) {
				System.out.println("Making new root for left fusion");
				parent.setChild(leftSib.wcit(), null);
				leftSib.setParent(null);
				treeRoot = leftSib;
			}
			else {
				System.out.println("parent");
				underflow(parent);
			}
		}
	}
	
	private void rightFusion(TFNode node, TFNode parent, TFNode rightSib) {
		System.out.println("Right fusion");
		//remove item from parent at node's index
		Item parentItem = parent.removeItem(node.wcit());
		//null parent pointer of node
		node.setParent(null);
		//insert parent item into rightChild at index 0. This shifts stuff also.
		rightSib.insertItem(0, parentItem);
		//put child of node into child[0] of right sib
		TFNode nodeChild = node.getChild(0);
		node.setChild(0, null);
		
		if (nodeChild != null) {
			nodeChild.setParent(rightSib);
		}
		
		rightSib.setChild(0, nodeChild);
		
		//this.printAllElements();
		
		if (parent.getNumItems() == 0){
			if (parent == treeRoot) {
				System.out.println("Making new root for right fusion");
				parent.setChild(rightSib.wcit(), null);
				rightSib.setParent(null);
				treeRoot = rightSib;
			}
			else {
				System.out.println("parent");
				underflow(parent);
			}
		}
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
        final int TEST_SIZE = 1000;

		Random random = new Random();
		int[] randomNums = new int[TEST_SIZE];
		
        for (int i = 0; i < TEST_SIZE; i++) {
			randomNums[i] = random.nextInt(10000);
            myTree.insertElement(randomNums[i], randomNums[i]);
			//myTree.insertElement(i, i);
			//System.out.println(i);
            //myTree.printAllElements();
            myTree.checkTree();
        }
		
		System.out.print("searching");
		for (int i = 0; i < TEST_SIZE; i++) {
			int out = (Integer) myTree.findElement(randomNums[i]);
			//int out = (Integer) myTree.findElement(i);
			myTree.checkTree();
			
            if (out != randomNums[i]) {
                throw new TwoFourTreeException("main: wrong element found");
            }
        }
		System.out.println(" - success!");
		
        System.out.print("removing\n");
        for (int i = 0; i < TEST_SIZE; i++) {
			System.out.println("\n" + (TEST_SIZE - i) + "\t" + randomNums[i]);
			//myTree.printAllElements();

			int out = (Integer) myTree.removeElement(randomNums[i]);
			//int out = (Integer) myTree.removeElement(i);

			myTree.checkTree();
			
            if (out != randomNums[i]) {
                throw new TwoFourTreeException("main: wrong element removed");
            }

        }
		System.out.println(" - success!");
    }
}