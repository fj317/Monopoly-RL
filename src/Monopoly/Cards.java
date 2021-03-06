package Monopoly;

import java.util.Random;

public class Cards {
    private String text;
    private int value;
    private CardAction action;
    private int travelTo;
    private int travel;
    private int houseCost;
    private int hotelCost;
    private boolean outOfJailCardDrawn;
    private final CardType type;

    public enum CardAction {
        BANK_MONEY, PLAYER_MONEY, MOVE, MOVE_TO, STREET_REPAIRS, OUT_JAIL;
    }

    public enum CardType {
        CHANCE, COMMUNITY_CHEST;
    }

    public Cards(CardType type) {
        this.type = type;
        outOfJailCardDrawn = false;
    }

    public Cards(Cards newCard) {
        this.text = newCard.text;
        this.value = newCard.value;
        this.action = newCard.action;
        this.travelTo = newCard.travelTo;
        this.travel = newCard.travel;
        this.houseCost = newCard.houseCost;
        this.hotelCost = newCard.hotelCost;
        this.outOfJailCardDrawn = newCard.outOfJailCardDrawn;
        this.type = newCard.type;
    }

    public Cards getCard() {
        switch (this.type) {
            case CHANCE:
                return getChanceCard();
            case COMMUNITY_CHEST:
                return getCommunityCard();
            default:
                return null;
        }
    }

    private int getRandom() {
        Random random = new Random();
        return random.nextInt(16);
    }

    public CardType getType() {
        return this.type;
    }

    private Cards getCommunityCard() {
        int number = getRandom();
        while (outOfJailCardDrawn && number == 2) {
            number = getRandom();
        }
        switch (number) {
            case 0:
                this.text = "Annuity matures collect £100";
                this.action = CardAction.BANK_MONEY;
                this.value = 100;
                break;
            case 1:
                this.text = "Receieve interest on 7% preference shares collect £25";
                this.action = CardAction.BANK_MONEY;
                this.value = 25;
                break;
            case 2:
                this.text = "Get out of jail free. This card may be kept until needed or sold";
                this.action = CardAction.OUT_JAIL;
                this.outOfJailCardDrawn = true;
                break;
            case 3:
                this.text = "Bank error in your favour collect £200";
                this.value = 200;
                this.action = CardAction.BANK_MONEY;
                break;
            case 4:
                this.text = "Advance to 'GO'";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 0;
                break;
            case 5:
                this.text = "You inherit £100";
                this.value = 100;
                this.action = CardAction.BANK_MONEY;
                break;
            case 6:
                this.text = "You have won second prize in a beauty contest collect £10";
                this.value = 10;
                this.action = CardAction.BANK_MONEY;
                break;
            case 7: // CHANGING RULES HERE MAYBE
                this.text = "Go back to Old Kent Road";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 1;
                break;
            case 8:
                this.text = "Pay hospital £100";
                this.value = -100;
                this.action = CardAction.BANK_MONEY;
                break;
            case 9:
                this.text = "Income tax refund collect £20";
                this.value = 20;
                this.action = CardAction.BANK_MONEY;
                break;
            case 10:
                this.text = "Doctor's fee pay £50";
                this.value = -50;
                this.action = CardAction.BANK_MONEY;
                break;
            case 11: // CHANGING RULES TO JUST PAY FINE RATHER THAN OPTION BETWEEN FINE AND CHANCE
                this.text = "Pay £10 fine";
                this.value = -10;
                this.action = CardAction.BANK_MONEY;
                break;
            case 12:
                this.text = "Go to jail. Move directly to jail. Do not pass 'GO'. Do not collect £200";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 30;
                break;
            case 13:
                this.text = "From sale of stock you get £50";
                this.value = 50;
                this.action = CardAction.BANK_MONEY;
                break;
            case 14:
                this.text = "Pay your insurance premium £50";
                this.value = -50;
                this.action = CardAction.BANK_MONEY;
                break;
            case 15: // swapped chance street repair with birthday card so both had option to get street repairs
                this.text = "You are assessed for street repairs. £40 per house. £115 per hotel.";
                this.action = CardAction.STREET_REPAIRS;
                this.houseCost = 40;
                this.hotelCost = 110;
                break;

        }
        return this;
    }

    private Cards getChanceCard() {
        int number = getRandom();
        while (outOfJailCardDrawn && number == 13) {
            number = getRandom();
        }
        switch (number) {
            case 0:
                this.text = "Pay school fees of £150";
                this.value = -150;
                this.action = CardAction.BANK_MONEY;
                break;
            case 1:
                this.text = "Speeding fine £15";
                this.value = -15;
                this.action = CardAction.BANK_MONEY;
                break;
            case 2:
                this.text = "Advance to Pall Mall. If you pass 'GO' collect £200";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 11;
                break;
            case 3:
                this.text = "Advance to Trafalgar Square. If you pass 'GO' collect £200";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 24;
                break;
            case 4:
                this.text = "Advance to 'GO'";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 0;
                break;
            case 5:
                this.text = "Bank pays you dividend of £50";
                this.action = CardAction.BANK_MONEY;
                this.value = 50;
                break;
            case 6:
                this.text = "Advance to Mayfair";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 39;
                break;
            case 7:
                this.text = "Your building loan matures receieve £150";
                this.action = CardAction.BANK_MONEY;
                this.value = 150;
                break;
            case 8:
                this.text = "Go to jail. Move directly to jail. Do not pass 'GO'. Do not collect £200";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 30;
                break;
            case 9:
                this.text = "Go back three spaces";
                this.action = CardAction.MOVE;
                this.travel = -3;
                break;
            case 10:
                this.text = "You have won a crossword competition collect £100";
                this.action = CardAction.BANK_MONEY;
                this.value = 100;
                break;
            case 11:
                this.text = "'Drunk in charge' fine £20";
                this.action = CardAction.BANK_MONEY;
                this.value = -20;
                break;
            case 12:
                this.text = "Take a trip to Marlebone station and if you pass 'GO' collect £200";
                this.action = CardAction.MOVE_TO;
                this.travelTo = 15;
                break;
            case 13:
                this.text = "Get out of jail free. This card may be kept until needed or sold";
                this.action = CardAction.OUT_JAIL;
                this.outOfJailCardDrawn = true;
                break;
            case 14:
                this.text = "Make general repairs on all of your houses. For each house pay £25. For each hotel pay £100";
                this.action = CardAction.STREET_REPAIRS;
                this.houseCost = 25;
                this.hotelCost = 100;
                break;
            case 15:
                this.text = "It is your birthday collect £10 from each player";
                this.value = 10;
                this.action = CardAction.PLAYER_MONEY;
                break;
        }
        return this;
    }

    void returnOutOfJailCard() {
        this.outOfJailCardDrawn = false;
    }

    public String getText() {
        return this.text;
    }

    public int getValue() {
        return this.value;
    }

    public CardAction getAction() {
        return this.action;
    }

    public int getTravelTo() {
        return this.travelTo;
    }

    public int getTravel() {
        return this.travel;
    }

    public int getHouseCost() {
        return this.houseCost;
    }

    public int getHotelCost() {
        return this.hotelCost;
    }

}
