package Monopoly;

public class Board {
    private Square[] board = new Square[40];


    public Board() {
        for (int i = 0; i < 40; i++ ) {
            this.board[i] = makeSquares(i);
        }
        makeGroups();
    }

    public Board(Board newBoard) {
        this.board = newBoard.board;
    }

    public Square[] getBoard() {
        return this.board;
    }

    public Square getSquare(int pos) {
        return board[pos];
    }

    private Square makeSquares(int position) {
        switch (position) {
            case 0:
                // go
                return new OtherSquares(position, "Go");
            case 1:
                return new Property("Old Kent Road", 60, position, 10, 50, 10, 30, 90, 160, 250);
            case 2:
                // community chest
                return new CardSquare("Community Chest", position, Cards.CardType.COMMUNITY_CHEST);
            case 3:
                return new Property("Whitechapel Road", 60, position, 4, 50, 20, 60, 180, 320, 450);
            case 4:
                // income tax
                return new Taxes("Income Tax", position, 200);
            case 5:
                // kings cross
                return new Railroad(position, "Kings Cross Station");
            case 6:
                return new Property("The Angel, Islington", 100, position, 6, 50, 30, 90, 270, 400, 550);
            case 7:
                // chance
                return new CardSquare("Chance", position, Cards.CardType.CHANCE);
            case 8:
                return new Property("Euston Road", 100, position, 6, 50, 30, 90, 270, 400, 550);
            case 9:
                return new Property("Pentonville Road", 120, position, 8, 50, 40, 100, 300, 450, 600);
            case 10:
                // jail
                return new Jail(position, "Just Visiting Jail", Jail.JailType.VISITING);
            case 11:
                return new Property("Pall Mall", 140, position, 10, 100, 50, 150, 450, 625, 750);
            case 12:
                // electric company
                return new Utilty(position, "Electric Company", 10);
            case 13:
                return new Property("Whitehall", 140, position, 10, 100, 50, 150, 450, 625, 750);
            case 14:
                return new Property("Northumberl'd Avenue", 160, position, 12, 100, 60, 180, 500, 700, 900);
            case 15:
                // marylebone station
                return new Railroad(position, "Marylebone Station");
            case 16:
                return new Property("Bow Street", 180, position, 14, 100, 70, 200, 550, 750, 950);
            case 17:
                // community chest
                return new CardSquare("Community Chest", position, Cards.CardType.COMMUNITY_CHEST);
            case 18:
                return new Property("Marlborough Street", 180, position, 14, 100, 70, 200, 550, 750, 950);
            case 19:
                return new Property("Vine Street", 200, position, 16, 100, 80, 220, 600, 800, 1000);
            case 20:
                // freeparking
                return new OtherSquares(position, "Free Parking");
            case 21:
                return new Property("The Strand", 220, position, 18, 150, 90, 250, 700, 875, 1050);
            case 22:
                // chance
                return new CardSquare("Chance", position, Cards.CardType.CHANCE);
            case 23:
                return new Property("Fleet Street", 220, position, 18, 150, 90, 250, 700, 875, 1050);
            case 24:
                return new Property("Trafalgar Square", 240, position, 20, 150, 100, 300, 750, 925, 1100);
            case 25:
                // Fenchurch St Station
                return new Railroad(position, "Fenchurch St Station");
            case 26:
                return new Property("Leicester Square", 260, position, 22, 150, 110, 330, 800, 975, 1150);
            case 27:
                return new Property("Coventry Street", 260, position, 22, 150, 110, 330, 800, 975, 1150);
            case 28:
                // water works
                return new Utilty(position, "Water Works", 10);
            case 29:
                return new Property("Piccadilly", 280, position, 22, 150, 120, 360, 850, 1025, 1200);
            case 30:
                // go JAIL
                return new Jail(position, "Go to Jail", Jail.JailType.GOTO_JAIL);
            case 31:
                return new Property("Regent Street", 300, position, 26, 200, 130, 390, 900, 1100, 1275);
            case 32:
                return new Property("Oxford Street", 300, position, 26, 200, 130, 390, 900, 1100, 1275);
            case 33:
                // community chest
                return new CardSquare("Community Chest", position, Cards.CardType.COMMUNITY_CHEST);
            case 34:
                return new Property("Bond Street", 320, position, 28, 200, 150, 450, 1000, 1200, 1400);
            case 35:
                // liverpool st station
                return new Railroad(position, "Liverpool St Station");
            case 36:
                // chance
                return new CardSquare("Chance", position, Cards.CardType.CHANCE);
            case 37:
                return new Property("Park Lane", 350, position, 35, 200, 175, 500, 1100, 1300, 1500);
            case 38:
                // super tax
                return new Taxes("Supertax", position, 100);
            case 39:
                return new Property("Mayfair", 400, position, 50, 200, 200, 600, 1400, 1700, 2000);
        }
        return null;
    }

    private void makeGroups() {
        // brown properties
        ((Property) this.board[1]).setGroup((Property) this.board[3], null);
        ((Property) this.board[3]).setGroup((Property) this.board[1], null);

        // light blues
        ((Property) this.board[6]).setGroup((Property) this.board[8], (Property) this.board[9]);
        ((Property) this.board[8]).setGroup((Property) this.board[6], (Property) this.board[9]);
        ((Property) this.board[9]).setGroup((Property) this.board[6], (Property) this.board[8]);

        // purples
        ((Property) this.board[11]).setGroup((Property) this.board[13], (Property) this.board[14]);
        ((Property) this.board[13]).setGroup((Property) this.board[11], (Property) this.board[14]);
        ((Property) this.board[14]).setGroup((Property) this.board[11], (Property) this.board[13]);

        // oranges
        ((Property) this.board[16]).setGroup((Property) this.board[18], (Property) this.board[19]);
        ((Property) this.board[18]).setGroup((Property) this.board[16], (Property) this.board[19]);
        ((Property) this.board[19]).setGroup((Property) this.board[16], (Property) this.board[18]);

        // red
        ((Property) this.board[21]).setGroup((Property) this.board[23], (Property) this.board[24]);
        ((Property) this.board[23]).setGroup((Property) this.board[21], (Property) this.board[24]);
        ((Property) this.board[24]).setGroup((Property) this.board[21], (Property) this.board[23]);

        // yellow
        ((Property) this.board[26]).setGroup((Property) this.board[27], (Property) this.board[29]);
        ((Property) this.board[27]).setGroup((Property) this.board[26], (Property) this.board[29]);
        ((Property) this.board[29]).setGroup((Property) this.board[26], (Property) this.board[27]);

        // green
        ((Property) this.board[31]).setGroup((Property) this.board[32], (Property) this.board[34]);
        ((Property) this.board[32]).setGroup((Property) this.board[31], (Property) this.board[34]);
        ((Property) this.board[34]).setGroup((Property) this.board[31], (Property) this.board[32]);

        // dark blue
        ((Property) this.board[37]).setGroup((Property) this.board[39], null);
        ((Property) this.board[39]).setGroup((Property) this.board[37], null);


    }
}
