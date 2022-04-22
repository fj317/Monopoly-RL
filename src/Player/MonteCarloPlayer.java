package Player;

import Monopoly.*;
import Tree.Node;

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
    private boolean sim;
    private int action;

    public MonteCarloPlayer(State state) {
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = "RL";
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
        sim = false;
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
        moveTo(10);
        jailTurn = 0;
    }

    public void leaveJail() {
        inJail = false;
        moveTo(10);
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
        if (pos < position) {
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
        if (sim) {
            return randomAction(state);
        }
        // perform tree search
        return UCTSearch(state);
    }

    public int UCTSearch(State state) {
        // create root node with state
        Node newRoot = new Node(state, null, null, 0, 0, 0);
        int rollouts = 500;
        int rolls = 0;
        while (rolls < rollouts) {
            Node selectedNode = treePolicy(newRoot);
            int reward = defaultPolicy(selectedNode.getData());
            backpropogate(selectedNode, reward);
            rolls++;
        }
        return bestChild(newRoot, 0.8).getIncomingAction();
    }

    public Node treePolicy(Node node) {
        while (!node.isTerminal()) {
            if (node.getChildren() == null) {
                return expand(node);
            } else {
                node = bestChild(node, 0.8);
            }
        }
        return node;
    }

    public int defaultPolicy(State state) {
        // while state isnt terminal
        while (state.getCurrState() != State.States.END) {
            // choose random action
            int action = randomAction(state);
            // get new state
            state = getNextStateNode(action, state).getData();
        }
        // return state's reward
        return state.getReward();
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

    // needed for bestChild
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

    // given a node, update the node with the children nodes
    public Node expand(Node node) {
        // get action list for current state
        List<String> actionList = node.getData().getActionList();
        // loop through each action and add node
        for (int i = 1; i <= actionList.size(); i++) {
            Node newNode = getNextStateNode(i, node.getData());
            node.addNode(newNode, node);
        }
        // return random child
        return randomChild(node);
    }

    public Node randomChild(Node node) {
        Random rand = new Random();
        int index =  rand.nextInt(node.getChildren().size());
        return node.getChildren().get(index);
    }

    // given a node, backpropogate the reward to the root node
    public void backpropogate(Node node, int reward) {
        Node currentNode = node;
        while (!currentNode.isRoot()) {
            currentNode.addReward(reward);
            currentNode.addVisitNumber(1);
            // if parent node is different player to current node (i.e. opponent node) then flip reward to negative
            if (currentNode.getParent().getData().getCurrentPlayer() != currentNode.getData().getCurrentPlayer()) {
                reward = -reward;
            }
            currentNode = currentNode.getParent();
        }

    }

    public Node getNextStateNode(int actionToPerform, State state) {
        // create new instance of Monopoly
        Monopoly simMonopoly = new Monopoly(state);
        sim = true;
        // perform action (i.e. tick() once)
        simMonopoly.tick();
        sim = false;
        // get state
        State newState = simMonopoly.getState();
        // get state reward
        int reward = newState.getReward();
        // put state into node format
        return new Node(newState, null, null, reward, 0, actionToPerform);
    }


    public int randomAction(State state) {
        Random rand = new Random();
        return rand.nextInt(state.actionList.size()) + 1;
    }
}

class simulateReturn<T> {
    public final Node leafNode;
    public final double reward;

    public simulateReturn(Node leafNode, double reward) {
        this.leafNode = leafNode;
        this.reward = reward;
    }
}

// SELECTION, EXPANSION, SIMULATION, BACKPROPOGATION
// Selection: select a node that is unexplored based on reward and visit count
// Expansion: successor states of the node are added to the tree
// Simulation: starting at selected node, perform simulations of the game, performing random actions until terminal state
// Backpropogation: the terminal node's value is propogated back along to the root node, the reward and visit counts of each node are updated
