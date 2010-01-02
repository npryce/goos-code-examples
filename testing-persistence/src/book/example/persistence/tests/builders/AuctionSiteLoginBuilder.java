package book.example.persistence.tests.builders;

import book.example.persistence.model.AuctionSite;
import book.example.persistence.model.AuctionSiteCredentials;
import book.example.persistence.model.Authorisation;

public class AuctionSiteLoginBuilder extends AbstractBuilder<AuctionSiteLoginBuilder, AuctionSiteCredentials> {
    private Builder<AuctionSite> auctionSiteBuilder = new AuctionSiteBuilder();
    private Builder<Authorisation> authorisationBuilder = new AuthorisationBuilder();

    public AuctionSiteCredentials build() {
        return new AuctionSiteCredentials(auctionSiteBuilder.build(), authorisationBuilder.build());
    }

    public AuctionSiteLoginBuilder forSite(Builder<AuctionSite> auctionSiteBuilder) {
        AuctionSiteLoginBuilder other = this.clone();
        other.auctionSiteBuilder = auctionSiteBuilder;
        return other;
    }

    public AuctionSiteLoginBuilder with(AuthorisationBuilder authorisationBuilder) {
        AuctionSiteLoginBuilder other = this.clone();
        other.authorisationBuilder = authorisationBuilder;
        return other;
    }
}
