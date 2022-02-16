import java.util.LinkedList;
import java.util.Queue;

public class State {
    public Board board;
    public Player currentPlayer;
    public int diceRoll;
    public StateActions action;
    public Queue<Player> players;
    public int value;

    public State() {
        this.players = new LinkedList<>();
        this.currentPlayer = null;
        this.action = StateActions.NONE;
        this.board = new Board();
        this.value = 0;
    }

    public Queue<Player> getPlayers() {
        return this.players;
    }

    public enum StateActions {
        NONE, BUY_JAIL, CASH_CARD_JAIL, BUY_HOUSE, SELL_HOUSE, MORTGAGE, UNMORTGAGE, TRADE, PURCHASE, AUCTION, OTHER
    }
}
