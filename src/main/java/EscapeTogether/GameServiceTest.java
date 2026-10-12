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


        // ----------------------------------------
        // Test: Spieler darf nicht seinem eigenen Raum beitreten
        // ----------------------------------------

        // Erstellt einen unabhängigen GameService für den Selbstbeitrittstest
        GameService eigenerTestService = new GameService();

        // Spieler 1 erstellt einen neuen Raum
        GameSession neueSession = eigenerTestService.createGame(spieler1);

        // Spieler 1 versucht, seinem eigenen Raum beizutreten
        boolean eigenerBeitritt = eigenerTestService.joinGame(
                neueSession.getRoomCode(), spieler1
        );

        // Gibt aus, ob Spieler 1 sich selbst beitreten konnte
        System.out.println("Spieler 1 tritt eigenem Raum bei: " + eigenerBeitritt);


        // ----------------------------------------
        // Test: Spielstatus
        // ----------------------------------------

        // Beendet die laufende Spielpartie
        session.finishGame();
        System.out.println("Spiel wurde beendet = " + session.getStatus());

        // Versucht, eine noch wartende Spielpartie zu beenden
        neueSession.finishGame();

        // Prüft, ob der Status weiterhin WAITING ist
        System.out.println("Status nach Beendungsversuch: " + neueSession.getStatus());


        // ----------------------------------------
        // Test: Spielpartie anhand der Chat-ID finden
        // ----------------------------------------

        // Erstellt einen unabhängigen GameService für den Suchtest
        GameService testService = new GameService();

        // Erstellt eine Spielpartie für Spieler 1
        GameSession testSession = testService.createGame(spieler1);

        // Spieler 2 tritt der Partie bei
        testService.joinGame(testSession.getRoomCode(), spieler2);

        // Prüft, ob die Partie über Spieler 1 gefunden wird
        System.out.println("Spieler 1 gefunden: " +
                (testService.getSessionByPlayer(spieler1) == testSession));

        // Prüft, ob die Partie über Spieler 2 gefunden wird
        System.out.println("Spieler 2 gefunden: " +
                (testService.getSessionByPlayer(spieler2) == testSession));

        // Prüft, ob ein unbekannter Spieler keine Partie findet
        System.out.println("Spieler 3 nicht gefunden: " +
                (testService.getSessionByPlayer(spieler3) == null));


        // ----------------------------------------
        // Test: Mehrere aktive Partien verhindern
        // ----------------------------------------

        // Erstellt einen unabhängigen GameService
        GameService testService2 = new GameService();

        // Spieler 1 erstellt seine erste Partie
        GameSession erstePartie = testService2.createGame(spieler1);

        // Spieler 1 versucht, eine zweite Partie zu erstellen
        GameSession zweitePartie = testService2.createGame(spieler1);

        // Prüft, ob die erste Partie erstellt wurde
        System.out.println("Erste Partie erstellt: " + (erstePartie != null));

        // Prüft, ob die zweite Partie verhindert wurde
        System.out.println("Zweite Partie verhindert: " + (zweitePartie == null));

        // ----------------------------------------
// Test: Mehrfachen Beitritt zu aktiven Partien verhindern
// ----------------------------------------

// Erstellt einen unabhängigen GameService für den Test
        GameService beitrittTestService = new GameService();

// Spieler 1 erstellt Raum A
        GameSession raumA = beitrittTestService.createGame(spieler1);

// Spieler 2 tritt Raum A bei
        boolean beitrittRaumA = beitrittTestService.joinGame(
                raumA.getRoomCode(), spieler2
        );

// Spieler 3 erstellt Raum B
        GameSession raumB = beitrittTestService.createGame(spieler3);

// Spieler 2 versucht, zusätzlich Raum B beizutreten
        boolean beitrittRaumB = beitrittTestService.joinGame(
                raumB.getRoomCode(), spieler2
        );

// Überprüft die Ergebnisse
        System.out.println("Spieler 2 tritt Raum A bei: " + beitrittRaumA);
        System.out.println("Spieler 2 tritt Raum B bei: " + beitrittRaumB);

// Überprüft, ob Raum B weiterhin auf einen zweiten Spieler wartet
        System.out.println("Raum B weiterhin WAITING: " +
                (raumB.getStatus() == GameStatus.WAITING));
    }
}