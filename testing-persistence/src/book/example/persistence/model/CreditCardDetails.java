package book.example.persistence.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Entity
public class CreditCardDetails extends PaymentMethod {
    private String cardNumber;
    private String nameOnCard;
    private Date expiryDate;

    @ManyToOne(cascade = PERSIST, fetch = EAGER, optional = false)
    private Address billingAddress;

    public CreditCardDetails(String cardNumber, String nameOnCard, Date expiryDate, Address billingAddress) {
        this.cardNumber = cardNumber;
        this.nameOnCard = nameOnCard;
        this.expiryDate = expiryDate;
        this.billingAddress = billingAddress;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    protected CreditCardDetails() {
        // for JPA
    }
}
