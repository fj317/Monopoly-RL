package Monopoly;

import java.util.Scanner;
import Player.*;

public class Input {
    private final Scanner scanner;


    public Input() {
        this.scanner = new Scanner(System.in);
    }

    public int inputDecision(String[] choices) {
        while (true) {
            String input = inputString();
            for (int i = 0; i < choices.length; i++) {
                if (input.equalsIgnoreCase(choices[i])) {
                    return i;
                }
            }
            System.out.println("Enter a valid decision.");
        }
    }

    public boolean inputBool() {
        // will return true if yes, false if no
        return  inputDecision(new String[] {"Yes", "No"}) == 0;
    }

    public int inputInt() {
        while (true) {
            int number;
            try {
                number = Integer.parseInt(inputString());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
                continue;
            }
            return number;
        }
    }

    public String inputString() {
        return scanner.nextLine();
    }

    public Player inputPlayer(Iterable<Player> players, Player notAllowed) {
        Player player = null;
        do {
            String name = inputString();
            for (Player p : players) {
                if (name.equals(p.getName()))
                    player = p;
            }
            if (player == null)
                System.out.println("Invalid player, please enter another name.");

            else if (notAllowed != null && player.getName().equals(notAllowed.getName())) {
                System.out.println("You may not select this player. Choose another.");
                player = null;
            }
        } while (player == null);
        return player;
    }
}
