package book.example.flexibility.ordering;

import java.util.Collection;
import java.util.Set;

public class AuctionSearcher {

    public interface Auction {
        boolean matches(String keyword);
    }

    public interface AuctionSearchListener {
        void searchMatched(Auction auction);

        void searchFinished();
    }

    private final AuctionSearchListener searchListener;
    private final Collection<Auction> auctions;

    public AuctionSearcher(AuctionSearchListener searchListener, Collection<Auction> auctions) {
        this.searchListener = searchListener;
        this.auctions = auctions;
    }

    public void searchFor(Set<String> keywords) {
        for (Auction auction : auctions) {
            announceIfAuctionMatches(auction, keywords);
        }
        searchListener.searchFinished();
    }

    private void announceIfAuctionMatches(Auction auction, Set<String> keywords) {
        for (String keyword : keywords) {
            if (auction.matches(keyword)) {
                searchListener.searchMatched(auction);
                return;
            }
        }
    }
}
