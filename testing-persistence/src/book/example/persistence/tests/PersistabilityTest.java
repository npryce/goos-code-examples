package book.example.persistence.tests;

import book.example.persistence.tests.builders.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static book.example.persistence.tests.PersistenceReflection.assertHaveSamePersistentFields;
import static book.example.persistence.tests.PersistenceReflection.idOf;
import static org.junit.Assert.assertNotNull;

public class PersistabilityTest {
    final EntityManagerFactory factory = Persistence.createEntityManagerFactory("example");
    final EntityManager entityManager = factory.createEntityManager();
    final JPATransactor transactor = new JPATransactor(entityManager);

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
    final List<? extends Builder<?>> persistentObjectBuilders = Arrays.asList(
            new AddressBuilder(),
            new PayMateDetailsBuilder(),
            new CreditCardDetailsBuilder(),
            new AuctionSiteBuilder(),
            new AuctionSiteLoginBuilder().forSite(persisted(new AuctionSiteBuilder())),
            new CustomerBuilder()
                    .usingAuctionSites(
                            new AuctionSiteLoginBuilder().forSite(persisted(new AuctionSiteBuilder())))
                    .withPaymentMethods(
                    new CreditCardDetailsBuilder(),
                    new PayMateDetailsBuilder()));

    @Test
    public void canRoundTripPersistentObjects() throws Exception {
        for (Builder<?> builder : persistentObjectBuilders) {
            assertCanBePersisted(builder);
        }
    }

    private void assertCanBePersisted(Builder<?> builder) throws Exception {
        try {
            assertReloadsWithSameStateAs(persistedObjectFrom(builder));
        }
        catch (PersistenceException e) {
            throw new PersistenceException("could not round-trip " + typeFor(builder), e);
        }
    }

    private String typeFor(Builder<?> builder) {
        return builder.getClass().getSimpleName().replace("Builder", "");
    }

    private Object persistedObjectFrom(final Builder<?> builder) throws Exception {
        return transactor.performQuery(new QueryUnitOfWork<Object>() {
            public Object query() throws Exception {
                // Build the saved object in the transaction so any sub-builders can do database activity if necessary
                Object saved = builder.build();
                entityManager.persist(saved);
                return saved;
            }
        });
    }

    private void assertReloadsWithSameStateAs(final Object original) throws Exception {
        transactor.perform(new UnitOfWork() {
            public void work() throws Exception {
                Object loaded = entityManager.find(original.getClass(), idOf(original));

                assertNotNull("should have found saved " + original.getClass().getSimpleName(), loaded);
                assertHaveSamePersistentFields(loaded, original);
            }
        });
    }

    private <T> Builder<T> persisted(final Builder<T> builder) {
        return new Builder<T>() {
            public T build() {
                T entity = builder.build();
                entityManager.persist(entity);
                return entity;
            }
        };
    }
}
