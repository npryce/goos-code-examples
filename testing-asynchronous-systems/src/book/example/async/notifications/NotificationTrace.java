package book.example.async.notifications;

import book.example.async.Timeout;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.ArrayList;
import java.util.List;

public class NotificationTrace<T> {
    private final Object traceLock = new Object();
    private final List<T> trace = new ArrayList<T>();
    private long timeoutMs = 1000L;

    public long getTimeout() {
        return timeoutMs;
    }

    public void setTimeout(long newTimeoutMs) {
        this.timeoutMs = newTimeoutMs;
    }

    public void append(T message) {
        synchronized (traceLock) {
            trace.add(message);
            traceLock.notifyAll();
        }
    }

    public void containsNotification(Matcher<? super T> criteria)
            throws InterruptedException {
        Timeout timeout = new Timeout(timeoutMs);

        synchronized (traceLock) {
            NotificationStream<T> stream = new NotificationStream<T>(trace, criteria);

            while (!stream.hasMatched()) {
                if (timeout.hasTimedOut()) {
                    throw new AssertionError(failureDescriptionFrom(criteria));
                }

                timeout.waitOn(traceLock);
            }
        }
    }

    private String failureDescriptionFrom(Matcher<? super T> acceptanceCriteria) {
        StringDescription description = new StringDescription();

        description.appendText("no message matching ")
                .appendDescriptionOf(acceptanceCriteria)
                .appendText("received within " + timeoutMs + "ms\n");

        if (trace.isEmpty()) {
            description.appendText("received nothing");
        } else {
            description.appendValueList("received:\n   ", "\n   ", "", trace);
        }

        return description.toString();
    }

    public static class NotificationStream<N> {
        private final List<N> notifications;
        private final Matcher<? super N> criteria;
        private int next = 0;

        public NotificationStream(List<N> notifications, Matcher<? super N> criteria) {
            this.notifications = notifications;
            this.criteria = criteria;
        }

        public boolean hasMatched() {
            while (next < notifications.size()) {
                if (criteria.matches(notifications.get(next)))
                    return true;
                next++;
            }
            return false;
        }
    }
}
