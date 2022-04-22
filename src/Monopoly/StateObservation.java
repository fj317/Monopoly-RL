package Monopoly;

public class StateObservation {
    private double position;
    private double[] properties;
    private double[] finance;
    private int reward;
    private boolean done;

    public StateObservation(double position, double[] properties, double[] finance, int reward, boolean done) {
        this.position = position;
        this.properties = properties;
        this.finance = finance;
        this.reward = reward;
        this.done = done;
    }

    public double getPosition() {
        return position;
    }

    public double[] getFinance() {
        return finance;
    }

    public double[] getProperties() {
        return properties;
    }

    public int getReward() {
        return reward;
    }

    public boolean isDone() {
        return done;
    }
}
