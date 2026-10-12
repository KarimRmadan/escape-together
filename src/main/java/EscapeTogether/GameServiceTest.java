package EscapeTogether;

public class GameServiceTest {

    public static void main(String[] args) {

        // Erstellt einen GameService für die Testumgebung
        GameService gameService = new GameService();

        // Simuliert drei Spieler mit unterschiedlichen Chat-IDs
        Long spieler1 = 12345L;
        Long spieler2 = 67890L;
        Long spieler3 = 99999L;

        // Spieler 1 erstellt eine neue Spielpartie
        GameSession session = gameService.createGame(spieler1);

        // Gibt den Spielstatus vor dem Beitritt aus
        System.out.println("Status vor Beitritt: " + session.getStatus());

        // Gibt den erzeugten Raumcode in der Konsole aus
        System.out.println("Raumcode: " + session.getRoomCode());

        // Spieler 2 versucht, der Spielpartie beizutreten
        boolean success = gameService.joinGame(session.getRoomCode(), spieler2);

        // Gibt den Spielstatus nach dem Beitritt aus
        System.out.println("Status nach Beitritt: " + session.getStatus());


        // Gibt aus, ob der Beitritt erfolgreich war
        System.out.println("Spieler 2 beigetreten: " + success);

        // Prüft, ob beide Spielerplätze belegt sind
        System.out.println("Raum vollständig: " + session.isFull());

        // Spieler 3 versucht, einem bereits vollen Raum beizutreten
        boolean dritterSpieler = gameService.joinGame(session.getRoomCode(), spieler3);

        // Gibt aus, ob Spieler 3 beitreten konnte
        System.out.println("Spieler 3 beigetreten: " + dritterSpieler);

        // Erstellt einen neuen Raum für Spieler 1
        GameSession neueSession = gameService.createGame(spieler1);

// Spieler 1 versucht, seinem eigenen Raum beizutreten
        boolean eigenerBeitritt = gameService.joinGame(
                neueSession.getRoomCode(), spieler1
        );

// Gibt aus, ob Spieler 1 sich selbst beitreten konnte
        System.out.println("Spieler 1 tritt eigenem Raum bei: " + eigenerBeitritt);

        session.finishGame();
        System.out.println("Spiel wurde beendet = " + session.getStatus());

        // Versucht, eine noch wartende Spielpartie zu beenden
        neueSession.finishGame();

// Prüft, ob der Status weiterhin WAITING ist
        System.out.println("Status nach Beendungsversuch: " + neueSession.getStatus());
    }
}
