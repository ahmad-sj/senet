public class Main {

    public static void main() {
        IO.println("\n=============================================================================\n");

        Senet.setPawnsCount();
        Senet senet = new Senet();
        senet.play();
    }
}
