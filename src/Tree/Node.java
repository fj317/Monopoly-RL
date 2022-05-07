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
    private int playerTurn;
    private boolean terminal;

    public Node(State data, Node parent, ArrayList<Node> children, int newReward, int newVisitNumber, int incomingAction, boolean terminal, int playerTurn) {
        this.data = data;
        this.parent = parent;
        this.children = Objects.requireNonNullElseGet(children, ArrayList::new);
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.incomingAction = incomingAction;
        this.terminal = terminal;
        this.playerTurn = playerTurn;
    }

    public Node(Node parent, ArrayList<Node> children, int newReward, int newVisitNumber, int incomingAction, boolean terminal, int playerTurn) {
        this.parent = parent;
        this.children = Objects.requireNonNullElseGet(children, ArrayList::new);
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.incomingAction = incomingAction;
        this.terminal = terminal;
        this.playerTurn = playerTurn;
    }

    public Node(Node node) {
        this.data = node.data;
        this.parent = node.parent;
        this.children = node.children;
        this.reward = node.reward;
        this.visitNumber = node.visitNumber;
        this.incomingAction = node.incomingAction;
        this.terminal = node.terminal;
        this.playerTurn = node.playerTurn;
    }

    // check whether for a node a child node with the action exists already
    public boolean checkNodeExists(int actionNumber) {
        // for each node
        for (Node currentNode : this.children) {
            if (currentNode.incomingAction == actionNumber) {
                return true;
            }
        }
        return false;
    }

    public void addNode(Node nodeToAdd, Node parent) {
        parent.children.add(nodeToAdd);
        nodeToAdd.parent = parent;
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

    public boolean isTerminal() {
        return terminal;
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

    public int getPlayerTurn() {
        return this.playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }
}
