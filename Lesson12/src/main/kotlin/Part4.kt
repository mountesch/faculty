import kotlinx.coroutines.*
import java.io.File
import java.net.URL
import kotlin.system.measureTimeMillis

data class DownloadStats(
    val totalTimeMs: Long,
    val successCount: Int,
    val failureCount: Int
)



object ImageDownloader {
    fun run(urls: List<String>, outputDir: String): DownloadStats = runBlocking {
        val dir = File(outputDir)
        if (!dir.exists()) dir.mkdirs()

        var success = 0
        var failure = 0

        val totalTime = measureTimeMillis {
            val jobs = urls.mapIndexed { index, url ->
                async(Dispatchers.IO) {
                    try {
                        val connection = URL(url).openConnection()
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000

                        val bytes = connection.getInputStream().use { it.readBytes() }
                        val file = File(dir, "image_${index + 1}.jpg")
                        file.writeBytes(bytes)

                        println("Downloaded ${index + 1}/${urls.size}")
                        success++
                    } catch (e: Exception) {
                        println("Failed to download ${index + 1}: ${e.message}")
                        failure++
                    }
                }
            }
            jobs.awaitAll()
        }

        DownloadStats(
            totalTimeMs = totalTime,
            successCount = success,
            failureCount = failure
        )
    }
}

fun use() {
    val urls = List(10) { "https://picsum.photos/200/300?random=$it" }
    val stats = ImageDownloader.run(urls, "downloads")
    println("\n=== Download Stats ===")
    println("Total time: ${stats.totalTimeMs} ms")
    println("Successful downloads: ${stats.successCount}")
    println("Failed downloads: ${stats.failureCount}")
}