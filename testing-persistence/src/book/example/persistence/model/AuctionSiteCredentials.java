package book.example.persistence.model;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class AuctionSiteCredentials {
    @ManyToOne
    private AuctionSite auctionSite;

    @Embedded
    private Authorisation auth;

    public AuctionSiteCredentials(AuctionSite site, Authorisation auth) {
        this.auctionSite = site;
        this.auth = auth;
    }

    public AuctionSite getSite() {
        return auctionSite;
    }

    public Authorisation getAuth() {
        return auth;
    }

    /**
     * Would really have an API like:
     * <p/>
     * AuctionSiteSession login() throws AuthorisationException {
     * return site.login(auth);
     * }
     * <p/>
     * but we don't have an Auction interface yet
     */

    @Id
    @GeneratedValue(strategy = AUTO)
    @SuppressWarnings("unused")
    private int id;

    protected AuctionSiteCredentials() {
        // For JPA
    }
}
