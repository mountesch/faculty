import java.math.BigInteger
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object FutureFactorial {
    fun run(): Map<Int, BigInteger> {
        val executor = Executors.newFixedThreadPool(4)
        val futures = mutableMapOf<Int, java.util.concurrent.Future<BigInteger>>()

        for (n in 1..10) {
            val callable = Callable<BigInteger> {
                var fact = BigInteger.ONE
                for (i in 2..n) {
                    fact = fact.multiply(BigInteger.valueOf(i.toLong()))
                }
                fact
            }
            futures[n] = executor.submit(callable)
        }

        val results = mutableMapOf<Int, BigInteger>()
        futures.forEach { (n, future) ->
            results[n] = future.get()
        }

        executor.shutdown()
        return results
    }
}