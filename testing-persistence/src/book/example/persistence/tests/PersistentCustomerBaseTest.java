package book.example.persistence.tests;

import book.example.common.exceptions.Defect;
import book.example.persistence.jpa.PersistentCustomerBase;
import book.example.persistence.model.Customer;
import book.example.persistence.tests.builders.CustomerBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static book.example.persistence.tests.builders.CreditCardDetailsBuilder.aCreditCard;
import static book.example.persistence.tests.builders.CustomerBuilder.aCustomer;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class PersistentCustomerBaseTest {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("example");
    final EntityManager entityManager = factory.createEntityManager();
    final JPATransactor transactor = new JPATransactor(entityManager);

    final PersistentCustomerBase customerBase = new PersistentCustomerBase(entityManager);

    @Before
    public void cleanDatabase() throws Exception {
        new DatabaseCleaner(entityManager).clean();
    }

    @After
    public void tearDown() {
        entityManager.close();
        factory.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findsCustomersWithCreditCardsThatAreAboutToExpire() throws Exception {
        final String deadline = "6 Jun 2009";

        addCustomers(
                aCustomer().withName("Alice (Expired)")
                        .withPaymentMethods(aCreditCard().withExpiryDate(date("1 Jan 2009"))),
                aCustomer().withName("Bob (Expired)")
                        .withPaymentMethods(aCreditCard().withExpiryDate(date("5 Jun 2009"))),
                aCustomer().withName("Carol (Valid)")
                        .withPaymentMethods(aCreditCard().withExpiryDate(date(deadline))),
                aCustomer().withName("Dave (Valid)")
                        .withPaymentMethods(aCreditCard().withExpiryDate(date("7 Jun 2009")))
        );

        assertCustomersExpiringOn(date(deadline),
                containsCustomersNamed("Alice (Expired)", "Bob (Expired)"));
    }

    // Needed to work around Java type inference not working for parameters
    private Matcher<Iterable<Customer>> containsCustomersNamed(String name1, String name2) {
        return containsInAnyOrder(customerNamed(name1), customerNamed(name2));
    }

    private void addCustomers(final CustomerBuilder... customers) throws Exception {
        transactor.perform(new UnitOfWork() {
            public void work() throws Exception {
                for (CustomerBuilder customer : customers) {
                    customerBase.addCustomer(customer.build());
                }
            }
        });
    }

    private void assertCustomersExpiringOn(final Date date,
                                           final Matcher<Iterable<Customer>> matcher)
            throws Exception {
        transactor.perform(new UnitOfWork() {
            public void work() throws Exception {
                assertThat(customerBase.customersWithExpiredCreditCardsAsOf(date), matcher);
            }
        });
    }

    protected Matcher<Customer> customerNamed(final String name) {
        return new TypeSafeMatcher<Customer>() {
            @Override
            protected boolean matchesSafely(Customer customer) {
                return customer.name().equals(name);
            }

            public void describeTo(Description description) {
                description.appendText("a customer named ").appendValue(name);
            }

            @Override
            protected void describeMismatchSafely(Customer customer, Description description) {
                description.appendText("name was ").appendValue(customer.name());
            }
        };
    }

    private DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    private Date date(String text) {
        try {
            return dateFormat.parse(text);
        } catch (ParseException e) {
            throw new Defect("invalid date syntax", e);
        }
    }

}
