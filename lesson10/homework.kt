import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class Logs(
    val date: LocalDateTime,
    val id: Int,
    val status: String
)
data class Delievered(
    val id: Int,
    val senttime: LocalDateTime? = null,
    val deltime: LocalDateTime? = null,
    val Duration: Long?=null,
    val err: Boolean=false,
    val error: String? = null
)
class Log{
    private val regexA=Regex("""(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2}).*?ID:(\d+).*?STATUS:(\w+)""", RegexOption.IGNORE_CASE)
    private val regexB=Regex("""TS=(\d{2}/\d{2}/\d{4})-(\d{2}:\d{2}).*?#(\d+).*?status=(\w+)""", RegexOption.IGNORE_CASE)
    private val regexC=Regex("""\[(\d{2}\.\d{2}\.\d{4})\s+(\d{2}:\d{2})\]\s+(\w+).*?id:(\d+)""", RegexOption.IGNORE_CASE)
    
    private val format=mapOf(
        "A" to DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        "B" to DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
        "C" to DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    )
    fun correct(line: String): Logs?{
        val correctedline=line.trim()
        return when{
            regexA.matches(correctedline) -> parseWithRegex(correctedline, regexA, "A")
            regexB.matches(correctedline) -> parseWithRegex(correctedline, regexB, "B")
            regexC.matches(correctedline) -> parseWithRegex(correctedline, regexC, "C", isFormatC=true)
            else -> null
        }
    }
    private fun parse(line: String, regex: Regex, form: String, isFormatC: Boolean=false): Logs?{
        val match=regex.find(line)?: return null
        return try{
            val groups=match.groupValues
            val dat:String
            val tm: String
            val ids: String
            val stat: String
            if (isFormatC){
                dat=groups[1]
                tm=groups[2]
                ids=groups[4]
                stat=groups[3].lowercase()
            }else{
                dat=groups[1]
                tm=groups[2]
                ids=groups[3]
                stat=groups[4].lowercase()
            }
            if(stat !in setOf("sent", "delivered")){
                return null
            }
            val dt="$dat $tm"
            val formatter=format[form]!!
            val datee=LocalDateTime.parse(dt, formatter)
            val  id=ids.toInt()
            Logs(datee, id, stat)
        }catch (e: Exception){
            null
        }
    }
    fun process(logs: List<String>): Map<String, Any>{
        val valid=mutableListOf<Logs>()
        val broken=mutableListOf<String>()
        logs.forEach{ line -> val entr=correct(line)
        if (entr!=null){
            valid.add(entr)
        }else{
            broken.add(line)
        }
    }
    val groupedById=valid.groupBy{it.id}
    val completed=mutableListOf<Delivered>()
    val incompletedid=mutableListOf<Delivered>()
    val timeerror=mutableListOf<Delivered>()
    groupedById.forEach { (id, entries) ->
            val sentEntries = entries.filter { it.status == "sent" }
            val deliveredEntries = entries.filter { it.status == "delivered" }
            if (sentEntries.size > 1 || deliveredEntries.size > 1) {
    println("ДУБЛИ: ID $id - sent: ${sentEntries.size}, delivered: ${deliveredEntries.size}")
            }
            
            if (sentEntries.isEmpty() || deliveredEntries.isEmpty()) {
                incompletedid.add(Delivered(id, 
                    sentEntries.firstOrNull()?.date,
                    deliveredEntries.firstOrNull()?.date,
                    error = "Неполные данные"
                ))
                return@forEach
            }
            val sentTime = sentEntries.first().date
            val deliveredTime = deliveredEntries.first().date
            
            if (deliveredTime.isBefore(sentTime)) {
                timeerror.add(DeliveryInfo(id, sentTime, deliveredTime,
                    error = "Доставка раньше отправки"
                ))
                return@forEach
            }
            
            val duration = Duration.between(sentTime, deliveredTime).toMinutes()
            completed.add(Delivered(id, sentTime, deliveredTime, duration))
        }
        val sortedDeliveries = completed.sortedByDescending { it.duration }
        
        println("ОТЧЁТ")
        println("1. Все доставки (отсортировано по убыванию длительности):")
        sortedDeliveries.forEach { info ->
            println("   ID: ${info.id}, Длительность: ${info.duration} минут")
        }
        val longestDelivery = sortedDeliveries.firstOrNull()
        if (longestDelivery != null) {
            println("2. Самый долгий заказ:")
            println("   ID: ${longestDelivery.id}, Длительность: ${longestDelivery.duration} минут")
        }
        val violators = sortedDeliveries.filter { it.duration!! > 20 }
        if (violators.isNotEmpty()) {
            println("3. Нарушители (дольше 20 минут):")
            violators.forEach { info ->
                println("   ID: ${info.id}, Длительность: ${info.duration} минут")
            }
        } else {
            println("3. Нарушителей нет.")
        }
        if (incompletedid.isNotEmpty()) {
            println("4. Неполные данные:")
            incompletedid.forEach { info ->
                println("   ID: ${info.id} - ${info.error}")
            }
        }
        if (timeerror.isNotEmpty()) {
            println("5. Ошибки времени:")
            timeerror.forEach { info ->
                println("   ID: ${info.id} - ${info.error}")
            }
        }
        if (broken.isNotEmpty()) {
            println("6. Битные строки:")
            broken.forEach { println("   '$it'") }
        }
        println("7. Статистика по часам (доставки):")
        val deliveriesByHour = valid
            .filter { it.status == "delivered" }
            .groupBy { it.date.hour }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
        
        if (deliveriesByHour.isNotEmpty()) {
            val (hour, count) = deliveriesByHour.first()
            println("   Больше всего доставок в $hour:00 - $count событий")
            deliveriesByHour.forEach { (hour, count) ->
                println("   $hour:00 - $count доставок")
            }
        }
        return mapOf(
            "validEntries" to valid,
            "brokenLines" to broken,
            "completeDeliveries" to completed,
            "incompleteIds" to incompletedid,
            "timeErrorIds" to timeerror,
            "sortedDeliveries" to sortedDeliveries
        )
    }
}

}

fun main(){
    val logs=listOf(
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
    val parser=Log()
    val res=parser.process(logs)
}
