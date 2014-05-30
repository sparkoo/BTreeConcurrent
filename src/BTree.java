import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

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

    private void infix(Node node) {
        if (node == null) return;
        if (node.getLeftChild() != null)
            infix(node.getLeftChild());
        System.out.print(node.getItem() + " ");
        if (node.getRightChild() != null)
            infix(node.getRightChild());
    }

    private void prefix(Node node) {
        if (node == null) return;
        System.out.print(node.getItem() + " ");
        if (node.getLeftChild() != null)
            prefix(node.getLeftChild());
        if (node.getRightChild() != null)
            prefix(node.getRightChild());
    }

    private void infixP(Node node, ArrayList<Node> boundaries, ArrayList<Node> threadRoots) {
        if (node.getLeftChild() != null && node.getRightChild() != null) {
            infixP(node.getLeftChild(), boundaries, threadRoots);
            boundaries.add(node);
            infixP(node.getRightChild(), boundaries, threadRoots);
        } else {
            threadRoots.add(node);
        }
    }
			
	public void addConcurrent(Item[] item, int threads) {
        final long start;
        start = System.currentTimeMillis();
        int count = threads * 2 - 1;
        sortN(item, count);
        root = new Node(item[count / 2]);
        prepareTree(item, root, count / 2, count / 2);
        /*for (Item i : item) {
            System.out.print(i + " ");
        }
        System.out.println();*/
        ArrayList<Node> boundaries = new ArrayList<Node>();
        ArrayList<Node> threadRoots = new ArrayList<Node>();
        infixP(root, boundaries, threadRoots);
        /*
        for (Node n : boundaries) {
            System.out.print(n.getItem() + "; ");
        }

        System.out.println();
        prefix(root);
        System.out.println();

        for (Node n : threadRoots) {
            System.out.print(n.getItem() + " > ");
        }
        System.out.println();
*/
        ArrayList<Queue> queues = new ArrayList<Queue>();
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < threads; i++) {
            Queue<Item> q = new LinkedBlockingDeque<Item>();
            queues.add(q);
            Thread t = new BTreeAddingThread(threadRoots.get(i), q);
            threadList.add(t);
            t.start();
        }

        for (int i = count; i < item.length; i++) {
            int bIndex = boundaries.size();
            for (int boundaryIndex = 0; boundaryIndex < boundaries.size(); boundaryIndex++) {
                if (item[i].compareTo(boundaries.get(boundaryIndex).getItem()) < 0) {
                    bIndex = boundaryIndex;
                    break;
                }
            }
            queues.get(bIndex).add(item[i]);
        }

        while (true) {
            boolean isAllEmpty = true;
            for (Queue q : queues) {
                if (!q.isEmpty()) {
                    isAllEmpty = false;
                    break;
                }
            }
            if (isAllEmpty) {
                break;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Thread t : threadList)
            t.interrupt();
        System.out.println("concurrent time: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
	}

    private void prepareTree(Item[] item, Node parent, int count, int index) {
        try {
            if (count > 0) {
                int c = count / 2 + 1;
                parent.addItem(item[index - c]);
                parent.addItem(item[index + c]);
                prepareTree(item, parent.leftChild, count / 2, index - c);
                prepareTree(item, parent.rightChild, count / 2, index + c);
            }
        } catch (Exception e) {
            System.out.println("count: " + count + "; index: " + index + "; item: " + parent.getItem());
        }
    }

    private void sortN(Item[] item, int count) {
        Item p;
        int min;
        for (int i = 0; i < count; i++) {
            min = i;
            for (int j = i + 1; j < count; j++)
                if (item[j].compareTo(item[min]) < 0)
                    min = j;
            p = item[i];
            item[i] = item[min];
            item[min] = p;
        }
    }

    private class BTreeAddingThread extends Thread {
        private Node root;
        private Queue<Item> queue;

        public BTreeAddingThread(Node root, Queue<Item> queue) {
            this.root = root;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                if (queue.isEmpty()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        return;
                    }
                } else {
                    root.addItem(queue.remove());
                }
            }
        }
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

        bTree2.addConcurrent(array, 16);
        System.out.println(isSorted(bTree2.getSorted()) + " ==> " + bTree2.getSorted().size());

        long start;
        start = System.currentTimeMillis();

		for (int i = 0; i < size; i++) {
			bTree.addItem(array[i]);
		}
        System.out.println("normal time: " + (System.currentTimeMillis() - start) / 1000.0 + "s");
    }
	
	public static boolean isSorted(ArrayList<Integer> list) {
		boolean sorted = true;        
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i-1).compareTo(list.get(i)) > 0) sorted = false;
		}

		return sorted;
	}
}
