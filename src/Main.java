public class Main {

    public static void main() {
        IO.println("\n=============================================================================\n");

        Senet senet = new Senet();
        senet.setPawnsCount();
        senet.setSearchDepth();
        senet.setDetailsViewMode();
        senet.setPlayMode();
        senet.play();
    }
}
