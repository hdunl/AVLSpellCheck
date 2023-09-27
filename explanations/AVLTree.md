# AVLTree Class - In-Depth Explanation

## Introduction

The `AVLTree` class is an implementation of the AVL Tree data structure, known for its self-balancing property. In this in-depth explanation, we'll delve deeper into the inner workings of this class, including rotations, heights, and balancing mechanisms.

## Class Structure

### Node Class

The `Node` class represents a node in the AVL Tree. It encompasses the following attributes:

- `data`: Stores the data (of type `T`) held by the node.
- `left`: Reference to the left child node.
- `right`: Reference to the right child node.
- `height`: Maintains the height of the node.

### Instance Variables

- `root`: A reference to the root node of the AVL Tree.

## Height Calculation

### Height Calculation Method (`height(Node node)`)

- The `height` method calculates the height of a given node. If the node is `null`, it returns 0. Otherwise, it returns the `height` attribute of the node.

- The height of a node is the length of the longest path from that node to a leaf node in its subtree. Accurate height calculation is essential for determining balance factors.

## Balance Factor Calculation

### Balance Factor Calculation Method (`balanceFactor(Node node)`)

- The `balanceFactor` method computes the balance factor for a given node. The balance factor is defined as the difference in heights between the left and right subtrees.

- A positive balance factor indicates that the left subtree is taller, while a negative balance factor suggests that the right subtree is taller. A balance factor of 0 indicates a perfectly balanced subtree.

## Rotations for Balancing
![Rotations Example](https://upload.wikimedia.org/wikipedia/commons/f/fd/AVL_Tree_Example.gif)


### Right Rotation (`rotateRight(Node y)`)

- The right rotation is a fundamental operation for maintaining AVL tree balance. It is performed on a given node `y`. The rotation restructures the tree, and `x` becomes the new root of the subtree.

- Here's how it works:
  - `x` is assigned the left child of `y`.
  - The right child of `x` becomes the left child of `y`.
  - Heights are updated for `x` and `y` to reflect the changes.

### Left Rotation (`rotateLeft(Node x)`)

- The left rotation is a counterpart to the right rotation. It is applied to a node `x`, and `y` becomes the new root of the subtree.

- The steps for left rotation are similar to right rotation, but the roles of `x` and `y` are reversed.

## Balancing the Tree

### Balancing Method (`balance(Node node)`)

![AVL Tree Balance](https://d18l82el6cdm1i.cloudfront.net/uploads/YieLsCqeuV-avlbal.gif)


- The `balance` method ensures that the AVL tree property is maintained. If the balance factor of a node indicates imbalance (greater than 1 or less than -1), it triggers appropriate rotations to restore balance.

- Specifically:
  - If the left subtree is taller (`balanceFactor > 1`), and the left child's right subtree is taller than its left (`balanceFactor(node.left) < 0`), a left rotation is performed on the left child followed by a right rotation on the node.
  - If the right subtree is taller (`balanceFactor < -1`), and the right child's left subtree is taller than its right (`balanceFactor(node.right) > 0`), a right rotation is performed on the right child followed by a left rotation on the node.

- These rotations ensure that the AVL tree remains balanced, and height discrepancies are resolved.

## Insertion Operation

### Insertion Method (`insert(T data)`)

- The `insert` method facilitates the insertion of new elements (`data`) into the AVL Tree while preserving its balance.

- A recursive helper method, `insert(Node node, T data)`, is utilized to traverse the tree and insert elements appropriately. Duplicate elements are not allowed.

- After insertion, the height of nodes is updated, and the `balance` method is called to restore balance if necessary.

## Search Operation

### Search Method (`search(T data)`)

- The `search` method enables the search for a specific element (`data`) within the AVL Tree. It returns `true` if the element is found and `false` otherwise.

- The recursive helper method, `search(Node node, T data)`, navigates through the tree to find the desired element based on comparisons.

## Conclusion

The `AVLTree` class is a sophisticated implementation of an AVL Tree data structure. It manages tree balance through precise height calculations, balance factor assessments, and rotations. The insertion and search operations maintain the tree's integrity while ensuring efficient and balanced data storage.

## Type Constraint

- The class is defined with a type parameter `T` that must be `Comparable`. This constraint ensures that elements stored in the tree can be compared to maintain order.

