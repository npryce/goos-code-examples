package book.example.persistence.tests.builders;

import book.example.persistence.model.CreditCardDetails;

import java.util.Date;

public class CreditCardDetailsBuilder extends AbstractBuilder<CreditCardDetailsBuilder, CreditCardDetails> {
    private String nameOnCard = "bob";
    private String cardNumber = "1234567890";
    private Date expiryDate = new Date();
    private AddressBuilder billingAddressBuilder = new AddressBuilder();

    public CreditCardDetails build() {
        return new CreditCardDetails(nameOnCard, cardNumber, expiryDate, billingAddressBuilder.build());
    }

    public CreditCardDetailsBuilder withNameOnCard(String nameOnCard) {
        CreditCardDetailsBuilder other = clone();
        other.nameOnCard = nameOnCard;
        return other;
    }

    public CreditCardDetailsBuilder withCardNumber(String cardNumber) {
        CreditCardDetailsBuilder other = clone();
        other.cardNumber = cardNumber;
        return other;
    }

    public CreditCardDetailsBuilder withExpiryDate(Date expiryDate) {
        CreditCardDetailsBuilder other = clone();
        other.expiryDate = expiryDate;
        return other;
    }

    public CreditCardDetailsBuilder withBillingAddress(AddressBuilder billingAddressBuilder) {
        CreditCardDetailsBuilder other = clone();
        other.billingAddressBuilder = billingAddressBuilder;
        return other;
    }

    public static CreditCardDetailsBuilder aCreditCard() {
        return new CreditCardDetailsBuilder();
    }

}
