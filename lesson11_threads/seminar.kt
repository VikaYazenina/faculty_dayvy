# Семинар: Многопоточность и корутины в Kotlin

---

## Часть 1. Потоки (Thread)

### Задание 1. Создание потоков
Создайте 3 потока с именами "Thread-A", "Thread-B", "Thread-C". Каждый поток должен вывести своё имя 5 раз с задержкой 500мс.


object CreateThreads {
    fun run(): List<Thread> {
    val threads=listOf{"Thread-A",
    "Thread-B",
    "Thread-C"}
    val running=threads.map{ thread -> Thread{
        println(Thread.currentThread().name)
        repeat(5){
            println("Hello from: $name")
            Thraed.sleep(500)
        }
    }.apply{this.name=name}
    threads.forEach
    {it.start()}
    threads.forEach{it.join()}
    return threads
    }
    
}

### Задание 2. Race condition
Создайте переменную `counter = 0`. Запустите 10 потоков,
каждый из которых увеличивает counter на 1000.
Выведите финальное значение и объясните результат.


object RaceCondition {
    fun run(): Int {
        val counter=0
        val threads=mutableListOf<Thread>()
        repeat(10){
        val thr=Thread{
            repeat(1000){
            counter++
            }
        }
        threads+=thr 
        thr.start()
        }
        
        threads.forEach{it.join()}
        return counter
        
    }
}



### Задание 3. Synchronized
Исправьте задание 2 с помощью `@Synchronized` или `synchronized {}` блока, чтобы результат всегда был 10000.


object SynchronizedCounter {
    fun run(): Int {
    val counter=0
    val lock=Any()
        val threads=mutableListOf<Thread>()
        repeat(10){
        val thr=Thread{
            repeat(1000){
            synchronized(lock){
                counter++
            }
            }
        }
        threads+=thr 
        thr.start()
        }
        
        threads.forEach{it.join()}
        return counter
        
    }
}


### Задание 4. Deadlock
Создайте пример deadlock с двумя ресурсами и двумя потоками. Затем исправьте его.


object Deadlock {
    
    fun runDeadlock() {
     val otvertka=Any()
    val kluch=Any()
    val thread1=Thread{
        synchronized(otvertka){
            println("Thread1: у меня есть отвертка")
            Thread.sleep(100)
            synchronized(kluch){
                println("Thread1: у меня есть ключ")
            }
        }
    }
    val thread2=Thread{
        synchronized(kluch){
            println("Thread2:у меня есть ключ")
            Thread.sleep(100)
            synchronized(otvertka){
                println("Thread2: у меня есть отвертка")
            }
        }
    }
    thread1.start()
    thread2.start()
    thread1.join()
    thread2.join()
    println("Task4 result: Deadlock detected")
        
    }

    fun runFixed(): Boolean {
        val otvertka=Any()
    val kluch=Any()
    val thread1=Thread{
        synchronized(otvertka){
            println("Fixed Thread1: у меня есть отвертка")
            Thread.sleep(100)
            synchronized(kluch){
                println("Fixed Thread1: у меня есть ключ")
            }
        }
    }
    val thread2=Thread{
        synchronized(otvertka){
            println("Fixed Thread2: у меня есть отвертка")
            Thread.sleep(100)
            synchronized(kluch){
                println("Fixed Thread2: у меня есть ключ")
            }
        }
    }
    thread1.start()
    thread2.start()
    thread1.join()
    thread2.join()
    println("Task4 result: no deadlock")
        return true
    }
}


## Часть 2. Executor Framework

### Задание 5. ExecutorService
Используя `Executors.newFixedThreadPool(4)`, выполните 20 задач. Каждая задача выводит свой номер и имя потока, затем спит 200мс.


import java.util.concurrent.Executors
import java.util.concurrent.CopyOnWriteArrayList
object ExecutorServiceExample {
    fun run(): List<String> {
        val executor =Executors.newFixedThreadPool(4)
        val threadNames = CopyOnWriteArrayList<String>()
        repeat(20){ taskNum ->
        executor.submit{
            val threadName= Thread.currentThread().name
            println("Task $taskNum executed by $threadName")
            threadNames.add(threadName)
            Thread.sleep(200)
        }
    }
    executor.shutdown()
    while (!executor.isTerminated){
        Thread.sleep(50)
    })
    return threadNames.toList()
}
  

### Задание 6. Future
Используя ExecutorService и `Callable`, параллельно вычислите факториалы чисел от 1 до 10. Соберите результаты через `Future.get()`.


 object FutureFactorial {
    fun run(): Map<Int, BigInteger> {
        val executor=Executors.newFixedThreadPool(4)
        val tasks=(1..10).map{ n -> able<Pair<Int, BigInteger>>{
          n to factorial(n)
        }
    }
    val futures=tasks.map {executor.submit(it)}
    val res=futures.associate {it.get() }
    executor.shutdown()
    return res
}
  private fun factorial(n: Int): BigInteger{
    var res=BigInteger.Cloneablefor(i in 2..n){
      res=res.multiplay(BigInteger.valueOf(i.toLong()))
    }
    return res
  }
}


## Часть 3. Корутины

### Задание 7. Первая корутина
Используя `runBlocking` и `launch`, запустите 3 корутины, каждая из которых выводит своё имя 5 раз с `delay(500)`.


object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val output=mutableListOf<String>()
        val names=listOf("Coroutine1", "Coroutine2", "Coroutine3")
        val work=names.map{ name -> launch{
          repeat(5){
            output.add(name)
            delay(500)
          }
          }
        }
        work.forEach{it.join()}
        output
    }
}

 
### Задание 8. async/await
Используя `async`, параллельно вычислите сумму чисел от 1 до 1_000_000, разбив на 4 части. Соберите результаты через `await()`.


object AsyncAwait {
    fun run(): Long = runBlocking {
        val rangesize=1000000/4
        val def=(0 until 4).map{ i -> 
        async{
          val start=i*rangesize+1 
          val end=if(i==3) 1000000 else (i+1)*rangesize
          (start..end).sumOf{it.toLong()}
        }
    }
    def.sumOf{it.await()}
    }
}


### Задание 9. Structured concurrency
Создайте корутину, которая запускает 5 дочерних корутин. Если одна из них падает с исключением, все остальные должны отмениться.


object StructuredConcurrency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        var com=0
        try{
          coroutineScope{
            repeat(5){ index ->
            launch{
              if(index==failingCoroutineIndex){
                throw RuntimeException("Fai in coroutine $index")
              }
              delay(100L*index)
              com++
            }
          }
        }
    }catch(e:Exception){
    }
    com
    }
}



### Задание 10. withContext
Используя `withContext(Dispatchers.IO)`, прочитайте содержимое 3 файлов параллельно и объедините результаты.


object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking {
        val def=filePaths.map{ path->
        async(Dispatchers.IO){
          path to File(path).readText()
        }
        }
        def.awaitAll().toMap()
    }
}

 
## Часть 4. Практическое задание

### Задание 11. Многопоточный загрузчик изображений

Напишите программу, которая параллельно скачивает изображения из интернета.

**Требования:**
1. Использовать корутины с `Dispatchers.IO`
2. Скачать 10 изображений с https://picsum.photos/200/300
3. Сохранить в папку `downloads/`
4. Вывести прогресс: "Downloaded 1/10", "Downloaded 2/10", ...
5. В конце вывести статистику: общее время, количество успешных/неуспешных загрузок


object ImageDownloader {
    fun run(urls: List<String>, outputDir: String): DownloadStats = runBlocking {
        val  dir=File(outputDir)
        if(!dir.exists()) dir.mkdirs()
        var success=0
        val failure=0
        val time=measureTimeMillis{
          val work=urls.mapIndexed{ index, url ->
          async(Dispatchers.IO){
            try{
              val bytes=URL(url).openStream().use {it.readBytes()}
              File(dir, "image_${index+1}.jpg").writeBytes(bytes)
              println("Downloaded ${index+1}/${urls.size}: ${e.message}")
              false
            }
          }
        }
        val res=work.awaitAll()
        sucess=res.count{it}
        failure=res.size-sucess
        }
        DownloadedStats(time, sucess, failure)
    }
}

   
