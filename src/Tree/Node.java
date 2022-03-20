package Tree;

import Monopoly.MonteCarloState;

import java.util.List;
import java.util.Random;

public class Node {
    private MonteCarloState data;
    private Node parent;
    private List<Node> children;

    public Node(MonteCarloState data, Node parent, List<Node> children) {
        this.data = data;
        this.parent = parent;
        this.children = children;
    }

    public void addNode(Node nodeToAdd, Node parent) {
        parent.children.add(nodeToAdd);
        nodeToAdd.parent = parent;
    }

    public void removeNode(Node nodeToRemove, Node parent) {
        parent.children.remove(nodeToRemove);
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    public Node getParent() {
        return this.parent;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public MonteCarloState getData() {
        return this.data;
    }

    public Node expand() {
        Node currentNode = this;
        while (!this.isTerminal()) {
            // using heurstic/randomly expand nodes
            Random rand = new Random();
            int actionIndex = rand.nextInt(this.children.size());
            // follow action to next node
            currentNode = currentNode.children.get(actionIndex);
        }
        return currentNode;
    }

    public boolean isTerminal() {
        return this.data.getTerminal();
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setData(MonteCarloState newData) {
        this.data = newData;
    }


}
