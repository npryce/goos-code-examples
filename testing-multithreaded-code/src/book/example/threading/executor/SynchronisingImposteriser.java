package book.example.threading.executor;

import org.jmock.api.Imposteriser;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.jmock.internal.StatePredicate;
import org.jmock.lib.JavaReflectionImposteriser;
import org.junit.Assert;

import static org.hamcrest.StringDescription.asString;

/**
 * A Decorator that wraps an Imposteriser and makes the Mockery thread-safe.
 *
 * In the latest version of jMock, this is performed by plugging a Synchroniser into the Mockery
 *
 * @author Nat Pryce
 */
public class SynchronisingImposteriser extends DecoratingImposteriser {
    private final Object sync = new Object();
    private Error firstError = null;

    public SynchronisingImposteriser() {
        this(JavaReflectionImposteriser.INSTANCE);
    }

    public SynchronisingImposteriser(Imposteriser imposteriser) {
        super(imposteriser);
    }

    /**
     * Waits for a StatePredicate to become active.
     * <p/>
     * Warning: this will wait forever unless the test itself has a timeout.
     *
     * @param p the StatePredicate to wait for
     * @throws InterruptedException
     */
    public void waitUntil(StatePredicate p) throws InterruptedException {
        synchronized (sync) {
            while (!p.isActive()) {
                checkForFailure();
                sync.wait();
            }
        }
    }

    /**
     * Waits up to a timeout for a StatePredicate to become active. Fails the test
     * if the timeout expires.
     *
     * @param p         the StatePredicate to wait for
     * @param timeoutMs the timeout in milliseconds
     * @throws InterruptedException
     */
    public void waitUntil(StatePredicate p, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();

        synchronized (sync) {
            while (!p.isActive()) {
                checkForFailure();

                long now = System.currentTimeMillis();
                long timeLeft = timeoutMs - (now - start);

                if (timeLeft <= 0) {
                    Assert.fail("timeout waiting for " + asString(p));
                }

                sync.wait(timeLeft);
            }
        }
    }

    @Override
    protected Object applyInvocation(Invokable imposter, Invocation invocation) throws Throwable {
        synchronized (sync) {
            try {
                return imposter.invoke(invocation);
            } catch (Error e) {
                if (firstError == null) {
                    firstError = e;
                }

                sync.notifyAll();

                throw e;
            } finally {
                sync.notifyAll();
            }
        }
    }

    private void checkForFailure() {
        if (firstError != null) {
            throw firstError;
        }
    }
}
