package Monopoly;

import Player.*;

import java.util.*;

public class Monopoly {
    private State state;
    private int turnNumber;
    private int playerTurn;


    public Monopoly() {
        this.state = new State();
        turnNumber = 0;
        playerTurn = 1;
        // get players
        getPlayers();
    }

    public static void main(String[] args) {
        Monopoly monopoly = new Monopoly();
        monopoly.run();

    }

    public State getState() {
        return this.state;
    }


    private void getPlayers() {
        // add human or AI players etc
        // assume that playerOne is RL player
        state.setPlayerOne(new RandomPolicyPlayer("Freddie"));
        state.setPlayerTwo(new RandomPolicyPlayer("Random"));
    }

    private double getRLPosition() {
        int position = state.getPlayerOne().getPosition();
        return position / 40.0;
    }

    private double[] getRLProperties() {
        // goes through each square of the board
        double[] propertyList = new double[24];
        int pos = 0;

        for (int i = 0; i < 40; i++) {
            Square currentSquare = state.getBoard().getSquare(i);
            if (currentSquare.getOwner() == state.getPlayerOne()) {
                // if property
                if (currentSquare instanceof Property) {
                    propertyList[pos] = 1.0/6.0 + (((Property) currentSquare).getBuildings() * (1.0/6.0));
                    pos++;
                // if railroad
                } else if (currentSquare instanceof Railroad) {
                    propertyList[22] += 1.0/4.0;
                // if utilty
                } else if (currentSquare instanceof Utilty) {
                    propertyList[23] += 1.0/2.0;

                }
            }
        }
        return propertyList;
    }

    private double[] getRLFinance() {
        // money amount normalisation
        double money = boundMoney(state.getPlayerOne().getMoney());
        // property comparison measure
        double propertyComparison = (double) state.getPlayerOne().getProperties().size() / (double)(state.getPlayerOne().getProperties().size() + state.getPlayerTwo().getProperties().size());
        return new double[]{money, propertyComparison};
    }

    private double boundMoney(int money) {
        double maxAmount = 10000;
        if (money > maxAmount) {
            return 1.0;
        } else {
            return money / maxAmount;
        }
    }


    private void run() {
        System.out.println("Welcome to Monopoly! Starting the game...");
        while (state.getCurrState() != State.States.END) {
            tick();
        }
        Player winner = getCurrentPlayer();
        System.out.println("THE WINNER IS " + winner.getName());
        System.out.println("WELL DONE!!!");
        System.out.println("Total turns: " + turnNumber);
    }

    // auction code
    /*
    // return the winner of the auction
    private Player auction(Player currentPlayer, Square currentSquare) {
        System.out.println("Auctioning off " + currentSquare.getName());
        int currentBid = 0;
        final int bidIncrement = 10;
        int minimumBid;
        Player winner = null;
        state.action = State.StateActions.AUCTION;
        Queue<Player> auctionPlayers = new LinkedList<>(state.getPlayers());
        Player currentAuctionPlayer = auctionPlayers.remove();
        // while still players willing to participate in auction
        while (auctionPlayers.size() > 0) {
            System.out.println("It is " + currentAuctionPlayer.getName() + "'s turn to bid.");
            if (winner == currentAuctionPlayer) {
                // if winner of auction is current auction player skip their turn
                System.out.println(currentAuctionPlayer.getName() + " holds the highest bid currently, skipping their turn.");
                // move to next player and continue loop
                auctionPlayers.add(currentAuctionPlayer);
                currentAuctionPlayer = auctionPlayers.remove();
                continue;
            }
            minimumBid = currentBid + bidIncrement;
            state.value = minimumBid;
            if (minimumBid > currentAuctionPlayer.getMoney()) {
                System.out.println("Insufficient funds to bid, removing player from auction.");
                currentAuctionPlayer = auctionPlayers.remove();
                // dont add to auction list as not enough money anyway
                continue;
            }
            System.out.println("Would " + currentAuctionPlayer.getName() + " like to place a bid? Minimum bid £" + (currentBid + bidIncrement));
            state.setActionList(Arrays.asList("Yes", "No"));
            if (currentAuctionPlayer.inputBool(state)) {
                System.out.println("Enter your bid: ");
                state.setActionList(Arrays.asList(Integer.toString(minimumBid)));
                int playerBid = currentAuctionPlayer.inputInt(state);
                // if bid is more than available cash
                if (playerBid > currentAuctionPlayer.getMoney()) {
                    System.out.println("You do not have the required funds for that bid.");
                    continue;
                }
                // if bid is less than minimum bid
                if (playerBid < currentBid + bidIncrement) {
                    System.out.println("Bid is below minimum bid, try again");
                    continue;
                }
                currentBid = playerBid;
                winner = currentAuctionPlayer;
                System.out.println("Bid accepted. Current highest bid by " + currentAuctionPlayer.getName() + " for £" + currentBid);
            } else {
                if (winner != null) {
                    break;
                }
            }
            // move to next player
            auctionPlayers.add(currentAuctionPlayer);
            currentAuctionPlayer = auctionPlayers.remove();
        }

        if (winner == null) {
            System.out.println("No player wins the auction.");
        } else {
            winner.removeMoney(currentBid);
            System.out.println(winner.getName() + " wins auction for £" + currentBid);
        }
        return winner;
    } */

    public void tick() {
        int answer;
        Square currentSquare;
        int propertyCost;
        ArrayList<Square> propertyList;
        Property property;
        Property property1;
        Property property2;
        System.out.println("Current State: " + state.getCurrState().toString());
        Player currentPlayer = getCurrentPlayer();
        Player opponent = getOpponent();

        switch (state.getCurrState()) {
            case NONE:
                // check if doubles, if so reroll dice
                if (state.doubles) {
                    state.setState(State.States.ROLL);
                    break;
                }
                // menu
                System.out.println("Would you like to take any additional actions on this turn?");
                System.out.println("Please select choice");
                System.out.println("1) Buy/sell houses");
                System.out.println("2) Mortgage/unmortgage properties");
                System.out.println("3) Trade with another player");
                System.out.println("4) Nothing - end turn");
                state.setActionList(Arrays.asList("Buy/Sell House", "Mortgage/Unmortgage property", "Trade", "End turn"));
                answer = currentPlayer.input(state);
                switch (answer) {
                    case 1:
                        state.setState(State.States.HOUSE_DECISION);
                        break;
                    case 2:
                        state.setState(State.States.MORTGAGE_DECISION);
                        break;
                    case 3:
                        state.setState(State.States.TRADE);
                        break;
                    case 4:
                        state.setState(State.States.END_TURN);
                        break;
                }
                break;
            case TRADE:
                // must have properties
                if (currentPlayer.getProperties().size() == 0) {
                    System.out.println("You do not have any properties to trade.");
                    state.setState(State.States.NONE);
                    break;
                }
                // check opponent has properties
                if (opponent.getProperties().size() == 0) {
                    System.out.println("The selected trading player does not have any properties to trade.");
                    state.setState(State.States.NONE);
                    break;
                }
                state.setState(State.States.TRADE_PROPERTY_SELECT_1);
                break;
            case TRADE_PROPERTY_SELECT_1:
                System.out.println("You own the following properties: ");
                propertyList = propertySelect(currentPlayer, 0);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                currentSquare = propertyList.get(answer);
                state.addDataSquares(currentSquare);
                state.setState(State.States.TRADE_PROPERTY_SELECT_2);
                break;
            case TRADE_PROPERTY_SELECT_2:
                System.out.println("Your opponent owns the following properties: ");
                propertyList = propertySelect(opponent, 0);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                currentSquare = propertyList.get(answer);
                state.addDataSquares(currentSquare);
                state.setState(State.States.TRADE_CONFIRM);
                break;
            case TRADE_CONFIRM:
                List<Square> tradeSquares = state.getDataSquares();
                Square propertyOne = tradeSquares.get(0);
                Square propertyTwo = tradeSquares.get(1);
                state.resetDataSquares();

                // checks if legal trade
                if (propertyOne instanceof Property) {
                    if (((Property) propertyOne).getBuildings() > 0) { // check if property has houses
                        System.out.println(propertyOne.getName() + " has houses/hotels built on it. Please sell them to trade it.");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyOne).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                        System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyOne).getGroupPropertyB() != null) { // check if other group property has houses
                        if (((Property) propertyOne).getGroupPropertyB().getBuildings() > 0) {
                            System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                            state.setState(State.States.NONE);
                            break;
                        }
                    }
                }
                if (propertyTwo instanceof Property) {
                    if (((Property) propertyTwo).getBuildings() > 0) { // check if property has houses
                        System.out.println(propertyTwo.getName() + " has houses/hotels built on it and is untradable at the current moment.");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyTwo).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                        System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it .");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyTwo).getGroupPropertyB() != null) { // check if other group property has houses
                        if (((Property) propertyTwo).getGroupPropertyB().getBuildings() > 0) {
                            System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                            state.setState(State.States.NONE);
                            break;
                        }
                    }
                }
                System.out.println("You are attempting to trade " + propertyOne.getName() + " for " + propertyTwo.getName() + ". Do you agree with this trade?");
                System.out.print(opponent.getName() + " agree? ");
                state.setActionList(Arrays.asList("Yes", "No"));
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                answer = opponent.input(state);
                if (answer == 1) {
                    System.out.println("Trade accepted");
                    // perform deal
                    propertyOne.purchase(opponent);
                    currentPlayer.sellProperty(propertyOne);
                    opponent.addProperty(propertyOne);
                    propertyTwo.purchase(currentPlayer);
                    opponent.sellProperty(propertyTwo);
                    currentPlayer.addProperty(propertyTwo);
                    // if both properties
                    if (propertyOne instanceof Property && propertyTwo instanceof Property) {
                        // and both properties of same group
                        if (((Property) propertyOne).getGroupPropertyA() == propertyTwo || ((Property) propertyOne).getGroupPropertyB() == propertyTwo) {
                            // set monopoly to false as just exchanged
                            ((Property) propertyOne).setMonopoly(false);
                            ((Property) propertyTwo).setMonopoly(false);
                        }
                    }
                } else if (answer == 2) {
                    System.out.println("Trade declined.");
                }
                state.setState(State.States.NONE);
                break;
            case END_TURN:
                playerTurn++;
                turnNumber++;
                state.setState(State.States.TURN);
                break;
            case JAIL_TURN:
                System.out.println("You are in jail.");
                System.out.println("Please select choice");
                System.out.println("1) Use cash to get out of jail");
                System.out.println("2) Use 'get out of jail free' card to get out of jail");
                System.out.println("3) Stay in jail");
                state.setActionList(Arrays.asList("Use cash", "Use card", "Stay"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    state.setState(State.States.JAIL_OUT_CASH);
                } else if (answer == 2) {
                    state.setState(State.States.JAIL_OUT_CARD);
                } else if (answer == 3) {
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
            case MORTGAGE_DECISION:
                System.out.println("Please select choice");
                System.out.println("1) Mortgage");
                System.out.println("2) Unmortgage");
                state.setActionList(Arrays.asList("Mortgage", "Unmortgage"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    state.setState(State.States.MORTGAGE_PROPERTY_SELECT);
                } else if (answer == 2) {
                    state.setState(State.States.UNMORTGAGE_PROPERTY_SELECT);
                }
                break;
            case MORTGAGE_PROPERTY_SELECT:
                boolean unmortgagedProperty = false;
                for (Square sq: currentPlayer.getProperties()) {
                    if (!sq.isMortgaged()) {
                        unmortgagedProperty = true;
                    }
                }
                if (!unmortgagedProperty) {
                    System.out.println("You do not have any unmortgaged properties.");
                    state.setState(State.States.NONE);
                    break;
                }
                System.out.println("Which property would you like to mortgage?");
                System.out.println("You own the following unmortgaged properties: ");
                propertyList = propertySelect(currentPlayer, 1);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                currentSquare = propertyList.get(answer);
                if (currentSquare instanceof Property) {
                    if (((Property) currentSquare).getBuildings() > 0) { // if buildings on property then cant mortgage
                        System.out.println("You cannot mortgage a property while there are buildings on it.");
                        state.setState(State.States.NONE);
                        break;
                    }
                }
                state.addDataSquares(currentSquare);
                state.setState(State.States.MORTGAGE_ACTION);
                break;
            case MORTGAGE_ACTION:
                List<Square> mortgageSquare = state.getDataSquares();
                currentSquare = mortgageSquare.get(0);
                state.resetDataSquares();
                System.out.println("Mortgaging " + currentSquare.getName() + " will net £" + (currentSquare.getCost() / 2) + ". Are you sure you want to mortgage the property?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                state.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    currentPlayer.addMoney(currentSquare.mortgage());
                    System.out.println(currentSquare.getName() + " has been mortgaged!");
                } else if (answer == 2) {
                    System.out.println("Mortgage action cancelled.");
                }
                state.setState(State.States.NONE);
                break;
            case HOUSE_DECISION:
                if (currentPlayer.getBuildableProperties().size() == 0) {
                    System.out.println("You do not have any properties to buy/sell houses upon.");
                    state.setState(State.States.NONE);
                    break;
                }
                System.out.println("Please select choice");
                System.out.println("1) Buy houses");
                System.out.println("2) Sell houses");
                state.setActionList(Arrays.asList("Buy houses", "Sell houses"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    state.setState(State.States.BUY_HOUSE_PROPERTY_SELECT);
                } else if (answer == 2) {
                    state.setState(State.States.SELL_HOUSE_PROPERTY_SELECT);
                }
                break;
            case BUY_HOUSE_PROPERTY_SELECT:
                // decide which house to buy on
                propertyList = propertySelect(currentPlayer, 3);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                property = (Property) propertyList.get(answer);
                property1 = property.getGroupPropertyA();
                property2 = property.getGroupPropertyB();
                // do checks to ensure valid property
                // check if max houses
                if (property.getBuildings() == 5) {
                    System.out.println("You cannot buy more houses on " + property.getName());
                    state.setState(State.States.NONE);
                    break;
                } else if (!property.getMonopolyStatus()) { // check if monopoly status
                    System.out.println("You do not have a monopoly on " + property.getName());
                    state.setState(State.States.NONE);
                    break;
                } else if (currentPlayer.getMoney() < property.getHouseCost()) { // check if can afford
                    System.out.println("You cannot afford to buy houses on " + property.getName());
                    state.setState(State.States.NONE);
                    break;
                } else if (property2 == null) {
                    if (property.getBuildings() > property1.getBuildings()) { // check if houses built evenly
                        System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (property.isMortgaged() || property1.isMortgaged()) { // if properties are mortgaged cant build houses
                        System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                        state.setState(State.States.NONE);
                        break;
                    }

                } else { // check houses are built evenly
                    if (property.getBuildings() > property1.getBuildings() && property.getBuildings() > property2.getBuildings()) {
                        System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                        state.setState(State.States.NONE);
                        break;
                    }
                    if (property.isMortgaged() || property1.isMortgaged() || property2.isMortgaged()) { // if properties are mortgaged cant build houses
                        System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                        state.setState(State.States.NONE);
                        break;
                    }
                }
                state.addDataSquares(property);
                state.setState(State.States.BUY_HOUSE_ACTION);
                break;
            case BUY_HOUSE_ACTION:
                List<Square> buyHouseSquare = state.getDataSquares();
                currentSquare = buyHouseSquare.get(0);
                state.resetDataSquares();
                System.out.println("A house/hotel on " + currentSquare.getName() + " will cost £" + ((Property) currentSquare).getHouseCost() + ". Are you sure you want to buy a house/hotel?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                state.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    ((Property) currentSquare).buildBuilding(1);
                    currentPlayer.removeMoney(((Property) currentSquare).getHouseCost());
                    System.out.println("You now own " + ((Property) currentSquare).getBuildings() + " houses on " + currentSquare.getName());
                } else if (answer == 2) {
                    System.out.println("House buying action cancelled.");
                }
                state.setState(State.States.NONE);
                break;
            case SELL_HOUSE_PROPERTY_SELECT:
                // decide which house to buy on
                propertyList = propertySelect(currentPlayer, 3);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                property = (Property) propertyList.get(answer);
                property1 = property.getGroupPropertyA();
                property2 = property.getGroupPropertyB();
                // do checks to ensure valid property
                if (property.getBuildings() == 0) {
                    System.out.println("You do not have any houses to sell on " + property.getName());
                    state.setState(State.States.NONE);
                    break;
                } else if (property2 == null) {
                    if (property.getBuildings() < property1.getBuildings()) {
                        System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                        state.setState(State.States.NONE);
                        break;
                    }
                } else {
                    if (property.getBuildings() < property1.getBuildings() && property.getBuildings() < property2.getBuildings()) {
                        System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                        state.setState(State.States.NONE);
                        break;
                    }
                }
                state.addDataSquares(property);
                state.setState(State.States.SELL_HOUSE_ACTION);
                break;
            case SELL_HOUSE_ACTION:
                List<Square> sellHouseSquare = state.getDataSquares();
                currentSquare = sellHouseSquare.get(0);
                state.resetDataSquares();
                System.out.println("Selling a house/hotel on " + currentSquare.getName() + " will net £" + (((Property) currentSquare).getHouseCost() / 2) + ". Are you sure you want to sell a house/hotel?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                state.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    ((Property) currentSquare).buildBuilding(-1);
                    currentPlayer.addMoney(((Property) currentSquare).getHouseCost() / 2);
                    System.out.println("You now own " + ((Property) currentSquare).getBuildings() + " houses on " + currentSquare.getName());
                } else if (answer == 2) {
                    System.out.println("House selling action cancelled.");
                }
                state.setState(State.States.NONE);
                break;
            case UNMORTGAGE_PROPERTY_SELECT:
                boolean mortgagedProperty = false;
                for (Square sq: currentPlayer.getProperties()) {
                    if (sq.isMortgaged()) {
                        mortgagedProperty = true;
                    }
                }
                if (!mortgagedProperty) {
                    System.out.println("You do not have any mortgaged properties.");
                    state.setState(State.States.NONE);
                    break;
                }

                System.out.println("Which property would you like to unmortgage?");
                System.out.println("You own the following mortgaged properties: ");
                propertyList = propertySelect(currentPlayer, 2);
                state.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(state) - 1;
                currentSquare = propertyList.get(answer);
                state.addDataSquares(currentSquare);
                state.setState(State.States.UNMORTGAGE_ACTION);
                break;
            case UNMORTGAGE_ACTION:
                List<Square> unmortgageSquare = state.getDataSquares();
                currentSquare = unmortgageSquare.get(0);
                state.resetDataSquares();

                System.out.println("Unmortgaging " + currentSquare.getName() + " will cost £" + (int) Math.round((currentSquare.getCost() / 2.0) * 1.1) + ". Are you sure you want to unmortgage the property?");
                // all actions from now on relate to yes/no
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                state.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    currentPlayer.removeMoney(currentSquare.mortgage());
                    System.out.println(currentSquare.getName() + " has been unmortgaged!");
                } else if (answer == 2) {
                    System.out.println("Unmortgage action cancelled.");
                }
                state.setState(State.States.NONE);
                break;
            case BUYING_UNOWNED:
                // player is buying the square
                currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                propertyCost = currentSquare.getCost();
                currentPlayer.removeMoney(propertyCost);

                currentPlayer.addProperty(currentSquare);
                currentSquare.purchase(currentPlayer);

                System.out.println(currentSquare.getName() + " has been purchased by " + currentPlayer.getName());
                state.setState(State.States.NONE);
                break;
            case UNOWNED_LANDED:
                currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                propertyCost = currentSquare.getCost();
                if (currentPlayer.getMoney() < propertyCost) {
                    System.out.println("Insufficient funds & assets to buy property.");
                    state.setState(State.States.NONE);
                    break;
                }
                System.out.println("Would you like to purchase " + currentSquare.getName() + " for £" + currentSquare.getCost() + "?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                state.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(state);
                if (answer == 1) {
                    state.setState(State.States.BUYING_UNOWNED);
                } else if (answer == 2) {
                    state.setState(State.States.NONE);
                    //state.addDataSquares(currentSquare);
                }
                break;
            case OWNED_LANDED:
                // landed on a owned property
                currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                int rent = currentSquare.getRent(state.getDiceRoll());
                System.out.println("You have landed on " + currentSquare.getName() + " and owe " + currentSquare.getOwner().getName() + " £" + rent +" in rent.");
                // if player doesn't have enough  money then lost
                if (currentPlayer.getMoney() < rent) {
                    System.out.println("Insufficient funds to pay rent. " + currentPlayer.getName() + " has lost the game!");
                    state.setState(State.States.END);
                    printState();
                    // game ended. Winner is opponent
                    playerTurn++;
                    break;
                }
                // pay player
                currentPlayer.removeMoney(rent);
                currentSquare.getOwner().addMoney(rent);
                state.setState(State.States.NONE);
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
            case SQUARE_ACTION:
                currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
                boolean owned = currentSquare.isOwned();
                boolean ownable = currentSquare.isOwnable();
                // set state to none - if there are changes it will be overwritten
                state.setState(State.States.NONE);

                // if not owned, and ownable
                if (!owned && ownable) {
                    state.setState(State.States.UNOWNED_LANDED);
                    // if owned and not mortgaged
                } else if (ownable && !currentSquare.isMortgaged()) {
                    state.setState(State.States.OWNED_LANDED);
                    // if taxes
                } else if (currentSquare instanceof Taxes) {
                    payTax();
                    // if chance or community chest
                } else if (currentSquare instanceof CardSquare) {
                    drawCard(state.getDiceRoll());
                    // if jail
                } else if (currentSquare instanceof Jail && ((Jail) currentSquare).getType() == Jail.JailType.GOTO_JAIL) {
                    System.out.println("Go directly to jail.");
                    currentPlayer.sendToJail();
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
                                printState();
                                // game ended. Winner is opponent
                                playerTurn++;
                                break;
                            }
                            currentPlayer.leaveJail();
                            currentPlayer.removeMoney(50);
                        }
                    }
                }
                // if third double, go to jail
                if (this.state.getValue() == 3) {
                    currentPlayer.sendToJail();
                    state.setState(State.States.NONE);
                    break;
                }
                // go to square
                System.out.print("You rolled a " + roll.value);
                if (roll.isDouble) System.out.print( " (double)");
                Square[] board = state.board.getBoard();
                System.out.println(" and landed on " + board[(currentPlayer.getPosition() + roll.value) % 40].getName());
                currentPlayer.move(roll.value);
                // deal with square's actions
                state.setState(State.States.SQUARE_ACTION);
                break;
        }
    }

    private Player getCurrentPlayer() {
        if (playerTurn % 2 == 1) {
            return state.getPlayerOne();
        } else {
            return state.getPlayerTwo();
        }
    }

    private Player getOpponent() {
        if (playerTurn % 2 == 1) {
            return state.getPlayerTwo();
        } else {
            return state.getPlayerOne();
        }
    }


    private ArrayList<String> SquareListToStringList(ArrayList<Square> list) {
        ArrayList<String> newlist = new ArrayList<String>();
        for (Square item: list) {
            newlist.add(item.getName());
        }
        return newlist;
    }

    private void payTax() {
        Player currentPlayer = getCurrentPlayer();
        Square currentSquare = state.getBoard().getSquare(currentPlayer.getPosition());
        int taxCost = currentSquare.getCost();
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + taxCost + " in tax.");
        if (currentPlayer.getMoney() < taxCost) {
            System.out.println("Insufficient funds to pay tax. " + currentPlayer.getName() + " has lost the game!");
            state.setState(State.States.END);
            printState();
            // game ended. Winner is opponent
            playerTurn++;
            return;
        }
        currentPlayer.removeMoney(taxCost);
    }

    private void drawCard(int roll) {
        Player currentPlayer = getCurrentPlayer();
        CardSquare currentSquare = (CardSquare) state.getBoard().getSquare(currentPlayer.getPosition());
        Cards card = null;
        // get the card
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            card = state.getChance().getCard();
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            card = state.getCommunityChest().getCard();
        }
        System.out.println(card.getText());
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
                break;
            case PLAYER_MONEY:
                // special case
                playerMoney(currentPlayer, getOpponent(), card.getValue());
                break;
            case MOVE:
                currentPlayer.move(card.getTravel());
                state.setState(State.States.SQUARE_ACTION);
                //handleSquareActions(currentPlayer, state.board.getSquare(currentPlayer.getPosition()), roll);
                break;
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
                state.setState(State.States.SQUARE_ACTION);
                //handleSquareActions(currentPlayer, state.board.getSquare(currentPlayer.getPosition()), roll);
                break;
            case STREET_REPAIRS:
                streetRepairs(currentPlayer, card.getHouseCost(), card.getHotelCost());
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

    private void streetRepairs(Player currentPlayer, int houseCost, int hotelCost) {
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
            printState();
            // game ended. Winner is opponent
            playerTurn++;
            return;
        }
        currentPlayer.removeMoney(cost);
        state.setState(State.States.NONE);
    }

    // remove amount from each player and give to the currentPlayer
    private void playerMoney(Player currentPlayer, Player opponent, int amount) {
        if (opponent.getMoney() < amount) {
            System.out.println("Insufficient funds to pay. " + opponent.getName() + " has lost the game!");
            state.setState(State.States.END);
            printState();
            // game ended. no need to change player as winner is the current player (since opponent couldnt pay)
            return;
        }
        opponent.removeMoney(amount);
        currentPlayer.addMoney(amount);
        state.setState(State.States.NONE);
    }

    // skipProperties: 0- skip none, 1 - skip mortgaged, 2 - skip unmortgaged, 3 - skip utilities/stations
    private ArrayList<Square> propertySelect(Player player, int skipProperties) {
        int counter = 0;
        ArrayList<Square> chosenProperties = new ArrayList<Square>();
        for (Square square: player.getProperties()) {
            if (skipProperties == 1 && square.isMortgaged()) continue;
            else if (skipProperties == 2 && !square.isMortgaged()) continue;
            else if (skipProperties == 3 && (square instanceof Utilty || square instanceof Railroad)) continue;
            counter++;
            System.out.println(counter + ")  " + square.getName());
            chosenProperties.add(square);
        }
        return chosenProperties;
    }

    private MonteCarloState getMonteCarloState(){
        // from board calculate properties

        // get player position
        // work out player finances
        // state whether terminal state or not
        // get action list for the state
        // currentPlayer
        return null;
    }

    private void printState() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(state.getPlayerOne());
        players.add(state.getPlayerTwo());
        for (Player player : players) {
            System.out.println("-----------------------------");
            System.out.println("Name: " + player.getName());
            System.out.println("Money: " + player.getMoney());
            System.out.println("Current position: " + state.board.getSquare(player.getPosition()).getName());
            System.out.print("Owned properties: ");
            boolean firstProperty = true;
            for (Square sq: player.getProperties()) {
                if (firstProperty) {
                    System.out.print(sq.getName());
                    firstProperty = false;
                } else {
                    System.out.print(", " + sq.getName());
                }
                if (sq.isMortgaged()) {
                    System.out.print(" [mortgaged]");
                } else if (sq instanceof Property) {
                    int buildings = ((Property) sq).getBuildings();
                    if (buildings > 0) {
                        if (((Property) sq).getBuildings() > 4) {
                            System.out.print(" (hotel)");
                        } else {
                            System.out.print(" (" + buildings + " houses)");
                        }
                    }
                }
            }

            System.out.println();
            if (player.inJail()) System.out.println("In jail.");
            if (player.getNumberGetOutOfJailCards() > 0) System.out.println(player.getNumberGetOutOfJailCards() + " out of jail free cards held.");
            System.out.println("-----------------------------");
        }
    }
}

