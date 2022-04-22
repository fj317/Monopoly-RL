package Monopoly;

import Player.Player;

import java.util.ArrayList;
import java.util.List;

public class State {
    public Board board;
    public Player playerOne;
    public Player playerTwo;

    public int diceRoll;
    public int value;
    public boolean doubles;
    private final Dice dice;
    private Cards chance;
    private Cards communityChest;

    public List<String> actionList;
    public States currState;
    public ArrayList<Square> dataSquares;
    private int playerTurn;

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
        int reward;
        if (currState == State.States.END) {
            if (getCurrentPlayer() == getPlayerOne()) {
                reward = 1;
            } else {
                reward = -1;
            }
        } else {
            reward = 0;
        }
        return reward;
    }


    public enum States {
        UNOWNED_LANDED, OWNED_LANDED, BUYING_UNOWNED, BUY_HOUSE_PROPERTY_SELECT, BUY_HOUSE_ACTION, SELL_HOUSE_PROPERTY_SELECT, SELL_HOUSE_ACTION,
        MORTGAGE_PROPERTY_SELECT, MORTGAGE_ACTION, UNMORTGAGE_PROPERTY_SELECT, UNMORTGAGE_ACTION, TRADE, TRADE_PROPERTY_SELECT_1, TRADE_PROPERTY_SELECT_2,
        TRADE_CONFIRM, NONE, END_TURN, JAIL_TURN, HOUSE_DECISION, MORTGAGE_DECISION, JAIL_OUT_CASH, JAIL_OUT_CARD, END, ROLL, TURN, SQUARE_ACTION
    }
}
