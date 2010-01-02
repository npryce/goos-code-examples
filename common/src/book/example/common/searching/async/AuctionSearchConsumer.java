package book.example.common.searching.async;

import book.example.common.searching.AuctionDescription;

import java.util.List;

public interface AuctionSearchConsumer {
    void auctionSearchFound(List<AuctionDescription> auctions);

    void auctionSearchFinished();
}
