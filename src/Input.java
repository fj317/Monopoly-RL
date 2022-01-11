import java.util.Scanner;

public class Input {
    private final Scanner scanner;


    public Input(Scanner scanner) {
        this.scanner = scanner;
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

    public String inputString() {
        return scanner.nextLine();
    }
}
