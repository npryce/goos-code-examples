package book.example.threading.executor;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This fixes the synchronisation error that is detected by the {@link AuctionSearchStressTests}
 */
public class AuctionSearch_v4 {
    private final Executor executor;
    private final List<AuctionHouse> auctionHouses;
    private final AuctionSearchConsumer consumer;
    private final AtomicInteger runningSearchCount = new AtomicInteger();

    public AuctionSearch_v4(Executor executor, List<AuctionHouse> auctionHouses, AuctionSearchConsumer consumer) {
        this.executor = executor;
        this.auctionHouses = auctionHouses;
        this.consumer = consumer;
    }

    public void search(Set<String> keywords) {
        runningSearchCount.set(auctionHouses.size());

        for (AuctionHouse auctionHouse : auctionHouses) {
            startSearching(auctionHouse, keywords);
        }
    }

    private void startSearching(final AuctionHouse auctionHouse, final Set<String> keywords) {
        executor.execute(new Runnable() {
            public void run() {
                search(auctionHouse, keywords);
            }
        });
    }

    private void search(AuctionHouse auctionHouse, Set<String> keywords) {
        // BUG1: THIS IS CORRECT
        List<AuctionDescription> found = auctionHouse.findAuctions(keywords);
        if (!found.isEmpty()) {
            consumer.auctionSearchFound(found);
        }

        // BUG1: THIS IS INCORRECT
        //consumer.auctionSearchFound(auctionHouse.findAuctions(keywords));

        if (runningSearchCount.decrementAndGet() == 0) {
            consumer.auctionSearchFinished();
        }
    }
}
