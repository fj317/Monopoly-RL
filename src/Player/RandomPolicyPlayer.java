package Player;

import Monopoly.Cards;
import Monopoly.Property;
import Monopoly.Square;
import Monopoly.State;

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
    private Random rand = new Random();

    public RandomPolicyPlayer(String name) {
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = name;
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
    }

    public RandomPolicyPlayer(RandomPolicyPlayer newPlayer) {
        this.money = newPlayer.money;
        this.properties = new ArrayList<Square>(newPlayer.properties);
        this.position = newPlayer.position;
        this.playerName = newPlayer.playerName;
        this.inJail = newPlayer.inJail;
        this.jailTurn = newPlayer.jailTurn;
        this.numberGetOutOfJailCards = newPlayer.getNumberGetOutOfJailCards();
        this.chanceGetOutOfJailCardHeld = newPlayer.chanceGetOutOfJailCardHeld;
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
        if (position < 0) {
            position += 40;
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

    public ArrayList<Property> getBuildableProperties() {
        ArrayList<Property> buildableProperties = new ArrayList<>();
        for (Square i : getProperties()) {
            if (i instanceof Property) {
                buildableProperties.add((Property) i);
            }
        }
        return buildableProperties;
    }

    // https://gibberblot.github.io/rl-notes/single-agent/mcts.html
    // https://github.com/sorinMD/MCTS/tree/master/src/main/java/mcts


    public int input(State state) {

        int value = rand.nextInt(state.getActionList().size()) + 1;
        //System.out.println(value);
        return value;
    }
}
