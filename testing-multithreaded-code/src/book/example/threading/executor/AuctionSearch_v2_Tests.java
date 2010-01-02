package book.example.threading.executor;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.StubAuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.DeterministicExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Note: deterministic scheduler won't catch the synchronisation error in the
 * class under test.  So, this makes it easier to test the functionality
 * of the class but you still need to stress test the synchronisation.
 */
@RunWith(JMock.class)
public class AuctionSearch_v2_Tests {
    final Mockery context = new JUnit4Mockery();
    final DeterministicExecutor executor = new DeterministicExecutor();
    final StubAuctionHouse houseA = new StubAuctionHouse("houseA");
    final StubAuctionHouse houseB = new StubAuctionHouse("houseB");

    List<AuctionDescription> resultsFromA = asList(auction(houseA, "1"));
    List<AuctionDescription> resultsFromB = asList(auction(houseB, "2"));
    ;

    final AuctionSearchConsumer consumer = context.mock(AuctionSearchConsumer.class);
    AuctionSearch_v3 search = new AuctionSearch_v3(executor, houses(houseA, houseB), consumer);

    @Test
    public void
    searchesAllAuctionHouses() throws Exception {
        final Set<String> keywords = set("sheep", "cheese");
        houseA.willReturnSearchResults(keywords, resultsFromA);
        houseB.willReturnSearchResults(keywords, resultsFromB);

        context.checking(new Expectations() {{
            final States searching = context.states("searching");

            oneOf(consumer).auctionSearchFound(resultsFromA);
            when(searching.isNot("done"));
            oneOf(consumer).auctionSearchFound(resultsFromB);
            when(searching.isNot("done"));
            oneOf(consumer).auctionSearchFinished();
            then(searching.is("done"));
        }});

        search.search(keywords);
        executor.runUntilIdle();
    }

    @Test
    public void doesNotAnnounceEmptySearchResults() throws Exception {
        final Set<String> keywords = set("keywords");
        houseA.willReturnSearchResults(keywords, resultsFromA);
        houseB.willReturnSearchResults(keywords, noResults());

        context.checking(new Expectations() {{
            oneOf(consumer).auctionSearchFound(resultsFromA);
            oneOf(consumer).auctionSearchFinished();
        }});

        search.search(keywords);
        executor.runUntilIdle();
    }

    private List<AuctionDescription> noResults() {
        return emptyList();
    }

    private List<AuctionHouse> houses(AuctionHouse... houses) {
        return asList(houses);
    }

    private AuctionDescription auction(AuctionHouse house, String id) {
        return new AuctionDescription(house, id, "test auction " + id);
    }

    private Set<String> set(String... strings) {
        return new HashSet<String>(Arrays.asList(strings));
    }
}
