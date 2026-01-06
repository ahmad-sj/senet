package cells;

public class Cell {
    public String symbol;

    public Cell() {
        symbol = "...";
    }

    public Cell(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
