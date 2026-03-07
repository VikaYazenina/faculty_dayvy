import kotlin.concurrent.thread

class VisibilityProblem {
	@Volatile
    private var running = true

    fun startWriter(): Thread {
        return Thread {
            // Имитация работы
            repeat(100) {
                Thread.sleep(10)
                Thread.yield()
            }

            runningVolatile = false
            println("Writer: установил running = false ")
        }
    }

    
    fun startReader(): Thread {
        return Thread {
            println("Reader: начал работу")

            while (runningVolatile) {
		Thread.sleep(1)

            }

            println("Reader: завершил работу ")
        }
    }
}
