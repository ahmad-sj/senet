package cells;

public class Pawn extends Cell {

    public Pawn(String pawnSymbol) {
        super(pawnSymbol);
    }

    public boolean colorEquals(Pawn o) {
        return this.symbol.equals(o.symbol);
    }

    public String getColor() {
        return this.symbol;
    }
}
