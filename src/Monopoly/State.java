package Monopoly;

import Player.Player;
import Player.*;

import java.util.ArrayList;
import java.util.List;

public class State {
    private Board board;
    private Player playerOne;
    private Player playerTwo;

    private int diceRoll;
    private int value;
    private boolean doubles;
    private final Dice dice;
    private Cards chance;
    private Cards communityChest;

    private List<String> actionList;
    private States currState;
    private ArrayList<Square> dataSquares;
    private int playerTurn;
    private int tickNumber;

    public State() {
        this.board = new Board();
        this.value = 0;
        this.dice = new Dice();
        this.chance = new Cards(Cards.CardType.CHANCE);
        this.communityChest = new Cards(Cards.CardType.COMMUNITY_CHEST);
        this.actionList = new ArrayList<>();
        this.dataSquares = new ArrayList<>();
        this.currState = States.TURN;
        this.playerOne = null;
        this.playerTwo = null;
        this.playerTurn = 1;
    }

    public State(State newState) {
        this.value = newState.value;
        this.dice = newState.dice;
        this.chance = new Cards(newState.chance);
        this.communityChest = new Cards(newState.communityChest);
        this.actionList = newState.actionList;
        this.dataSquares = newState.dataSquares;
        this.currState = newState.currState;
        this.playerOne = new MonteCarloPlayer((MonteCarloPlayer) newState.playerOne);
        if (newState.playerTwo instanceof  RandomPolicyPlayer) {
            this.playerTwo = new RandomPolicyPlayer((RandomPolicyPlayer) newState.playerTwo);
        } else if (newState.playerTwo instanceof BallisPlayer) {
            this.playerTwo = new BallisPlayer((BallisPlayer) newState.playerTwo);
        }
        this.board = new Board(newState.board, this.playerOne, this.playerTwo);
        this.playerTurn = newState.playerTurn;
        this.doubles = newState.doubles;
        this.diceRoll = newState.diceRoll;
        this.tickNumber = newState.tickNumber;
        this.currState = newState.currState;
    }



    public Player getCurrentPlayer() {
        if (playerTurn == 1) {
            return playerOne;
        } else if (playerTurn == 2) {
            return playerTwo;
        }
        return null;
    }

    public Player getOpponent() {
        if (playerTurn == 1) {
            return playerTwo;
        } else if (playerTurn == 2) {
            return playerOne;
        }
        return null;
    }

    public void nextTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        } else if (playerTurn == 2) {
            playerTurn = 1;
        }
    }

    public Cards getChance() {
        return this.chance;
    }

    public Cards getCommunityChest() {
        return this.communityChest;
    }

    public void setChance(Cards newChance) {
        this.chance = newChance;
    }

    public void setCommunityChest(Cards newCommunityChest) {
        this.communityChest = newCommunityChest;
    }

    public Dice getDice() {
        return this.dice;
    }

    public States getCurrState() {
        return this.currState;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Player getPlayerOne() {
        return this.playerOne;
    }

    public Player getPlayerTwo() {
        return this.playerTwo;
    }

    public void setState(States newState) {
        this.currState = newState;
    }

    public Board getBoard() {
        return this.board;
    }

    public int getDiceRoll() {
        return this.diceRoll;
    }

    public void setDiceRoll(int newDiceRoll) {
        this.diceRoll = newDiceRoll;
    }

    public boolean getDoubles() {
        return this.doubles;
    }

    public void setDoubles(boolean value) {
        this.doubles = value;
    }

    public void setValue(int newValue) {
        this.value = newValue;
    }

    public int getValue() {
        return this.value;
    }

    public void addValue(int amount) {
        this.value += amount;
    }

    public void addDataSquares(Square newSquare) {
        this.dataSquares.add(newSquare);
    }

    public List<Square> getDataSquares() {
        return this.dataSquares;
    }

    public void resetDataSquares() {
        this.dataSquares.clear();
    }


    public void setActionList(List<String> newActionList) {
        actionList = newActionList;
    }

    public List<String> getActionList() {
        return this.actionList;
    }

    public int getReward() {
        Player winner = getWinner();
        int reward = 0;
        if (winner == getPlayerOne()) {
            reward = 1;
        } else if (winner == getPlayerTwo()) {
            reward = -1;
        }
        return reward;
    }

    public int getTickNumber() {
        return this.tickNumber;
    }

    public void addOneTick() {
        this.tickNumber ++;
    }

    public int getPlayerTurn() {
        return this.playerTurn;
    }

    public Player getWinner() {
        if (currState == States.END) { // if END state then one player lost, return current player as logic ensures this player is the winner
            return getCurrentPlayer();
        } else if (currState == States.END_DRAW) { // if game ended in DRAW then need to total money and property cost of each player and compare
            int playerOneValue = playerOne.getMoney();
            int playerTwoValue = playerTwo.getMoney();
            for (Square property: playerOne.getProperties()) {
                playerOneValue += property.getCost();
            }
            for (Square property: playerTwo.getProperties()) {
                playerTwoValue += property.getCost();
            }
            if (playerOneValue > playerTwoValue) {
                return playerOne;
            } else if (playerTwoValue > playerOneValue) {
                return playerTwo;
            }
        }
        return null;
    }

    public enum States {
        UNOWNED_LANDED, UNOWNED_DECISION, OWNED_LANDED, BUYING_UNOWNED, BUY_HOUSE_PROPERTY_SELECT, BUY_HOUSE_ACTION, SELL_HOUSE_PROPERTY_SELECT, SELL_HOUSE_ACTION,
        MORTGAGE_PROPERTY_SELECT, MORTGAGE_ACTION, UNMORTGAGE_PROPERTY_SELECT, UNMORTGAGE_ACTION, TRADE, TRADE_PROPERTY_SELECT_1, TRADE_PROPERTY_SELECT_2,
        TRADE_CONFIRM, NONE, END_TURN, JAIL_TURN, HOUSE_DECISION, MORTGAGE_DECISION, JAIL_DECISION, JAIL_OUT_CASH, JAIL_OUT_CARD, END, ROLL, TURN, SQUARE_ACTION, END_DRAW
    }
}
