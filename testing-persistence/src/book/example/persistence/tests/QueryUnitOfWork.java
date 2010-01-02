package book.example.persistence.tests;

public abstract class QueryUnitOfWork<T> implements UnitOfWork {
    public T result;

    public void work() throws Exception {
        result = query();
    }

    public abstract T query() throws Exception;
}
