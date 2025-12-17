public class AVLTree {

    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public AVLNode getRoot() {
        return root;
    }

    // -------------------
    // AVLNode inner class
    // -------------------
    public static class AVLNode {
        private Player player;
        private int height, wins;
        private AVLNode left, right;

        public AVLNode(Player player, int wins) {
            this.player = player;
            this.wins = wins;
            this.height = 1;
        }

        public Player getPlayer() { return player; }
        public AVLNode getLeft() { return left; }
        public AVLNode getRight() { return right; }
        public int getHeight() { return height; }
        public int getWins() { return wins; }

        public void setLeft(AVLNode left) { this.left = left; }
        public void setRight(AVLNode right) { this.right = right; }
        public void setHeight(int height) { this.height = height; }
        public void setWins(int wins) { this.wins = wins; }
    }


    private int height(AVLNode node) {
        return node == null ? 0 : node.getHeight();
    }
    
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }

    private AVLNode rotateRight(AVLNode a){
        AVLNode b = a.getLeft();
        AVLNode c = b.getRight();
        b.setRight(a);
        a.setLeft(c);
        a.setHeight(Math.max(height(a.getLeft()), height(a.getRight())) + 1);
        b.setHeight(Math.max(height(b.getLeft()), height(b.getRight())) + 1);
        return b;
    }

    private AVLNode rotateLeft(AVLNode a){
        AVLNode b = a.getRight();
        AVLNode c = b.getLeft();
        b.setLeft(a);
        a.setRight(c);
        a.setHeight(Math.max(height(a.getLeft()), height(a.getRight())) + 1);
        b.setHeight(Math.max(height(b.getLeft()), height(b.getRight())) + 1);
        return b;
    }

    private AVLNode balance(AVLNode node){
        if(node == null) {
            return null;
        }
        int balance = getBalance(node);
        if(balance > 1){
            if(getBalance(node.getLeft()) < 0){
                node.setLeft(rotateLeft(node.getLeft()));
            }
            return rotateRight(node);
        }
        if(balance < -1){
            if(getBalance(node.getRight()) > 0){
                node.setRight(rotateRight(node.getRight()));
            }
            return rotateLeft(node);
        }
        return node;
    }

    private int compareNodes(AVLNode node1, AVLNode node2) {
        if (node1.getWins() != node2.getWins()) {
            return node2.getWins() - node1.getWins();
        }
        return node1.getPlayer().name().compareTo(node2.getPlayer().name());
    }

    private AVLNode insert(AVLNode current, AVLNode newNode) {
        if (current == null) {
            // Subtree is empty, place new node here
            return newNode;
        }

        // Compare the new node with the current node
        int cmp = compareNodes(newNode, current);

        if (cmp < 0) {
            current.setLeft(insert(current.getLeft(), newNode));
        } else if (cmp > 0) {
            current.setRight(insert(current.getRight(), newNode));
        } else {
            // Node with same key already exists: do nothing
            return current;
        }

        // Update height after insertion
        current.setHeight(1 + Math.max(height(current.getLeft()), height(current.getRight())));

        // Rebalance if needed
        return balance(current);
    }

    public void insert(Player player, int wins) {
        AVLNode newNode = new AVLNode(player, wins);
        root = insert(root, newNode);
    }

    public int remove(Player player) {
        AVLNode targetNode = findNodeByPlayer(root, player);
        if (targetNode != null) {
            int wins = targetNode.getWins();
            root = remove(root, targetNode);
            return wins;
        }
        return Integer.MIN_VALUE; // Player not found
    }

    private AVLNode remove(AVLNode node, AVLNode target) {
    if (node == null) return null;

    int cmp = compareNodes(target, node);

    if (cmp < 0) node.setLeft(remove(node.getLeft(), target));
    else if (cmp > 0) node.setRight(remove(node.getRight(), target));
    else {
        // Node found
        if (node.getLeft() == null || node.getRight() == null) {
            node = (node.getLeft() != null) ? node.getLeft() : node.getRight();
        } else {
            // Two children: get in-order successor
            AVLNode successor = minValueNode(node.getRight());
            node.player = successor.player;
            node.wins = successor.wins;
            node.setRight(remove(node.getRight(), successor));
        }
    }

    if (node == null) return null;

    node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
    return balance(node);
}

    private AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.getLeft() != null) current = current.getLeft();
        return current;
    }

    //Kinda weird, defeats the purpose of an AVL tree ordered by wins but I don't really have a choice if I want to update wins do I?
    private AVLNode findNodeByPlayer(AVLNode node, Player player) {
        if (node == null) return null;
        if (node.getPlayer().equals(player)) return node;

        AVLNode leftResult = findNodeByPlayer(node.getLeft(), player);
        if (leftResult != null) return leftResult;

        return findNodeByPlayer(node.getRight(), player);
    }

    public void printLeaderboard() {
        System.out.println("Leaderboard:");
        printInOrder(root);
    }

    private void printInOrder(AVLNode node) {
        if (node == null) return;

        // Inorder traversal: left, node, right
        printInOrder(node.getLeft());

        // Print current player
        System.out.println(node.getPlayer().name() + " - Wins: " + node.getWins());

        printInOrder(node.getRight());
    }

    
}
