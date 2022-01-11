import java.util.ArrayList;

// interface so different player classes can be made (human, AI, etc)
interface Player {
    int position();
    int money();
    String playerName();
    ArrayList<Square> properties();

    String getName();

    // money

    int getMoney();

    void removeMoney(int amount);

    void addMoney(int amount);

    // jail

    void sendToJail();

    void leaveJail();

    Boolean inJail();

    int numberGetOutOfJailCards();

    // return chance = 0, community = 1
    int useGetOutOfJailCard();

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



    // input stuff

    boolean inputBool(State state);

    int inputInt(State state);

    int inputDecision(State state, String[] choices);




}
