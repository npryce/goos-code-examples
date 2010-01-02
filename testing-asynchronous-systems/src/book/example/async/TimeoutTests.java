package book.example.async;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TimeoutTests {
    @Test
    public void reportsIfTimedOut() throws InterruptedException {
        Timeout timeout = new Timeout(100);
        assertTrue("should not have timed out", !timeout.hasTimedOut());
        Thread.sleep(100);
        assertTrue("should have timed out", timeout.hasTimedOut());
    }

    @Test(timeout = 300)
    public void waitsForTimeout() throws InterruptedException {
        final Object lock = new Object();

        long start = System.currentTimeMillis();
        Timeout timeout = new Timeout(250);

        synchronized (lock) {
            timeout.waitOn(lock);
        }

        long woken = System.currentTimeMillis();

        assertTrue("should have waited until the timeout", (woken - start) >= 250);
    }
}
