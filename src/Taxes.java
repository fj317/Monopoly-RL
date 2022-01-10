public class Taxes implements Square {
    private final String name;
    private final int pos;
    private final int taxAmount;

    public Taxes(String name, int pos, int taxAmount) {
        this.name = name;
        this.pos = pos;
        this.taxAmount = taxAmount;
    }

    public int getPosition() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public int getTaxAmount() {
        return taxAmount;
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
