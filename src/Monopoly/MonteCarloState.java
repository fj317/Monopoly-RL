package Monopoly;

public class MonteCarloState {
    private double properties[][] = new double[11][2];
    private int position;
    private double finance[] = new double[2];
    private double reward;
    private int visitNumber;
    private boolean terminal;


    public void setMDPState(double newProperties[][], int position, double newFinance[], double newReward, int newVisitNumber, boolean terminal) {
        this.properties = newProperties;
        this.position = position;
        this.finance = newFinance;
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
        this.terminal = terminal;
    }

    public void updateState(double newReward, int newVisitNumber) {
        this.reward = newReward;
        this.visitNumber = newVisitNumber;
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
}
