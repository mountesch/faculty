interface TBankEmployee {
    fun writeCode()
    fun deployToProduction()
    fun answerClientCall()
    fun processLoanRequest()
}

class Developer(private val name: String) : TBankEmployee {

    override fun writeCode() {
        println("$name пишет код")
    }

    override fun deployToProduction() {
        println("$name деплоит в прод")
    }

    override fun answerClientCall() {
        println("$name не отвечает на звонки клиентов")
    }

    override fun processLoanRequest() {
        println("$name не рассматривает кредитные заявки")
    }
}

class SupportOperator(private val name: String) : TBankEmployee {

    override fun writeCode() {
        println("$name не пишет код")
    }

    override fun deployToProduction() {
        println("$name не деплоит в прод")
    }

    override fun answerClientCall() {
        println("$name отвечает на звонки клиентов")
    }

    override fun processLoanRequest() {
        println("$name не обрабатывает кредитные заявки")
    }
}

class LoanManager(private val name: String) : TBankEmployee {

    override fun writeCode() {
        println("$name не пишет код")
    }

    override fun deployToProduction() {
        println("$name не деплоит в прод")
    }

    override fun answerClientCall() {
        println("$name не отвечает на звонки клиентов")
    }

    override fun processLoanRequest() {
        println("$name рассматривает кредитную заявку")
    }
}

fun main() {
    val dev = Developer("Алексей")
    val support = SupportOperator("Мария")
    val loanManager = LoanManager("Игорь")

    dev.writeCode()
    dev.processLoanRequest()

    support.answerClientCall()
    support.deployToProduction()

    loanManager.processLoanRequest()
    loanManager.writeCode()
}
