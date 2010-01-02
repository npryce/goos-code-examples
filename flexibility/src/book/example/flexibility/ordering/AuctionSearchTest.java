package book.example.flexibility.ordering;

import book.example.common.searching.AuctionDescription;
import book.example.common.searching.StubAuctionHouse;
import book.example.common.searching.async.AuctionSearchConsumer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@RunWith(JMock.class)
public class AuctionSearchTest {
    Mockery context = new JUnit4Mockery();

    AuctionSearchConsumer consumer = context.mock(AuctionSearchConsumer.class, "consumer");

    Set<String> keywords = new HashSet<String>(Arrays.asList("some", "keywords"));

    @Test
    public void performsSearchOnOneAuctionHouse_NoOrdering() {
        StubAuctionHouse auctionHouse = new StubAuctionHouse();
        final List<AuctionDescription> results = someResultsFrom(auctionHouse);

        auctionHouse.willReturnSearchResults(keywords, results);

        AuctionSearch search = new AuctionSearch(asList(auctionHouse), consumer);

        context.checking(new Expectations() {{
            oneOf(consumer).auctionSearchFound(results);
            oneOf(consumer).auctionSearchFinished();
        }});

        search.search(keywords);
    }


    @Test
    public void performsSearchOnOneAuctionHouse_Sequence() {
        StubAuctionHouse auctionHouse = new StubAuctionHouse();
        final List<AuctionDescription> results = someResultsFrom(auctionHouse);

        auctionHouse.willReturnSearchResults(keywords, results);

        AuctionSearch search = new AuctionSearch(asList(auctionHouse), consumer);

        context.checking(new Expectations() {{
            Sequence events = context.sequence("events");

            oneOf(consumer).auctionSearchFound(results);
            inSequence(events);
            oneOf(consumer).auctionSearchFinished();
            inSequence(events);
        }});

        search.search(keywords);
    }

    @Test
    public void performsSearchOnMultipleAuctionHouses_BrittleTestWithSequences() {
        StubAuctionHouse auctionHouseA = new StubAuctionHouse("a");
        StubAuctionHouse auctionHouseB = new StubAuctionHouse("b");

        AuctionSearch search = new AuctionSearch(asList(auctionHouseA, auctionHouseB), consumer);

        final List<AuctionDescription> resultsA = someResultsFrom(auctionHouseA);
        auctionHouseA.willReturnSearchResults(keywords, resultsA);

        final List<AuctionDescription> resultsB = someResultsFrom(auctionHouseB);
        auctionHouseB.willReturnSearchResults(keywords, resultsB);

        context.checking(new Expectations() {{
            Sequence events = context.sequence("events");

            oneOf(consumer).auctionSearchFound(resultsB);
            inSequence(events);
            oneOf(consumer).auctionSearchFound(resultsA);
            inSequence(events);
            oneOf(consumer).auctionSearchFinished();
            inSequence(events);
        }});

        search.search(keywords);
    }

    @Test
    public void notifiesConsumerOfResultsThenSearchFinished_FlexibleTestWithStates() {
        StubAuctionHouse auctionHouseA = new StubAuctionHouse("a");
        StubAuctionHouse auctionHouseB = new StubAuctionHouse("b");

        AuctionSearch search = new AuctionSearch(asList(auctionHouseA, auctionHouseB), consumer);

        final List<AuctionDescription> resultsA = someResultsFrom(auctionHouseA);
        auctionHouseA.willReturnSearchResults(keywords, resultsA);

        final List<AuctionDescription> resultsB = someResultsFrom(auctionHouseB);
        auctionHouseB.willReturnSearchResults(keywords, resultsB);

        context.checking(new Expectations() {{
            States searching = context.states("searching");

            oneOf(consumer).auctionSearchFound(resultsB);
            when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFound(resultsA);
            when(searching.isNot("finished"));
            oneOf(consumer).auctionSearchFinished();
            then(searching.is("finished"));
        }});

        search.search(keywords);
    }

    private List<AuctionDescription> someResultsFrom(StubAuctionHouse h) {
        return asList(new AuctionDescription(h, "1", h + "-1"));
    }
}
