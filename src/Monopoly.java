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
        while (true) {
            // if in jail, deal with actions
            if (state.currentPlayer.inJail()) {
                System.out.println("Would you like to get out of jail using cash or GOOJF card?");
                state.action = State.StateActions.BUY_JAIL;
                // if yes
                if (state.currentPlayer.inputBool(state)) {
                    System.out.println("Select cash or GOOJF card.");
                    state.action = State.StateActions.CASH_CARD_JAIL;
                    int choice = state.currentPlayer.inputDecision(state, new String[] {"Cash", "Card"});
                    // if cash chosen
                    if (choice == 0) {
                        // use cash
                        state.currentPlayer.removeMoney(50);
                        state.currentPlayer.leaveJail();
                    } else if (state.currentPlayer.numberGetOutOfJailCards() > 0) {
                        // use the card
                        if (state.currentPlayer.useGetOutOfJailCard() == 0) {
                            chance.returnOutOfJailCard();
                        } else {
                            communityChest.returnOutOfJailCard();
                        }
                        state.currentPlayer.leaveJail();
                    } else {
                        System.out.println("You don't have any Get Out of Jail cards to use!");
                    }
                }
            }
            // roll the dice
            Dice.Roll roll = dice.roll();
            if (roll.isDouble) {
                doubleCount++;
            }
            // if doubles & in jail deal with escaping
            if (state.currentPlayer.inJail()) {
                if (roll.isDouble) {
                    System.out.println("You rolled a double and escaped jail!");
                    state.currentPlayer.leaveJail();
                    // don't reroll
                    roll.isDouble = false;
                } else {
                    System.out.println("You didn't roll a double");
                    // if last turn in jail
                    if (!state.currentPlayer.stayInJail()) {
                        state.currentPlayer.leaveJail();
                        state.currentPlayer.removeMoney(50);
                    } else {
                        break;
                    }
                }
            }
            // if third double, go to jail
            if (doubleCount == 3) {
                state.currentPlayer.sendToJail();
                break;
            }
            // go to square
            System.out.print("You rolled a " + roll.value);
            if (roll.isDouble) System.out.print( " (double) ");
            Square[] board = state.board.getBoard();
            System.out.println("and landed on " + board[(state.currentPlayer.getPosition() + roll.value) % 40].getName());
            state.currentPlayer.move(roll.value);
            // deal with square's actions
            handleSquareActions(state.currentPlayer, board[state.currentPlayer.getPosition()], roll.value);
            // if roll isn't double, or player is in jail continue (don't allow second roll)
            if (!roll.isDouble || state.currentPlayer.inJail()) break;
        }
        // do additional actions

        // buy/sell houses
        // mortgage/unmortgage
        // trade
        // nothing
    }

    private void handleSquareActions(Player currentPlayer, Square currentSquare, int roll) {
        boolean owned = currentSquare.isOwned();
        boolean ownable = currentSquare.isOwnable();

        // if not owned, and ownable
        if (!owned && ownable) {

            // if owned and not mortgaged
        } else if (ownable && !currentSquare.isMortgaged()) {

            // if taxes
        } else if (currentSquare instanceof Taxes) {

            // if chance or community chest
        } else if (currentSquare instanceof CardSquare) {

            // if jail
        } else if (currentSquare instanceof Jail) {

        }
    }

    private void unowned(Player currentPlayer, Square currentSquare) {

    }

    private void purchase (Player currentPlayer, Square currentSquare) {

    }

    // return the winner of the auction
    private Player auction(Player currentPlayer, Square currentSquare) {
        return null;
    }

    private void owned(Player currentPlayer, Square currentSquare, int roll) {

    }

    private void payTax(Player currentPlayer, Square currentSquare) {

    }

    private void drawCard(Player currentPlayer, CardSquare currentSquare) {
        Cards card = currentSquare.drawCard();
        System.out.println(card.getText());
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
            case PLAYER_MONEY:
                currentPlayer.addMoney(card.getValue());
            case MOVE:
                currentPlayer.move(card.getTravel());
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
            case STREET_REPAIRS:
                // do stuff
            case OUT_JAIL:
                currentPlayer.addGetOutOfJailCard(card.getType());

        }
    }

    private void toJail(Player currentPlayer) {
        System.out.println("Go directly to Jail!");
        currentPlayer.sendToJail();
    }
}

