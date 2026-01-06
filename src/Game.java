import cells.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    ArrayList<Cell> board;
    boolean isComputerTurn;
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

    // ========================================================================
    // play functions

    // player move function
    public void move(int cellRow, int cellCol, int steps) {
        // check if entered row and col are in board boundaries
        if (!isValidCoors(cellRow, cellCol)) {
            IO.println("------------- incorrect row or col number! -------------\n");
            return;
        }

        // get chosen cell index and object
        int chosenCellIndex = getCellIndex(cellRow, cellCol);
        Cell chosenCellObject = board.get(chosenCellIndex);

        // check if chosen cell has a pawn in it
        if (!(chosenCellObject instanceof Pawn)) {
            IO.println("------------- chosen cell has no pawn in it! -------------\n");
            return;
        }

        // check if player did not choose his own pawn color
        String playerColor = (isComputerTurn ? " O " : " X ");
        if (!chosenCellObject.symbol.equals(playerColor)) {
            IO.println("------------- chosen pawn is not owned by current player! -------------\n");
            return;
        }

        // if chosen pawn is promoted
        if (isPromotionMove(chosenCellIndex, steps)) {
            IO.println("------------- pawn promoted! -------------\n");

            // switch to the other player's turn
            switchTurns();

            // end current player's turn
            return;
        }

        // get target cell index
        int targetCellIndex = chosenCellIndex + steps;

        // check if steps lead to out of boundaries index
        // this conditions is used for testing in development
        if (!indexInBoundaries(targetCellIndex) && !(chosenCellIndex < 25)) {
            IO.println("------------- incorrect move! -------------\n");
            return;
        }

        // check if player is jumping over happiness house
        if (jumpingOverHappiness(chosenCellIndex, steps)) {
            IO.println("------------- jumping over happiness house is not allowed -------------\n");
            return;
        }

        // check if target cell is water house
        if (targetCellIndex == 26) {
            sendToRebirth(chosenCellIndex);
        }
        // move pawn to an empty cell or swap pawns if target cell is busy
        else {
            // check if target cell is empty
            if (isEmpty(targetCellIndex)) {
                performMove(chosenCellIndex, targetCellIndex);
            }
            // target cell has a pawn in it
            else {
                swapPawns(chosenCellIndex, targetCellIndex);
            }
        }

        // switching players turns
        switchTurns();
    }


    // ========================================================================
    // helper functions

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

    // check if cell index is in boundaries
    boolean indexInBoundaries(int index) {
        return index >= 0 && index <= 29;
    }

    // check if the pawn is NOT jumping over happiness cell
    boolean jumpingOverHappiness(int pawnIndex, int steps) {
        // check if target cell index is greater than happiness cell index
        if (pawnIndex + steps > 25) {
            // check if pawn is standing in or after happiness cell
            // then move is allowed (passing / passed through happiness cell)
            if (pawnIndex >= 25)
                return false;
            else
                // not allowed move (jumping over happiness cell)
                return true;
        }
        // target cell index is less than 25
        else
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

    boolean isEmpty(int index) {
        // putting all symbols of empty cells in a list
        ArrayList<String> symbolList = new ArrayList<>(List.of(this.cellsSymbols));

        // getting cell to be checked if empty
        Cell cell = board.get(index);

        // check if symbol of chosen cell is in empty cell symbol
        return symbolList.contains(cell.symbol);
    }

    // perform move if target cell is empty
    void performMove(int sourceCellIndex, int targetCellIndex) {
        Cell sourceCell = board.get(sourceCellIndex);

        //place sourceCell in target cell
        board.set(targetCellIndex, sourceCell);

        // reset sourceCell cell to its original type
        resetCell(sourceCellIndex);
    }

    // switch positions of two pawns
    void swapPawns(int cell1Index, int cell2Index) {
        Pawn pawn1 = (Pawn) board.get(cell1Index);
        Pawn pawn2 = (Pawn) board.get(cell2Index);

        // check if pawns has same symbol
        if (pawn1.colorEquals(pawn2)) {
            IO.println("------------- can't swap between two pawns of the same color! -------------\n");
        }
        // perform pawns swap
        else {
            Collections.swap(board, cell1Index, cell2Index);
        }
    }

    void sendToRebirth(int index) {
        Cell chosenPawn = board.get(index);

        // if cell 14 is empty (cell 14 = " & ")
        if (board.get(14).symbol.equals(cellsSymbols[0]))
            board.set(14, chosenPawn);
        else {
            int i = 1;
            // while the symbol != "..." go back one step
            while (!(board.get(14 - i).symbol.equals(cellsSymbols[1]))) {
                i++;
            }
            board.set(14 - i, chosenPawn);
        }
        resetCell(index);
    }

    void printPlayerName() {
        if (isComputerTurn)
            IO.println("~ [ O's turn ] ~");
        else
            IO.println("~ [ X's turn ] ~");
    }

    void switchTurns() {
        isComputerTurn = !isComputerTurn;
    }

    // check if chosen pawn is promotable and promote it
    boolean isPromotionMove(int chosenCellIndex, int steps) {
        String playerColor = (isComputerTurn ? " O " : " X ");

        // tells if current player's turn should be ended at the end of this function
        boolean endTurn = false;

        // player chose pawn in cell 25
        if (chosenCellIndex == 25) {
            // if steps leads to a promotion
            if (steps == 5) {
                // promote pawn
                resetCell(25);

                // end turn after this function is executed
                endTurn = true;
            }
        }

        // player chose pawn in cell 27
        if (chosenCellIndex == 27) {
            // if steps leads to a promotion
            if (steps == 3) {
                // promote pawn
                resetCell(27);

                // end turn after this function is executed
                endTurn = true;
            }
        }
        // player did not choose cell 27
        else {
            // check if a pawn already exists in cell 27, and it's owned by the current player
            if (board.get(27).symbol.equals(playerColor)) {
                sendToRebirth(27);
            }
        }

        // player chose pawn in cell 28
        if (chosenCellIndex == 28) {
            // if steps leads to a promotion
            if (steps == 2) {
                // promote pawn
                resetCell(28);

                // end turn after this function is executed
                endTurn = true;
            }
        }
        // player did not choose cell 28
        else {
            // check if a pawn already exists in cell 28, and it's owned by the current player
            if (board.get(28).symbol.equals(playerColor)) {
                sendToRebirth(28);
            }
        }

        // player chose pawn in cell 29
        if (chosenCellIndex == 29) {
            // whatever steps count is, promote pawn
            resetCell(29);

            // end turn after this function is executed
            endTurn = true;
        }
        // player did not choose cell 29
        else {
            // check if a pawn already exists in cell 29, and it's owned by the current player
            if (board.get(29).symbol.equals(playerColor)) {
                sendToRebirth(29);
            }
        }

        return endTurn;
    }
}
