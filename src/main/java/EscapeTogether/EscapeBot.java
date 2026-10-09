package EscapeTogether;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;
// Importiert die Klasse zum Erstellen ausgehender Telegram-Nachrichten
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

// Interface für die Kommunikation mit der Telegram-API
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

// Implementierung des Clients zum Senden von Telegram-Nachrichten
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public class EscapeBot implements LongPollingUpdateConsumer {
    private final TelegramClient telegramClient;

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

                // Liest die Chat-ID aus, damit der Bot an den richtigen Chat antworten kann
                Long chatId = update.getMessage().getChatId();

                // Erstellt ein neues Nachrichtenobjekt für die Antwort des Bots
                SendMessage antwort = SendMessage.builder()
                        .chatId(chatId)
                        .text("Willkommen bei Escape Together!" )
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
}