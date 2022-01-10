public class Railroad implements Square {
    private final int value = 200; //cost to purchase property
    private final int pos;
    private final String name;
    private boolean owned;  //is property owned?
    private boolean mortgaged; //is property mortgaged
    private Player owner;
    private final int group; // what group the property is in
    private int numberOwned;

    public Railroad(int pos, String name, int group) {
        this.pos = pos;
        this.name = name;
        this.group = group;
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
        this.owned = true;
        this.owner = player;
    }

    public int getRent(int data) {
        switch (this.numberOwned) {
            case 1: return 25;
            case 2: return 50;
            case 3: return 100;
            case 4: return 200;
        }
    }

    public int getMortgageCost() {
        return value / 2;
    }

    public Player getOwner() {
        return owner;
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
