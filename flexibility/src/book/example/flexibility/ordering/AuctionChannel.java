package book.example.flexibility.ordering;


public class AuctionChannel {
    public interface AuctionConnection {
        void send(String message);
    }

    public static class AuctionDescription {
    }

    ;

    public interface AuctionConnectionFinder {
        AuctionConnection findConnectionFor(AuctionDescription description);
    }

    private final AuctionConnectionFinder connectionFinder;

    public AuctionChannel(AuctionConnectionFinder connectionFinder) {
        this.connectionFinder = connectionFinder;
    }

    public void sendMessageTo(String message, Iterable<AuctionDescription> auctionDescriptions) {
        for (AuctionDescription auctionDescription : auctionDescriptions) {
            final AuctionConnection connection = connectionFinder.findConnectionFor(auctionDescription);
            if (connection != null) {
                connection.send(message);
                return;
            }
        }
    }
}
