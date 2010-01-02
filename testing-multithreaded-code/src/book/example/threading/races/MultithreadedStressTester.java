package book.example.threading.races;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A class that "blitzes" an object by calling it many times, from
 * multiple threads.  Used for stress-testing synchronisation.
 *
 * @author nat
 */
public class MultithreadedStressTester {
    /**
     * The default number of threads to run concurrently.
     */
    public static final int DEFAULT_THREAD_COUNT = 2;

    private final ExecutorService executor;
    private final int threadCount;
    private final int iterationCount;


    public MultithreadedStressTester(int iterationCount) {
        this(DEFAULT_THREAD_COUNT, iterationCount);
    }

    public MultithreadedStressTester(int threadCount, int iterationCount) {
        this.threadCount = threadCount;
        this.iterationCount = iterationCount;
        this.executor = Executors.newCachedThreadPool();
    }

    public MultithreadedStressTester(int threadCount, int iterationCount, ThreadFactory threadFactory) {
        this.threadCount = threadCount;
        this.iterationCount = iterationCount;
        this.executor = Executors.newCachedThreadPool(threadFactory);
    }

    public int totalActionCount() {
        return threadCount * iterationCount;
    }

    public void stress(final Runnable action) throws InterruptedException {
        spawnThreads(action).await();
    }

    public void blitz(long timeoutMs, final Runnable action) throws InterruptedException, TimeoutException {
        if (!spawnThreads(action).await(timeoutMs, MILLISECONDS)) {
            throw new TimeoutException("timed out waiting for blitzed actions to complete successfully");
        }
    }

    private CountDownLatch spawnThreads(final Runnable action) {
        final CountDownLatch finished = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        repeat(action);
                    }
                    finally {
                        finished.countDown();
                    }
                }
            });
        }
        return finished;
    }

    private void repeat(Runnable action) {
        for (int i = 0; i < iterationCount; i++) {
            action.run();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}