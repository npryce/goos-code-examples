package book.example.persistence.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
public class PayMateDetails extends PaymentMethod {
    @Embedded
    private Authorisation authorisation;

    public PayMateDetails(Authorisation authorisation) {
        this.authorisation = authorisation;
    }

    public Authorisation getAuthorisation() {
        return authorisation;
    }

    public void setAuthorisation(Authorisation authorisation) {
        this.authorisation = authorisation;
    }

    public PayMateDetails() {
        // For JPA
    }
}
