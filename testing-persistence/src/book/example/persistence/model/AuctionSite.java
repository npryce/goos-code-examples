package book.example.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class AuctionSite {
    private String name;
    private String siteURL;


    public AuctionSite(String name, String siteURL) {
        this.name = name;
        this.siteURL = siteURL;
    }

    public String getName() {
        return name;
    }

    public String getSiteURL() {
        return siteURL;
    }

    @Id()
    @GeneratedValue(strategy = AUTO)
    @SuppressWarnings("unused")
    private Integer id;

    protected AuctionSite() {
        // For JPA
    }
}
