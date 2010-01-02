package book.example.common.searching;

import java.util.List;
import java.util.Set;

public interface AuctionHouse {
    List<AuctionDescription> findAuctions(Set<String> keywords);

    Auction joinAuction(String auctionId);
}
