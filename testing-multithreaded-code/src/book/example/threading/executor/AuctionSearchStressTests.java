package book.example.threading.executor;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.AuctionHouse;
import book.example.common.searching.StubAuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.anything;

@RunWith(JMock.class)
public class AuctionSearchStressTests {
    private static final HashSet<String> KEYWORDS = new HashSet<String>(asList("sheep", "cheese"));
    private static final int NUMBER_OF_AUCTION_HOUSES = 80;
    private static final int NUMBER_OF_SEARCHES = 20;

    Synchroniser synchroniser = new Synchroniser();
    Mockery context = new JUnit4Mockery() {{
        setThreadingPolicy(synchroniser);
    }};


    final AuctionSearchConsumer consumer = context.mock(AuctionSearchConsumer.class, "consumer");
    final States searching = context.states("searching");
    
    final ExecutorService executor = Executors.newCachedThreadPool();

    // Change to v2, v3, v4 to test different versions...
    AuctionSearch_v4 search = new AuctionSearch_v4(executor, auctionHouses(), consumer);

    @Test(timeout = 500)
    public void
    onlyOneAuctionSearchFinishedNotificationPerSearch() throws InterruptedException {
        context.checking(new Expectations() {{
            ignoring(consumer).auctionSearchFound(with(anyResults()));
        }});

        for (int i = 0; i < NUMBER_OF_SEARCHES; i++) {
            completeASearch();
        }
    }

    private void completeASearch() throws InterruptedException {
        searching.startsAs("in progress");

        context.checking(new Expectations() {{
            exactly(1).of(consumer).auctionSearchFinished();
            then(searching.is("done"));
        }});

        search.search(KEYWORDS);

        synchroniser.waitUntil(searching.is("done"));
    }

    private List<AuctionHouse> auctionHouses() {
        ArrayList<AuctionHouse> auctionHouses = new ArrayList<AuctionHouse>();

        for (int i = 0; i < NUMBER_OF_AUCTION_HOUSES; i++) {
            auctionHouses.add(stubbedAuctionHouse(i));
        }

        return auctionHouses;
    }

    private AuctionHouse stubbedAuctionHouse(int i) {
        StubAuctionHouse house = new StubAuctionHouse("house" + i);
        house.willReturnSearchResults(
                KEYWORDS, asList(new AuctionDescription(house, "id" + i, "description")));
        return house;
    }

    @After
    public void cleanUp() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, SECONDS);
    }
    
    private Matcher<List<AuctionDescription>> anyResults() {
        return anything();
    }
}
