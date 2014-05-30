import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

public class BTree<Item extends Comparable> {
    private Node root;
	
    public void addItem(Item item) {
        if (root == null) {
            root = new Node(item);
        } else {
            root.addItem(item);
        }
    }

    public ArrayList<Item> getSorted() {
        ArrayList<Item> sorted = new ArrayList<Item>();
        infix(root, sorted);
        return sorted;
    }

    private void infix(Node node, ArrayList<Item> sorted) {
        if (node == null) return;
        if (node.getLeftChild() != null)
            infix(node.getLeftChild(), sorted);
        sorted.add(node.getItem());
        if (node.getRightChild() != null)
            infix(node.getRightChild(), sorted);
    }
	
			
	public void addConcurrent(Item[] item) {
		root = new Node(item[0]);
		final Queue<Item> queue1 = new LinkedBlockingDeque<Item>();
		final Queue<Item> queue2 = new LinkedBlockingDeque<Item>();
		for (int i = 1; i < item.length; i++) {
			if (item[i].compareTo(root.getItem()) > 0) {
				queue1.add(item[i]);
			} else {
				queue2.add(item[i]);
			}
		}
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				long start;
				start = System.currentTimeMillis();
				for (Item i : queue2) {
					if (root.leftChild != null)
						root.leftChild.addItem(i);
					else
						root.leftChild = new Node(i);
				}
				System.out.println("Thread 1 time: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				long start;
				start = System.currentTimeMillis();
				for (Item i : queue1) {
					if (root.rightChild != null)
						root.rightChild.addItem(i);
					else
						root.rightChild = new Node(i);
				}
				System.out.println("Thread 2 time: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
			}
		});
		t1.start();
		t2.start();
	}

    private class Node {
        private Item item;
        private Node leftChild;
        private Node rightChild;

        public Node(Item item) {
            this.item = item;
        }

        public Item getItem() { return item; }
        public Node getLeftChild() { return leftChild; }
        public Node getRightChild() { return rightChild; }

        @Override
        public String toString() {
            return item.toString();
        }

        public void addItem(Item item) {
            if (item.compareTo(this.item) > 0) {
                if (rightChild == null)
                    rightChild = new Node(item);
                else
                    rightChild.addItem(item);
            } else {
                if (leftChild == null)
                    leftChild = new Node(item);
                else
                    leftChild.addItem(item);
            }
        }
    }

    public static void main(String[] args) {
        BTree<Integer> bTree = new BTree<Integer>();
		BTree<Integer> bTree2 = new BTree<Integer>();
        Random r = new Random();
        int size = Integer.parseInt(args[0]);
		Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = r.nextInt(size * 10);
        }

        long start;
        start = System.currentTimeMillis();

		for (int i = 0; i < size; i++) {
			bTree.addItem(array[i]);
		}
        System.out.println((System.currentTimeMillis() - start) / 1000.0 + "s");
		start = System.currentTimeMillis();
		bTree2.addConcurrent(array);
    }
	
	public boolean isSorted(ArrayList<Item> list) {
		boolean sorted = true;        
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i-1).compareTo(list.get(i)) > 0) sorted = false;
		}

		return sorted;
	}
}
