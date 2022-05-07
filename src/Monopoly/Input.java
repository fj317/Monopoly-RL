package Monopoly;

import java.util.Scanner;
import Player.*;

public class Input {
    private final Scanner scanner;


    public Input() {
        this.scanner = new Scanner(System.in);
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
}
