package book.example.common.searching;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;


public class StubAuctionHouse implements AuctionHouse {
    private final Map<Set<String>, List<AuctionDescription>> searchResults = new HashMap<Set<String>, List<AuctionDescription>>();
    private final String name;

    public StubAuctionHouse() {
        this.name = getClass().getSimpleName();
    }

    public StubAuctionHouse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void willReturnSearchResults(Set<String> keywords, List<AuctionDescription> results) {
        searchResults.put(keywords, results);
    }

    public List<AuctionDescription> findAuctions(Set<String> keywords) {
        if (searchResults.containsKey(keywords)) {
            return searchResults.get(keywords);
        } else {
            return emptyList();
        }
    }

    public Auction joinAuction(String auctionId) {
        throw new UnsupportedOperationException("not implemented");
    }
}
