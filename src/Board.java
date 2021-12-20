public class Board {
    private Square[] board;


    public Board() {
        for (int i = 0; i < 41; i++ ) {
            this.board[i] = getSquare(i);
        }
    }

    private Square getSquare(int position) {
        switch (position) {
            case 0:
                // go
            case 1:
                return new Property("Old Kent Road", 60, position, 10, 50, 10, 30, 90, 160, 250);
            case 2:
                // community chest
            case 3:
                return new Property("Whitechapel Road", 60, position, 4, 50, 20, 60, 180, 320, 450);
            case 4:
                // income tax
            case 5:
                // kings cross
            case 6:
                return new Property("The Angel, Islington", 100, position, 6, 50, 30, 90, 270, 400, 550);
            case 7:
                // chance
            case 8:
                return new Property("Euston Road", 100, position, 6, 50, 30, 90, 270, 400, 550);
            case 9:
                return new Property("Pentonville Road", 120, position, 8, 50, 40, 100, 300, 450, 600);
            case 10:
                // jail
            case 11:
                return new Property("Pall Mall", 140, position, 10, 100, 50, 150, 450, 625, 750);
            case 12:
                // electric company
            case 13:
                return new Property("Whitehall", 140, position, 10, 100, 50, 150, 450, 625, 750);
            case 14:
                return new Property("Northumberl'd Avenue", 160, position, 12, 100, 60, 180, 500, 700, 900);
            case 15:
                // marylebone station
            case 16:
                return new Property("Bow Street", 180, position, 14, 100, 70, 200, 550, 750, 950);
            case 17:
                // community chest
            case 18:
                return new Property("Marlborough Street", 180, position, 14, 100, 70, 200, 550, 750, 950);
            case 19:
                return new Property("Vine Street", 200, position, 16, 100, 80, 220, 600, 800, 1000);
            case 20:
                // freeparking
            case 21:
                return new Property("The Strand", 220, position, 18, 150, 90, 250, 700, 875, 1050);
            case 22:
                // chance
            case 23:
                return new Property("Fleet Street", 220, position, 18, 150, 90, 250, 700, 875, 1050);
            case 24:
                return new Property("Trafalgar Square", 240, position, 20, 150, 100, 300, 750, 925, 1100);
            case 25:
                // Fenchurch St Station
            case 26:
                return new Property("Leicester Square", 260, position, 22, 150, 110, 330, 800, 975, 1150);
            case 27:
                return new Property("Coventry Street", 260, position, 22, 150, 110, 330, 800, 975, 1150);
            case 28:
                // water works
            case 29:
                return new Property("Piccadilly", 280, position, 22, 150, 120, 360, 850, 1025, 1200);
            case 30:
                // go JAIL
            case 31:
                return new Property("Regent Street", 300, position, 26, 200, 130, 390, 900, 1100, 1275);
            case 32:
                return new Property("Oxford Street", 300, position, 26, 200, 130, 390, 900, 1100, 1275);
            case 33:
                // community chest
            case 34:
                return new Property("Bond Street", 320, position, 28, 200, 150, 450, 1000, 1200, 1400);
            case 35:
                // liverpool st station
            case 36:
                // chance
            case 37:
                return new Property("Park Lane", 350, position, 35, 200, 175, 500, 1100, 1300, 1500);
            case 38:
                // super tax
            case 39:
                return new Property("Mayfair", 400, position, 50, 200, 200, 600, 1400, 1700, 2000);





        }
        return null;
    }
}
