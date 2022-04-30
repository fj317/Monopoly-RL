package Tree;

import Monopoly.State;

import java.util.*;

public class Node {
    private State data;
    private Node parent;
    private ArrayList<Node> children;
    private int reward;
    private int visitNumber;
    private int incomingAction;

    public Node(State data, Node parent, ArrayList<Node> children, int newReward, int newVisitNumber, int incomingAction) {
        this.data = data;
        this.parent = parent;
        this.children = Objects.requireNonNullElseGet(children, ArrayList::new);
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.incomingAction = incomingAction;
    }

    public Node(Node parent, ArrayList<Node> children, int newReward, int newVisitNumber, int incomingAction) {
        this.parent = parent;
        this.children = Objects.requireNonNullElseGet(children, ArrayList::new);
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.incomingAction = incomingAction;
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

    public int getReward() {
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

    public int getIncomingAction() {
        return this.incomingAction;
    }

    public void setIncomingAction(int action) {
        this.incomingAction = action;
    }

}
