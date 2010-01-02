package book.example.persistence.tests;

import book.example.persistence.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class DatabaseCleaner {
    private static final Class<?>[] ENTITY_TYPES = {
            Customer.class,
            PaymentMethod.class,
            AuctionSiteCredentials.class,
            AuctionSite.class,
            Address.class
    };

    private final EntityManager entityManager;

    public DatabaseCleaner(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void clean() {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        for (Class<?> entityType : ENTITY_TYPES) {
            deleteEntities(entityType);
        }

        transaction.commit();
    }

    private void deleteEntities(Class<?> entityType) {
        entityManager
                .createQuery("delete from " + entityNameOf(entityType))
                .executeUpdate();
    }

    private String entityNameOf(Class<?> entityType) {
        return entityType.getSimpleName();
    }
}
