package EscapeTogether;

public class GameSession {

    // Speichert die Chat-ID des Spielers, der die Partie erstellt hat
    private final Long player1ChatId;

    // Speichert die Chat-ID des zweiten Spielers, sobald dieser beitritt
    private Long player2ChatId;

    // Speichert den eindeutigen Raumcode der Spielpartie
    private final String roomCode;

    //Speichert den aktuellen Status der Session
    private GameStatus status = GameStatus.WAITING;

    private Puzzle currentPuzzle = new Puzzle("Finde die Richtige Zahlenkombi", "puzzle_01_spieler1.png",
            "puzzle_01_spieler2.png", "4286");

    //Constructor
    public GameSession(Long player1ChatId, String roomCode) {
        this.player1ChatId = player1ChatId;
        this.roomCode = roomCode;
    }

    // Fügt Spieler 2 hinzu, sofern der Platz frei ist.
    // Gibt true zurück, wenn der Beitritt erfolgreich war und setzt den Status auf RUNNING.

    public boolean joinGame(Long chatId){
        if (player2ChatId == null && !player1ChatId.equals(chatId)) {
            player2ChatId = chatId;
            status = GameStatus.RUNNING;
            return true;
        }
        return false;
    }

    public void finishGame(){
        if (status == GameStatus.RUNNING){
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
}
