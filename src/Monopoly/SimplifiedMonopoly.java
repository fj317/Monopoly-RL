package Monopoly;

import Player.*;

import java.util.Arrays;

public class SimplifiedMonopoly {

    public static final int maxTurns = 500;

    public static void main(String[] args) {
        SimplifiedMonopoly monopoly = new SimplifiedMonopoly();
        monopoly.run();
    }

    private void run() {
        System.out.println("Welcome to Monopoly! Starting the game...");
        State currState = new State();
        getPlayers(currState);
        step(currState, 0);
        int inputAction;
        while (!gameFinished(currState)) {
            inputAction = currState.getCurrentPlayer().input(currState);
            step(currState, inputAction);
        }

        Player winner = currState.getWinner();
        if (winner == null) {
            System.out.println("The game was a draw.");
        } else {
            System.out.println("THE WINNER IS " + winner.getName());
            System.out.println("WELL DONE!!!");
        }
    }


    private void getPlayers(State currState) {
        // add human or AI players etc
        // assume that playerOne is RL player
        currState.setPlayerOne(new MonteCarloPlayer());
        currState.setPlayerTwo(new RandomPolicyPlayer("Bob"));
    }

    public static boolean gameFinished(State state) {
        return state.getCurrState() == State.States.END || state.getCurrState() == State.States.END_DRAW;
    }


    public static void step(State state, int action) {
        Square currentSquare;
        int propertyCost;
        boolean actionRequired = false;
        while (!actionRequired) {
            if (state.getTickNumber() > maxTurns) {
                state.setState(State.States.END_DRAW);
                System.out.println("More than " + maxTurns + " turns completed. Ending game.");
                break;
            }
            Player currentPlayer = state.getCurrentPlayer();
            switch (state.getCurrState()) {
                case END:
                case END_DRAW:
                    actionRequired = true;
                    break;
                case TURN:
                    System.out.println("It is " + currentPlayer.getName() + "'s turn.");
                    state.setValue(0);
                    // if in jail go to jail state
                    if (currentPlayer.inJail()) {
                        state.setState(State.States.JAIL_TURN);
                    } else {
                        state.setState(State.States.ROLL);
                    }
                    break;
                case ROLL:
                    // roll the dice
                    Dice.Roll roll = state.getDice().roll();
                    state.setDiceRoll(roll.value);
                    if (roll.isDouble) {
                        state.setDoubles(true);
                        state.addValue(1);
                    } else {
                        state.setDoubles(false);
                    }
                    // check if jail and roll was double
                    if (currentPlayer.inJail()) {
                        if (roll.isDouble) {
                            System.out.println("You rolled a double and escaped jail!");
                            currentPlayer.leaveJail();
                            // don't reroll
                            state.setDoubles(false);
                        } else {
                            System.out.println("You didn't roll a double");
                            // if last turn in jail, then have to pay
                            if (!currentPlayer.stayInJail()) {
                                if (currentPlayer.getMoney() < 50) {
                                    System.out.println("Insufficient funds to pay jail fee. " + currentPlayer.getName() + " has lost the game!");
                                    state.setState(State.States.END);
                                    // game ended. Winner is opponent
                                    state.nextTurn();
                                    break;
                                }
                                System.out.println("You paid £50 to escape jail.");
                                currentPlayer.leaveJail();
                                currentPlayer.removeMoney(50);
                            } else {
                                // if in jail and roll not double then end turn
                                state.setState(State.States.END_TURN);
                                break;
                            }
                        }
                    }
                    // output roll value
                    System.out.print("You rolled a " + roll.value);
                    if (roll.isDouble) System.out.print( " (double)");
                    System.out.println();

                    // if third double, go to jail
                    if (state.getValue() == 3) {
                        System.out.println("You rolled three doubles in a row so go to jail!");
                        currentPlayer.sendToJail();
                        state.setState(State.States.END_TURN);
                        // set doubles to false so another roll isnt done
                        state.setDoubles(false);
                        break;
                    }
                    // go to square
                    Square[] board = state.getBoard().getBoard();
                    System.out.println("You landed on " + board[(currentPlayer.getPosition() + roll.value) % 40].getName());
                    currentPlayer.move(roll.value);
                    // deal with square's actions
                    state.setState(State.States.SQUARE_ACTION);
                    break;

                case SQUARE_ACTION:
                    // what type of square was landed upon
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    boolean owned = currentSquare.isOwned();
                    boolean ownable = currentSquare.isOwnable();

                    // if not owned, and ownable
                    if (!owned && ownable) {
                        state.setState(State.States.UNOWNED_LANDED);
                        break;
                        // if owned and not mortgaged
                    } else if (ownable && !currentSquare.isMortgaged()) {
                        state.setState(State.States.OWNED_LANDED);
                        break;
                        // if taxes
                    } else if (currentSquare instanceof Taxes) {
                        payTax(state);
                        // if chance or community chest
                    } else if (currentSquare instanceof CardSquare) {
                        drawCard(state);
                        break;
                        // if jail
                    } else if (currentSquare instanceof Jail && ((Jail) currentSquare).getType() == Jail.JailType.GOTO_JAIL) {
                        System.out.println("Go directly to jail.");
                        currentPlayer.sendToJail();
                    }

                    state.setState(State.States.END_TURN);
                    break;
                case UNOWNED_LANDED:
                    // unowned property. Can buy ACTION REQUIRED
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    propertyCost = currentSquare.getCost();
                    if (currentPlayer.getMoney() < propertyCost) {
                        System.out.println("Insufficient funds & assets to buy property.");
                        state.setState(State.States.END_TURN);
                        break;
                    }
                    System.out.println("Would you like to purchase " + currentSquare.getName() + " for £" + currentSquare.getCost() + "?");
                    System.out.println("Please select choice");
                    System.out.println("1) Yes");
                    System.out.println("2) No");
                    state.setActionList(Arrays.asList("Yes", "No"));
                    actionRequired = true;
                    state.setState(State.States.UNOWNED_DECISION);
                    break;
                case UNOWNED_DECISION:
                    if (action == 1) {
                        state.setState(State.States.BUYING_UNOWNED);
                    } else if (action == 2) {
                        state.setState(State.States.END_TURN);
                    }
                    break;
                case BUYING_UNOWNED:
                    // player is buying the square
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    propertyCost = currentSquare.getCost();
                    currentPlayer.removeMoney(propertyCost);

                    currentPlayer.addProperty(currentSquare);
                    currentSquare.purchase(currentPlayer);

                    System.out.println(currentSquare.getName() + " has been purchased by " + currentPlayer.getName());
                    state.setState(State.States.END_TURN);
                    break;
                case OWNED_LANDED:
                    // landed on a owned property
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    if (currentSquare.getOwner().getName().equals(currentPlayer.getName())) {
                        System.out.println("You already own this property.");
                        state.setState(State.States.END_TURN);
                        break;
                    }
                    int rent = currentSquare.getRent(state.getDiceRoll());
                    System.out.println("You have landed on " + currentSquare.getName() + " and owe " + currentSquare.getOwner().getName() + " £" + rent +" in rent.");
                    // if player doesn't have enough  money then lost
                    if (currentPlayer.getMoney() < rent) {
                        System.out.println("Insufficient funds to pay rent. " + currentPlayer.getName() + " has lost the game!");
                        state.setState(State.States.END);
                        // game ended. Winner is opponent
                        state.nextTurn();
                        break;
                    }
                    // pay player
                    currentPlayer.removeMoney(rent);
                    currentSquare.getOwner().addMoney(rent);
                    state.setState(State.States.END_TURN);
                    break;
                case END_TURN:
                    if (state.getDoubles()) {
                        state.setState(State.States.ROLL);
                        break;
                    }
                    System.out.println("END OF " + currentPlayer.getName() + "'S TURN");
                    System.out.println("Players money: " + currentPlayer.getMoney());
                    System.out.println("Player's position: " + currentPlayer.getPosition());
                    state.nextTurn();
                    state.addOneTick();
                    state.setState(State.States.TURN);
                    System.out.println();
                    break;
                case JAIL_TURN:
                    System.out.println("You are in jail.");
                    System.out.println("Please select choice");
                    System.out.println("1) Use cash to get out of jail");
                    System.out.println("2) Use 'get out of jail free' card to get out of jail");
                    System.out.println("3) Stay in jail");
                    actionRequired = true;
                    state.setActionList(Arrays.asList("Use cash", "Use card", "Stay"));
                    state.setState(State.States.JAIL_DECISION);
                    break;
                case JAIL_DECISION:
                    if (action == 1) {
                        state.setState(State.States.JAIL_OUT_CASH);
                    } else if (action == 2) {
                        state.setState(State.States.JAIL_OUT_CARD);
                    } else if (action == 3) {
                        state.setState(State.States.ROLL);
                    }
                    break;
                case JAIL_OUT_CARD:
                    if (currentPlayer.getNumberGetOutOfJailCards() > 0) {
                        // use the card
                        if (currentPlayer.useGetOutOfJailCard() == Cards.CardType.CHANCE) {
                            state.getChance().returnOutOfJailCard();
                        } else {
                            state.getCommunityChest().returnOutOfJailCard();
                        }
                        currentPlayer.leaveJail();
                        System.out.println("You have left jail.");
                    } else {
                        System.out.println("You don't have any Get Out of Jail cards to use!");
                    }
                    state.setState(State.States.ROLL);
                    break;
                case JAIL_OUT_CASH:
                    // use cash
                    if (currentPlayer.getMoney() > 50) {
                        currentPlayer.removeMoney(50);
                        currentPlayer.leaveJail();
                        System.out.println("You have left jail.");
                    } else {
                        System.out.println("Insufficient funds to leave jail.");
                    }
                    state.setState(State.States.ROLL);
                    break;
            }

        }
    }

    private static void payTax(State state) {
        Player currentPlayer = state.getCurrentPlayer();
        Square currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
        int taxCost = currentSquare.getCost();
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + taxCost + " in tax.");
        if (currentPlayer.getMoney() < taxCost) {
            System.out.println("Insufficient funds to pay tax. " + currentPlayer.getName() + " has lost the game!");
            state.setState(State.States.END);
            // game ended. Winner is opponent
            state.nextTurn();
            return;
        }
        currentPlayer.removeMoney(taxCost);
    }

    private static void drawCard(State state) {
        Player currentPlayer = state.getCurrentPlayer();
        CardSquare currentSquare = (CardSquare) state.getBoard().getSquare(currentPlayer.getPosition());
        Cards card = null;
        // get the card
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            card = state.getChance().getCard();
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            card = state.getCommunityChest().getCard();
        }
        System.out.println(card.getText());
        state.setState(State.States.END_TURN);
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
                break;
            case PLAYER_MONEY:
                // special case
                playerMoney(currentPlayer, state.getOpponent(), card.getValue(), state);
                break;
            case MOVE:
                currentPlayer.move(card.getTravel());
                state.setState(State.States.SQUARE_ACTION);
                break;
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
                state.setState(State.States.SQUARE_ACTION);
                break;
            case STREET_REPAIRS:
                streetRepairs(currentPlayer, card.getHouseCost(), card.getHotelCost(), state);
                break;
            case OUT_JAIL:
                currentPlayer.addGetOutOfJailCard(card.getType());
                break;
        }
        // reflect any updates to card object (i.e. jail card taken)
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            state.setChance(card);
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            state.setCommunityChest(card);
        }
    }

    private static void streetRepairs(Player currentPlayer, int houseCost, int hotelCost, State state) {
        int totalHouses = 0;
        int totalHotels = 0;
        int sqBuildings;
        // for each property, count number of houses/hotels
        for (Square sq : currentPlayer.getProperties()) {
            if (sq instanceof Property) {
                sqBuildings = ((Property) sq).getBuildings();
                if (sqBuildings == 5) {
                    totalHotels ++;
                } else {
                    totalHouses += sqBuildings;
                }
            }
        }
        int cost = (totalHotels * hotelCost) + (totalHouses * houseCost);
        System.out.println("You own £" + cost + " for street repairs.");
        if (currentPlayer.getMoney() < cost) {
            System.out.println("Insufficient funds to pay street repairs. " + currentPlayer.getName() + " has lost the game!");
            state.setState(State.States.END);
            // game ended. Winner is opponent
            state.nextTurn();
            return;
        }
        currentPlayer.removeMoney(cost);
    }

    // remove amount from each player and give to the currentPlayer
    private static void playerMoney(Player currentPlayer, Player opponent, int amount, State currState) {
        if (opponent.getMoney() < amount) {
            System.out.println("Insufficient funds to pay. " + opponent.getName() + " has lost the game!");
            currState.setState(State.States.END);
            // game ended. no need to change player as winner is the current player (since opponent couldnt pay)
            return;
        }
        opponent.removeMoney(amount);
        currentPlayer.addMoney(amount);
    }

    public static void stepNoOutput(State state, int action) {
        Square currentSquare;
        int propertyCost;
        boolean actionRequired = false;
        while (!actionRequired) {
            if (state.getTickNumber() > maxTurns) {
                state.setState(State.States.END_DRAW);
                break;
            }
            //System.out.println(state.getCurrState());
            Player currentPlayer = state.getCurrentPlayer();
            switch (state.getCurrState()) {
                case END:
                case END_DRAW:
                    actionRequired = true;
                    break;
                case TURN:
                    state.setValue(0);
                    // if in jail go to jail state
                    if (currentPlayer.inJail()) {
                        state.setState(State.States.JAIL_TURN);
                    } else {
                        state.setState(State.States.ROLL);
                    }
                    break;
                case ROLL:
                    // roll the dice
                    Dice.Roll roll = state.getDice().roll();
                    state.setDiceRoll(roll.value);
                    if (roll.isDouble) {
                        state.setDoubles(true);
                        state.addValue(1);
                    } else {
                        state.setDoubles(false);
                    }
                    // check if jail and roll was double
                    if (currentPlayer.inJail()) {
                        if (roll.isDouble) {
                            currentPlayer.leaveJail();
                            // don't reroll
                            state.setDoubles(false);
                        } else {
                            // if last turn in jail, then have to pay
                            if (!currentPlayer.stayInJail()) {
                                if (currentPlayer.getMoney() < 50) {
                                    state.setState(State.States.END);
                                    // game ended. Winner is opponent
                                    state.nextTurn();
                                    break;
                                }
                                currentPlayer.leaveJail();
                                currentPlayer.removeMoney(50);
                            } else {
                                // if in jail and roll not double then end turn
                                state.setState(State.States.END_TURN);
                                break;
                            }
                        }
                    }

                    // if third double, go to jail
                    if (state.getValue() == 3) {
                        currentPlayer.sendToJail();
                        state.setState(State.States.END_TURN);
                        // set doubles to false so another roll isnt done
                        state.setDoubles(false);
                        break;
                    }
                    // go to square
                    currentPlayer.move(roll.value);
                    // deal with square's actions
                    state.setState(State.States.SQUARE_ACTION);
                    break;

                case SQUARE_ACTION:
                    // what type of square was landed upon
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    boolean owned = currentSquare.isOwned();
                    boolean ownable = currentSquare.isOwnable();

                    // if not owned, and ownable
                    if (!owned && ownable) {
                        state.setState(State.States.UNOWNED_LANDED);
                        break;
                        // if owned and not mortgaged
                    } else if (ownable && !currentSquare.isMortgaged()) {
                        state.setState(State.States.OWNED_LANDED);
                        break;
                        // if taxes
                    } else if (currentSquare instanceof Taxes) {
                        payTaxNoOutput(state);
                        // if chance or community chest
                    } else if (currentSquare instanceof CardSquare) {
                        drawCardNoOutput(state);
                        break;
                        // if jail
                    } else if (currentSquare instanceof Jail && ((Jail) currentSquare).getType() == Jail.JailType.GOTO_JAIL) {
                        currentPlayer.sendToJail();
                    }

                    state.setState(State.States.END_TURN);
                    break;
                case UNOWNED_LANDED:
                    // unowned property. Can buy ACTION REQUIRED
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    propertyCost = currentSquare.getCost();
                    if (currentPlayer.getMoney() < propertyCost) {
                        state.setState(State.States.END_TURN);
                        break;
                    }
                    state.setActionList(Arrays.asList("Yes", "No"));
                    actionRequired = true;
                    state.setState(State.States.UNOWNED_DECISION);
                    break;
                case UNOWNED_DECISION:
                    if (action == 1) {
                        state.setState(State.States.BUYING_UNOWNED);
                    } else if (action == 2) {
                        state.setState(State.States.END_TURN);
                    }
                    break;
                case BUYING_UNOWNED:
                    // player is buying the square
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    propertyCost = currentSquare.getCost();
                    currentPlayer.removeMoney(propertyCost);

                    currentPlayer.addProperty(currentSquare);
                    currentSquare.purchase(currentPlayer);

                    state.setState(State.States.END_TURN);
                    break;
                case OWNED_LANDED:
                    // landed on a owned property
                    currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                    if (currentSquare.getOwner().getName().equals(currentPlayer.getName())) {
                        state.setState(State.States.END_TURN);
                        break;
                    }
                    int rent = currentSquare.getRent(state.getDiceRoll());
                    // if player doesn't have enough  money then lost
                    if (currentPlayer.getMoney() < rent) {
                        state.setState(State.States.END);
                        // game ended. Winner is opponent
                        state.nextTurn();
                        break;
                    }
                    // pay player
                    currentPlayer.removeMoney(rent);
                    currentSquare.getOwner().addMoney(rent);
                    state.setState(State.States.END_TURN);
                    break;
                case END_TURN:
                    if (state.getDoubles()) {
                        state.setState(State.States.ROLL);
                        break;
                    }
                    state.nextTurn();
                    state.addOneTick();
                    state.setState(State.States.TURN);
                    break;
                case JAIL_TURN:
                    actionRequired = true;
                    state.setActionList(Arrays.asList("Use cash", "Use card", "Stay"));
                    state.setState(State.States.JAIL_DECISION);
                    break;
                case JAIL_DECISION:
                    if (action == 1) {
                        state.setState(State.States.JAIL_OUT_CASH);
                    } else if (action == 2) {
                        state.setState(State.States.JAIL_OUT_CARD);
                    } else if (action == 3) {
                        state.setState(State.States.ROLL);
                    }
                    break;
                case JAIL_OUT_CARD:
                    if (currentPlayer.getNumberGetOutOfJailCards() > 0) {
                        // use the card
                        if (currentPlayer.useGetOutOfJailCard() == Cards.CardType.CHANCE) {
                            state.getChance().returnOutOfJailCard();
                        } else {
                            state.getCommunityChest().returnOutOfJailCard();
                        }
                        currentPlayer.leaveJail();
                    }
                    state.setState(State.States.ROLL);
                    break;
                case JAIL_OUT_CASH:
                    // use cash
                    if (currentPlayer.getMoney() > 50) {
                        currentPlayer.removeMoney(50);
                        currentPlayer.leaveJail();
                    }
                    state.setState(State.States.ROLL);
                    break;
            }

        }
    }

    private static void drawCardNoOutput(State state) {
        Player currentPlayer = state.getCurrentPlayer();
        CardSquare currentSquare = (CardSquare) state.getBoard().getSquare(currentPlayer.getPosition());
        Cards card = null;
        // get the card
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            card = state.getChance().getCard();
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            card = state.getCommunityChest().getCard();
        }
        state.setState(State.States.END_TURN);
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
                break;
            case PLAYER_MONEY:
                // special case
                playerMoneyNoOutput(currentPlayer, state.getOpponent(), card.getValue(), state);
                break;
            case MOVE:
                currentPlayer.move(card.getTravel());
                state.setState(State.States.SQUARE_ACTION);
                break;
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
                state.setState(State.States.SQUARE_ACTION);
                break;
            case STREET_REPAIRS:
                streetRepairsNoOutput(currentPlayer, card.getHouseCost(), card.getHotelCost(), state);
                break;
            case OUT_JAIL:
                currentPlayer.addGetOutOfJailCard(card.getType());
                break;
        }
        // reflect any updates to card object (i.e. jail card taken)
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            state.setChance(card);
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            state.setCommunityChest(card);
        }
    }

    private static void payTaxNoOutput(State state) {
        Player currentPlayer = state.getCurrentPlayer();
        Square currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
        int taxCost = currentSquare.getCost();
        if (currentPlayer.getMoney() < taxCost) {
            state.setState(State.States.END);
            // game ended. Winner is opponent
            state.nextTurn();
            return;
        }
        currentPlayer.removeMoney(taxCost);
    }

    private static void streetRepairsNoOutput(Player currentPlayer, int houseCost, int hotelCost, State state) {
        int totalHouses = 0;
        int totalHotels = 0;
        int sqBuildings;
        // for each property, count number of houses/hotels
        for (Square sq : currentPlayer.getProperties()) {
            if (sq instanceof Property) {
                sqBuildings = ((Property) sq).getBuildings();
                if (sqBuildings == 5) {
                    totalHotels ++;
                } else {
                    totalHouses += sqBuildings;
                }
            }
        }
        int cost = (totalHotels * hotelCost) + (totalHouses * houseCost);
        if (currentPlayer.getMoney() < cost) {
            state.setState(State.States.END);
            // game ended. Winner is opponent
            state.nextTurn();
            return;
        }
        currentPlayer.removeMoney(cost);
    }

    private static void playerMoneyNoOutput(Player currentPlayer, Player opponent, int amount, State currState) {
        if (opponent.getMoney() < amount) {
            currState.setState(State.States.END);
            // game ended. no need to change player as winner is the current player (since opponent couldnt pay)
            return;
        }
        opponent.removeMoney(amount);
        currentPlayer.addMoney(amount);
    }
}
