package Monopoly;

import Player.Player;

public interface Square {
        // get player position
        int getPosition();

        // get player name
        String getName();

        // get whether the square is ownable
        boolean isOwnable();

        // get whether the square is owned by a player
        boolean isOwned();

        // get whether the square is mortgaged
        boolean isMortgaged();

        // get the cost of buying the square
        int getCost();

        // purchase the square (update fields with player info)
        void purchase(Player player);

        // get the amount of rent for the square
        int getRent(int data);

        // get the cost of mortgaged the square
        int getMortgageCost();

        // get the square owner
        Player getOwner();

        // mortgage / unmortgage the square and return how much money was spent / received
        int mortgage();
}
