package Monopoly;

import Player.MonteCarloPlayer;
import Player.Player;
import Player.RandomPolicyPlayer;

public class Tournament {

    // class for testing vs MCTS agent

    private int MCTSWins;
    private int opponentWins;
    private int draws;
    private final int totalMatches;


    public Tournament(int totalMatches) {
        this.MCTSWins = 0;
        this.opponentWins = 0;
        this.draws = 0;
        this.totalMatches = totalMatches;
    }

    private int getTotalMatches() {
        return totalMatches;
    }

    private void getPlayers(State currState) {
        // add human or AI players etc
        // assume that playerOne is RL player
        currState.setPlayerOne(new MonteCarloPlayer());
        currState.setPlayerTwo(new RandomPolicyPlayer("Bob"));
    }


    public static void main(String[] args) {
        Tournament tournament = new Tournament(500);
        System.out.println("Welcome to Monopoly! Starting the game...");
        for (int i = 0; i < tournament.getTotalMatches() ; i++) {
            System.out.println("GAME NO " + i);
            State currState = new State();
            tournament.getPlayers(currState);
            SimplifiedMonopoly.stepNoOutput(currState, 0);
            int inputAction;
            while (!SimplifiedMonopoly.gameFinished(currState)) {
                inputAction = currState.getCurrentPlayer().input(currState);
                SimplifiedMonopoly.stepNoOutput(currState, inputAction);
            }
            Player winner = currState.getWinner();
            System.out.println("Winner was " + winner.getName());
            if (winner == null) {
                tournament.draws++;
            } else if (winner.getName().equals("RL")) {
                tournament.MCTSWins++;
            } else if (winner.getName().equals("Bob")) {
                tournament.opponentWins++;
            }
        }

        System.out.println("---STATISTICS---");
        System.out.println("MCTS WINS: " + tournament.MCTSWins);
        System.out.println("OPPONENT WINS: " + tournament.opponentWins);
        System.out.println("DRAWS: " + tournament.draws);
    }
}
