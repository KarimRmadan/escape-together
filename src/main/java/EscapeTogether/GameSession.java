package EscapeTogether;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    // Speichert die Chat-ID des Spielers, der die Partie erstellt hat
    private final Long player1ChatId;

    // Speichert die Chat-ID des zweiten Spielers, sobald dieser beitritt
    private Long player2ChatId;

    // Speichert den eindeutigen Raumcode der Spielpartie
    private final String roomCode;

    //Speichert den aktuellen Status der Session
    private GameStatus status = GameStatus.WAITING;

    // Speichert das aktuelle Rätsel der Spielpartie
    private final List<Puzzle> puzzles = new ArrayList<>();
    private int currentPuzzleIndex = 0;


    //Constructor
    public GameSession(Long player1ChatId, String roomCode) {
        this.player1ChatId = player1ChatId;
        this.roomCode = roomCode;
        puzzles.add(new Puzzle(
                "Findet die richtige Zahlenkombination um diesen Raum zu verlassen!",
                "images/puzzle_01_spieler1.png",
                "images/puzzle_01_spieler2.png",
                "4286"
        ));
        puzzles.add(new Puzzle(
                "Ihr steht in einem dunklen Flur. \nFindet gemeinsam den Richtigen Schlüssel für die nächste Tür!",
                "images/puzzle_02_spieler1.png",
                "images/puzzle_02_spieler2.png",
                "D"
        ));
        puzzles.add(new Puzzle(
                "Ihr erreicht einen dunklen Innenhof."
                        + " Vier alte Laternen beleuchten das verschlossene Tor. "
                        + "Jeder von euch besitzt einen Teil des Rätsels. "
                        + "Findet gemeinsam den vierstelligen Code!",
                "images/puzzle_03_spieler1.png",
                "images/puzzle_03_spieler2.png",
                "7392"
        ));
        puzzles.add(new Puzzle(
                "Das letzte Tor.\n" +
                        "Ihr steht vor dem letzten verschlossenen Tor.\n "
                        + "Vier Gegenstände liegen auf einem steinernen Tisch.\n "
                        + "Jeder von euch besitzt einen Hinweis auf den "
                        + "richtigen Gegenstand des anderen. \n"
                        + "Nur wenn ihr beide richtig entscheidet, "
                        + "öffnet sich das Tor.",
                "images/puzzle_04_spieler1.png",
                "images/puzzle_04_spieler2.png",
                "SCHLUESSEL SPIEGEL"
        ));
    }

    // Fügt Spieler 2 hinzu, sofern der Platz frei ist.
    // Gibt true zurück, wenn der Beitritt erfolgreich war und setzt den Status auf RUNNING.

    public boolean joinGame(Long chatId) {
        if (player2ChatId == null && !player1ChatId.equals(chatId)) {
            player2ChatId = chatId;
            status = GameStatus.RUNNING;
            return true;
        }
        return false;
    }

    public void finishGame() {
        if (status == GameStatus.RUNNING) {
            status = GameStatus.FINISHED;
        }
    }


    // Prüft, ob beide Spielerplätze belegt sind.
    // Gibt true zurück, wenn Spieler 2 vorhanden ist, andernfalls false.
    public boolean isFull() {
        return player2ChatId != null;
    }


    //Getter

    // Gibt den eindeutigen Raumcode der Spielpartie zurück.
    public String getRoomCode() {
        return roomCode;
    }

    //Getter für die ChatId's
    public Long getPlayer1ChatId() {
        return player1ChatId;
    }

    public Long getPlayer2ChatId() {
        return player2ChatId;
    }

    //Getter für den GameStatus
    public GameStatus getStatus() {
        return status;
    }


    // Gibt das aktuelle Rätsel aus der Liste zurück
    public Puzzle getCurrentPuzzle() {
        return puzzles.get(currentPuzzleIndex);
    }

    public boolean nextPuzzle() {
            if (currentPuzzleIndex < puzzles.size() - 1) {
                currentPuzzleIndex++;
                return true;
        }
            return false;
    }
}
