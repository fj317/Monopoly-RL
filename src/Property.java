public class Property implements Square {
    //costs of rent for all possible property states
    private final int rent;
    private final int oneHouseCost;
    private final int twoHouseCost;
    private final int threeHouseCost;
    private final int fourHouseCost;
    private final int hotelCost;

    private final int value; //cost to purchase property
    private final int houseCost; //cost to purchase one house on property
    private final int pos;
    private final String name;
    private int buildings;  //building status
    private boolean monopoly; //does one player own all properties in set?
    private boolean owned;  //is property owned?
    private boolean mortgaged; //is property mortgaged
    private Player owner;


    public Property(String name, int value, int pos, int rent, int houseCost, int oneHouse, int twoHouse, int threeHouse, int fourHouse, int hotel) {
        this.name = name;
        this.value = value;
        this.pos = pos;
        this.rent = rent;
        this.houseCost = houseCost;
        this.oneHouseCost = oneHouse;
        this.twoHouseCost = twoHouse;
        this.threeHouseCost = threeHouse;
        this.fourHouseCost = fourHouse;
        this.hotelCost = hotel;
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
        return owned;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }

    public int getCost() {
        return value;
    }

    public Player getOwner() {
        return owner;
    }

    public void purchase(Player playerName) {
        this.owned = true;
        this.owner = playerName;
        // update players money
    }

    public void changeOwnership(Player newPlayerName) {
        // when changing hands, update owner and reset buildings to 0 again
        this.owner = newPlayerName;
        this.buildings = 0;
    }

    public int getRent() {
        // if not owned then return 0, meaning property can be bought
        if (!owned) {
            return 0;
        } else {
            switch (this.buildings) {
                case 0:
                    if (monopoly) {
                        return this.rent * 2;
                    } else {
                        return rent;
                    }
                case 1: return oneHouseCost;
                case 2: return twoHouseCost;
                case 3: return threeHouseCost;
                case 4: return fourHouseCost;
                case 5: return hotelCost;
                // if error then return 0
                default: return 0;
            }
        }
    }

    public void buildBuilding(int amount) {
        // if havent got monopoly then cant build, error
        if (!monopoly) {
            throw new IllegalArgumentException("You must have a monopoly before buidling!");
        }
        this.buildings += amount;
        if (this.buildings > 5) {
            // error as cannot have more than 5
            throw new IllegalArgumentException("Cannot build more buidlings!");
        }
        // update player's money
    }

    public int getMortgageCost() {
        if (this.mortgaged) {
            // if morgaged already, return cost to unmortgage property
            this.mortgaged = false;
            // morgage for returning is morgage cost plus 10%
            return (int) Math.round((this.value / 2) * 1.1);

        } else {
            this.mortgaged = true;
            return this.value / 2;
        }

    }

    @Override
    public int getMortgage() {
        return 0;
    }

}
