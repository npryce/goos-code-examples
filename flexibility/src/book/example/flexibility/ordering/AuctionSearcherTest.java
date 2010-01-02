package book.example.flexibility.ordering;

import book.example.flexibility.ordering.AuctionSearcher.Auction;
import book.example.flexibility.ordering.AuctionSearcher.AuctionSearchListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static java.util.Arrays.asList;

@RunWith(JMock.class)
public class AuctionSearcherTest {
    private static final Auction STUB_AUCTION2 = stubAuctionToMatch("word2");
    private static final Auction STUB_AUCTION1 = stubAuctionToMatch("word1");
    private static final HashSet<String> KEYWORDS = new HashSet<String>(asList("word1", "word2"));
    private final Mockery context = new JUnit4Mockery();
    private final AuctionSearchListener searchListener = context.mock(AuctionSearchListener.class);

    @Test
    public void
    announcesMatchForOneAuction_NoOrdering() {
        final AuctionSearcher auctionSearch = new AuctionSearcher(searchListener, asList(STUB_AUCTION1));

        context.checking(new Expectations() {{
            oneOf(searchListener).searchMatched(STUB_AUCTION1);
            oneOf(searchListener).searchFinished();
        }});

        auctionSearch.searchFor(KEYWORDS);
    }

    @Test
    public void
    announcesMatchForOneAuction_Sequence() {
        final AuctionSearcher auctionSearch = new AuctionSearcher(searchListener, asList(STUB_AUCTION1));

        context.checking(new Expectations() {{
            Sequence events = context.sequence("events");

            oneOf(searchListener).searchMatched(STUB_AUCTION1);
            inSequence(events);
            oneOf(searchListener).searchFinished();
            inSequence(events);
        }});

        auctionSearch.searchFor(KEYWORDS);
    }


    @Test
    public void
    announcesMatchForTwoAuctions_Sequence() {
        final AuctionSearcher auctionSearch = new AuctionSearcher(searchListener, asList(STUB_AUCTION1, STUB_AUCTION2));

        context.checking(new Expectations() {{
            Sequence events = context.sequence("events");

            oneOf(searchListener).searchMatched(STUB_AUCTION1);
            inSequence(events);
            oneOf(searchListener).searchMatched(STUB_AUCTION2);
            inSequence(events);
            oneOf(searchListener).searchFinished();
            inSequence(events);
        }});

        auctionSearch.searchFor(KEYWORDS);
    }


    @Test
    public void
    announcesMatchForTwoAuctions_States() {
        final AuctionSearcher auctionSearch = new AuctionSearcher(searchListener, asList(STUB_AUCTION1, STUB_AUCTION2));

        context.checking(new Expectations() {{
            States searching = context.states("searching");

            oneOf(searchListener).searchMatched(STUB_AUCTION1);
            when(searching.isNot("finished"));
            oneOf(searchListener).searchMatched(STUB_AUCTION2);
            when(searching.isNot("finished"));
            oneOf(searchListener).searchFinished();
            then(searching.is("finished"));
        }});

        auctionSearch.searchFor(KEYWORDS);
    }

    private static Auction stubAuctionToMatch(final String match) {
        return new Auction() {
            public boolean matches(String keyword) {
                return match.equals(keyword);
            }

            @Override
            public String toString() {
                return "Auction for " + match;
            }

            ;
        };
    }
}
