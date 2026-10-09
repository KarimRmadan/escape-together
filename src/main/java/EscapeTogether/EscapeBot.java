package EscapeTogether;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EscapeBot implements LongPollingSingleThreadUpdateConsumer {

    //consume verarbeitet eingehende Telegram-Updates
    @Override
    public void consume(Update update) {

        //hasMessage prüft, ob eine Nachricht vorhanden ist
        //hasText prüft, ob die Nachricht Text enthält
        if (update.hasMessage() && update.getMessage().hasText()) {

            String message = update.getMessage().getText();

            System.out.println("Nachricht empfangen: " + message);
        }
    }
}