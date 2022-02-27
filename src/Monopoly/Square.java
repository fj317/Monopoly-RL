package Monopoly;

import Player.Player;

public interface Square {
        int getPosition();

        String getName();

        boolean isOwnable();

        boolean isOwned();

        boolean isMortgaged();

        int getCost();

        void purchase(Player player);

        int getRent(int data);

        int getMortgageCost();

        Player getOwner();

        int mortgage();
}
