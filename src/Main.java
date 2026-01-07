import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main() {

        Game game = new Game();

        IO.println("\n=============================================================================");
        IO.println(game);

        while (game.hasWinner() == 0) {

            IO.println("---------------------------------------------------------------------------\n");

            game.printPlayerName();

            // generating steps randomly (throwing sticks)
//            int steps = game.toss();
//            IO.print("------- allowed steps: [ " + steps + " ] -------\n\n");
            
            // Entering steps manually
            IO.print("Enter steps: ");
            int steps = Integer.parseInt(IO.readln());

            // printing player's movable pawns
            ArrayList<Integer> movablePawnsIndexes = game.getPossibleMoves(steps);
            IO.print("indexes of current player movable pawns with (" + steps + ") steps are:\n");

            IO.print("[");
            for (int i = 0; i < movablePawnsIndexes.size(); i++) {
                IO.print((movablePawnsIndexes.get(i) + 1));

                if (i + 1 < movablePawnsIndexes.size())
                    IO.print(", ");
            }
            IO.println("]\n");

            // choosing a pawn to move
            IO.print("Enter pawn row: ");
            int row = Integer.parseInt(IO.readln());

            IO.print("Enter pawn col: ");
            int col = Integer.parseInt(IO.readln());

            IO.println();

            game.playerMove(row, col, steps);
            IO.println(game);
        }

    }
}
