package Monopoly;

import Player.Player;

public class CardSquare implements Square {
    private final String name;
    private final int pos;
    private final Cards.CardType type;

    public CardSquare(String name, int pos, Cards.CardType type) {
        this.name = name;
        this.pos = pos;
        this.type = type;
    }

    public Cards drawCard() {
        return new Cards(this.type);
    }

    public int getPosition() {
        return pos;
    }

    public Cards.CardType getType() {
        return this.type;
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
