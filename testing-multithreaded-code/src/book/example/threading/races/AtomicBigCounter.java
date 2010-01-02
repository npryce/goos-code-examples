package book.example.threading.races;

import java.math.BigInteger;

/**
 * This class is not properly synchronised, to demonstrate how the {@link MultithreadedStressTester} works.
 */
public class AtomicBigCounter {
    private BigInteger count = BigInteger.ZERO;

    public BigInteger count() {
        return count;
    }

    public void inc() {
        count = count.add(BigInteger.ONE);
    }
}
