package book.example.persistence.tests.builders;

import book.example.persistence.model.Address;

public class AddressBuilder extends AbstractBuilder<AddressBuilder, Address> {
    private String street = "1 High Street";
    private String town = "Bognor Regis";
    private String country = "UK";
    private String postCode = "BG1 2FO";

    public AddressBuilder withStreet(String street) {
        AddressBuilder other = this.clone();
        other.street = street;
        return other;
    }

    public AddressBuilder withTown(String town) {
        AddressBuilder other = this.clone();
        other.town = town;
        return other;
    }

    public AddressBuilder withCountry(String country) {
        AddressBuilder other = this.clone();
        other.country = country;
        return other;
    }

    public AddressBuilder withPostCode(String postCode) {
        AddressBuilder other = this.clone();
        other.postCode = postCode;
        return other;
    }

    public Address build() {
        return new Address(street, town, country, postCode);
    }
}
