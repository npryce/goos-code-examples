package book.example.persistence.tests;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isTransient;
import static org.junit.Assert.assertEquals;

public class PersistenceReflection {
    public static Object idOf(Object entity) {
        for (Class<?> c = entity.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return fieldValue(entity, field);
                }
            }
        }

        throw new IllegalArgumentException(entity + " does not have an entity id");
    }

    public static void assertHaveSamePersistentFields(Object e1, Object e2) {
        for (Class<?> c = e1.getClass(); c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                if (!isTransient(field.getModifiers()) && !field.isAnnotationPresent(Transient.class)) {
                    assertEquals(c.getSimpleName() + "." + field.getName(), fieldValue(e1, field), fieldValue(e2, field));
                }
            }
        }
    }

    private static Object fieldValue(Object entity, Field field) throws Error {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new Error("could not access accessible field " + field);
        }
    }
}
