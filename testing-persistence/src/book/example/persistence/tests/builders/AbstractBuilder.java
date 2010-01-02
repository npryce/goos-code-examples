package book.example.persistence.tests.builders;

public abstract class AbstractBuilder<T extends AbstractBuilder<T, B>, B> implements Cloneable, Builder<B> {
    @Override
    @SuppressWarnings("unchecked")
    protected T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
