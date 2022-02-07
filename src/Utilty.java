public class Utilty implements Square {
    private final int pos;
    private final String name;
    private final int value = 150; // cost to buy
    private boolean mortgaged; //is property mortgaged
    private Player owner;
    private boolean owned;  //is property owned?

    public Utilty(int pos, String name, int group) {
        this.pos = pos;
        this.name = name;
    }


    public int getPosition() {
        return this.pos;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOwnable() {
        return true;
    }

    public boolean isOwned() {
        return this.owned;
    }

    public boolean isMortgaged() {
        return this.mortgaged;
    }

    public int getCost() {
        return this.value;
    }

    public void purchase(Player player) {
        owned = true;
        owner = player;
    }

    public int getRent(int roll) {
        int numberOwned = 0;
        // go through each property and check if utility, if it is add to numberOwned
        for (Square square: owner.getProperties())
            if (square instanceof Utilty) {
                numberOwned++;
            }
        switch (numberOwned) {
            case 1: return roll * 4;
            case 2: return roll * 10;
            default: return 0;
        }
    }

    public int getMortgageCost() {
        return this.value / 2;
    }

    public Player getOwner() {
        return this.owner;
    }

    public int mortgage() {
        if (this.mortgaged) {
            // if morgaged already, return cost to unmortgage property
            this.mortgaged = false;
            // morgage for returning is morgage cost plus 10%
            return (int) Math.round((this.value / 2.0) * 1.1);
        } else {
            this.mortgaged = true;
            return this.value / 2;
        }
    }
}
