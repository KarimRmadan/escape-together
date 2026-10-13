package EscapeTogether;

public class Puzzle {
    private String description;
    private String player1Image;
    private String player2Image;
    private String solution;

    //Constructor erstellt
    public Puzzle(String description, String player1Image, String player2Image, String solution) {
        this.description = description;
        this.player1Image = player1Image;
        this.player2Image = player2Image;
        this.solution = solution;
    }

    //Methode zum Prüfen der antwort
    public boolean checkSolution (String answer){
  return solution.equalsIgnoreCase(answer.trim());
    }

    //Getter für die Bildpfade
    public String getPlayer2Image() {
        return player2Image;
    }

    public String getPlayer1Image() {
        return player1Image;
    }

    public String getDescription() {
        return description;
    }
}
