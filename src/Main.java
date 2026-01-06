public class Main {
    public static void main() {

        Game game = new Game();

        IO.println("\n=============================================================================");
        IO.println(game);


        while (true) {

            game.printPlayerName();

            IO.print("Enter pawn row: ");
            int row = Integer.parseInt(IO.readln());
            IO.print("Enter pawn col: ");
            int col = Integer.parseInt(IO.readln());
            IO.print("Enter steps: ");
            int steps = Integer.parseInt(IO.readln());
            IO.println("\n");

            game.move(row - 1, col - 1, steps);

            IO.println(game);
        }
    }
}
