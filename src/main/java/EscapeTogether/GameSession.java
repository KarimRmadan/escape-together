package EscapeTogether;

public class GameSession {

    // Speichert die Chat-ID des Spielers, der die Partie erstellt hat
    private final Long player1ChatId;

    // Speichert die Chat-ID des zweiten Spielers, sobald dieser beitritt
    private Long player2ChatId;

    private final String roomCode;

    public GameSession(Long player1ChatId, String roomCode) {
        this.player1ChatId = player1ChatId;
        this.roomCode = roomCode;
    }

    // Fügt Spieler 2 hinzu, sofern der Platz frei ist.
    // Gibt true zurück, wenn der Beitritt erfolgreich war.
    public boolean joinGame(Long chatId){
        if (player2ChatId == null && !player1ChatId.equals(chatId)) {
            player2ChatId = chatId;
            return true;
        }
        return false;
    }

    // Prüft, ob beide Spielerplätze belegt sind.
    // Gibt true zurück, wenn Spieler 2 vorhanden ist, andernfalls false.
    public boolean isFull() {
        if (player2ChatId != null){
            return true;
        }
        return false;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
