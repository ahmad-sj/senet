import java.util.ArrayList;

public class Node {
    public State state;
    public Node bestChild;
    public ArrayList<Node> children;
    public double evaluation;

    public Node(State state) {
        this.state = state;
        this.bestChild = null;
        this.children = new ArrayList<>();
        this.evaluation = 0;
    }

    public Node(State state, double evaluation) {
        this.state = state;
        this.bestChild = null;
        this.children = new ArrayList<>();
        this.evaluation = evaluation;
    }

    @Override
    public String toString() {
        return state.toString() + "\nevaluation: " + evaluation + ", node type: " + (state.isComputerTurn ? "max node" : "min node");
    }

    public void addChild(Node node) {
        this.children.add(node);
    }

    public String getType() {
        return state.isComputerTurn ? "max" : "min";
    }

    public int childrenCount() {
        return children.size();
    }
}
