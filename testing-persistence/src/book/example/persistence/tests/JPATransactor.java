package book.example.persistence.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

public class JPATransactor {
    private final EntityManager entityManager;

    public JPATransactor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void perform(UnitOfWork unitOfWork) throws Exception {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            unitOfWork.work();
            transaction.commit();
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public <T> T performQuery(QueryUnitOfWork<T> query) throws Exception {
        perform(query);
        return query.result;
    }
}