package book.example.common.searching;

import org.apache.commons.lang.builder.ToStringStyle;

import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;

public class AuctionDescription {
    public final AuctionHouse auctionHouse;
    public final String auctionId;
    public final String description;
    // etc...

    public AuctionDescription(AuctionHouse auctionHouse, String auctionId, String description) {
        this.auctionHouse = auctionHouse;
        this.auctionId = auctionId;
        this.description = description;
    }

    @Override
    public String toString() {
        return reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
