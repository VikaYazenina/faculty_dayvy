import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class LogEntry(
    val dt: LocalDateTime,
    val id: Int,
    val status: String
)


val regexA = Regex("""^(\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2})\s*\|\s*ID\s*:\s*(\d+)\s*\|\s*STATUS\s*:\s*(\w+)$""", RegexOption.IGNORE_CASE)
val regexB = Regex("""^TS\s*=\s*(\d{2}/\d{2}/\d{4}-\d{2}:\d{2})\s*;\s*STATUS\s*=\s*(\w+)\s*;\s*#\s*(\d+)$""", RegexOption.IGNORE_CASE)
val regexC = Regex("""^\[(\d{2}\.\d{2}\.\d{4}\s+\d{2}:\d{2})\]\s*(\w+)\s*\(\s*ID\s*:\s*(\d+)\s*\)$""", RegexOption.IGNORE_CASE)

val formatterA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
val formatterB = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm")
val formatterC = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

fun normalize(line: String): LogEntry? {
    val trimmed = line.trim() 
    fun cleanSpaces(str: String) = str.replace(Regex("\\s+"), " ")

    regexA.matchEntire(trimmed)?.let { match ->
        val dt = LocalDateTime.parse(cleanSpaces(match.groupValues[1]), formatterA)
        return LogEntry(dt, match.groupValues[2].toInt(), match.groupValues[3].lowercase())
    }

    regexB.matchEntire(trimmed)?.let { match ->
        val dt = LocalDateTime.parse(match.groupValues[1], formatterB)
        return LogEntry(dt, match.groupValues[3].toInt(), match.groupValues[2].lowercase())
    }

    regexC.matchEntire(trimmed)?.let { match ->
        val dt = LocalDateTime.parse(cleanSpaces(match.groupValues[1]), formatterC)
        return LogEntry(dt, match.groupValues[3].toInt(), match.groupValues[2].lowercase())
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
        " [22.01.2026 10:05]   DELIVERED   (ID:046) ",
        //грязные данные для доп.задания
        "Битая строка, которая должна уйти в ошибки",
        "2026-01-22 11:00 | ID:099 | STATUS:sent" 
    )

    val (validEntries, corruptedLogs) = logs.map { normalize(it) to it }
        .partition { it.first != null }
    
    val cleanEntries = validEntries.map { it.first!! }
    println("Успешно обработано логов: ${cleanEntries.size}")
    if (corruptedLogs.isNotEmpty()) {
        println("Обнаружены битые строки:")
        corruptedLogs.forEach { println("  - \"${it.second}\"") }
    }

    var hasDuplicates = false
    cleanEntries.groupBy { it.id }.forEach { (id, entries) ->
        val sents = entries.count { it.status == "sent" }
        val delivs = entries.count { it.status == "delivered" }
        if (sents > 1 || delivs > 1) {
            hasDuplicates = true
            println("ID: ${id.toString().padStart(3, '0')} содержит дубли,отправок: $sents, доставок: $delivs")
        }
    }
    if (!hasDuplicates) println("Дубликатов не обнаружено.")

    val successDeliveries = mutableListOf<Pair<Int, Long>>() 
    val incompleteOrders = mutableListOf<Int>()
    val timeErrors = mutableListOf<Int>()

    cleanEntries.groupBy { it.id }.forEach { (id, entries) ->
        val sentEntry = entries.filter { it.status == "sent" }.minByOrNull { it.dt }
        val deliveredEntry = entries.filter { it.status == "delivered" }.maxByOrNull { it.dt }

        if (sentEntry == null || deliveredEntry == null) {
            incompleteOrders.add(id)
        } else {
            val minutes = ChronoUnit.MINUTES.between(sentEntry.dt, deliveredEntry.dt)
            if (minutes < 0) timeErrors.add(id) else successDeliveries.add(id to minutes)
        }
    }

    println("\n[Журнал времени доставки (по убыванию)]")
    successDeliveries.sortByDescending { it.second }
    successDeliveries.forEach { (id, mins) ->
        println("  ID: ${id.toString().padStart(3, '0')} -> $mins мин")
    }

    successDeliveries.firstOrNull()?.let { (id, mins) ->
        println("Самый долгий заказ: ID ${id.toString().padStart(3, '0')} ($mins мин)")
    }
    println("Нарушители правила 20 минут")
    val violators = successDeliveries.filter { it.second > 20 }
    if (violators.isEmpty()) {
        println("Нарушений не зафиксировано.")
    } else {
        violators.forEach { (id, mins) ->
            println("ID ${id.toString().padStart(3, '0')} — доставка заняла $mins мин (превышение на ${mins - 20} мин)")
        }
    }

    if (incompleteOrders.isNotEmpty()) {
        val formattedIncomplete = incompleteOrders.joinToString { it.toString().padStart(3, '0') }
        println("Неполные заказы: ID [$formattedIncomplete]")
    }
    if (timeErrors.isNotEmpty()) {
        val formattedErrors = timeErrors.joinToString { it.toString().padStart(3, '0') }
        println("Аномалии времени (delivered раньше sent): ID [$formattedErrors]")
    }

    println("Пиковые часы доставок (delivered)")
    val hourlyStats = cleanEntries
        .filter { it.status == "delivered" }
        .groupBy { it.dt.hour }
        .mapValues { it.value.size }

    hourlyStats.toSortedMap().forEach { (hour, count) ->
        println("  ${hour.toString().padStart(2, '0')}:00 -> $count шт.")
    }
    
    
}
