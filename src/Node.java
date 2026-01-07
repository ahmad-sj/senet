public class Node {
    public Game state;
    public Node parent;
    public int cost;

    public Node(Game state, Node parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return this.state.toString();
    }
}
