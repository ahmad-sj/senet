public class Node {
    public Game state;
    public Node parent;
    public int evaluation;

    public Node(Game state, Node parent, int evaluation) {
        this.state = state;
        this.parent = parent;
        this.evaluation = evaluation;
    }

    @Override
    public String toString() {
        return state.toString() + "\n evaluation: " + evaluation;
    }
}
