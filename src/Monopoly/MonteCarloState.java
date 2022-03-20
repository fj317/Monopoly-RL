package Monopoly;

import Player.Player;

import java.util.List;

public class MonteCarloState {
    private double properties[][] = new double[11][2];
    private int position;
    private double finance[] = new double[2];
    private double reward;
    private int visitNumber;
    private boolean terminal;
    private List<String> actionList;
    private Player currentPlayer;


    public void setMDPState(double newProperties[][], int position, double newFinance[], double newReward, int newVisitNumber, boolean terminal, List<String> newActionList, Player newCurrentPlayer) {
        this.properties = newProperties;
        this.position = position;
        this.finance = newFinance;
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.terminal = terminal;
        this.actionList = newActionList;
        this.currentPlayer = newCurrentPlayer;
    }

    public MonteCarloState getState() {
        return this;
    }

    public double[][] getProperties() {
        return properties;
    }

    public int getPosition() {
        return position;
    }

    public double[] getFinance() {
        return finance;
    }

    public boolean getTerminal() {
        return terminal;
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

    public List<String> getActionList() {
        return this.actionList;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}
