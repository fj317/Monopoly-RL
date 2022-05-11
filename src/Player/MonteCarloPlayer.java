package Player;

import Monopoly.*;
import Tree.Node;
import Tree.SelectedNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MonteCarloPlayer implements Player {
    private final ArrayList<Square> properties;
    private final String playerName;
    private int money;
    private int position;
    private boolean inJail;
    private int jailTurn;
    private int numberGetOutOfJailCards;
    private boolean chanceGetOutOfJailCardHeld;

    public MonteCarloPlayer() {
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = "RL";
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
    }

    public MonteCarloPlayer(MonteCarloPlayer newPlayer) {
        this.money = newPlayer.money;
        this.properties = new ArrayList<Square>(newPlayer.properties);
        this.position = newPlayer.position;
        this.playerName = newPlayer.playerName;
        this.inJail = newPlayer.inJail;
        this.jailTurn = newPlayer.jailTurn;
        this.numberGetOutOfJailCards = newPlayer.getNumberGetOutOfJailCards();
        this.chanceGetOutOfJailCardHeld = newPlayer.chanceGetOutOfJailCardHeld;
    }

    public String getName() {
        return playerName;
    }

    public int getMoney() {
        return money;
    }

    public void removeMoney(int amount) {
        this.money -= amount;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public void sendToJail() {
        inJail = true;
        position = 10;
        jailTurn = 0;
    }

    public void leaveJail() {
        inJail = false;
    }

    public Boolean inJail() {
        return this.inJail;
    }

    public int getNumberGetOutOfJailCards() {
        return numberGetOutOfJailCards;
    }

    public Cards.CardType useGetOutOfJailCard() {
        if (numberGetOutOfJailCards < 1) {
            throw new RuntimeException("You do not have any get out of jail cards!");
        }
        numberGetOutOfJailCards--;
        if (chanceGetOutOfJailCardHeld) {
            chanceGetOutOfJailCardHeld = false;
            return Cards.CardType.CHANCE;
        } else {
            return  Cards.CardType.COMMUNITY_CHEST;
        }
    }

    public boolean stayInJail() {
        jailTurn++;
        return jailTurn != 3;
    }

    public void addGetOutOfJailCard(Cards.CardType type) {
        numberGetOutOfJailCards++;
        if (type == Cards.CardType.CHANCE) {
            chanceGetOutOfJailCardHeld = true;
        }
    }

    public void move(int numberOfSpaces) {
        position += numberOfSpaces;
        final int boardSize = 40;
        // if pass go, add 200 and make sure correct position
        if (position >= boardSize) {
            position = position % boardSize;
            addMoney(200);
        }
        if (position < 0) {
            position += 40;
        }
    }

    public void moveTo(int pos) {
        // check if pass GO on way
        if (pos < position && pos != 30) {
            addMoney(200);
        }
        position = pos;
    }

    public int getPosition() {
        return position;
    }

    public void addProperty(Square square) {
        properties.add(square);
    }

    public void sellProperty(Square square) {
        properties.remove(square);
    }

    public ArrayList<Square> getProperties() {
        return properties;
    }

    public ArrayList<Property> getBuildableProperties() {
        ArrayList<Property> buildableProperties = new ArrayList<>();
        for (Square i : getProperties()) {
            if (i instanceof Property) {
                buildableProperties.add((Property) i);
            }
        }
        return buildableProperties;
    }

    public int input(State state) {
        // perform tree search
        State UCTSearchState = new State(state);
        return MCTSearch(UCTSearchState);
    }

    public int MCTSearch(State state) {
        // create root node with state
        Node newRoot = new Node(state, null, null, 0, 0, 0, false, 1);
        int rollouts = 50000;
        int rolls = 0;
        while (rolls < rollouts) {
            SelectedNode selectedNode = treePolicy(newRoot);
            int reward = defaultPolicy(selectedNode.getSelectedNodeState());
            backpropogate(selectedNode.getSelectedNode(), reward);
            rolls++;
        }
        return mostRobustChild(newRoot);
    }

    public SelectedNode treePolicy(Node node) {
        // get root node state
        State currentState = new State(node.getData());
        // while not terminal
        while (!node.isTerminal()) {
            // if node isn't fully expanded
            if (node.getChildren().size() != currentState.getActionList().size()) {
                return expand(node, currentState);
            } else {
                // choose best node
                node = bestChild(node, 0.8);
                // update state using action from node
                SimplifiedMonopoly.stepNoOutput(currentState, node.getIncomingAction());
                node.setPlayerTurn(currentState.getPlayerTurn());
            }
        }
        return new SelectedNode(node, currentState);
    }

    // given a node, update the node with the children nodes
    public SelectedNode expand(Node node, State currentNodeState) {
        boolean terminal;
        // loop through each action and add node
        for (int i = 1; i <= currentNodeState.getActionList().size(); i++) {
            terminal = false;
            if (!node.checkNodeExists(i)) {
                // play one tick with action to find terminal and player turn
                State tempState = new State(currentNodeState);
                SimplifiedMonopoly.stepNoOutput(tempState, i);
                if (SimplifiedMonopoly.gameFinished(tempState)) {
                    terminal = true;
                }
                Node newNode = new Node(null, null, 0, 0, i, terminal, tempState.getPlayerTurn());
                // addNode method deals with parent and children fields
                node.addNode(newNode, node);
            }
        }
        // return random child with Node and State
        Random rand = new Random();
        int index = rand.nextInt(currentNodeState.getActionList().size());
        Node chosenNode = node.getChildren().get(index);
        SimplifiedMonopoly.stepNoOutput(currentNodeState, chosenNode.getIncomingAction());
        return new SelectedNode(chosenNode, currentNodeState);
    }

    public int defaultPolicy(State state) {
        // while state isnt terminal
        while (!SimplifiedMonopoly.gameFinished(state)) {
            // generate random action and perform in environment
            int action = randomAction(state);
            SimplifiedMonopoly.stepNoOutput(state, action);
        }
        // return state's reward
        return state.getReward();
    }

    public int randomAction(State state) {
        Random rand = new Random();
        return rand.nextInt(state.getActionList().size()) + 1;
    }

    public Node bestChild(Node node, double exploration) {
        List<Node> children = node.getChildren();
        List<Double> uctValues = new ArrayList<Double>();
        // for each child node
        for (Node child: children) {
            // get reward & visit number
            int reward = child.getReward();
            int visitNumber = child.getVisitNumber();
            // calculate uct of each child
            double uct = (reward / (double)visitNumber) + exploration * Math.sqrt(2*Math.log(node.getVisitNumber()) / visitNumber);
            // add uct to list
            uctValues.add(uct);
        }
        // get child with highest uct
        return children.get(getLargestValueIndex(uctValues));
    }

    private int mostRobustChild(Node root) {
        // get children
        List<Node> children = root.getChildren();
        int largestCount = 0;
        int largestCountAction = 0;
        // go through each child and compare visitCounts
        for (Node child: children) {
            if (child.getVisitNumber() > largestCount) {
                largestCount = child.getVisitNumber();
                largestCountAction = child.getIncomingAction();
            }
        }
        return largestCountAction;
    }

    // given a list, return the highest value's index
    // TO DO: DEAL WITH EQUAL UCT VALUES BY RANDOM
    private int getLargestValueIndex(List<Double> list) {
        int highestIndex = 0;
        double largestValue = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > largestValue) {
                largestValue = list.get(i);
                highestIndex = i;
            }
        }
        return highestIndex;
    }

    // given a node, backpropogate the reward to the root node
    public void backpropogate(Node node, int reward) {
        Node currentNode = node;
        // while node isn't null
        while (currentNode != null) {
            // increment reward and visit number
            currentNode.addReward(reward);
            currentNode.addVisitNumber(1);
            if (currentNode.getParent() != null) {
                // if parent node is different player to current node (i.e. opponent node) then flip reward to negative
                if (currentNode.getPlayerTurn() != currentNode.getParent().getPlayerTurn()) {
                    reward = -reward;
                }
            }
            // get parent node
            currentNode = currentNode.getParent();
        }

    }
}

// SELECTION, EXPANSION, SIMULATION, BACKPROPOGATION
// Selection: select a node that is unexplored based on reward and visit count
// Expansion: successor states of the node are added to the tree
// Simulation: starting at selected node, perform simulations of the game, performing random actions until terminal state
// Backpropogation: the terminal node's value is propogated back along to the root node, the reward and visit counts of each node are updated
