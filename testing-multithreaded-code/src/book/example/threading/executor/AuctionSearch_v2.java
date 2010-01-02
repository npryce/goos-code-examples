package book.example.threading.executor;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * No synchronisation yet... let's get the functionality sorted
 */
public class AuctionSearch_v2 {
    private final Executor executor;
    private final List<AuctionHouse> auctionHouses;
    private final AuctionSearchConsumer consumer;

    private int runningSearchCount = 0;

    public AuctionSearch_v2(Executor executor,
                            List<AuctionHouse> auctionHouses,
                            AuctionSearchConsumer consumer) {
        this.executor = executor;
        this.auctionHouses = auctionHouses;
        this.consumer = consumer;
    }

    public void search(Set<String> keywords) {
        for (AuctionHouse auctionHouse : auctionHouses) {
            startSearching(auctionHouse, keywords);
        }
    }

    private void startSearching(final AuctionHouse auctionHouse,
                                final Set<String> keywords) {
        runningSearchCount++;

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

        runningSearchCount--;
        if (runningSearchCount == 0) {
            consumer.auctionSearchFinished();
        }
    }
}
