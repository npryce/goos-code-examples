package book.example.flexibility.ordering;

import book.example.flexibility.ordering.AuctionChannel.AuctionConnection;
import book.example.flexibility.ordering.AuctionChannel.AuctionConnectionFinder;
import book.example.flexibility.ordering.AuctionChannel.AuctionDescription;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;

@RunWith(JMock.class)
public class AuctionChannelTest {
    private static final String MESSAGE_BODY = "a message";
    private final Mockery context = new JUnit4Mockery();
    private final AuctionDescription description = namedDescription("valid");
    private final AuctionDescription description0 = namedDescription("0");
    private final AuctionDescription description1 = namedDescription("1");
    private final AuctionConnection connection = context.mock(AuctionConnection.class);
    private final AuctionConnectionFinder connectionFinder = context.mock(AuctionConnectionFinder.class);
    private final AuctionChannel auctionChannel = new AuctionChannel(connectionFinder);


    @Test
    public void
    sendsMessageToValidConnection() {
        context.checking(new Expectations() {{
            allowing(connectionFinder).findConnectionFor(description);
            will(returnValue(connection));

            oneOf(connection).send(MESSAGE_BODY);
        }});

        auctionChannel.sendMessageTo(MESSAGE_BODY, asList(description));
    }


    @Test
    public void
    searchesConnectionsInOrder() {
        final Sequence connectionSearch = context.sequence("connection");
        context.checking(new Expectations() {{
            oneOf(connectionFinder).findConnectionFor(description0);
            inSequence(connectionSearch);
            will(returnValue(null));
            oneOf(connectionFinder).findConnectionFor(description1);
            inSequence(connectionSearch);
            will(returnValue(null));
            oneOf(connectionFinder).findConnectionFor(description);
            inSequence(connectionSearch);
            will(returnValue(connection));

            oneOf(connection).send(MESSAGE_BODY);
        }});

        auctionChannel.sendMessageTo(MESSAGE_BODY, asList(description0, description1, description));
    }

    private static AuctionDescription namedDescription(final String name) {
        return new AuctionDescription() {
            @Override
            public String toString() {
                return "Description: " + name;
            }
        };
    }
}
