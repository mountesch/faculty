
// Плохое решение
// Проблемы:
// хотим перейти на Telegram-бота или email → надо лезть внутрь HomeworkReminder и переписывать код;
// в тестах нельзя подставить «фейкового отправителя», придётся реально слать SMS или эмулировать его.



// Хорошее решение
interface MessageSender {
    fun send(to: String, text: String)
}

class SmsSender : MessageSender {
    override fun send(to: String, text: String) {
        println("SMS на $to: $text")
    }
}

class EmailSender : MessageSender {
    override fun send(to: String, text: String) {
        println("Email на $to: $text")
    }
}

class HomeworkReminder(
    private val sender: MessageSender   // зависимость приходит снаружи
) {
    fun remind(to: String, homeworkText: String) {
        val message = "Не забудь сделать: $homeworkText"
        sender.send(to, message)
    }
}
