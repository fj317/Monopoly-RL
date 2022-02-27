package Monopoly;

import java.util.*;

public class Dice {

    public Roll roll() {
        Random random = new Random();
        Roll roll = new Roll();
        int rollOne = random.nextInt(6) + 1;
        int rollTwo = random.nextInt(6) + 1;
        roll.isDouble = rollOne == rollTwo;
        roll.value = rollOne + rollTwo;
        return roll;
    }

    class Roll {
        public int value;
        public boolean isDouble;
    }
}
