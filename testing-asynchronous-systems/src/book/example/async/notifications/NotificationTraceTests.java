package book.example.async.notifications;

import book.example.threading.races.MultithreadedStressTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class NotificationTraceTests {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    MultithreadedStressTester blitzer = new MultithreadedStressTester(100);

    @After
    public void stopScheduler() {
        scheduler.shutdownNow();
    }

    @Before
    public void startBlitzing() throws InterruptedException {
        blitzer.stress(new Runnable() {
            public void run() {
                trace.append("NOT-WANTED");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @After
    public void stopBlitzer() {
        blitzer.shutdown();
    }

    NotificationTrace<String> trace = new NotificationTrace<String>();

    @Test(timeout = 500)
    public void waitsForMatchingMessage() throws InterruptedException {
        scheduler.schedule(new Runnable() {
            public void run() {
                trace.append("WANTED");
            }
        }, 100, TimeUnit.MILLISECONDS);

        trace.containsNotification(equalTo("WANTED"));
    }

    @Test
    public void failsIfNoMatchingMessageReceived() throws InterruptedException {
        try {
            trace.containsNotification(equalTo("WANTED"));
        }
        catch (AssertionError e) {
            assertThat("error message includes trace of messages received before failure",
                    e.getMessage(), containsString("NOT-WANTED"));
            return;
        }

        fail("should have thrown AssertionError");
    }
}
