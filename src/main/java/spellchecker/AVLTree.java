package spellchecker;

/**
 * An AVL Tree data structure implementation.
 * @param <T> the type of elements stored in the tree, must be Comparable.
 */
class AVLTree<T extends Comparable<T>> {

    /**
     * Represents a node in the AVL Tree.
     */
    protected class Node {
        T data;
        Node left;
        Node right;
        int height;

        /**
         * Creates a new node with the specified data.
         * @param data the data to be stored in the node.
         */
        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node root;

    /**
     * Get the root node of the tree.
     * @return the root node.
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Get the height of a node.
     * @param node the node to get the height of.
     * @return the height of the node, or 0 if the node is null.
     */
    private int height(Node node) {
        if (node == null) return 0;
        return node.height;
    }

    /**
     * Calculate the balance factor of a node.
     * @param node the node to calculate the balance factor for.
     * @return the balance factor (difference in heights of left and right subtrees).
     */
    private int balanceFactor(Node node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    /**
     * Perform a right rotation on the given node.
     * @param y the node to rotate.
     * @return the new root of the subtree after rotation.
     */
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    /**
     * Perform a left rotation on the given node.
     * @param x the node to rotate.
     * @return the new root of the subtree after rotation.
     */
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    /**
     * Balance the given node to maintain the AVL property.
     * @param node the node to balance.
     * @return the new root of the subtree after balancing.
     */
    private Node balance(Node node) {
        if (node == null) return null;

        int balanceFactor = balanceFactor(node);

        if (balanceFactor > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (balanceFactor < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Insert a new element into the AVL Tree.
     * @param data the element to insert.
     */
    public void insert(T data) {
        root = insert(root, data);
    }

    /**
     * Recursive helper method to insert an element into the AVL Tree.
     * @param node the current node being considered.
     * @param data the element to insert.
     * @return the new root of the subtree after insertion.
     */
    private Node insert(Node node, T data) {
        if (node == null) return new Node(data);

        int cmp = data.compareTo(node.data);

        if (cmp < 0) {
            node.left = insert(node.left, data);
        } else if (cmp > 0) {
            node.right = insert(node.right, data);
        } else {
            // Duplicate words not allowed
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        return balance(node);
    }

}
