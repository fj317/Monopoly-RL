package Monopoly;

import Player.HumanPlayer;
import Player.Player;
import Player.RandomPolicyPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class Monopoly {
    private final Dice dice;
    private Cards chance;
    private Cards communityChest;
    private State state;

    private Monopoly() {
        this.dice = new Dice();
        this.chance = new Cards(Cards.CardType.CHANCE);
        this.communityChest = new Cards(Cards.CardType.COMMUNITY_CHEST);
        this.state = new State();
        // get players
        getPlayers();
    }

    private void getPlayers() {
        int totalPlayers = 2;
        // add human or AI players etc
        state.players.add(new HumanPlayer("A"));
        state.players.add(new HumanPlayer("B"));
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
                state.currentPlayer = state.players.peek();
                // take their turn
                turn();
                // remove player from queue
                state.players.remove();
                // add player to end of queue now
                state.players.add(state.currentPlayer);
            } catch (NoSuchElementException e) {
                System.out.println("NoSuchElementException error.");
                return;
            } finally {
                printState();
            }
        }

        Player winner = state.players.remove();
        System.out.println("THE WINNER IS " + winner.getName());
        System.out.println("WELL DONE!!!");
    }

    private void turn() {
        System.out.println("It's " + state.currentPlayer.getName() + "'s turn.");
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
                    } else if (state.currentPlayer.getNumberGetOutOfJailCards() > 0) {
                        // use the card
                        if (state.currentPlayer.useGetOutOfJailCard() == Cards.CardType.CHANCE) {
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
            if (roll.isDouble) System.out.print( " (double)");
            Square[] board = state.board.getBoard();
            System.out.println(" and landed on " + board[(state.currentPlayer.getPosition() + roll.value) % 40].getName());
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
            drawCard(currentPlayer, (CardSquare) currentSquare, roll);
            // if jail
        } else if (currentSquare instanceof Jail && ((Jail) currentSquare).getType() == Jail.JailType.GOTO_JAIL) {
            toJail(currentPlayer);
        }
    }

    private void unowned(Player currentPlayer, Square currentSquare) {
        int propertyCost = currentSquare.getCost();
        if (currentPlayer.getMoney() < propertyCost) {
            System.out.println("Insufficient funds & assets to buy property.");
            // auction property
            Player auctionWinner = auction(currentPlayer, currentSquare);
            if (auctionWinner != null) {
                purchase(auctionWinner, currentSquare);
            } else {
                System.out.println(currentSquare.getName() + " was not bought by any player.");
            }
            return;
        }
        System.out.println("Would you like to purchase " + currentSquare.getName() + " for " + currentSquare.getCost() + " (Yes/No)?");
        state.action = State.StateActions.PURCHASE;
        if (currentPlayer.getMoney() < propertyCost) {
            // more money needed to buy so sell assets
            System.out.println("Additional funds required to buy.");
            int lost = additionalFunds(propertyCost, currentPlayer);
            if (lost == 1) {
                Player squareOwner = currentSquare.getOwner();
                lose(currentPlayer, squareOwner);
            }
        }
        if (currentPlayer.inputBool(state)) {
            currentPlayer.removeMoney(propertyCost);
            purchase(currentPlayer, currentSquare);
            System.out.println(currentSquare.getName() + " has been purchased by " + currentPlayer.getName());
        } else {
            Player auctionWinner = auction(currentPlayer, currentSquare);
            if (auctionWinner != null) {
                purchase(auctionWinner, currentSquare);
            } else {
                System.out.println(currentSquare.getName() + " was not bought by any player.");
            }
        }
    }

    private void purchase (Player currentPlayer, Square currentSquare) {
        currentPlayer.addProperty(currentSquare);
        currentSquare.purchase(currentPlayer);
    }

    // return the winner of the auction
    private Player auction(Player currentPlayer, Square currentSquare) {
        System.out.println("Auctioning off " + currentSquare.getName());
        int currentBid = 0;
        final int bidIncrement = 10;
        int minimumBid = currentBid;
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
            if (currentAuctionPlayer.inputBool(state)) {
                System.out.println("Enter your bid: ");
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
    }

    private int additionalFunds(int cost, Player currentPlayer) {
        int playerBalance = currentPlayer.getMoney();
        System.out.println("You require £" + cost + " in additional funds. ");
        // if player doesn't have enough money with all assets then return 0 (fail)
        int totalAssets = getTotalAssets(currentPlayer);
        if (totalAssets < cost) {
            return 1;
        }
        // otherwise ...
        while (playerBalance < cost) {
            System.out.println("Current balance: £" + playerBalance + ". Required funds: £" + cost + ".");
            System.out.println("Do you want to mortgage or sell houses to acquire the funds?");
            int choice = currentPlayer.inputDecision(state, new String[]{"Mortgage", "Sell Houses"});
            if (choice == 0) {
                state.action = State.StateActions.MORTGAGE;
                mortgage(currentPlayer);
            } else if (choice == 1) {
                state.action = State.StateActions.SELL_HOUSE;
                sellHouses(currentPlayer);
            }
            // update players balance
            playerBalance = currentPlayer.getMoney();
        }
        return 0;
    }

    private int getTotalAssets(Player currentPlayer) {
        // get assets -> gets total player balance, total value of mortgaging every property, selling all houses/hotels
        int totalAssets = currentPlayer.getMoney();
        ArrayList<Square> properties = currentPlayer.getProperties();
        for (Square sq : properties) {
            if (!sq.isMortgaged()) {
                // add mortgage cost of each owned square to total assets
                totalAssets += sq.getMortgageCost();
                // if property then add possible house costs
                if (sq instanceof Property) {
                    totalAssets += ((Property) sq).getBuildings() * ((Property) sq).getHouseCost() / 2;
                }
            }
        }
        return totalAssets;
    }

    private void lose(Player loserPlayer, Player winningPlayer) {
//        // add properties to winner
//        for (Square sq: loserPlayer.getProperties()) {
//            winningPlayer.addProperty(sq);
//        }
//        // add money to winner
//        winningPlayer.addMoney(loserPlayer.getMoney());
//        // add get out of jail card
//        while (loserPlayer.getNumberGetOutOfJailCards() > 0) {
//            winningPlayer.addGetOutOfJailCard(loserPlayer.useGetOutOfJailCard());
//        }
        // if lost remove player from players
        state.players.remove();
        System.out.println(loserPlayer.getName() + " has lost!");
        System.out.println(state.players.remove().getName() + " has won!");
        printState();
        System.exit(0);

    }

    private void owned(Player currentPlayer, Square currentSquare, int roll) {
        int rent = currentSquare.getRent(roll);
        Player squareOwner = currentSquare.getOwner();
        // if land on own property, no cost
        if (currentPlayer == squareOwner) return;
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + squareOwner.getName() + " £" + rent +" in rent.");
        if (currentPlayer.getMoney() < rent) {
            System.out.println("Additional funds required to pay rent.");
            int lost = additionalFunds(rent, currentPlayer);
            if (lost == 1) {
                lose(currentPlayer, squareOwner);
            }
        }
        currentPlayer.removeMoney(rent);
        squareOwner.addMoney(rent);

    }

    private void payTax(Player currentPlayer, Square currentSquare) {
        int taxCost = currentSquare.getCost();
        System.out.println("You have landed on " + currentSquare.getName() + " and owe " + taxCost + " in tax.");
        if (currentPlayer.getMoney() < taxCost) {
            System.out.println("Additional funds required to pay tax.");
            int lost = additionalFunds(taxCost, currentPlayer);
            if (lost == 1) {
                lose(currentPlayer, null);
            }
        }
        currentPlayer.removeMoney(taxCost);
    }

    private void drawCard(Player currentPlayer, CardSquare currentSquare, int roll) {
        Cards card = null;
        // get the card
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            card = chance.getCard();
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            card = communityChest.getCard();
        }
        System.out.println(card.getText());
        switch (card.getAction()) {
            case BANK_MONEY:
                currentPlayer.addMoney(card.getValue());
                break;
            case PLAYER_MONEY:
                // special case
                playerMoney(currentPlayer, card.getValue());
                break;
            case MOVE:
                currentPlayer.move(card.getTravel());
                handleSquareActions(currentPlayer, state.board.getSquare(currentPlayer.getPosition()), roll);
                break;
            case MOVE_TO:
                currentPlayer.moveTo(card.getTravelTo());
                handleSquareActions(currentPlayer, state.board.getSquare(currentPlayer.getPosition()), roll);
                break;
            case STREET_REPAIRS:
                streetRepairs(currentPlayer, card.getHouseCost(), card.getHotelCost());
                break;
            case OUT_JAIL:
                currentPlayer.addGetOutOfJailCard(card.getType());
                break;
        }
        // reflect any updates to card object
        if (currentSquare.getType() == Cards.CardType.CHANCE) {
            chance = card;
        } else if (currentSquare.getType() == Cards.CardType.COMMUNITY_CHEST) {
            communityChest = card;
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
        System.out.println("Would you like to sell houses?");
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
        System.out.println("Who would you like to trade with?");
        state.action = State.StateActions.TRADE;
        Player tradingPlayer = currentPlayer.inputPlayer(state, currentPlayer);
        // select a property to trade
        if (currentPlayer.getProperties().size() == 0) {
            System.out.println("You do not have any properties to trade.");
            return;
        } else if (tradingPlayer.getProperties().size() == 0) {
            System.out.println("The selected trading player does not have any properties to trade.");
            return;
        }
        Square propertyOne = propertySelect(currentPlayer, 0, currentPlayer);
        // select tradingPlayer property
        Square propertyTwo = propertySelect(currentPlayer, 0, tradingPlayer);
        if (propertyOne instanceof Property) {
            if (((Property) propertyOne).getBuildings() > 0) { // check if property has houses
                System.out.println(propertyOne.getName() + " has houses/hotels built on it. Please sell them to trade it.");
                return;
            }
            if (((Property) propertyOne).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                return;
            }
            if (((Property) propertyOne).getGroupPropertyB() != null) { // check if other group property has houses
                if (((Property) propertyOne).getGroupPropertyB().getBuildings() > 0) {
                    System.out.println("One of " + propertyOne.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                    return;
                }
            }
        }
        if (propertyTwo instanceof Property) {
            if (((Property) propertyTwo).getBuildings() > 0) { // check if property has houses
                System.out.println(propertyTwo.getName() + " has houses/hotels built on it and is untradable at the current moment.");
                return;
            }
            if (((Property) propertyTwo).getGroupPropertyA().getBuildings() > 0) { // check if other group property has houses
                System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it .");
                return;
            }
            if (((Property) propertyTwo).getGroupPropertyB() != null) { // check if other group property has houses
                if (((Property) propertyTwo).getGroupPropertyB().getBuildings() > 0) {
                    System.out.println("One of " + propertyTwo.getName() + "'s group properties has buildings on it. Please sell them to trade this property.");
                    return;
                }
            }
        }
        System.out.println("You are attempting to trade " + propertyOne.getName() + " for " + propertyTwo.getName() + ". Do you agree with this trade?");
        System.out.print(tradingPlayer.getName() + " agree? ");
        boolean tradingInput = tradingPlayer.inputBool(state);
        // if both agree
        if (tradingInput) {
            // perform deal
            propertyOne.purchase(tradingPlayer);
            currentPlayer.sellProperty(propertyOne);
            tradingPlayer.addProperty(propertyOne);
            propertyTwo.purchase(currentPlayer);
            tradingPlayer.sellProperty(propertyTwo);
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
            System.out.println("Trade successfully completed.");
        } else {
            System.out.println(tradingPlayer.getName() + " did not agree to the trade terms");
        }
    }

    private void buyHouses(Player currentPlayer) {
        System.out.println(currentPlayer.getProperties().size());
        if (currentPlayer.getBuildableProperties().size() == 0) {
            System.out.println("You do not have any properties to buy houses upon.");
            return;
        }
        do {
            System.out.println("Which property would you like to buy houses on?");
            Property property = (Property) propertySelect(currentPlayer, 3, currentPlayer);
            Property property1 = property.getGroupPropertyA();
            Property property2 = property.getGroupPropertyB();
            // check if max houses
            if (property.getBuildings() == 5) {
                System.out.println("You cannot buy more houses on " + property.getName());
                System.out.println("Would you like to buy additional houses?");
                continue;
            } else if (!property.getMonopolyStatus()) { // check if monopoly status
                System.out.println("You do not have a monopoly on " + property.getName());
                System.out.println("Would you like to buy additional houses?");
                continue;
            } else if (currentPlayer.getMoney() < property.getHouseCost()) { // check if can afford
                System.out.println("You cannot afford to buy houses on " + property.getName());
                System.out.println("Would you like to buy additional houses?");
                continue;
            } else if (property2 == null) {
                if (property.getBuildings() > property1.getBuildings()) { // check if houses built evenly
                    System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                    System.out.println("Would you like to buy additional houses?");
                    continue;
                }
                if (property.isMortgaged() || property1.isMortgaged()) { // if properties are mortgaged cant build houses
                    System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                    System.out.println("Would you like to buy additional houses?");
                    continue;
                }

            } else { // check houses are built evenly
                if (property.getBuildings() > property1.getBuildings() && property.getBuildings() > property2.getBuildings()) {
                    System.out.println("You cannot build houses on this property as houses must be built evenly across a group's properties.");
                    System.out.println("Would you like to buy additional houses?");
                    continue;
                }
                if (property.isMortgaged() || property1.isMortgaged() || property2.isMortgaged()) { // if properties are mortgaged cant build houses
                    System.out.println("You cannot build houses while one of the group's properties are mortgaged.");
                    System.out.println("Would you like to buy additional houses?");
                    continue;
                }
            }
            System.out.println("A house/hotel on " + property.getName() + " will cost £" + property.getHouseCost() + ". Are you sure you want to buy a house/hotel?");
            if (currentPlayer.inputBool(state)) {
                property.buildBuilding(1);
                currentPlayer.removeMoney(property.getHouseCost());
                System.out.println("You now own " + property.getBuildings() + " houses on " + property.getName());
            } else {
                System.out.println("House buying action cancelled.");
            }
            System.out.println("Would you like to buy additional houses?");
        } while (currentPlayer.inputBool(state));
    }

    private void sellHouses(Player currentPlayer) {
        if (currentPlayer.getBuildableProperties().size() == 0) {
            System.out.println("You do not have any properties to sell houses upon.");
            return;
        }
        do {
            System.out.println("Which property would you like to sell houses on?");
            Property property = (Property) propertySelect(currentPlayer, 3, currentPlayer);
            Property property1 = property.getGroupPropertyA();
            Property property2 = property.getGroupPropertyB();
            if (property.getBuildings() == 0) {
                System.out.println("You do not have any houses to sell on " + property.getName());
                System.out.println("Would you like to sell additional houses?");
                continue;
            } else if (property2 == null) {
                if (property.getBuildings() < property1.getBuildings()) {
                    System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                    System.out.println("Would you like to sell additional houses?");
                    continue;
                }
            } else {
                if (property.getBuildings() < property1.getBuildings() && property.getBuildings() < property2.getBuildings()) {
                    System.out.println("You cannot sell houses on this property as houses must be sold evenly across a group's properties.");
                    System.out.println("Would you like to sell additional houses?");
                    continue;
                }
            }
            System.out.println("Selling a house/hotel on " + property.getName() + " will net £" + (property.getHouseCost() / 2) + ". Are you sure you want to sell a house/hotel?");
            if (currentPlayer.inputBool(state)) {
                property.buildBuilding(-1);
                currentPlayer.addMoney(property.getHouseCost() / 2);
                System.out.println("You now own " + property.getBuildings() + " houses on " + property.getName());
            } else {
                System.out.println("House selling action cancelled.");
            }
            System.out.println("Would you like to sell additional houses?");
        } while (currentPlayer.inputBool(state));
    }

    private void mortgage(Player currentPlayer) {
        do {
            boolean unmortgagedProperty = false;
            for (Square sq: currentPlayer.getProperties()) {
                if (!sq.isMortgaged()) {
                    unmortgagedProperty = true;
                }
            }
            if (!unmortgagedProperty) {
                System.out.println("You do not have any unmortgaged properties.");
                return;
            }
            System.out.println("Which property would you like to mortgage?");
            System.out.println("You own the following unmortgaged properties: ");
            Square squareToMortgage = propertySelect(currentPlayer, 1, currentPlayer);
            if (squareToMortgage instanceof Property) {
                if (((Property) squareToMortgage).getBuildings() > 0) { // if buildings on property then cant mortgage
                    System.out.println("You cannot mortgage a property while there are buildings on it.");
                    System.out.println("Would you like to mortgage additional properties?");
                    continue;
                }
            }
            System.out.println("Mortgaging " + squareToMortgage.getName() + " will net £" + (squareToMortgage.getCost() / 2) + ". Are you sure you want to mortgage the property?");
            if (currentPlayer.inputBool(state)) {
                currentPlayer.addMoney(squareToMortgage.mortgage());
                System.out.println(squareToMortgage.getName() + " has been mortgaged!");
            } else {
                System.out.println("Mortgage action cancelled.");
            }
            System.out.println("Would you like to mortgage additional properties?");
        } while (currentPlayer.inputBool(state));
    }

    private void unmortgage(Player currentPlayer) {
        do {
            boolean mortgagedProperty = false;
            for (Square sq: currentPlayer.getProperties()) {
                if (sq.isMortgaged()) {
                    mortgagedProperty = true;
                }
            }
            if (!mortgagedProperty) {
                System.out.println("You do not have any mortgaged properties.");
                return;
            }
            System.out.println("Which property would you like to unmortgage?");
            System.out.println("You own the following mortgaged properties: ");
            Square squareToUnmortgage = propertySelect(currentPlayer, 2, currentPlayer);
            System.out.println("Unmortgaging " + squareToUnmortgage.getName() + " will cost £" + (int) Math.round((squareToUnmortgage.getCost() / 2.0) * 1.1) + ". Are you sure you want to unmortgage the property?");
            if (currentPlayer.inputBool(state)) {
                currentPlayer.removeMoney(squareToUnmortgage.mortgage());
                System.out.println(squareToUnmortgage.getName() + " has been unmortgaged!");
            } else {
                System.out.println("Unmortgage action cancelled.");
            }
            System.out.println("Would you like to unmortgage additional properties?");
        } while (currentPlayer.inputBool(state));
    }

    // skipProperties: 0- skip none, 1 - skip mortgaged, 2 - skip unmortgaged, 3 - skip utilities/stations
    private Square propertySelect(Player currentPlayer, int skipProperties, Player playerProperties) {
        int counter = 0;
        ArrayList<Square> chosenProperties = new ArrayList<Square>();
        for (Square square: playerProperties.getProperties()) {
            if (skipProperties == 1 && square.isMortgaged()) continue;
            else if (skipProperties == 2 && !square.isMortgaged()) continue;
            else if (skipProperties == 3 && (square instanceof Utilty || square instanceof Railroad)) continue;
            counter++;
            System.out.println(counter + ")  " + square.getName());
            chosenProperties.add(square);
        }
        int input;
        state.value = counter;
        do {
            input = currentPlayer.inputInt(state);
        } while (input >= counter && input < 0);
        return chosenProperties.get(input - 1);
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
            // more money needed to buy so sell assets
            System.out.println("Additional funds required to buy.");
            int lost = additionalFunds(cost, currentPlayer);
            if (lost == 1) {
                lose(currentPlayer, null);
            }
        }
        currentPlayer.removeMoney(cost);
    }

    private void playerMoney(Player currentPlayer, int amount) {
        Queue<Player> players = new LinkedList<>(state.getPlayers());
        // remove first player as this is player whos turn it is
        players.remove();
        for (Player person: players) {
            if (person.getMoney() < amount) {
                int lost = additionalFunds(amount, person);
                if (lost == 1) {
                    lose(person, currentPlayer);
                }
            }
            person.removeMoney(amount);
            currentPlayer.addMoney(amount);
        }
    }

    private void printState() {
        for (Player player : state.getPlayers()) {
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

