package book.example.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Address {
    private String street;
    private String town;
    private String country;
    private String postCode;

    public Address(String street, String town, String country, String postCode) {
        this.street = street;
        this.town = town;
        this.country = country;
        this.postCode = postCode;
    }

    public String getStreet() {
        return street;
    }

    public String getTown() {
        return town;
    }

    public String getCountry() {
        return country;
    }

    public String getPostCode() {
        return postCode;
    }

    // JPA stuff
    protected Address() {
    }

    @Id
    @GeneratedValue(strategy = AUTO)
    @SuppressWarnings("unused")
    private int id;
}
