package book.example.persistence.model;

import java.util.Date;
import java.util.List;


public interface CustomerBase {
    void addCustomer(Customer user);

    List<Customer> customersWithExpiredCreditCardsAsOf(Date deadline);
}
