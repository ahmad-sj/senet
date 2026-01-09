import cells.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game implements Cloneable {

    ArrayList<Cell> board;
    boolean isComputerTurn;
    int player1Score;
    int player2Score;
    public int lastMovedPawn;

    String[] cellsSymbols = {" & ", "...", "|||", "√√√", "∑∑∑", "% %", " @ "};
    String[] playersSymbols = {" O ", " X "};

    // default constructor, used to create a new game object
    public Game() {
        board = new ArrayList<>();

        // adding players pawns, i: 0 -> 13
        for (int i = 0; i <= 13; i++) {
            if (i % 2 == 0)
                board.add(new Pawn(playersSymbols[0]));
            else
                board.add(new Pawn(playersSymbols[1]));
        }

//        for (int i = 0; i <= 3; i++) {
//            if (i % 2 == 0)
//                board.add(new Pawn(playersSymbols[0]));
//            else
//                board.add(new Pawn(playersSymbols[1]));
//        }
//
//        for (int i = 4; i <= 13; i++) {
//            board.add(new Cell());
//        }


        // adding rebirth house, i = 14
        board.add(new Cell(cellsSymbols[0]));

        // adding empty cells, i: 15 -> 24 empty cells
        for (int i = 15; i <= 24; i++) {
            board.add(new Cell(cellsSymbols[1]));
        }

        // adding special cells, i: 25 -> 29
        for (int i = 2; i <= 6; i++) {
            board.add(new Cell(cellsSymbols[i]));
        }
    }

    // overriding to string to print board arraylist on multiple lines
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("player").append(playersSymbols[0])
                .append("score : ").append(player1Score).append("\n");
        sb.append("player").append(playersSymbols[1])
                .append("score : ").append(player2Score).append("\n\n");

        sb.append("      ");
        // printing col numbers
        for (int i = 0; i < 10; i++) {
            String colNo = " " + (i + 1) + " ";
            sb.append(colNo);

            if (i + 1 < 10)
                sb.append("   ");
        }
        sb.append("\n\n");

        // printing rows
        for (int i = 0; i < 3; i++) {
            if (i % 2 == 0) {
                // printing row number
                sb.append(" ").append(i + 1).append("    ");

                for (int j = 0; j < 10; j++) {
                    sb.append(board.get(i * 10 + j));

                    if (j + 1 < 10)
                        sb.append("   ");
                }
                sb.append("\n");
            } else {
                // printing row number
                sb.append(" ").append(i + 1).append("    ");

                for (int j = 9; j >= 0; j--) {
                    sb.append(board.get(i * 10 + j));

                    if (j - 1 >= 0)
                        sb.append("   ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * the heuristic value function looks at the number of squares the opponent needs to move their pawns to the end of the board,
     * and compares that to the number of squares the current player needs to move its pawns to the end of the board.
     */
    int heuristic() {
        int player1SquaresTotal = 0;
        int player2SquaresTotal = 0;

        // walk on board cells
        for (int i = 0; i < board.size(); i++) {
            // if pawn is for player 1
            if (board.get(i).symbol.equals(playersSymbols[0])) {
                // calc the number of squares for this pawn to reach the end of board
                // add this number to player1 total square count
                player1SquaresTotal += board.size() - i;
            }
            // if pawn is for player 2
            if (board.get(i).symbol.equals(playersSymbols[1])) {
                // calc the number of squares for this pawn to reach the end of board
                // add this number to player2 total square count
                player2SquaresTotal += board.size() - i;
            }
        }
        return player1SquaresTotal - player2SquaresTotal;
    }

    // main move function
    public void move(int chosenCellIndex, int steps) {
        if (!isValidMove(chosenCellIndex, steps)) {
            IO.println("------------- incorrect move! -------------\n");
            return;
        }

        // if move leads to a promotion
        if (isPromoted(chosenCellIndex, steps)) {
            IO.println("------------- pawn promoted! -------------\n");

            // switch to the other player's turn
            switchTurns();

            // end current player's turn
            return;
        }
        // if it's not a promotion move
        // send current player pawn from cells 28 -> 30 to rebirth
        else {
            cleanSpecialCells();
        }

        // get target cell index
        int targetCellIndex = chosenCellIndex + steps;

        // check if target cell is water house and send pawn to rebirth
        if (targetCellIndex == 26) {
            sendToRebirth(chosenCellIndex);
        }
        // move pawn to an empty cell or swap pawns if target cell is busy
        else {
            performMove(chosenCellIndex, targetCellIndex);
        }

        lastMovedPawn = chosenCellIndex;
        // switch players turns after move is done correctly
        switchTurns();
    }

    // get possible moves, takes allowed steps and checks current player
    // returns a list of current player movable pawns indexes.
    ArrayList<Integer> getPossibleMoves(int allowedSteps) {
        ArrayList<Integer> pawnIndexes = new ArrayList<Integer>();

        String currentPlayerSymbol = getCurrentPlayerSymbol();

        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).symbol.equals(currentPlayerSymbol)) {
                if (isValidMove(i, allowedSteps)) {
                    pawnIndexes.add(i);
                }
            }
        }

        return pawnIndexes;
    }

    ArrayList<Game> getPossibleGames(int allowedSteps) {
        ArrayList<Game> possibleGames = new ArrayList<>();

        // bring movable pawn indexes list
        ArrayList<Integer> movablePawnIndexes = getPossibleMoves(allowedSteps);

        for (Integer movablePawnIndex : movablePawnIndexes) {
            // clone current game
            Game cloned = this.clone();

            // perform a move
            cloned.move(movablePawnIndex, allowedSteps);

            // add result to array list
            possibleGames.add(cloned);
        }

        return possibleGames;
    }

    // check if entered row and col are in board boundaries
    boolean isValidCoors(int row, int col) {
        if (row >= 0 && row <= 2)
            return col >= 0 && col <= 9;
        return false;
    }

    // convert entered coordinates (row, col) to index in board arraylist
    int getCellIndex(int row, int col) {
        if (row % 2 == 0)
            return ((row * 10) + col);

        return ((row * 10) + 9 - col);
    }

    // check if the pawn is NOT jumping over happiness cell
    boolean jumpingOverHappiness(int pawnIndex, int steps) {
        // check if chosen pawn index < 25 and target cell index > 25
        if (pawnIndex < 25 && pawnIndex + steps > 25) {
            // jumping over happiness house
            return true;
        }
        // not jumping
        return false;
    }

    // return an object of the cell original type
    void resetCell(int cellIndex) {
        var temp = switch (cellIndex) {
            case 14 -> new Cell(cellsSymbols[0]);
            case 25 -> new Cell(cellsSymbols[2]);
            case 26 -> new Cell(cellsSymbols[3]);
            case 27 -> new Cell(cellsSymbols[4]);
            case 28 -> new Cell(cellsSymbols[5]);
            case 29 -> new Cell(cellsSymbols[6]);
            default -> new Cell(cellsSymbols[1]);
        };
        board.set(cellIndex, temp);
    }

    // check if cell is empty
    boolean isEmpty(int index) {
        // putting all symbols of empty cells in a list
        ArrayList<String> symbolList = new ArrayList<>(List.of(this.cellsSymbols));

        // getting cell to be checked if empty
        Cell cell = board.get(index);

        // check if symbol of chosen cell is in empty cell symbol
        return symbolList.contains(cell.symbol);
    }

    // move pawn to an empty cell or swap pawns if target cell is busy
    void performMove(int sourceCellIndex, int targetCellIndex) {
        // check if target cell is empty
        if (isEmpty(targetCellIndex)) {
            Cell sourceCell = board.get(sourceCellIndex);

            //place sourceCell in target cell
            board.set(targetCellIndex, sourceCell);

            // reset sourceCell cell to its original type
            resetCell(sourceCellIndex);
        }
        // target cell has a pawn in it
        else {
            swapPawns(sourceCellIndex, targetCellIndex);
        }

    }

    // switch positions of two pawns
    void swapPawns(int cell1Index, int cell2Index) {
        Collections.swap(board, cell1Index, cell2Index);
    }

    // send pawn to rebirth cell or first empty cell before it
    void sendToRebirth(int index) {
        Cell chosenPawn = board.get(index);

        // if cell 14 is empty (cell 14 = " & ")
        if (isEmpty(14))
            board.set(14, chosenPawn);
        else {
            int i = 1;
            // while the symbol != "..." go back one step
            while (!isEmpty(14 - i)) {
                i++;
            }
            board.set(14 - i, chosenPawn);
        }
        resetCell(index);
    }

    // print name of current player
    void printPlayerName() {
        if (isComputerTurn)
            IO.println("~ [ O's turn ] ~");
        else
            IO.println("~ [ X's turn ] ~");
    }

    String getCurrentPlayerSymbol() {
        return (isComputerTurn ? playersSymbols[0] : playersSymbols[1]);
    }

    // pass turns between players
    void switchTurns() {
        isComputerTurn = !isComputerTurn;
    }

    // check if chosen pawn is promotable and promote it
    boolean isPromoted(int chosenCellIndex, int steps) {
        // tells if current player's turn should be ended at the end of this function
        boolean isPromoted = false;

        // player chose pawn in cell 25 and steps leads to a promotion
        if (chosenCellIndex == 25 && steps == 5) {
            // promote pawn
            resetCell(25);

            // increase score of current player
            if (isComputerTurn)
                player1Score++;
            else player2Score++;

            // end turn after this function is executed
            isPromoted = true;
        }

        // player chose pawn in cell 27 and steps leads to a promotion
        if (chosenCellIndex == 27 && steps == 3) {
            // promote pawn
            resetCell(27);

            // increase score of current player
            if (isComputerTurn)
                player1Score++;
            else player2Score++;

            // end turn after this function is executed
            isPromoted = true;
        }

        // player chose pawn in cell 28 and steps leads to a promotion
        if (chosenCellIndex == 28 && steps == 2) {
            // promote pawn
            resetCell(28);

            // increase score of current player
            if (isComputerTurn)
                player1Score++;
            else player2Score++;

            // end turn after this function is executed
            isPromoted = true;
        }

        // player chose pawn in cell 29 and whatever steps count is
        if (chosenCellIndex == 29) {
            // promote pawn
            resetCell(29);

            // increase score of current player
            if (isComputerTurn)
                player1Score++;
            else player2Score++;

            // end turn after this function is executed
            isPromoted = true;
        }

        return isPromoted;
    }

    // send pawns to in cells 27 -> 29 rebirth
    void cleanSpecialCells() {
        String currentPlayerSymbol = getCurrentPlayerSymbol();

        // check if a pawn already exists in cell 27, and it's owned by the current player
        if (board.get(27).symbol.equals(currentPlayerSymbol)) {
            sendToRebirth(27);
        }

        // check if a pawn already exists in cell 28, and it's owned by the current player
        if (board.get(28).symbol.equals(currentPlayerSymbol)) {
            sendToRebirth(28);
        }

        // check if a pawn already exists in cell 29, and it's owned by the current player
        if (board.get(29).symbol.equals(currentPlayerSymbol)) {
            sendToRebirth(29);
        }
    }

    // throw sticks to get steps count
    public int toss() {
        int steps = 0;
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            // generate 0 or 1 randomly with equal possibility
            steps += random.nextInt(2);
        }
        return steps == 0 ? 5 : steps;
    }

    public double tossValueProbability(int tossedValue) {
        int throwCount = 4;
        int singleThrowResults = 2;
        double totalResults = Math.powExact(throwCount, singleThrowResults);  // 4^2

        double PointsOccurrences
                = (double) fact(throwCount)
                / (double) (fact(tossedValue) * fact(throwCount - tossedValue));

        return PointsOccurrences / totalResults;
    }

    // calculate factorial value of number
    public long fact(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    // check wining status and returns 0: no winner, 1: player1 won, 2: player2 won
    int hasWinner() {
        boolean win = false;
        int player1Pawns = 0;
        int player2Pawns = 0;

        for (Cell cell : board) {
            if (cell.symbol.equals(playersSymbols[0]))
                player1Pawns++;

            if (cell.symbol.equals(playersSymbols[1]))
                player2Pawns++;
        }

        if (player1Pawns == 0) {
            return 1;
        }

        if (player2Pawns == 0) {
            return 2;
        }

        return 0;
    }

    // check if a move is correct
    boolean isValidMove(int chosenCellIndex, int steps) {
        // get current player symbol
        String currentPlayerSymbol = getCurrentPlayerSymbol();

        // get chosen cell object
        Cell chosenCellObject = board.get(chosenCellIndex);

        // check if chosen cell do not have a pawn in it
        if (!(chosenCellObject instanceof Pawn))
            return false;

        // check if player did not choose a pawn of his own
        if (!chosenCellObject.symbol.equals(currentPlayerSymbol))
            return false;

        // if pawn is standing in cells 25 -> 29 and steps lead outside board
        // examples: 25 + 5 or 27 + 3 or 28 + 2, ...
        // we don't have to check target cell object symbol
        // this why we only check if index is smaller than board size
        if (chosenCellIndex + steps < board.size()) {
            Cell targetCellObject = board.get(chosenCellIndex + steps);

            // check if swapping between two pawns having the same symbol
            if (chosenCellObject.symbol.equals(targetCellObject.symbol))
                return false;
        }

        // check if player is jumping over happiness house
        if (jumpingOverHappiness(chosenCellIndex, steps))
            return false;

        // applying wrong steps on a promotable pawn
        if (chosenCellIndex == 27 && steps != 3)
            return false;

        // applying wrong steps on a promotable pawn
        if (chosenCellIndex == 28 && steps != 2)
            return false;

        return true;
    }

    @Override
    public Game clone() {
        try {
            Game cloned = (Game) super.clone();

            cloned.board = new ArrayList<>();
            for (Cell cell : this.board) {
                cloned.board.add(cell.clone());
            }
            return cloned;

        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}

