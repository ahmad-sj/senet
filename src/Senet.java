import java.util.ArrayList;

public class Senet {
    Game game;
    Node solutionPath;

    public Senet() {
        this.game = new Game();
    }

    public void getComputerMove(int steps) {
        ArrayList<Game> possibleGames = game.getPossibleGames(steps);

        if (possibleGames.isEmpty()) {
            return;
        }

        Game bestState = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Game nextState : possibleGames) {
            double value = expectminimax(nextState, 2); // 2: max depth
            if (value > bestValue) {
                bestValue = value;
                bestState = nextState;
            }
        }
//        return bestState;
        IO.println("best move: " + bestState.lastMovedPawn + "\n");
        game.move(bestState.lastMovedPawn, steps);
    }

    double expectminimax(Game originalGame, int depth) {
        // checking if state is a terminal state
        if (originalGame.hasWinner() == 1)
            return Double.POSITIVE_INFINITY;

        if (originalGame.hasWinner() == 2)
            return Double.NEGATIVE_INFINITY;

        if (depth == 0)
            return originalGame.heuristic();

        // =====================================

        boolean isMax = originalGame.isComputerTurn;
        double expectedValue = 0.0;

        // for each result of throwing sticks
        for (int i = 0; i < 5; i++) {
            double stateProbability = this.game.tossValueProbability(i);

            ArrayList<Game> possibleGames = this.game.getPossibleGames((i == 0 ? 5 : i));

            if (possibleGames.isEmpty()) {
                Game skippedGame = originalGame.clone();
                skippedGame.switchTurns();
                double skippedGameValue = expectminimax(skippedGame, depth - 1); // not sure is correct
                expectedValue += stateProbability * skippedGameValue;
                continue;
//                IO.println("minimax no move available");
            }


            double bestValue = originalGame.isComputerTurn ?
                    Double.NEGATIVE_INFINITY :
                    Double.POSITIVE_INFINITY;


            for (Game possibleGame : possibleGames) {
                double value = expectminimax(possibleGame, depth - 1);

                if (isMax) {
                    bestValue = Math.max(bestValue, value);
                } else {
                    bestValue = Math.min(bestValue, value);
                }
            }
            expectedValue += stateProbability * bestValue;
        }

        return expectedValue;
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
            int steps = game.toss();
            IO.print("------- allowed steps: [" + steps + "] -------\n\n");

            ArrayList<Game> games = game.getPossibleGames(steps);
            if (games.isEmpty()) {
                IO.println("------------------------- no moves available! -------------------------\n");
                game.switchTurns();
                return;
            }

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
