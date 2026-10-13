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

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import java.util.Set;
import java.util.HashSet;


public class EscapeBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    // Verwaltet alle Spielpartien während der Laufzeit des Bots
    private final GameService gameService = new GameService();
   private final Set<Long> waitingForRoomCode = new HashSet<>();

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

                //prüft ob die chatId auf der warteliste steht
                if (waitingForRoomCode.contains(chatId)){
                    join(chatId,message.trim());
                    waitingForRoomCode.remove(chatId);
                    continue;
                }

                // Speichert den Antworttext abhängig vom eingegebenen Befehl
                String antwortText = "";


                // Unterscheidet die Befehle des Nutzers und führt die passende Aktion aus
                switch (parts[0]) {
                    case "/start" -> {
                        InlineKeyboardButton createButton = InlineKeyboardButton.builder()
                                .text("Spiel erstellen")
                                .callbackData("create_game")
                                .build();

                        InlineKeyboardButton joinButton = InlineKeyboardButton.builder()
                                .text("Spiel beitreten")
                                .callbackData("join_game")
                                .build();

                        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                                .keyboardRow(new InlineKeyboardRow(createButton))
                                .keyboardRow(new InlineKeyboardRow(joinButton))
                                .build();


                        // Erstellt die Willkommensnachricht mit dem Button
                        SendMessage startMessage = SendMessage.builder()
                                .chatId(chatId)
                                .text("Willkommen bei Escape Together!")
                                .replyMarkup(keyboard)
                                .build();

                        // Verschickt die Willkommensnachricht an Telegram
                        try {
                            telegramClient.execute(startMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Versucht, Spieler 2 zur Partie hinzuzufügen und speichert das Ergebnis

                    // Verarbeitet die eingegebene Lösung eines Spielers
                    case "/answer" -> {

                        // Prüft, ob nach dem Befehl eine Antwort eingegeben wurde
                        if (parts.length > 1) {

                            // Entfernt den Befehl /answer und überflüssige Leerzeichen
                            String answer = message.substring("/answer".length()).trim();

                            // Sucht die Spielpartie anhand der Chat-ID des Spielers
                            GameSession session = gameService.getSessionByPlayer(chatId);

                            // Prüft, ob der Spieler einer Spielpartie zugeordnet ist
                            if (session == null) {
                                antwortText = "Du bist in keinem aktiven Spiel!";
                            } else {
                                if (session.getStatus() == GameStatus.WAITING) {
                                    antwortText = "Warte auf Spieler 2!";

                                } else {
                                    // Prüft, ob die Spielpartie aktuell läuft
                                    if (session.getStatus() == GameStatus.RUNNING) {

                                        // Holt das aktuelle Rätsel der Spielpartie
                                        Puzzle puzzle = session.getCurrentPuzzle();

                                        // Überprüft, ob die eingegebene Lösung richtig ist
                                        if (puzzle.checkSolution(answer)) {
                                            sendDescription(session.getPlayer1ChatId(), "Richtig ihr habt das Rätsel gelöst!");
                                            sendDescription(session.getPlayer2ChatId(), "Richtig ihr habt das Rätsel gelöst!");

                                            // Wechselt zum nächsten Rätsel, sofern eines vorhanden ist
                                            if (session.nextPuzzle()) {

                                                // Holt das neue Rätsel nach dem erfolgreichen Wechsel
                                                puzzle = session.getCurrentPuzzle();

                                                // Sendet beiden Spielern ihr jeweiliges Rätselbild
                                                sendDescription(session.getPlayer1ChatId(), puzzle.getDescription());
                                                sendPuzzleImage(session.getPlayer1ChatId(), puzzle.getPlayer1Image());

                                                sendDescription(session.getPlayer2ChatId(), puzzle.getDescription());
                                                sendPuzzleImage(session.getPlayer2ChatId(), puzzle.getPlayer2Image());


                                            } else {
                                                // Beendet die Spielpartie, wenn alle Rätsel gelöst wurden
                                                session.finishGame();

                                                // Sendet beiden Spielern das Abschlussbild
                                                sendPuzzleImage(session.getPlayer1ChatId(), "images/abschluss.png");
                                                sendPuzzleImage(session.getPlayer2ChatId(), "images/abschluss.png");
                                            }

                                        } else {
                                            // Informiert den Spieler über eine falsche Lösung
                                            antwortText = "Leider falsch! Versucht es erneut!";
                                        }
                                    } else {
                                        antwortText = "Das Spiel ist bereits beendet!";
                                    }
                                }
                            }
                        } else {
                            // Fordert den Spieler auf, eine Lösung einzugeben
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

                if (!antwortText.isEmpty()) {
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
                }
                // Empfangene Nachricht in der Konsole ausgeben
                System.out.println("Nachricht empfangen: " + message);
            }
// Prüft, ob ein Inline-Button angeklickt wurde
            if (update.hasCallbackQuery()) {

                // Liest aus, welcher Button gedrückt wurde
                String callbackData = update.getCallbackQuery().getData();

                // Ermittelt die Chat-ID des Spielers
                Long chatId = update.getCallbackQuery().getMessage().getChatId();

                // Liest die eindeutige Callback-ID aus
                String callbackId = update.getCallbackQuery().getId();

                // Erstellt die Bestätigung für Telegram
                AnswerCallbackQuery bestaetigung = AnswerCallbackQuery.builder()
                        .callbackQueryId(callbackId)
                        .build();

                // Bestätigt den Button-Klick gegenüber Telegram
                try {
                    telegramClient.execute(bestaetigung);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                // Spieler möchte einen neuen Raum erstellen
                if (callbackData.equals("create_game")) {
                    create(chatId);
                }

                // Spieler möchte einem bestehenden Raum beitreten
                else if (callbackData.equals("join_game")) {

                    // Merkt sich, dass dieser Spieler einen Raumcode eingeben soll
                    waitingForRoomCode.add(chatId);

                    // Fordert den Spieler zur Eingabe des Raumcodes auf
                    sendDescription(chatId, "Bitte gib deinen vierstelligen Raumcode ein.");
                }
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

    public void sendDescription(Long chatId, String description) {

        SendMessage descriptionText = SendMessage.builder()
                .chatId(chatId)
                .text(description)
                .build();
        try {
            telegramClient.execute(descriptionText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // Erstellt eine neue Escape-Room-Partie und sendet den Raumcode an Spieler 1
    public void create(Long chatId) {
        GameSession session = gameService.createGame(chatId);
        if (session != null) {
            String roomcode = session.getRoomCode();
            sendDescription(chatId, "Dein Escape Room wurde erstellt!\nRaumcode: " + roomcode + "\nTeile diesen Code mit Spieler 2!");

        } else {
            sendDescription(chatId, "Du bist bereits in einem aktiven Spiel");

        }
    }

    public void join(Long chatId, String roomCode) {
        boolean success = gameService.joinGame(roomCode, chatId);
        if (success) {
            sendDescription(chatId, "Du bist dem Spiel erfolgreich beigetreten!");

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
            sendDescription(player1ChatId, puzzle.getDescription());
            sendPuzzleImage(player1ChatId, puzzle.getPlayer1Image());

            // Sendet Spieler 2 sein Rätselbild
            sendDescription(chatId, puzzle.getDescription());
            sendPuzzleImage(chatId, puzzle.getPlayer2Image());

        } else {
            sendDescription(chatId, "Beitritt fehlgeschlagen! Raumcode ungültig oder Raum bereits voll.");

        }
    }
}

