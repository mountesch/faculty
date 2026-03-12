import kotlinx.coroutines.*

object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val names = listOf("Coroutine-A", "Coroutine-B", "Coroutine-C")
        val output = mutableListOf<String>()

        val jobs = names.map { name ->
            launch {
                repeat(5) {
                    val msg = "Hello from $name"
                    println(msg)
                    output.add(msg)
                    delay(500)
                }
            }
        }

        jobs.forEach { it.join() }
        output
    }
}

object AsyncAwait {
    fun run(): Long = runBlocking {
        val range = 1..1_000_000
        val chunkSize = range.last / 4
        val deferred = (0..3).map { i ->
            async {
                val start = i * chunkSize + 1
                val end = if (i == 3) range.last else (i + 1) * chunkSize
                (start..end).sumOf { it.toLong() }
            }
        }
        deferred.sumOf { it.await() }
    }
}

object StructuredConcurrency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        var completed = 0
        try {
            coroutineScope {
                (1..5).forEach { index ->
                    launch {
                        if (index == failingCoroutineIndex) {
                            throw RuntimeException("Coroutine $index failed")
                        } else {
                            delay(500)
                            println("Coroutine $index completed")
                            completed++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Exception caught: ${e.message}")
        }
        completed
    }
}

import java.io.File

object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking {
        val results = mutableMapOf<String, String>()
        val jobs = filePaths.map { path ->
            async(Dispatchers.IO) {
                val content = File(path).readText()
                results[path] = content
            }
        }
        jobs.awaitAll()
        results
    }
}