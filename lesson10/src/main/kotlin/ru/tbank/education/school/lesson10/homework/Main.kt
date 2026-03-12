import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

data class LogEntry(val dt: LocalDateTime, val id: Int, val status: String)

fun normalize(line: String): LogEntry? {
    val trimmed = line.trim()

    val patterns = listOf(
        Regex("""(\d{4}-\d{2}-\d{2} \d{2}:\d{2})\s*\|\s*id\s*:\s*(\d+)\s*\|\s*status\s*:\s*(sent|delivered)""", RegexOption.IGNORE_CASE),
        Regex("""ts\s*=\s*(\d{2}/\d{2}/\d{4}-\d{2}:\d{2})\s*;\s*status\s*=\s*(sent|delivered)\s*;\s*#(\d+)""", RegexOption.IGNORE_CASE),
        Regex("""\[\s*(\d{2}\.\d{2}\.\d{4} \d{2}:\d{2})\s*\]\s*(sent|delivered)\s*\(id\s*:\s*(\d+)\)""", RegexOption.IGNORE_CASE)
    )

    for ((index, pattern) in patterns.withIndex()) {
        val match = pattern.find(trimmed)
        if (match != null) {
            val groups = match.groupValues
            return when (index) {
                0 -> {
                    val dt = LocalDateTime.parse(groups[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    val id = groups[2].toInt()
                    val status = groups[3].lowercase()
                    LogEntry(dt, id, status)
                }
                1 -> {
                    val dt = LocalDateTime.parse(groups[1], DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"))
                    val id = groups[3].toInt()
                    val status = groups[2].lowercase()
                    LogEntry(dt, id, status)
                }
                2 -> {
                    val dt = LocalDateTime.parse(groups[1], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                    val id = groups[3].toInt()
                    val status = groups[2].lowercase()
                    LogEntry(dt, id, status)
                }
                else -> null
            }
        }
    }
    return null
}


fun main() {
    val logs = listOf(
        "2026-01-22 09:14 | ID:042 | STATUS:sent",
        "TS=22/01/2026-09:27; status=delivered; #042",
        "2026-01-22 09:10 | ID:043 | STATUS:sent",
        "2026-01-22 09:18 | ID:043 | STATUS:delivered",
        "TS=22/01/2026-09:05; status=sent; #044",
        "[22.01.2026 09:40] delivered (id:044)",
        "2026-01-22 09:20 | ID:045 | STATUS:sent",
        "[22.01.2026 09:33] delivered (id:045)",
        "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
        " [22.01.2026 10:05]   DELIVERED   (ID:046) "
    )

    val norm = mutableListOf<LogEntry>()
    val broken = mutableListOf<String>()

    for (line in logs) {
        val entry = normalize(line)
        if (entry != null) norm.add(entry) else broken.add(line)
    }

    println("Нормализованные записи:")
    norm.forEach { println(it) }
    println("\nБитые строки:")
    broken.forEach { println(it) }

    val eventsById = norm.groupBy { it.id }
    val times = mutableMapOf<Int, Long>()
    val incomplete = mutableListOf<Int>()
    val timeErrors = mutableListOf<Int>()

    val dupl = mutableMapOf<Int, MutableMap<String, Int>>()

    for ((id, records) in eventsById) {
        val sent = records.filter { it.status == "sent" }.map { it.dt }
        val delivered = records.filter { it.status == "delivered" }.map { it.dt }

        if (sent.size > 1 || delivered.size > 1) {
            dupl[id] = mutableMapOf("sent" to sent.size, "delivered" to delivered.size)
        }

        if (sent.isEmpty() || delivered.isEmpty()) {
            incomplete.add(id)
            continue
        }

        val delta = java.time.Duration.between(sent.first(), delivered.first()).toMinutes()
        if (delta < 0) {
            timeErrors.add(id)
            continue
        }

        times[id] = delta
    }

    println("\nДлительность доставки (мин):")
    times.forEach { (id, mins) -> println("ID $id: $mins мин") }
    println("Неполные записи: $incomplete")
    println("Ошибки времени: $timeErrors")
    println("Дубликаты: $dupl")

    val srt = times.entries.sortedByDescending { it.value }
    println("\nСписок ID с длительностью доставки (по убыванию):")
    srt.forEach { println("ID ${it.key}: ${it.value} мин") }

    if (srt.isNotEmpty()) {
        val longest = srt.first()
        println("\nСамый долгий заказ: ID ${longest.key}, ${longest.value} мин")
    }

    val violators = times.filter { it.value > 20 }.keys
    println("\nНарушители (доставка >20 мин): $violators")

    val hrs = norm.filter { it.status == "delivered" }.map { it.dt.hour }
    val hourCount = hrs.groupingBy { it }.eachCount()
    val common = hourCount.maxByOrNull { it.value }
    println("\nЧас с наибольшим количеством доставок: ${common?.key}:00, ${common?.value} событий")
}