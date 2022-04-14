package Tree;

import Monopoly.State;

import java.util.List;
import java.util.Random;

public class Node {
    private State data;
    private Node parent;
    private List<Node> children;
    private int reward;
    private int visitNumber;

    public Node(State data, Node parent, List<Node> children, int newReward, int newVisitNumber) {
        this.data = data;
        this.parent = parent;
        this.children = children;
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
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

    public State getData() {
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
        return this.data.getCurrState() == State.States.END;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setData(State newData) {
        this.data = newData;
    }

    public double getReward() {
        return reward;
    }

    public int getVisitNumber() {
        return visitNumber;
    }

    public void addReward(double newReward) {
        this.reward += newReward;
    }

    public void addVisitNumber(int newVisitNumber) {
        this.visitNumber += newVisitNumber;
    }

}
