package Player;

import Monopoly.Cards;
import Monopoly.Property;
import Monopoly.Square;
import Monopoly.State;

import java.util.ArrayList;

// interface so different player classes can be made (human, AI, etc)
public interface Player {
    String getName();

    // money

    int getMoney();

    void removeMoney(int amount);

    void addMoney(int amount);

    // jail

    void sendToJail();

    void leaveJail();

    Boolean inJail();

    int getNumberGetOutOfJailCards();

    Cards.CardType useGetOutOfJailCard();

    boolean stayInJail();

    void addGetOutOfJailCard(Cards.CardType type);

    // movement

    void move(int numberOfSpaces);

    void moveTo(int pos);

    int getPosition();

    // property

    void addProperty(Square square);

    void sellProperty(Square square);

    ArrayList<Square> getProperties();

    ArrayList<Property> getBuildableProperties();

    // input stuff

    int input(State state);




}
