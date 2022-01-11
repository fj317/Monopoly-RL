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

        boolean actionToDo = true;
        while (actionToDo) {
            System.out.println("Would you like to take any additional actions on this turn?");
            System.out.println("Please select choice");
            System.out.println("1) Buy/sell houses");
            System.out.println("2) Mortgage/unmortgage properties");
            System.out.println("3) Trade with another player");
            System.out.println("4) Nothing");
            state.action = State.StateActions.OTHER;
            int decision = state.currentPlayer.inputInt(state);
            switch (decision) {
                case 1:
                    houseAction(state.currentPlayer);
                    break;
                case 2:
                    mortgageAction(state.currentPlayer);
                    break;
                case 3:
                    tradeAction(state.currentPlayer);
                    break;
                case 4:
                    actionToDo = false;
                    break;
                default:
                    System.out.println("Please enter a valid decision.");
            }
        }
        System.out.println();
    }



    private void handleSquareActions(Player currentPlayer, Square currentSquare, int roll) {
        boolean owned = currentSquare.isOwned();
        boolean ownable = currentSquare.isOwnable();

        // if not owned, and ownable
        if (!owned && ownable) {
            unowned(currentPlayer, currentSquare);
            // if owned and not mortgaged
        } else if (ownable && !currentSquare.isMortgaged()) {
            owned(currentPlayer, currentSquare, roll);
            // if taxes
        } else if (currentSquare instanceof Taxes) {
            payTax(currentPlayer, currentSquare);
            // if chance or community chest
        } else if (currentSquare instanceof CardSquare) {
            drawCard(currentPlayer, (CardSquare) currentSquare);
            // if jail
        } else if (currentSquare instanceof Jail) {
            toJail(currentPlayer);
        }
    }

    private void unowned(Player currentPlayer, Square currentSquare) {
        int propertyCost = currentSquare.getCost();
        if (currentPlayer.getMoney() < propertyCost) {
            System.out.println("Insufficient funds & assets to buy property.");
            // auction property
            purchase(auction(currentPlayer, currentSquare), currentSquare);
            return;
        }
        System.out.println("Would you like to purchase " + currentSquare.getName() + " for " + currentSquare.getCost() + " (Yes/No)?");
        state.action = State.StateActions.PURCHASE;
        if (currentPlayer.getMoney() < propertyCost) {
            // more money needed to buy so sell assets
            System.out.println("Additional funds required to buy.");
            // do stuff
        }
        if (currentPlayer.inputBool(state)) {
            currentPlayer.removeMoney(propertyCost);
            purchase(currentPlayer, currentSquare);
        } else {
            purchase(auction(currentPlayer, currentSquare), currentSquare);
        }
    }

    private void purchase (Player currentPlayer, Square currentSquare) {
        currentPlayer.addProperty(currentSquare);
        currentSquare.purchase(currentPlayer);
    }

    // return the winner of the auction
    private Player auction(Player currentPlayer, Square currentSquare) {
        return null;
    }

    private void owned(Player currentPlayer, Square currentSquare, int roll) {
        int rent = currentSquare.getRent(roll);
        Player squareOwner = currentSquare.getOwner();
        // if land on own property, no cost
        if (currentPlayer == squareOwner) return;
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + squareOwner.getName() + " in rent.");
        if (currentPlayer.getMoney() < rent) {
            System.out.println("Additional funds required to pay rent.");
            // do stuff
        }
        currentPlayer.removeMoney(rent);
        squareOwner.addMoney(rent);

    }

    private void payTax(Player currentPlayer, Square currentSquare) {
        int taxCost = currentSquare.getCost();
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + taxCost + " in tax.");
        if (currentPlayer.getMoney() < taxCost) {
            System.out.println("Additional funds required to pay rent.");
            // DO STUFF
        }
        currentPlayer.removeMoney(taxCost);
    }

    private void drawCard(Player currentPlayer, CardSquare currentSquare) {
        Cards card = currentSquare.drawCard();
        System.out.println(card.getText());
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
            case PLAYER_MONEY:
                // special case
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

    private void houseAction(Player currentPlayer) {
        System.out.println("Would you like to buy houses?");
        state.action = State.StateActions.BUY_HOUSE;
        if (currentPlayer.inputBool(state)) buyHouses(currentPlayer);
        System.out.println("Would you like to sell houses");
        state.action = State.StateActions.SELL_HOUSE;
        if (currentPlayer.inputBool(state)) sellHouses(currentPlayer);
    }

    private void mortgageAction(Player currentPlayer) {
        System.out.println("Would you like to mortgage properties?");
        state.action = State.StateActions.MORTGAGE;
        if (currentPlayer.inputBool(state)) mortgage(currentPlayer);
        System.out.println("Would you like to unmortgage properties?");
        state.action = State.StateActions.UNMORTGAGE;
        if (currentPlayer.inputBool(state)) unmortgage(currentPlayer);
    }

    private void tradeAction(Player currentPlayer) {

    }

    private void buyHouses(Player currentPlayer) {
        do {
            System.out.println("Which property would you like to buy houses on?");
            Property property = (Property) propertySelect(currentPlayer, 3);
            if (property.getBuildings() == 5 || !property.getMonopolyStatus()) {
                System.out.println("You cannot buy houses on " + property.getName());
                System.out.println("Would you like to buy additional houses?");
                continue;
            }
            if (currentPlayer.getMoney() < property.getHouseCost()) {
                System.out.println("You cannot afford to buy houses on " + property.getName());
                System.out.println("Would you like to buy additional houses?");
                continue;
            }
            // deal with evenly buying houses

            property.buildBuilding(1);
            currentPlayer.removeMoney(property.getHouseCost());
            System.out.println("You now own " + property.getBuildings() + " houses on " + property.getName());
            System.out.println("Would you like to buy additional houses?");
        } while (currentPlayer.inputBool(state));
    }

    private void sellHouses(Player currentPlayer) {
        do {
            System.out.println("Which property would you like to sell houses on?");
            Property property = (Property) propertySelect(currentPlayer, 3);
            if (property.getBuildings() == 0) {
                System.out.println("You cannot sell houses on " + property.getName());
                System.out.println("Would you like to sell additional houses?");
                continue;
            }
            // deal with evenly selling houses

            property.buildBuilding(-1);
            currentPlayer.addMoney(property.getHouseCost() / 2);
            System.out.println("You now own " + property.getBuildings() + " houses on " + property.getName());
            System.out.println("Would you like to sell additional houses?");
        } while (currentPlayer.inputBool(state));
    }

    private void mortgage(Player currentPlayer) {
        do {
            System.out.println("Which property would you like to mortgage?");
            System.out.println("You own the following unmortgaged properties: ");
            Square squareToMortgage = propertySelect(currentPlayer, 1);
            currentPlayer.addMoney(squareToMortgage.mortgage());
            System.out.println("Would you like to mortgage additional properties?");
        } while (currentPlayer.inputBool(state));
    }

    private void unmortgage(Player currentPlayer) {
        do {
            System.out.println("Which property would you like to unmortgage?");
            System.out.println("You own the following mortgaged properties: ");
            Square squareToUnmortgage = propertySelect(currentPlayer, 2);
            currentPlayer.removeMoney(squareToUnmortgage.mortgage());
            System.out.println("Would you like to unmortgage additional properties?");
        } while (currentPlayer.inputBool(state));
    }

    // skipProperties: 1 - skip mortgaged, 2 - skip unmortgaged, 3 - skip utilities/stations
    private Square propertySelect(Player currentPlayer, int skipProperties) {
        int counter = 0;
        for (Square square: currentPlayer.getProperties()) {
            if (skipProperties == 1 && square.isMortgaged()) continue;
            else if (skipProperties == 2 && !square.isMortgaged()) continue;
            else if (skipProperties == 3 && !(square instanceof Property)) continue;
            counter++;
            System.out.println(counter + ")  " + square.getName());
        }
        int input;
        do {
            input = currentPlayer.inputInt(state);
        } while (input <= counter && input > 0);
        return currentPlayer.getProperties().get(input - 1);
    }
}

