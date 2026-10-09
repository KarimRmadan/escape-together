package EscapeTogether;
// Importiert die Klasse zum Starten und Verwalten der Telegram-Bot-Verbindung
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public class Main {
    public static void main(String[] args) {

        // Liest den Bot-Token aus der Umgebungsvariable TELEGRAM_BOT_TOKEN aus und speichert ihn in dem String
        String token = System.getenv("TELEGRAM_BOT_TOKEN");

        // Prüft, ob der Token fehlt, und beendet in diesem Fall das Programm
        if (token == null) {
                System.out.println("Bot-Token-Fehlt");
                return;
            }

            // Erstellt und verwaltet die Telegram-Anwendung.
            // Die Ressourcen werden nach Verlassen des try-Blocks automatisch geschlossen.

            try (TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication()) {
                // Erstellt einen Telegram-Client mit dem Bot-Token,
                // um Nachrichten über die Telegram-API versenden zu können
                OkHttpTelegramClient client = new OkHttpTelegramClient(token);

                // Registriert unseren EscapeBot mit dem Token bei der Telegram-Anwendung
                app.registerBot(token, new EscapeBot(client));

                //Gibt eine Startmeldung, wenn alles funktioniert
                System.out.println("Escape Together Bot läuft");

                // Hält den Hauptthread aktiv, damit der Telegram-Bot weiterlaufen kann
                Thread.currentThread().join();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
    }

