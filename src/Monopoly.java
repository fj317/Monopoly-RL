import java.util.NoSuchElementException;

public class Monopoly {
    private final Dice dice;
    private final Cards chance;
    private final Cards communityChest;
    private State state;

    private Monopoly() {
        this.dice = new Dice();
        this.chance = new Cards(Cards.CardType.CHANCE);
        this.communityChest = new Cards(Cards.CardType.COMMUNITY_CHEST);
        this.state = new State();
        // get players

    }

    private void getPlayers() {
        int totalPlayers = 2;
        // add human or AI players etc
    }

    public static void main(String[] args) {
        Monopoly monopoly = new Monopoly();
        monopoly.run();
    }

    private void run() {
        // while more than 1 player keep player
        while (state.players.size() > 1) {
            try {
                // get current player
                state.currentPlayer = state.players.remove();
                // take their turn
                turn();
                // add player to end of queue now
                state.players.add(state.currentPlayer);
            } catch (NoSuchElementException e) {
                System.out.println("NoSuchElementException error.");
                return;
            } finally {
                // printState();
            }
        }

        Player winner = state.players.remove();
        System.out.println("THE WINNER IS " + winner.playerName());
        System.out.println("WELL DONE!!!");
    }

    private void turn() {
        System.out.println("It's " + state.currentPlayer.playerName() + "'s turn.");
        int doubleCount = 0;
        // if in jail, deal with actions

        // roll the dice

        // if doubles & in jail deal with escaping

        // if third double, go to jail

        // go to square

        // deal with square's actions



    }

    private void handleSquareActions(Square square, int roll) {
        boolean owned = square.isOwned();
        boolean ownable = square.isOwnable();

        // if not owned, and ownable
        if (!owned && ownable) {

            // if owned and not mortgaged
        } else if (ownable && !square.isMortgaged()) {

            // if taxes
        } else if (square instanceof Taxes) {

            // if chance or community chest
        } else if (square instanceof CardSquare) {

            // if jail
        } else if (square instanceof Jail) {

        }
    }
}

