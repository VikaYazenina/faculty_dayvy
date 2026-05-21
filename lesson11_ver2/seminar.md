# Семинар: Многопоточность и корутины в Kotlin

---

## Часть 1. Потоки (Thread)

### Задание 1. Создание потоков
Создайте 3 потока с именами "Thread-A", "Thread-B", "Thread-C". Каждый поток должен вывести своё имя 5 раз с задержкой 500мс.

```kotlin
object CreateThreads {
    fun run(): List<Thread> {
        val names = listOf("Thread-A", "Thread-B", "Thread-C") 
        
        val threads = names.map { name ->
            Thread {
                repeat(5) { 
                    println("$name выводит сообщение") 
                    Thread.sleep(500)
                }
            }.apply { 
                this.name = name
                start() 
            }
        }
        threads.forEach { it.join() }
        return threads
    }
}
```

### Задание 2. Race condition
Создайте переменную `counter = 0`. Запустите 10 потоков, каждый из которых увеличивает counter на 1000. Выведите финальное значение и объясните результат.

```kotlin
object RaceCondition {
    fun run(): Int {
        var counter = 0 
        val threads = List(10) { 
            Thread {
                repeat(1000) { 
                    counter++ 
                }
            }.apply { start() }
        }
        
        threads.forEach { it.join() }
        println("Финальное значение counter: $counter") 
        return counter
    }
}
```

### Задание 3. Synchronized
Исправьте задание 2 с помощью `@Synchronized` или `synchronized {}` блока, чтобы результат всегда был 10000.

```kotlin
object SynchronizedCounter {
    fun run(): Int {
        var counter = 0
        val lock = Any()
        
        val threads = List(10) { 
            Thread {
                repeat(1000) { 
                    synchronized(lock) { 
                        counter++
                    }
                }
            }.apply { start() }
        }
        
        threads.forEach { it.join() }
        return counter
    }
}
```

### Задание 4. Deadlock
Создайте пример deadlock с двумя ресурсами и двумя потоками. Затем исправьте его.

```kotlin
object Deadlock {
    private val res1 = Any()
    private val res2 = Any()

    fun runDeadlock() { 
        val thread1 = Thread {
            synchronized(res1) {
                Thread.sleep(50)
                synchronized(res2) {
                    println("Thread 1 захватил оба ресурса")
                }
            }
        }

        val thread2 = Thread {
            synchronized(res2) {
                Thread.sleep(50)
                synchronized(res1) {
                    println("Thread 2 захватил оба ресурса")
                }
            }
        }

        thread1.start()
        thread2.start()
        thread1.join(2000)
        thread2.join(2000)
    }

    fun runFixed(): Boolean { 
        val thread1 = Thread {
            synchronized(res1) {
                Thread.sleep(50)
                synchronized(res2) { println("Thread 1 в норме") }
            }
        }

        val thread2 = Thread {
            synchronized(res1) { 
                Thread.sleep(50)
                synchronized(res2) { println("Thread 2 в норме") }
            }
        }

        thread1.start()
        thread2.start()

        thread1.join()
        thread2.join()
        return true
    }
}
```

---

## Часть 2. Executor Framework

### Задание 5. ExecutorService
Используя `Executors.newFixedThreadPool(4)`, выполните 20 задач. Каждая задача выводит свой номер и имя потока, затем спит 200мс.

```kotlin
import java.util.concurrent.Executors
import java.util.concurrent.CopyOnWriteArrayList

object ExecutorServiceExample {
    fun run(): List<String> {
        val logList = CopyOnWriteArrayList<String>()
        val executor = Executors.newFixedThreadPool(4) 
        repeat(20) { index -> 
            executor.submit {
                val log = "Задача #$index выполняется в потоке: ${Thread.currentThread().name}" 
                println(log)
                logList.add(log)
                Thread.sleep(200) 
            }
        }

        executor.shutdown()
        while (!executor.isTerminated) { Thread.sleep(10) } 
        return logList
    }
}
```

### Задание 6. Future
Используя ExecutorService и `Callable`, параллельно вычислите факториалы чисел от 1 до 10. Соберите результаты через `Future.get()`.

```kotlin

import java.math.BigInteger
import java.util.concurrent.Executors
import java.util.concurrent.Future

object FutureFactorial {
    fun run(): Map<Int, BigInteger> {
        val executor = Executors.newFixedThreadPool(4)
        val futures = mutableMapOf<Int, Future<BigInteger>>() 
        fun calculateFactorial(n: Int): BigInteger {
            var result = BigInteger.ONE
            for (i in 2..n) {
                result = result.multiply(BigInteger.valueOf(i.toLong()))
            }
            return result
        }
        for (i in 1..10) { 
            val future = executor.submit<BigInteger> { calculateFactorial(i) }
            futures[i] = future
        }

        val results = futures.mapValues { it.value.get() } 

        executor.shutdown()
        return results
    }
}
```

---

## Часть 3. Корутины

### Задание 7. Первая корутина
Используя `runBlocking` и `launch`, запустите 3 корутины, каждая из которых выводит своё имя 5 раз с `delay(500)`.

```kotlin
import kotlinx.coroutines.*
import java.util.concurrent.CopyOnWriteArrayList

object CoroutineLaunch {
    fun run(): List<String> = runBlocking { 
        val logs = CopyOnWriteArrayList<String>()
        
        val jobs = listOf("Coroutine-1", "Coroutine-2", "Coroutine-3").map { name ->
            launch { 
                repeat(5) { 
                    val message = "$name выводит сообщение" 
                    println(message)
                    logs.add(message)
                    delay(500) 
                }
            }
        }
        
        jobs.joinAll() 
        logs
    }
}
```

### Задание 8. async/await
Используя `async`, параллельно вычислите сумму чисел от 1 до 1_000_000, разбив на 4 части. Соберите результаты через `await()`.

```kotlin

import kotlinx.coroutines.*

object AsyncAwait {
    fun run(): Long = runBlocking { 
        val totalNumbers = 1_000_000 
        val parts = 4
        val chunkSize = totalNumbers / parts 
        val deferreds = (0 until parts).map { part ->
            async { 
                val start = part * chunkSize + 1
                val end = (part + 1) * chunkSize
                var partialSum = 0L
                for (i in start..end) {
                    partialSum += i
                }
                partialSum
            }
        }
        val totalSum = deferreds.awaitAll().sum() 
        totalSum
    }
}
```

### Задание 9. Structured concurrency
Создайте корутину, которая запускает 5 дочерних корутин. Если одна из них падает с исключением, все остальные должны отмениться.

```kotlin

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

object StructuredConcurrency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking { 
        val successCounter = AtomicInteger(0)

        try {
            coroutineScope { 
                List(5) { index -> 
                    launch { 
                        if (index == failingCoroutineIndex) { 
                            delay(100)
                            throw IllegalStateException("Корутина $index упала") 
                        } else {
                            delay(500) 
                            successCounter.incrementAndGet()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Исключение: ${e.message}") 
        }
        successCounter.get()
    }
}
```

### Задание 10. withContext
Используя `withContext(Dispatchers.IO)`, прочитайте содержимое 3 файлов параллельно и объедините результаты.

```kotlin

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking { 
        val results = ConcurrentHashMap<String, String>()
        withContext(Dispatchers.IO) { 
            val jobs = filePaths.map { path ->
                launch { 
                    try {
                        val content = "Содержимое файла по пути $path" 
                        results[path] = content
                    } catch (e: Exception) {
                        results[path] = "Ошибка чтения: ${e.message}"
                    }
                }
            }
            jobs.joinAll()
        }

        results 
    }
}
```

---

## Часть 4. Практическое задание

### Задание 11. Многопоточный загрузчик изображений

Напишите программу, которая параллельно скачивает изображения из интернета.

**Требования:**
1. Использовать корутины с `Dispatchers.IO`
2. Скачать 10 изображений с https://picsum.photos/200/300
3. Сохранить в папку `downloads/`
4. Вывести прогресс: "Downloaded 1/10", "Downloaded 2/10", ...
5. В конце вывести статистику: общее время, количество успешных/неуспешных загрузок

```kotlin

data class DownloadStats(
    val totalTimeMs: Long, 
    val successfulCount: Int,
    val failedCount: Int 
)


import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

object ImageDownloader {
    fun run(urls: List<String>, outputDir: String): DownloadStats = runBlocking { 
        val startTime = System.currentTimeMillis()
        val successCounter = AtomicInteger(0)
        val failedCounter = AtomicInteger(0)
        val totalDownloaded = AtomicInteger(0)

        val dir = File(outputDir) 
        if (!dir.exists()) dir.mkdirs()
        withContext(Dispatchers.IO) { 
            val downloadJobs = urls.mapIndexed { index, urlString ->
                launch { 
                    try {
                        val url = URL(urlString)
                        val connection = url.openConnection()
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000

                        val file = File(dir, "image_${index + 1}.jpg")
                        url.openStream().use { inputStream ->
                            file.outputStream().use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        successCounter.incrementAndGet()
                    } catch (e: Exception) {
                        failedCounter.incrementAndGet()
                    } finally {
                        val currentTotal = totalDownloaded.incrementAndGet()
                        println("Downloaded $currentTotal/${urls.size}") 
                    }
                }
            }
            downloadJobs.joinAll() 
        }

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime 

        val stats = DownloadStats(
            totalTimeMs = totalTime, 
            successfulCount = successCounter.get(),
            failedCount = failedCounter.get() 
        )
        println("Общее время: ${stats.totalTimeMs} мс") 
        println("Успешно загружено: ${stats.successfulCount}") 
        println("Неудачно загружено: ${stats.failedCount}") 

        stats
    }
}

// Пример вызова
fun main() {
    
    val imageUrls = List(10) { "https://picsum.photos/200/300" } 
    ImageDownloader.run(imageUrls, "downloads") 
}
```
