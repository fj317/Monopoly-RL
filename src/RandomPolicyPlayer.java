import java.util.*;

public class RandomPolicyPlayer implements Player {
    private final ArrayList<Square> properties;
    private final String playerName;
    private int money;
    private int position;
    private boolean inJail;
    private int jailTurn;
    private int numberGetOutOfJailCards;
    private boolean chanceGetOutOfJailCardHeld;

    public RandomPolicyPlayer() {
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = "Random Policy Agent";
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
    }

    public String getName() {
        return playerName;
    }

    public int getMoney() {
        return money;
    }

    public void removeMoney(int amount) {
        this.money -= amount;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public void sendToJail() {
        inJail = true;
        moveTo(10);
        jailTurn = 0;
    }

    public void leaveJail() {
        inJail = false;
        moveTo(10);
    }

    public Boolean inJail() {
        return this.inJail;
    }

    public int getNumberGetOutOfJailCards() {
        return numberGetOutOfJailCards;
    }

    public Cards.CardType useGetOutOfJailCard() {
        if (numberGetOutOfJailCards < 1) {
            throw new RuntimeException("You do not have any get out of jail cards!");
        }
        numberGetOutOfJailCards--;
        if (chanceGetOutOfJailCardHeld) {
            chanceGetOutOfJailCardHeld = false;
            return Cards.CardType.CHANCE;
        } else {
            return  Cards.CardType.COMMUNITY_CHEST;
        }
    }

    public boolean stayInJail() {
        jailTurn++;
        return jailTurn != 3;
    }

    public void addGetOutOfJailCard(Cards.CardType type) {
        numberGetOutOfJailCards++;
        if (type == Cards.CardType.CHANCE) {
            chanceGetOutOfJailCardHeld = true;
        }
    }

    public void move(int numberOfSpaces) {
        position += numberOfSpaces;
        final int boardSize = 40;
        // if pass go, add 200 and make sure correct position
        if (position >= boardSize) {
            position = position % boardSize;
            addMoney(200);
        }
    }

    public void moveTo(int pos) {
        // check if pass GO on way
        if (pos < position) {
            addMoney(200);
        }
        position = pos;
    }

    public int getPosition() {
        return position;
    }

    public void addProperty(Square square) {
        properties.add(square);
    }

    public void sellProperty(Square square) {
        properties.remove(square);
    }

    public ArrayList<Square> getProperties() {
        return properties;
    }

    // use cash/card to get out of jail, buy property (or auction it), bidding in auction, buying/selling houses, mortgaging/unmortgating houses, agreeing to trade
    public boolean inputBool(State state) {
        Random rand = new Random();
        boolean decision =  rand.nextBoolean();
        System.out.println(decision);
        return decision;
//        switch (state.action) {
//            case BUY_JAIL:
//                return rand.nextBoolean();
//            case PURCHASE:
//                return false;
//            case AUCTION:
//                return false;
//            case BUY_HOUSE:
//                return false;
//            case SELL_HOUSE:
//                return false;
//            case MORTGAGE:
//                return false;
//            case UNMORTGAGE:
//                return false;
//            case TRADE:
//                return false;
//            default:
//                return false;
//        }
    }

    // selecting action, selecting property, bidding in auctions,
    public int inputInt(State state) {
        Random rand = new Random();
        int decision = 0;
        switch (state.action) {
            case OTHER:
                // selecting initial action, options: 1-3
                do {
                    decision = rand.nextInt(5) + 1;
                } while (decision == 4);
                System.out.println(decision);
                return decision;
            case AUCTION:
                // bid some random amount more than the minimum
                decision = rand.nextInt(money - state.value) + state.value;
                System.out.println(decision);
                return decision;
            default:
                // selecting property
                // value gives max range
                decision = rand.nextInt(state.value) + 1;
                System.out.println(decision);
                return decision;
        }
    }

    // choosing between options (cash/card jail, mortgage/sell houses when out of money)
    public int inputDecision(State state, String[] choices) {
        Random rand = new Random();
        int decision = rand.nextInt(choices.length);
        System.out.println(decision);
        return decision;
    }

    // for trading, pick a player
    public Player inputPlayer(State state, Player notPickable) {
        Random rand = new Random();
        Queue<Player> inputPlayers = new LinkedList<>(state.getPlayers());
        inputPlayers.remove();
        int chosenPlayer = rand.nextInt(inputPlayers.size());
        for (int i = 0; i < chosenPlayer; i++) {
            inputPlayers.remove();
        }
        Player decision = inputPlayers.remove();
        System.out.println(decision.getName());
        return decision;
    }
}
