package book.example.threading.races;

import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AtomicBigCounterTests {
    AtomicBigCounter counter = new AtomicBigCounter();

    @Test
    public void
    isInitiallyZero() {
        assertThat(counter.count(), equalTo(BigInteger.ZERO));
    }

    @Test
    public void
    canIncreaseCounter() {
        counter.inc();
        assertThat(counter.count(), equalTo(BigInteger.valueOf(1)));

        counter.inc();
        assertThat(counter.count(), equalTo(BigInteger.valueOf(2)));

        counter.inc();
        assertThat(counter.count(), equalTo(BigInteger.valueOf(3)));
    }

    @Test
    public void
    canIncrementCounterFromMultipleThreadsSimultaneously() throws InterruptedException {
        MultithreadedStressTester stressTester = new MultithreadedStressTester(25000);

        stressTester.stress(new Runnable() {
            public void run() {
                counter.inc();
            }
        });

        stressTester.shutdown();

        assertThat("final count", counter.count(), equalTo(BigInteger.valueOf(stressTester.totalActionCount())));
    }
}
