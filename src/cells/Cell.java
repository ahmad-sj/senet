package cells;

import java.util.ArrayList;

public class Cell implements Cloneable {
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

    @Override
    public Cell clone() {
        try {
            return (Cell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}
