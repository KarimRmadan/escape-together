package EscapeTogether;

public class PuzzleTest {
    public static void main(String[] args) {
        Puzzle puzzle1 = new Puzzle("Finde die Richtige Zahlenkombi", "puzzle_01_spieler1.png",
                "puzzle_01_spieler2.png", "1234");

        System.out.println("Die lösung ist: " + puzzle1.checkSolution("1234"));
        System.out.println("Die lösung ist: " + puzzle1.checkSolution("12345"));
    }

}
