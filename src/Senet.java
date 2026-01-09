import java.util.ArrayList;

public class Senet {
    Game game;
    Node solutionPath;

    public Senet() {
        this.game = new Game();
    }

    /*
    When does a node become terminal?
    The game rules say the state is game over (win, loss, draw).

    The depth limit of the search is reached (for practical reasons, even if game is not over).

    No legal moves exist.

    At a terminal node, you return a heuristic/utility score:

    For a finished game: +∞ (win), −∞ (loss), 0 (draw), or some fixed high/low number.

    For a non-terminal but depth-limited leaf: a heuristic evaluation (e.g., material advantage in chess).
    */

    /* pseudocode
    function expectiminimax(node, depth)
    if node is a terminal node or depth = 0
            return the heuristic value of node

    if the adversary is to play at node
    // Return value of minimum-valued child node
    let α := +∞
    foreach child of node
    α := min(α, expectiminimax(child, depth-1))


            else if we are to play at node
    // Return value of maximum-valued child node
    let α := -∞
    foreach child of node
    α := max(α, expectiminimax(child, depth-1))


            else if random event at node
    // Return weighted average of all child nodes' values
    let α := 0
    foreach child of node
    α := α + (Probability[child] × expectiminimax(child, depth-1))
            return α
    */

    public void getComputerMove(int steps) {
        // replace with expectminimax algorithm
//        getPlayerMove(steps);
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
        IO.println(bestState);
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
        for (int i = 1; i <= 5; i++) {
            double stateProbability = this.game.tossValueProbability(i);

            ArrayList<Game> possibleGames = this.game.getPossibleGames(i);

            if (possibleGames.isEmpty()) {
                Game skippedGame = originalGame.clone();
                skippedGame.switchTurns();
                double skippedGameValue = expectminimax(skippedGame, depth - 1); // not sure is correct
                expectedValue += stateProbability * skippedGameValue;
                continue;
//                IO.println("minimax no move availbale");
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
