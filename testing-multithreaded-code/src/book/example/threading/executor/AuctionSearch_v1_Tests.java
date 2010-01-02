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
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@RunWith(JMock.class)
public class AuctionSearch_v1_Tests {
    Synchroniser synchroniser = new Synchroniser();

    Mockery context = new JUnit4Mockery() {{
        setThreadingPolicy(synchroniser);
    }};

    AuctionSearchConsumer consumer = context.mock(AuctionSearchConsumer.class, "consumer");

    StubAuctionHouse auctionHouseA = new StubAuctionHouse("houseA");
    StubAuctionHouse auctionHouseB = new StubAuctionHouse("houseB");

    AuctionSearch_v1 search = new AuctionSearch_v1(houses(auctionHouseA, auctionHouseB), consumer);

    Set<String> keywords = set("sheep", "cheese");
    List<AuctionDescription> resultsFromA = asList(auction(auctionHouseA, "1"), auction(auctionHouseA, "2"));
    List<AuctionDescription> resultsFromB = asList(auction(auctionHouseB, "1"), auction(auctionHouseB, "2"));

    States searching = context.states("activity");

    @Test(timeout = 100)
    public void searchesAuctionHouses() throws Exception {
        auctionHouseA.willReturnSearchResults(keywords, resultsFromA);
        auctionHouseB.willReturnSearchResults(keywords, resultsFromB);

        context.checking(new Expectations() {{
            oneOf(consumer).auctionSearchFound(resultsFromA);
                when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFound(resultsFromB);
                when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFinished();
                then(searching.is("finished"));
        }});

        search.search(keywords);

        waitForSearchToFinish();
    }

    @Test(timeout = 100)
    public void doesNotAnnounceEmptySearchResults() throws Exception {
        Set<String> keywords = set("keywords");

        auctionHouseA.willReturnSearchResults(keywords, resultsFromA);
        auctionHouseB.willReturnSearchResults(keywords, noResults());

        context.checking(new Expectations() {{
            oneOf(consumer).auctionSearchFound(resultsFromA);
            oneOf(consumer).auctionSearchFinished();
                then(searching.is("finished"));
        }});

        search.search(keywords);

        waitForSearchToFinish();
    }


    // more reliable and meaningful than a call to Thread.sleep
    private void waitForSearchToFinish() throws InterruptedException {
        synchroniser.waitUntil(searching.is("finished"));
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
