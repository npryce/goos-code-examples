package book.example.flexibility.ordering;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;

import java.util.List;
import java.util.Set;

public class AuctionSearch {
    private final List<? extends AuctionHouse> auctionHouses;
    private final AuctionSearchConsumer consumer;

    public AuctionSearch(List<? extends AuctionHouse> auctionHouses, AuctionSearchConsumer consumer) {
        this.auctionHouses = auctionHouses;
        this.consumer = consumer;
    }

    public void search(Set<String> keywords) {
        for (AuctionHouse auctionHouse : auctionHouses) {
            search(auctionHouse, keywords);
        }
        consumer.auctionSearchFinished();
    }

    private void search(AuctionHouse auctionHouse, Set<String> keywords) {
        List<AuctionDescription> found = auctionHouse.findAuctions(keywords);
        if (!found.isEmpty()) {
            consumer.auctionSearchFound(found);
        }
    }
}
