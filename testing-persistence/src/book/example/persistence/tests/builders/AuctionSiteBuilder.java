package book.example.persistence.tests.builders;

import book.example.persistence.model.AuctionSite;

public class AuctionSiteBuilder extends AbstractBuilder<AuctionSiteBuilder, AuctionSite> {
    private String name = "eCove";
    private String siteURL = "http://www.ecove.com";

    public AuctionSite build() {
        return new AuctionSite(name, siteURL);
    }

    public static AuctionSiteBuilder anAuctionSite() {
        return new AuctionSiteBuilder();
    }

    public AuctionSiteBuilder withName(String aName) {
        AuctionSiteBuilder other = this.clone();
        other.name = aName;
        return other;
    }

    public AuctionSiteBuilder withSiteURL(String aSiteURL) {
        AuctionSiteBuilder other = this.clone();
        other.siteURL = aSiteURL;
        return other;
    }
}
