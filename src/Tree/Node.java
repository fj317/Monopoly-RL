package Tree;

import Monopoly.MonteCarloState;

import java.util.List;
import java.util.Random;

public class Node<T> {
    private MonteCarloState data;
    private Node<T> parent;
    private List<Node<T>> children;

    public Node(MonteCarloState data, Node<T> parent, List<Node<T>> children) {
        this.data = data;
        this.parent = parent;
        this.children = children;
    }

    public void addNode(Node<T> nodeToAdd, Node<T> parent) {
        parent.children.add(nodeToAdd);
        nodeToAdd.parent = parent;
    }

    public void removeNode(Node<T> nodeToRemove, Node<T> parent) {
        parent.children.remove(nodeToRemove);
    }

    public boolean isLeaf(Node<T> node) {
        return children.isEmpty();
    }

    public Node<T> backpropagate(Node<T> node) {
        return this.parent;
    }

    public Node<T> expand(Node<T> node) {
        Node<T> currentNode = node;
        while (!isTerminal(currentNode)) {
            // using heurstic/randomly expand nodes
            Random rand = new Random();
            int actionIndex = rand.nextInt(node.children.size());
            // follow action to next node
            currentNode = currentNode.children.get(actionIndex);
        }
        return currentNode;
    }

    public boolean isTerminal(Node<T> node) {
        return node.data.getTerminal();
    }

}
