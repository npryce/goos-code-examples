package book.example.persistence.model;

import javax.persistence.*;

import static javax.persistence.DiscriminatorType.STRING;
import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.InheritanceType.JOINED;

@Entity
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = STRING)
public abstract class PaymentMethod {
    @Id
    @GeneratedValue(strategy = AUTO)
    @SuppressWarnings("unused")
    private Integer id;

    protected PaymentMethod() {
    }
}
