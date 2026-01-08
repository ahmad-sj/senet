import java.util.ArrayList;

public class Senet {
    Game game;

    public Senet() {
        this.game = new Game();
    }

    public void getComputerMove(int steps) {
        // replace with expectminimax algorithm
//        getPlayerMove(steps);

    }

    void minMove(int steps) {

    }

    void maxMove(int steps) {

    }

    void chanceMove() {

    }

    public void getPlayerMove(int steps) {
        // taking input from user to choose a pawn to move it
        IO.print("Enter pawn row: ");
        int cellRow = Integer.parseInt(IO.readln());
        IO.print("Enter pawn col: ");
        int cellCol = Integer.parseInt(IO.readln());
        IO.println();

        // check if entered row and col are in board boundaries
        if (!game.isValidCoors(cellRow - 1, cellCol - 1)) {
            IO.println("------------- incorrect row or col number! -------------\n");
            return;
        }

        // get chosen cell index and object
        int chosenCellIndex = game.getCellIndex(cellRow - 1, cellCol - 1);

        // passing values to main move function
        game.move(chosenCellIndex, steps);
    }

    public void play() {
        IO.println("\n=============================================================================");
        IO.println(game);

        while (game.hasWinner() == 0) {
            IO.println("---------------------------------------------------------------------------\n");
            game.printPlayerName();

            // generating steps randomly (throwing sticks)
//            int steps = game.toss();
//            IO.print("------- allowed steps: [" + steps + "] -------\n\n");


            // ============================== testing section start ==============================
            // Entering steps manually
            IO.print("Enter steps: ");
            int steps = Integer.parseInt(IO.readln());

            IO.println();

            // printing player's movable pawns
            ArrayList<Integer> movablePawnsIndexes = game.getPossibleMoves(steps);
            IO.print("indexes of current player movable pawns with (" + steps + ") steps are:\n");
            IO.print("[");
            for (int i = 0; i < movablePawnsIndexes.size(); i++) {
                IO.print((movablePawnsIndexes.get(i) + 1));
                if (i + 1 < movablePawnsIndexes.size()) IO.print(", ");
            }
            IO.println("]\n");


            // printing possible games using current steps
            IO.println("----- possible games list start -----");
            ArrayList<Game> possibleGames = game.getPossibleGames(steps);
            for (Game possibleGame : possibleGames) {
                IO.println(possibleGame);
                IO.println("\n>>> heuristic value: " + possibleGame.heuristic() + "\n");
            }
            IO.println("----- possible games list end -----");
            // ============================== testing section end ==============================


            IO.println("toss value probability: " + game.tossValueProbability(steps));


            // calling appropriate play function
            if (game.isComputerTurn) {
                getComputerMove(steps);
            } else {
                getPlayerMove(steps);
            }

            // printing game after taken move
            IO.println(game);
        }

        if (game.hasWinner() == 1)
            IO.println("----------------- Computer Won! -----------------");
        if (game.hasWinner() == 2)
            IO.println("----------------- player Won! -----------------");
    }
}
