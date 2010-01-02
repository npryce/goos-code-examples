package book.example.persistence.tests.builders;

import book.example.persistence.model.Address;
import book.example.persistence.model.AuctionSiteCredentials;
import book.example.persistence.model.Customer;
import book.example.persistence.model.PaymentMethod;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

public class CustomerBuilder extends AbstractBuilder<CustomerBuilder, Customer> {
    private String name = "customer";
    private String email = "customer@example.com";
    private Builder<Address> addressBuilder = new AddressBuilder();
    private Set<Builder<? extends PaymentMethod>> paymentMethods = emptySet();
    private Set<Builder<? extends AuctionSiteCredentials>> auctionSitesUsed = emptySet();

    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder();
    }

    public Customer build() {
        return new Customer(name, email, addressBuilder.build(), buildSet(paymentMethods), buildSet(auctionSitesUsed));
    }

    public CustomerBuilder withName(String name) {
        CustomerBuilder other = this.clone();
        other.name = name;
        return other;
    }

    public CustomerBuilder withEmailAddress(String email) {
        CustomerBuilder other = this.clone();
        other.email = email;
        return other;
    }

    public CustomerBuilder withAddress(Builder<Address> addressBuilder) {
        CustomerBuilder other = this.clone();
        other.addressBuilder = addressBuilder;
        return other;
    }

    public CustomerBuilder withPaymentMethods(Builder<? extends PaymentMethod>... paymentMethodBuilders) {
        CustomerBuilder other = this.clone();
        other.paymentMethods = setOf(paymentMethodBuilders);
        return other;
    }

    public CustomerBuilder usingAuctionSites(Builder<AuctionSiteCredentials>... auctionSiteLoginBuilders) {
        CustomerBuilder other = this.clone();
        other.auctionSitesUsed = setOf(auctionSiteLoginBuilders);
        return other;
    }

    private <T> HashSet<Builder<? extends T>> setOf(Builder<? extends T>... builders) {
        return new HashSet<Builder<? extends T>>(asList(builders));
    }

    private static <T> Set<T> buildSet(Set<Builder<? extends T>> builders) {
        HashSet<T> set = new HashSet<T>();

        for (Builder<? extends T> builder : builders) {
            set.add(builder.build());
        }

        return set;
    }
}
