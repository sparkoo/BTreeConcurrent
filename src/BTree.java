import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

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
        Random r = new Random();
        int size = Integer.parseInt(args[0]);
        for (int i = 0; i < size; i++) {
            bTree.addItem(r.nextInt(size * 10));
        }

        System.out.println("sorting started");
        long start;
        start = System.currentTimeMillis();

        bTree.getSorted();

        long now = System.currentTimeMillis();
        System.out.println((now - start) / 1000.0 + "s");
    }
}
