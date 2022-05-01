package Player;

import Monopoly.*;

import java.util.ArrayList;

public class HumanPlayer implements Player {
    private final Input input;
    private final ArrayList<Square> properties;
    private final String playerName;
    private int money;
    private int position;
    private boolean inJail;
    private int jailTurn;
    private int numberGetOutOfJailCards;
    private boolean chanceGetOutOfJailCardHeld;

    public HumanPlayer(String name) {
        input = new Input();
        money = 1500;
        properties = new ArrayList<>();
        position = 0;
        this.playerName = name;
        inJail = false;
        numberGetOutOfJailCards = 0;
        chanceGetOutOfJailCardHeld = false;
    }

    public String getName() {
        return this.playerName;
    }

    public ArrayList<Square> getProperties() {
        return this.properties;
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

    public int getMoney() {
        return this.money;
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
        return this.numberGetOutOfJailCards;
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

    public int input(State state) {
        int answer;
        do {
            answer = input.inputInt();
        } while (answer < 1 || answer > state.getActionList().size());
        return answer;
    }
}
