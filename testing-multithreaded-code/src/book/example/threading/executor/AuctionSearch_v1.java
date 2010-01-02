package book.example.threading.executor;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AuctionSearch_v1 {
    private final List<AuctionHouse> auctionHouses;
    private final AuctionSearchConsumer consumer;
    private final AtomicInteger runningSearchCount = new AtomicInteger();

    public AuctionSearch_v1(List<AuctionHouse> auctionHouses, AuctionSearchConsumer consumer) {
        this.auctionHouses = auctionHouses;
        this.consumer = consumer;
    }

    public void search(Set<String> keywords) {
        //BUG2: THIS IS CORRECT
        runningSearchCount.set(auctionHouses.size());

        for (AuctionHouse auctionHouse : auctionHouses) {
            startSearching(auctionHouse, keywords);
        }
    }

    private void startSearching(final AuctionHouse auctionHouse, final Set<String> keywords) {
        //BUG2: THIS IS INCORRECT
        //searchCount.incrementAndGet();

        Thread searchThread = new Thread(new Runnable() {
            public void run() {
                search(auctionHouse, keywords);
            }
        });
        searchThread.start();
    }

    private void search(AuctionHouse auctionHouse, Set<String> keywords) {
        List<AuctionDescription> found = auctionHouse.findAuctions(keywords);

        //BUG1: THIS IS CORRECT
        if (!found.isEmpty()) {
            consumer.auctionSearchFound(found);
        }

        //BUG1: THIS IS INCORRECT
        //consumer.auctionSearchFound(found);

        if (runningSearchCount.decrementAndGet() == 0) {
            consumer.auctionSearchFinished();
        }
    }
}
