package book.example.persistence.jpa;

import book.example.persistence.model.Customer;
import book.example.persistence.model.CustomerBase;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

public class PersistentCustomerBase implements CustomerBase {
    private final EntityManager entityManager;

    public PersistentCustomerBase(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void addCustomer(Customer customer) {
        entityManager.persist(customer);
    }

    @SuppressWarnings("unchecked")
    public List<Customer> customersWithExpiredCreditCardsAsOf(Date deadline) {
        Query query = entityManager.createQuery(
                "select c from Customer c, CreditCardDetails d " +
                        "where d member of c.paymentMethods " +
                        "  and d.expiryDate < :deadline");
        query.setParameter("deadline", deadline);
        return query.getResultList();
    }
}
