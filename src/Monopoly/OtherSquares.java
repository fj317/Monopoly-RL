package Monopoly;

import Player.Player;

public class OtherSquares implements Square {
    private final int pos;
    private final String name;

    public OtherSquares(int pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    public int getPosition() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public boolean isOwnable() {
        return false;
    }

    public boolean isOwned() {
        return false;
    }

    public boolean isMortgaged() {
        return false;
    }

    public int getCost() {
        return 0;
    }

    public void purchase(Player player) {

    }

    public int getRent(int data) {
        return 0;
    }

    public int getMortgageCost() {
        return 0;
    }

    public Player getOwner() {
        return null;
    }

    public int mortgage() {
        return 0;
    }
}
