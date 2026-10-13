package EscapeTogether;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
// Importiert die Klasse zum Erstellen ausgehender Telegram-Nachrichten
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

// Interface für die Kommunikation mit der Telegram-API
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
// Repräsentiert eine Bilddatei für die Telegram-API
import org.telegram.telegrambots.meta.api.objects.InputFile;

// Ermöglicht das Einlesen von Dateien aus dem Ressourcenordner
import java.io.InputStream;


public class EscapeBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    // Verwaltet alle Spielpartien während der Laufzeit des Bots
    private final GameService gameService = new GameService();

    public EscapeBot(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public void consume(List<Update> updates) {

        // Alle empfangenen Updates nacheinander durchgehen
        for (Update update : updates) {

            // Prüfen, ob das Update eine Nachricht mit Text enthält
            if (update.hasMessage() && update.getMessage().hasText()) {

                // Text aus der Telegram-Nachricht auslesen
                String message = update.getMessage().getText();

                // Teilt die Nachricht in Befehl und mögliche Argumente auf
                String[] parts = message.split(" ");

                // Liest die Chat-ID aus, damit der Bot an den richtigen Chat antworten kann
                Long chatId = update.getMessage().getChatId();
                // Speichert den Antworttext abhängig vom eingegebenen Befehl

                String antwortText = "";
                // Unterscheidet die Befehle des Nutzers und führt die passende Aktion aus
                switch (parts[0]) {
                    case "/start" -> {
                        antwortText = "Willkommen bei Escape Together!";
                    }

                    // Erstellt eine neue Escape-Room-Partie und sendet den Raumcode an Spieler 1
                    case "/create" -> {
                        GameSession session = gameService.createGame(chatId);
                        if (session != null) {
                            String roomcode = session.getRoomCode();
                            antwortText = "Dein Escape Room wurde erstellt!\nRaumcode: " + roomcode + "\nTeile diesen Code mit Spieler 2!";
                        } else {
                            antwortText = "Du bist bereits in einem aktiven Spiel";
                        }
                    }

                    // Versucht, Spieler 2 zur Partie hinzuzufügen und speichert das Ergebnis
                    case "/join" -> {
                        if (parts.length > 1) {
                            String roomCode = parts[1];
                            boolean success = gameService.joinGame(roomCode, chatId);

                            if (success) {
                                antwortText = "Du bist dem Spiel erfolgreich beigetreten!";

                                // Ermittelt die Chat-ID von Spieler 1 und benachrichtigt ihn über den Beitritt
                                GameSession session = gameService.getSession(roomCode);
                                Puzzle puzzle = session.getCurrentPuzzle();
                                Long player1ChatId = session.getPlayer1ChatId();

                                SendMessage benachrichtigung = SendMessage.builder()
                                        .chatId(player1ChatId)
                                        .text("Spieler 2 ist beigetreten")
                                        .build();
                                try {
                                    telegramClient.execute(benachrichtigung);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                                // Sendet Spieler 1 sein Rätselbild
                                sendPuzzleImage(player1ChatId, puzzle.getPlayer1Image());
                                // Sendet Spieler 2 sein Rätselbild
                                sendPuzzleImage(chatId, puzzle.getPlayer2Image());
                            } else {
                                antwortText = "Beitritt fehlgeschlagen! Raumcode ungültig oder Raum bereits voll.";
                            }

                        } else {
                            antwortText = "Bitte gib einen Raumcode ein! Beispiel: /join a3f7";
                        }
                    }

                    case "/answer" -> {
                        if (parts.length > 1) {
                            String answer = message.substring("/answer".length()).trim();
                            GameSession session = gameService.getSessionByPlayer(chatId);
                            if (session == null){
                                antwortText = "Du bist in keinem aktiven Spiel!";
                            }
                            else {
                                if (session.getStatus() == GameStatus.RUNNING) {
                                  Puzzle puzzle =  session.getCurrentPuzzle();
                                  if (puzzle.checkSolution(answer)){
                                      antwortText = "Richtig! Ihr habt das Rätsel gelöst!";
                                      if (session.nextPuzzle()){
                                          puzzle =  session.getCurrentPuzzle();
                                          sendPuzzleImage(session.getPlayer1ChatId(), puzzle.getPlayer1Image());
                                          sendPuzzleImage(session.getPlayer2ChatId(), puzzle.getPlayer2Image());
                                      }else {
                                          session.finishGame();
                                          sendPuzzleImage(session.getPlayer1ChatId(), "images/abschluss.png");
                                          sendPuzzleImage(session.getPlayer2ChatId(), "images/abschluss.png");

                                      }
                                  }
                                  else {
                                      antwortText = "Leider falsch! Versucht es erneut!";
                                  }

                                }
                            }
                        }
                        else {
                            antwortText = "Bitte gib eine Lösung ein! Beispiel: /answer 1234";
                        }

                    }

                    // Zeigt die Spielbeschreibung an, wenn der Nutzer /help eingibt
                    case "/help" -> {
                        antwortText = "Escape Together ist ein kooperativer Escape Room für zwei Spieler!";
                    }

                    // Gibt eine Standardantwort zurück, wenn kein bekannter Befehl eingegeben wurde
                    default -> {
                        antwortText = "Unbekannter Befehl";
                    }
                    case "/testbild" -> {
                        sendPuzzleImage(chatId, "images/puzzle_01_spieler2.png");
                        antwortText = "Rätselbild wurde angefordert!";

                    }

                }

                // Erstellt ein neues Nachrichtenobjekt für die Antwort des Bots
                SendMessage antwort = SendMessage.builder()
                        .chatId(chatId)
                        .text(antwortText)
                        .build();


                // Versendet die erstellte Antwort über den Telegram-Client
                try {
                    telegramClient.execute(antwort);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                // Empfangene Nachricht in der Konsole ausgeben
                System.out.println("Nachricht empfangen: " + message);
            }
        }
    }

    // Lädt ein Rätselbild und schließt den InputStream automatisch
    private void sendPuzzleImage(Long chatId, String imagePath) {

        try (InputStream bild = getClass().getClassLoader()
                .getResourceAsStream(imagePath)) {

            if (bild == null) {
                System.out.println("Bild nicht gefunden: " + imagePath);
                return;
            }

            SendPhoto foto = SendPhoto.builder()
                    .chatId(chatId).photo(new InputFile(bild, "raetsel.png"))
                    .build();

            telegramClient.execute(foto);

        } catch (Exception e) {
            System.out.println("Fehler beim Verarbeiten des Bildes: " + e.getMessage());
        }
    }
}