package book.example.persistence.model;

import javax.persistence.*;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.AUTO;

@Entity
public class Customer {
    private String name;

    private String email;

    @OneToOne(cascade = ALL)
    private Address address;

    @ManyToMany(cascade = ALL)
    private Set<PaymentMethod> paymentMethods;

    @ManyToMany(cascade = ALL)
    private Set<AuctionSiteCredentials> auctionSiteLogins;

    public Customer(String name, String email, Address address, Set<PaymentMethod> paymentMethods, Set<AuctionSiteCredentials> auctionSitesUsed) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.paymentMethods = paymentMethods;
        this.auctionSiteLogins = auctionSitesUsed;
    }

    public String name() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Set<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public Set<AuctionSiteCredentials> getAuctionSitesUsed() {
        return auctionSiteLogins;
    }

    public String toString() {
        return getClass().getSimpleName() + "[name=" + name + "]";
    }

    @Id
    @GeneratedValue(strategy = AUTO)
    @SuppressWarnings("unused")
    private Integer id;

    protected Customer() {
        // for JPA
    }
}
