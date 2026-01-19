import java.util.ArrayList;

public class Senet {
    State state;
    int count = 0;                 // used to count nodes in search tree
    int roundNo = 1;               // keep track of round number
    static int pawnsCount = 2;     // set pawns number to play with
    int depth = 2;                 // search tree depth
    boolean viewDetails;           // used to show search details in console or not
    int playMode = 1;              // to set play mode (player vs computer or computer vs computer)

    public Senet() {
        this.state = new State(pawnsCount);
    }

    // computer as a max player
    public void getComputerMaxMove(int steps) {
        ArrayList<State> possibleStates = state.getPossibleStates(steps);

        ArrayList<Node> exploredNodes = new ArrayList<>();

        Node bestNode = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (State nextState : possibleStates) {
            Node node = expectminimax(nextState, this.depth);
            exploredNodes.add(node);

            if (node.evaluation > bestValue) {
                bestValue = node.evaluation;
                bestNode = node;
            }
        }

        IO.println("~ best move: [" + (bestNode.state.lastMovedPawn + 1) + "]\n");

        if (viewDetails)
            printSearchTree(exploredNodes);

        state.move(bestNode.state.lastMovedPawn, steps);
    }

    // computer as a min player
    public void getComputerMinMove(int steps) {
        ArrayList<State> possibleStates = state.getPossibleStates(steps);

        Node bestNode = null;
        double bestValue = Double.POSITIVE_INFINITY;

        for (State nextState : possibleStates) {
            Node node = expectminimax(nextState, this.depth);

            if (node.evaluation < bestValue) {
                bestValue = node.evaluation;
                bestNode = node;
            }
        }

        IO.println("~ best move: [" + (bestNode.state.lastMovedPawn + 1) + "]\n");
        state.move(bestNode.state.lastMovedPawn, steps);
    }

    // determines computer's next move according to expect minimax algorithm
    Node expectminimax(State initialState, int depth) {
        // checking if state is a terminal state
        if (initialState.hasWinner() == 1)
            return new Node(initialState, +10000);

        if (initialState.hasWinner() == 2)
            return new Node(initialState, -10000);

        if (depth == 0)
            return new Node(initialState, initialState.heuristic());

        // =====================================

        boolean isMax = initialState.isComputerTurn;
        double expectedValue = 0.0;
        Node parentNode = new Node(initialState);
        Node bestChild = null;

        // for each result of throwing sticks
        for (int i = 0; i < 5; i++) {
            double stateProbability = tossValueProbability(i);

            ArrayList<State> possibleStates = initialState.getPossibleStates((i == 0 ? 5 : i));

            if (possibleStates.isEmpty()) {
                State skippedState = initialState.clone();
                skippedState.switchTurns();
                Node skippedStateNode = expectminimax(skippedState, depth - 1);
                parentNode.addChild(skippedStateNode);
                expectedValue += stateProbability * skippedStateNode.evaluation;
                continue;
            }

            double bestValue = initialState.isComputerTurn ?
                    Double.NEGATIVE_INFINITY :
                    Double.POSITIVE_INFINITY;

            for (State possibleState : possibleStates) {
                Node node = expectminimax(possibleState, depth - 1);
                parentNode.addChild(node);

                if (isMax) {
                    if (node.evaluation > bestValue) {
                        bestValue = node.evaluation;
                        bestChild = node;
                    }
                } else {
                    if (node.evaluation < bestValue) {
                        bestValue = node.evaluation;
                        bestChild = node;
                    }
                }
            }
            expectedValue += stateProbability * bestValue;
        }

        parentNode.evaluation = expectedValue;
        parentNode.bestChild = bestChild;
        return parentNode;
    }

    // takes user input to perform a move
    public void getPlayerMove(int steps) {
        // taking input from user to choose a pawn to move it
        while (true) {
            IO.print("Enter pawn row: ");
            int cellRow = Integer.parseInt(IO.readln());
            IO.print("Enter pawn col: ");
            int cellCol = Integer.parseInt(IO.readln());
            IO.println();

            // check if entered row and col are in board boundaries
            if (!state.isValidCoors(cellRow - 1, cellCol - 1)) {
                IO.println("------------- incorrect row or col number! -------------\n");
                return;
            }

            // get chosen cell index and object
            int chosenCellIndex = state.getCellIndex(cellRow - 1, cellCol - 1);

            if (state.isValidMove(chosenCellIndex, steps)) {
                // passing values to main move function
                state.move(chosenCellIndex, steps);
                break;
            } else {
                IO.println("------------- incorrect move! -------------\n");
            }
        }
    }

    // function to start playing game
    public void play() {
        getDepthFromUser();
        askToViewSearchDetails();
        setPlayMode();

        while (state.hasWinner() == 0) {
            IO.println("--------------------------- round[" + roundNo + "] ---------------------------\n");
            IO.println(state);
            state.printPlayerName();

            // generating steps randomly (throwing sticks)
            int steps = state.toss();
            IO.println("~ allowed steps: [" + steps + "]");

            if (shouldSkipTurn(steps))
                continue;

            // calling appropriate player function
            if (state.isComputerTurn) getComputerMaxMove(steps);
            else {
                if (playMode == 1) getPlayerMove(steps);
                else getComputerMinMove(steps);
            }
            count = 0;
            roundNo++;
        }

        if (state.hasWinner() == 1)
            IO.println("----------------- Player O Won! -----------------");
        if (state.hasWinner() == 2)
            IO.println("----------------- player X Won! -----------------");
    }

    // functions to calculate the probability of a tossed value
    public double tossValueProbability(int tossedValue) {
        int throwCount = 4;
        int singleThrowResults = 2;
        double totalResults = Math.powExact(throwCount, singleThrowResults);  // 4^2

        double PointsOccurrences
                = (double) fact(throwCount)
                / (double) (fact(tossedValue) * fact(throwCount - tossedValue));

        return PointsOccurrences / totalResults;
    }

    // calculate factorial value of number n
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

    // return true if turn should be skipped
    public boolean shouldSkipTurn(int steps) {
        ArrayList<State> states = state.getPossibleStates(steps);
        if (states.isEmpty()) {
            IO.println("""
                    ----------------------- no possible moves -----------------------\s
                    ------------------------- skipping turn -------------------------
                    """);
            state.cleanSpecialCells();
            state.switchTurns();
            roundNo++;
            count = 0;
            return true;
        }
        return false;
    }

    // functions to print search tree details
    void printSearchTree(ArrayList<Node> nodesList) {
        this.count = 0;

        IO.println("=========================== search details ===========================");
        System.out.format("%-10s%-10s%-20s%-10s%-10s%n", "no", "type", "eval", "move", "child count");

        for (Node node : nodesList) {
            printSearchNode(node);

            if (node.bestChild != null) {
                IO.println("\n~ best node:\n"
                        + "  - move: " + (node.bestChild.state.lastMovedPawn + 1)
                        + " : " + (node.bestChild.state.lastSteps) + "\n"
                        + "  - evaluation: " + (node.bestChild.evaluation) + "\n"

                );
            } else {
                IO.println("no best node\n");
            }
        }
        IO.println("=====================================================================\n");
    }

    void printSearchNode(Node node) {
        count += 1;

        System.out.format("%-10s%-10s%-20s%-10s%-10s%n",
                count,
                node.getType(),
                node.evaluation,
                (node.state.lastMovedPawn + 1) + " : " + node.state.lastSteps,
                node.childrenCount() == 0 ? "-" : node.childrenCount());

        if (!node.children.isEmpty()) {
            for (int i = 0; i < node.children.size(); i++) {
                Node child = node.children.get(i);
                printSearchNode(child);
            }
        }
    }

    // helper functions to take input from user
    public void getDepthFromUser() {
        while (true) {
            IO.print("> enter expectminimax depth value (default = 2): ");
            String input = IO.readln();

            if (input.trim().isEmpty()) {
                return;
            } else {
                try {
                    int depthVal = Integer.parseInt(input);
                    if (depthVal < 1) {
                        IO.println("--------------------- entered value is incorrect! ---------------------");
                    } else {
                        this.depth = Integer.parseInt(input);
                        break;
                    }
                } catch (NumberFormatException e) {
                    IO.println("--------------------- entered value is incorrect! ---------------------");
                }
            }
        }
    }

    void askToViewSearchDetails() {
        IO.print("> show search details? [y] / [n] (default): ");
        String input = IO.readln();

        if (input.equalsIgnoreCase("y")) {
            viewDetails = true;
        }
    }

    void setPlayMode() {
        while (true) {
            IO.print("""
                    > choose playing mode:
                      [1] player vs computer (default)
                      [2] computer vs computer
                      enter mode no:\s""");

            String input = IO.readln();

            if (input.trim().isEmpty())
                return;

            try {
                int mode = Integer.parseInt(input);

                if (mode == 1 || mode == 2) {
                    this.playMode = Integer.parseInt(input);
                    IO.println();
                    break;
                } else {
                    IO.println("--------------------- entered value is incorrect! ---------------------");
                }
            } catch (NumberFormatException e) {
                IO.println("--------------------- entered value is incorrect! ---------------------");
            }
        }
    }

    public static void setPawnsCount() {
        while (true) {
            IO.print("> enter number of pawns to play with (default = 2): ");
            String input = IO.readln();

            if (input.trim().isEmpty()) {
                return;
            } else {
                try {
                    int count = Integer.parseInt(input);
                    if (count < 2 || count > 14) {
                        IO.println("--------------------- entered value is incorrect! ---------------------");
                    } else {
                        pawnsCount = count;
                        break;
                    }
                } catch (NumberFormatException e) {
                    IO.println("--------------------- entered value is incorrect! ---------------------");
                }
            }
        }
    }
}
