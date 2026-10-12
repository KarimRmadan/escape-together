package EscapeTogether;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class GameService {

    // Speichert alle Spielpartien anhand ihrer eindeutigen Raumcodes
    private final Map<String, GameSession> sessions = new HashMap<>();

    // Erstellt eine neue Spielpartie für den angegebenen Spieler
    public GameSession createGame(Long chatId) {

        // Erzeugt einen zufälligen vierstelligen Raumcode für die neue Partie
        String roomCode = UUID.randomUUID().toString().substring(0, 4);

        // Erzeugt bei einer Überschneidung einen neuen Raumcode
        while (sessions.containsKey(roomCode)) {
            roomCode = UUID.randomUUID().toString().substring(0, 4);
        }

        // Erstellt die neue Spielpartie mit dem freien Raumcode
        GameSession session = new GameSession(chatId, roomCode);

        // Speichert die Spielpartie unter ihrem Raumcode in der HashMap
        sessions.put(roomCode, session);

        return session;
    }

    // Ermöglicht einem zweiten Spieler, einer bestehenden Partie beizutreten
    // Gibt zurück, ob der Beitritt erfolgreich war
    public boolean joinGame(String roomCode, Long chatId) {

        GameSession session = sessions.get(roomCode);

        if (session != null) {
            return session.joinGame(chatId);
        }

        return false;
    }

    // Sucht ein Spiel anhand des Raumcodes und gibt es zurück
    public GameSession getSession(String roomCode) {
        return sessions.get(roomCode);
    }

    // Sucht anhand der Chat-ID eines Spielers die zugehörige Spielpartie
    // Gibt null zurück, wenn keine passende Partie gefunden wurde
    public GameSession getSessionByPlayer(Long chatId) {
        for (GameSession session : sessions.values()){
            if (session.getPlayer1ChatId().equals(chatId) ||
                    Objects.equals(session.getPlayer2ChatId(),chatId)){
                return session;
            }
        }
        return null;
    }
}