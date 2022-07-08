package Monopoly;

import Player.*;

import java.util.*;

public class Monopoly {

    // THIS IS UNFINISHED MONOPOLY - AGENTS DO NOT INTERACT CORRECTLY WITH THIS ENVIRONMENT, PLEASE USE SimplifiedMonopoly.
    // REQUIRES REWRITING OF THE TICK FUNCTION TO WORK WITH THE MCTS AGENT

    public static void main(String[] args) {
        Monopoly monopoly = new Monopoly();
        monopoly.run();
    }


    private void getPlayers(State currState) {
        // add human or AI players etc
        // assume that playerOne is RL player
        currState.setPlayerOne(new MonteCarloPlayer());
        currState.setPlayerTwo(new RandomPolicyPlayer("Random"));

    }


    private void run() {
        System.out.println("Welcome to Monopoly! Starting the game...");
        State currState = new State();
        getPlayers(currState);
        while (currState.getCurrState() != State.States.END) {
            tick(currState);
        }
        Player winner = currState.getCurrentPlayer();
        System.out.println("THE WINNER IS " + winner.getName());
        System.out.println("WELL DONE!!!");
    }

    public static void tick(State currState) {
        int answer;
        Square currentSquare;
        int propertyCost;
        ArrayList<Square> propertyList;
        Property property;
        Property property1;
        Property property2;
        System.out.println("Current State: " + currState.getCurrState().toString());
        Player currentPlayer = currState.getCurrentPlayer();
        Player opponent = currState.getOpponent();
        // set actionList to 1 null element to initialise
        // needed so that for states where there is no input actions, there is still 1 action outcome to 'take'
        currState.setActionList(Arrays.asList("null"));

        // check if game is too long
        if (currState.getTickNumber() > 10000) {
            currState.setState(State.States.END);
        }

        switch (currState.getCurrState()) {
            case NONE:
                // check if doubles, if so reroll dice
                if (currState.getDoubles()) {
                    currState.setState(State.States.ROLL);
                    break;
                }
                // menu
                System.out.println("Would you like to take any additional actions on this turn?");
                System.out.println("Please select choice");
                System.out.println("1) Buy/sell houses");
                System.out.println("2) Mortgage/unmortgage properties");
                System.out.println("3) Trade with another player");
                System.out.println("4) Nothing - end turn");
                currState.setActionList(Arrays.asList("Buy/Sell House", "Mortgage/Unmortgage property", "Trade", "End turn"));
                answer = currentPlayer.input(currState);
                switch (answer) {
                    case 1:
                        currState.setState(State.States.HOUSE_DECISION);
                        break;
                    case 2:
                        currState.setState(State.States.MORTGAGE_DECISION);
                        break;
                    case 3:
                        currState.setState(State.States.TRADE_PROPERTY_SELECT_1);
                        break;
                    case 4:
                        currState.nextTurn();
                        currState.addOneTick();
                        currState.setState(State.States.TURN);
                        break;
                }
                break;
            case TRADE_PROPERTY_SELECT_1:
                // must have properties
                if (currentPlayer.getProperties().size() == 0) {
                    System.out.println("You do not have any properties to trade.");
                    currState.setState(State.States.NONE);
                    break;
                }
                // check opponent has properties
                if (opponent.getProperties().size() == 0) {
                    System.out.println("The selected trading player does not have any properties to trade.");
                    currState.setState(State.States.NONE);
                    break;
                }

                System.out.println("You own the following properties: ");
                propertyList = propertySelect(currentPlayer, 0);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                currentSquare = propertyList.get(answer);
                currState.addDataSquares(currentSquare);
                currState.setState(State.States.TRADE_PROPERTY_SELECT_2);
                break;
            case TRADE_PROPERTY_SELECT_2:
                System.out.println("Your opponent owns the following properties: ");
                propertyList = propertySelect(opponent, 0);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                currentSquare = propertyList.get(answer);
                currState.addDataSquares(currentSquare);
                currState.setState(State.States.TRADE_CONFIRM);
                break;
            case TRADE_CONFIRM:
                List<Square> tradeSquares = currState.getDataSquares();
                Square propertyOne = tradeSquares.get(0);
                Square propertyTwo = tradeSquares.get(1);
                currState.resetDataSquares();

                // checks if legal trade
                if (propertyOne instanceof Property) {
                    if (((Property) propertyOne).getBuildings() > 0) { // check if property has houses
                        System.out.println(propertyOne.getName() + " has houses/hotels built on it. Please sell them to trade it.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyOne).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                        System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyOne).getGroupPropertyB() != null) { // check if other group property has houses
                        if (((Property) propertyOne).getGroupPropertyB().getBuildings() > 0) {
                            System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                            currState.setState(State.States.NONE);
                            break;
                        }
                    }
                }
                if (propertyTwo instanceof Property) {
                    if (((Property) propertyTwo).getBuildings() > 0) { // check if property has houses
                        System.out.println(propertyTwo.getName() + " has houses/hotels built on it and is untradable at the current moment.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyTwo).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                        System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it .");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (((Property) propertyTwo).getGroupPropertyB() != null) { // check if other group property has houses
                        if (((Property) propertyTwo).getGroupPropertyB().getBuildings() > 0) {
                            System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                            currState.setState(State.States.NONE);
                            break;
                        }
                    }
                }
                System.out.println("You are attempting to trade " + propertyOne.getName() + " for " + propertyTwo.getName() + ". Do you agree with this trade?");
                System.out.print(opponent.getName() + " agree? ");
                currState.setActionList(Arrays.asList("Yes", "No"));
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                answer = opponent.input(currState);
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
                currState.setState(State.States.NONE);
                break;
            case JAIL_TURN:
                System.out.println("You are in jail.");
                System.out.println("Please select choice");
                System.out.println("1) Use cash to get out of jail");
                System.out.println("2) Use 'get out of jail free' card to get out of jail");
                System.out.println("3) Stay in jail");
                currState.setActionList(Arrays.asList("Use cash", "Use card", "Stay"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    // use cash
                    if (currentPlayer.getMoney() > 50) {
                        currentPlayer.removeMoney(50);
                        currentPlayer.leaveJail();
                        System.out.println("You have left jail.");
                    } else {
                        System.out.println("Insufficient funds to leave jail.");
                    }
                } else if (answer == 2) {
                    if (currentPlayer.getNumberGetOutOfJailCards() > 0) {
                        // use the card
                        if (currentPlayer.useGetOutOfJailCard() == Cards.CardType.CHANCE) {
                            currState.getChance().returnOutOfJailCard();
                        } else {
                            currState.getCommunityChest().returnOutOfJailCard();
                        }
                        currentPlayer.leaveJail();
                        System.out.println("You have left jail.");
                    } else {
                        System.out.println("You don't have any Get Out of Jail cards to use!");
                    }
                }
                currState.setState(State.States.ROLL);
                break;
            case MORTGAGE_DECISION:
                System.out.println("Please select choice");
                System.out.println("1) Mortgage");
                System.out.println("2) Unmortgage");
                currState.setActionList(Arrays.asList("Mortgage", "Unmortgage"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    currState.setState(State.States.MORTGAGE_PROPERTY_SELECT);
                } else if (answer == 2) {
                    currState.setState(State.States.UNMORTGAGE_PROPERTY_SELECT);
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
                    currState.setState(State.States.NONE);
                    break;
                }
                System.out.println("Which property would you like to mortgage?");
                System.out.println("You own the following unmortgaged properties: ");
                propertyList = propertySelect(currentPlayer, 1);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                currentSquare = propertyList.get(answer);
                if (currentSquare instanceof Property) {
                    if (((Property) currentSquare).getBuildings() > 0) { // if buildings on property then cant mortgage
                        System.out.println("You cannot mortgage a property while there are buildings on it.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                }
                currState.addDataSquares(currentSquare);
                currState.setState(State.States.MORTGAGE_ACTION);
                break;
            case MORTGAGE_ACTION:
                List<Square> mortgageSquare = currState.getDataSquares();
                currentSquare = mortgageSquare.get(0);
                currState.resetDataSquares();
                System.out.println("Mortgaging " + currentSquare.getName() + " will net £" + (currentSquare.getCost() / 2) + ". Are you sure you want to mortgage the property?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                currState.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    currentPlayer.addMoney(currentSquare.mortgage());
                    System.out.println(currentSquare.getName() + " has been mortgaged!");
                } else if (answer == 2) {
                    System.out.println("Mortgage action cancelled.");
                }
                currState.setState(State.States.NONE);
                break;
            case HOUSE_DECISION:
                if (currentPlayer.getBuildableProperties().size() == 0) {
                    System.out.println("You do not have any properties to buy/sell houses upon.");
                    currState.setState(State.States.NONE);
                    break;
                }
                System.out.println("Please select choice");
                System.out.println("1) Buy houses");
                System.out.println("2) Sell houses");
                currState.setActionList(Arrays.asList("Buy houses", "Sell houses"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    currState.setState(State.States.BUY_HOUSE_PROPERTY_SELECT);
                } else if (answer == 2) {
                    currState.setState(State.States.SELL_HOUSE_PROPERTY_SELECT);
                }
                break;
            case BUY_HOUSE_PROPERTY_SELECT:
                // decide which house to buy on
                propertyList = propertySelect(currentPlayer, 3);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                property = (Property) propertyList.get(answer);
                property1 = property.getGroupPropertyA();
                property2 = property.getGroupPropertyB();
                // do checks to ensure valid property
                // check if max houses
                if (property.getBuildings() == 5) {
                    System.out.println("You cannot buy more houses on " + property.getName());
                    currState.setState(State.States.NONE);
                    break;
                } else if (!property.getMonopolyStatus()) { // check if monopoly status
                    System.out.println("You do not have a monopoly on " + property.getName());
                    currState.setState(State.States.NONE);
                    break;
                } else if (currentPlayer.getMoney() < property.getHouseCost()) { // check if can afford
                    System.out.println("You cannot afford to buy houses on " + property.getName());
                    currState.setState(State.States.NONE);
                    break;
                } else if (property2 == null) {
                    if (property.getBuildings() > property1.getBuildings()) { // check if houses built evenly
                        System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (property.isMortgaged() || property1.isMortgaged()) { // if properties are mortgaged cant build houses
                        System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                        currState.setState(State.States.NONE);
                        break;
                    }

                } else { // check houses are built evenly
                    if (property.getBuildings() > property1.getBuildings() && property.getBuildings() > property2.getBuildings()) {
                        System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                    if (property.isMortgaged() || property1.isMortgaged() || property2.isMortgaged()) { // if properties are mortgaged cant build houses
                        System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                }
                currState.addDataSquares(property);
                currState.setState(State.States.BUY_HOUSE_ACTION);
                break;
            case BUY_HOUSE_ACTION:
                List<Square> buyHouseSquare = currState.getDataSquares();
                currentSquare = buyHouseSquare.get(0);
                currState.resetDataSquares();
                System.out.println("A house/hotel on " + currentSquare.getName() + " will cost £" + ((Property) currentSquare).getHouseCost() + ". Are you sure you want to buy a house/hotel?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                currState.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    ((Property) currentSquare).buildBuilding(1);
                    currentPlayer.removeMoney(((Property) currentSquare).getHouseCost());
                    System.out.println("You now own " + ((Property) currentSquare).getBuildings() + " houses on " + currentSquare.getName());
                } else if (answer == 2) {
                    System.out.println("House buying action cancelled.");
                }
                currState.setState(State.States.NONE);
                break;
            case SELL_HOUSE_PROPERTY_SELECT:
                // decide which house to buy on
                propertyList = propertySelect(currentPlayer, 3);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                property = (Property) propertyList.get(answer);
                property1 = property.getGroupPropertyA();
                property2 = property.getGroupPropertyB();
                // do checks to ensure valid property
                if (property.getBuildings() == 0) {
                    System.out.println("You do not have any houses to sell on " + property.getName());
                    currState.setState(State.States.NONE);
                    break;
                } else if (property2 == null) {
                    if (property.getBuildings() < property1.getBuildings()) {
                        System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                } else {
                    if (property.getBuildings() < property1.getBuildings() && property.getBuildings() < property2.getBuildings()) {
                        System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                        currState.setState(State.States.NONE);
                        break;
                    }
                }
                currState.addDataSquares(property);
                currState.setState(State.States.SELL_HOUSE_ACTION);
                break;
            case SELL_HOUSE_ACTION:
                List<Square> sellHouseSquare = currState.getDataSquares();
                currentSquare = sellHouseSquare.get(0);
                currState.resetDataSquares();
                System.out.println("Selling a house/hotel on " + currentSquare.getName() + " will net £" + (((Property) currentSquare).getHouseCost() / 2) + ". Are you sure you want to sell a house/hotel?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                currState.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    ((Property) currentSquare).buildBuilding(-1);
                    currentPlayer.addMoney(((Property) currentSquare).getHouseCost() / 2);
                    System.out.println("You now own " + ((Property) currentSquare).getBuildings() + " houses on " + currentSquare.getName());
                } else if (answer == 2) {
                    System.out.println("House selling action cancelled.");
                }
                currState.setState(State.States.NONE);
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
                    currState.setState(State.States.NONE);
                    break;
                }

                System.out.println("Which property would you like to unmortgage?");
                System.out.println("You own the following mortgaged properties: ");
                propertyList = propertySelect(currentPlayer, 2);
                currState.setActionList(SquareListToStringList(propertyList));
                answer = currentPlayer.input(currState) - 1;
                currentSquare = propertyList.get(answer);
                currState.addDataSquares(currentSquare);
                currState.setState(State.States.UNMORTGAGE_ACTION);
                break;
            case UNMORTGAGE_ACTION:
                List<Square> unmortgageSquare = currState.getDataSquares();
                currentSquare = unmortgageSquare.get(0);
                currState.resetDataSquares();

                System.out.println("Unmortgaging " + currentSquare.getName() + " will cost £" + (int) Math.round((currentSquare.getCost() / 2.0) * 1.1) + ". Are you sure you want to unmortgage the property?");
                // all actions from now on relate to yes/no
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                currState.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    currentPlayer.removeMoney(currentSquare.mortgage());
                    System.out.println(currentSquare.getName() + " has been unmortgaged!");
                } else if (answer == 2) {
                    System.out.println("Unmortgage action cancelled.");
                }
                currState.setState(State.States.NONE);
                break;
            case UNOWNED_LANDED:
                currentSquare = currState.getBoard().getSquare(currentPlayer.getPosition());
                propertyCost = currentSquare.getCost();
                if (currentPlayer.getMoney() < propertyCost) {
                    System.out.println("Insufficient funds & assets to buy property.");
                    currState.setState(State.States.NONE);
                    break;
                }
                System.out.println("Would you like to purchase " + currentSquare.getName() + " for £" + currentSquare.getCost() + "?");
                System.out.println("Please select choice");
                System.out.println("1) Yes");
                System.out.println("2) No");
                currState.setActionList(Arrays.asList("Yes", "No"));
                answer = currentPlayer.input(currState);
                if (answer == 1) {
                    // player is buying the square
                    currentSquare = currState.getBoard().getSquare(currentPlayer.getPosition());
                    propertyCost = currentSquare.getCost();
                    currentPlayer.removeMoney(propertyCost);

                    currentPlayer.addProperty(currentSquare);
                    currentSquare.purchase(currentPlayer);

                    System.out.println(currentSquare.getName() + " has been purchased by " + currentPlayer.getName());
                } else if (answer == 2) {
                    currState.setState(State.States.NONE);
                }
                break;
            case OWNED_LANDED:
                // landed on a owned property
                currentSquare = currState.getBoard().getSquare(currentPlayer.getPosition());
                int rent = currentSquare.getRent(currState.getDiceRoll());
                System.out.println("You have landed on " + currentSquare.getName() + " and owe " + currentSquare.getOwner().getName() + " £" + rent +" in rent.");
                // if player doesn't have enough  money then lost
                if (currentPlayer.getMoney() < rent) {
                    System.out.println("Insufficient funds to pay rent. " + currentPlayer.getName() + " has lost the game!");
                    currState.setState(State.States.END);
                    printState(currState);
                    // game ended. Winner is opponent
                    currState.nextTurn();
                    break;
                }
                // pay player
                currentPlayer.removeMoney(rent);
                currentSquare.getOwner().addMoney(rent);
                currState.setState(State.States.NONE);
                break;
            case TURN:
                System.out.println("It is " + currentPlayer.getName() + "'s turn.");
                currState.setValue(0);
                // if in jail go to jail state
                if (currentPlayer.inJail()) {
                    currState.setState(State.States.JAIL_TURN);
                } else {
                    currState.setState(State.States.ROLL);
                }
                break;
            case SQUARE_ACTION:
                currentSquare = currState.getBoard().getSquare(currentPlayer.getPosition());
                boolean owned = currentSquare.isOwned();
                boolean ownable = currentSquare.isOwnable();
                // set state to none - if there are changes it will be overwritten
                currState.setState(State.States.NONE);

                // if not owned, and ownable
                if (!owned && ownable) {
                    currState.setState(State.States.UNOWNED_LANDED);
                    // if owned and not mortgaged
                } else if (ownable && !currentSquare.isMortgaged()) {
                    currState.setState(State.States.OWNED_LANDED);
                    // if taxes
                } else if (currentSquare instanceof Taxes) {
                    payTax(currState);
                    // if chance or community chest
                } else if (currentSquare instanceof CardSquare) {
                    drawCard(currState);
                    // if jail
                } else if (currentSquare instanceof Jail && ((Jail) currentSquare).getType() == Jail.JailType.GOTO_JAIL) {
                    System.out.println("Go directly to jail.");
                    currentPlayer.sendToJail();
                }
                break;
            case ROLL:
                // roll the dice
                Dice.Roll roll = currState.getDice().roll();
                currState.setDiceRoll(roll.value);
                if (roll.isDouble) {
                    currState.setDoubles(true);
                    currState.addValue(1);
                } else {
                    currState.setDoubles(false);
                }
                // check if jail and roll was double
                if (currentPlayer.inJail()) {
                    if (roll.isDouble) {
                        System.out.println("You rolled a double and escaped jail!");
                        currentPlayer.leaveJail();
                        // don't reroll
                        currState.setDoubles(false);
                    } else {
                        System.out.println("You didn't roll a double");
                        // if last turn in jail, then have to pay
                        if (!currentPlayer.stayInJail()) {
                            if (currentPlayer.getMoney() < 50) {
                                System.out.println("Insufficient funds to pay jail fee. " + currentPlayer.getName() + " has lost the game!");
                                currState.setState(State.States.END);
                                printState(currState);
                                // game ended. Winner is opponent
                                currState.nextTurn();
                                break;
                            }
                            currentPlayer.leaveJail();
                            currentPlayer.removeMoney(50);
                        }
                    }
                }
                // if third double, go to jail
                if (currState.getValue() == 3) {
                    currentPlayer.sendToJail();
                    currState.setState(State.States.NONE);
                    break;
                }
                // go to square
                System.out.print("You rolled a " + roll.value);
                if (roll.isDouble) System.out.print( " (double)");
                Square[] board = currState.getBoard().getBoard();
                System.out.println(" and landed on " + board[(currentPlayer.getPosition() + roll.value) % 40].getName());
                currentPlayer.move(roll.value);
                // deal with square's actions
                currState.setState(State.States.SQUARE_ACTION);
                break;
        }
    }

    private static ArrayList<String> SquareListToStringList(ArrayList<Square> list) {
        ArrayList<String> newlist = new ArrayList<String>();
        for (Square item: list) {
            newlist.add(item.getName());
        }
        return newlist;
    }

    private static void payTax(State currState) {
        Player currentPlayer = currState.getCurrentPlayer();
        Square currentSquare = currState.getBoard().getSquare(currentPlayer.getPosition());
        int taxCost = currentSquare.getCost();
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + taxCost + " in tax.");
        if (currentPlayer.getMoney() < taxCost) {
            System.out.println("Insufficient funds to pay tax. " + currentPlayer.getName() + " has lost the game!");
            currState.setState(State.States.END);
            printState(currState);
            // game ended. Winner is opponent
            currState.nextTurn();
            return;
        }
        currentPlayer.removeMoney(taxCost);
    }

    private static void drawCard(State currState) {
        Player currentPlayer = currState.getCurrentPlayer();
        CardSquare currentSquare = (CardSquare) currState.getBoard().getSquare(currentPlayer.getPosition());
        Cards card = null;
        // get the card
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            card = currState.getChance().getCard();
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            card = currState.getCommunityChest().getCard();
        }
        System.out.println(card.getText());
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
                break;
            case PLAYER_MONEY:
                // special case
                playerMoney(currentPlayer, currState.getOpponent(), card.getValue(), currState);
                break;
            case MOVE:
                currentPlayer.move(card.getTravel());
                currState.setState(State.States.SQUARE_ACTION);
                break;
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
                currState.setState(State.States.SQUARE_ACTION);
                break;
            case STREET_REPAIRS:
                streetRepairs(currentPlayer, card.getHouseCost(), card.getHotelCost(), currState);
                break;
            case OUT_JAIL:
                currentPlayer.addGetOutOfJailCard(card.getType());
                break;
        }
        // reflect any updates to card object (i.e. jail card taken)
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            currState.setChance(card);
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            currState.setCommunityChest(card);
        }
    }

    private static void streetRepairs(Player currentPlayer, int houseCost, int hotelCost, State currState) {
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
            currState.setState(State.States.END);
            printState(currState);
            // game ended. Winner is opponent
            currState.nextTurn();
            return;
        }
        currentPlayer.removeMoney(cost);
        currState.setState(State.States.NONE);
    }

    // remove amount from each player and give to the currentPlayer
    private static void playerMoney(Player currentPlayer, Player opponent, int amount, State currState) {
        if (opponent.getMoney() < amount) {
            System.out.println("Insufficient funds to pay. " + opponent.getName() + " has lost the game!");
            currState.setState(State.States.END);
            printState(currState);
            // game ended. no need to change player as winner is the current player (since opponent couldnt pay)
            return;
        }
        opponent.removeMoney(amount);
        currentPlayer.addMoney(amount);
        currState.setState(State.States.NONE);
    }

    // skipProperties: 0- skip none, 1 - skip mortgaged, 2 - skip unmortgaged, 3 - skip utilities/stations
    private static ArrayList<Square> propertySelect(Player player, int skipProperties) {
        int counter = 0;
        ArrayList<Square> chosenProperties = new ArrayList<Square>();
        for (Square square: player.getProperties()) {
            if (skipProperties == 1 && square.isMortgaged()) continue;
            else if (skipProperties == 2 && !square.isMortgaged()) continue;
            else if (skipProperties == 3 && (square instanceof Utility || square instanceof Railroad)) continue;
            counter++;
            System.out.println(counter + ")  " + square.getName());
            chosenProperties.add(square);
        }
        return chosenProperties;
    }

    private static void printState(State currState) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(currState.getPlayerOne());
        players.add(currState.getPlayerTwo());
        for (Player player : players) {
            System.out.println("-----------------------------");
            System.out.println("Name: " + player.getName());
            System.out.println("Money: " + player.getMoney());
            System.out.println("Current position: " + currState.getBoard().getSquare(player.getPosition()).getName());
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

