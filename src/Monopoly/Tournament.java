package Monopoly;

import Player.*;

public class Tournament {

    // class for testing vs MCTS agent

    private int playerOneWins;
    private int playerTwoWins;
    private int draws;
    private final int totalMatches;


    public Tournament(int totalMatches) {
        this.playerOneWins = 0;
        this.playerTwoWins = 0;
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
        Tournament tournament = new Tournament(20);
        System.out.println("Welcome to Monopoly! Starting the game...");
        for (int i = 0; i < tournament.getTotalMatches() ; i++) {
            System.out.println("GAME NO " + i);
            long startTime = System.nanoTime();
            State currState = new State();
            tournament.getPlayers(currState);
            SimplifiedMonopoly.stepNoOutput(currState, 0);
            int inputAction;
            while (!SimplifiedMonopoly.gameFinished(currState)) {
                inputAction = currState.getCurrentPlayer().input(currState);
                SimplifiedMonopoly.stepNoOutput(currState, inputAction);
            }
            long endTime = System.nanoTime();
            long timeTaken = (endTime - startTime) / 1000000000;
            System.out.println("Time taken: " + timeTaken);
            System.out.println("TURN NUMBER: " + currState.getTickNumber());
            Player winner = currState.getWinner();
            System.out.println("Winner was " + winner.getName());
            if (winner == null) {
                tournament.draws++;
            } else if (winner.getName().equals(currState.getPlayerOne().getName())) {
                tournament.playerOneWins++;
            } else if (winner.getName().equals(currState.getPlayerTwo().getName())) {
                tournament.playerTwoWins++;
            }
        }

        System.out.println("---STATISTICS---");
        System.out.println("PlayerOne WINS: " + tournament.playerOneWins);
        System.out.println("PlayerTwo WINS: " + tournament.playerTwoWins);
        System.out.println("DRAWS: " + tournament.draws);
    }
}
