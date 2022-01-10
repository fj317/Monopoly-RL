public class Jail implements Square {
    private final int pos;
    private final String name;
    private final JailType type;

    public Jail(int pos, String name, JailType type) {
        this.pos = pos;
        this.name = name;
        this.type = type;
    }

    public enum JailType {
        VISITING, IN_JAIL1, IN_JAIL2, IN_JAIL3, GOTO_JAIL
    }

    public int getPosition() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public JailType getType() {
        return type;
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
