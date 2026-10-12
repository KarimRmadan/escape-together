package EscapeTogether;

import java.util.Objects;

public class Puzzle {
    private String description;
    private String player1Image;
    private String player2Image;
    private String solution;

    public Puzzle(String description, String player1Image, String player2Image, String solution) {
        this.description = description;
        this.player1Image = player1Image;
        this.player2Image = player2Image;
        this.solution = solution;
    }

    public boolean checkSolution (String answer){
  return Objects.equals(solution, answer);
    }
}
