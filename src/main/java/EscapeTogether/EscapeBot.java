package EscapeTogether;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

public class EscapeBot implements LongPollingUpdateConsumer {

    @Override
    public void consume(List<Update> updates) {

        // Alle empfangenen Updates nacheinander durchgehen
        for (Update update : updates) {

            // Prüfen, ob das Update eine Nachricht mit Text enthält
            if (update.hasMessage() && update.getMessage().hasText()) {

                // Text aus der Telegram-Nachricht auslesen
                String message = update.getMessage().getText();

                // Empfangene Nachricht in der Konsole ausgeben
                System.out.println("Nachricht empfangen: " + message);
            }
        }
    }
}