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
public class AuctionSearchTests_v3 {
    Mockery context = new JUnit4Mockery();
    AuctionSearchConsumer consumer = context.mock(AuctionSearchConsumer.class, "consumer");

    StubAuctionHouse auctionHouseA = new StubAuctionHouse("houseA");
    StubAuctionHouse auctionHouseB = new StubAuctionHouse("houseB");

    List<AuctionDescription> resultsFromA = asList(auction(auctionHouseA, "1"), auction(auctionHouseA, "2"));
    List<AuctionDescription> resultsFromB = asList(auction(auctionHouseB, "1"), auction(auctionHouseB, "2"));

    DeterministicExecutor executor = new DeterministicExecutor();

    AuctionSearch_v2 search = new AuctionSearch_v2(executor, houses(auctionHouseA, auctionHouseB), consumer);

    @Test
    public void searchesAuctionHouses() throws Exception {
        Set<String> keywords = set("sheep", "cheese");

        auctionHouseA.willReturnSearchResults(keywords, resultsFromA);
        auctionHouseB.willReturnSearchResults(keywords, resultsFromB);

        context.checking(new Expectations() {{
            States searching = context.states("activity");

            oneOf(consumer).auctionSearchFound(resultsFromA);
            when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFound(resultsFromB);
            when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFinished();
            then(searching.is("finished"));
        }});

        search.search(keywords);
        executor.runUntilIdle();
    }

    @Test
    public void doesNotAnnounceEmptySearchResults() throws Exception {
        Set<String> keywords = set("keywords");

        auctionHouseA.willReturnSearchResults(keywords, resultsFromA);
        auctionHouseB.willReturnSearchResults(keywords, noResults());

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
