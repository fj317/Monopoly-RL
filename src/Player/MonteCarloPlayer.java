
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
    private Node currentState;

    public MonteCarloPlayer() {
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = "Random Policy Agent";
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
        sim = false;
        currentState = new Node(null, null, null, 0, 0);
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
        // perform tree search
        // if simulation then follow action already defined
        if (sim) {
            return action;
        }
        // otherwise perform tree search
        int rollout = 500;
        int rolls = 0;
        while(rolls < rollout) {
            Node selectedNode = select(currentState);
            expand(selectedNode, selectedNode.getData());
            simulateReturn simReturn = simulate(selectedNode);
            backpropogate(simReturn.leafNode, simReturn.reward);
            rolls++;
        }
        // choose best action from rollout
        return bestChild(currentState);
    }

    // given a node, find an unexplored descendent of the root node
    public Node select(Node root) {
        // use uct to select a node
        Node currentNode = root;
        while (!currentNode.isLeaf()) { // while not leaf node (and therefore has children)
            // select next node based on uct
            List<Node> children = currentNode.getChildren();
            List<Double> uctValues = new ArrayList<Double>();
            // for each child calculate uct value
            for (Node child : children) {
                double reward = child.getReward();
                int visitNumber = child.getVisitNumber();
                int exploreWeight = 1;
                double uct = reward / visitNumber + exploreWeight * Math.sqrt(Math.log(visitNumber) / visitNumber);
                uctValues.add(uct);
            }
            // find largest uct value node
            currentNode = children.get(getLargestValueIndex(uctValues));
        }
        // if root is leaf, then return the current node
        return currentNode;
    }

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
    public void expand(Node node, State currentState) {
        // get action list
        // given the current state, get possible moves
        List<String> actionList = node.getData().getActionList();
        // loop through each action and add node
        int actionInt;
        for (String action : actionList) {
            try {
                actionInt = Integer.parseInt(action);
            }
            catch (NumberFormatException e) { // if error then set to 0
                actionInt = 0;
            }
            Node newNode = getNextState(actionInt, currentState);
            node.addNode(newNode, node);
        }
    }

    // returns a reward & leaf node for a random simulation until terminal
    public simulateReturn simulate(Node node) {
        Node currentNode = node;
        State currentState = node.getData();
        while (!currentNode.isTerminal()) {
            // get random action for current state
            int action = randomAction(currentState);
            // get new node after performing that action
            Node newNode = getNextState(action, currentState);
            // add node to tree
            node.addNode(newNode, currentNode);
            // move to the child node
            currentNode = newNode;
        }
        return new simulateReturn(currentNode, currentNode.getReward());
    }

    // given a node, backpropogate the reward to the root node
    public void backpropogate(Node node, double reward) {
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

    public Node getNextState(int actionToPerform, State state) {
        // create new instance of Monopoly
        Monopoly simMonopoly = new Monopoly(state);
        sim = true;
        action = actionToPerform;
        // perform action (i.e. tick() once)
        simMonopoly.tick();
        sim = false;
        // get state
        State newState = simMonopoly.getState();
        // put state into node format
        return new Node(newState, null, null, 0, 0);
    }


    public int randomAction(State state) {
        Random rand = new Random();
        return rand.nextInt(state.actionList.size()) + 1;
    }

    public int bestChild(Node root) {
        // from root, choose best child
        double bestChildReward = root.getChildren().get(0).getReward();
        int bestChild = 0;
        for (int i = 0; i < root.getChildren().size(); i++) {
            Node child = root.getChildren().get(i);
            if (child.getReward() > bestChildReward) {
                bestChild = i;
                bestChildReward = child.getReward();
            }
        }

        // now we know bestChild, we can set the bestChild node to be the root
        currentState = root.getChildren().get(bestChild);
        return bestChild;
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
