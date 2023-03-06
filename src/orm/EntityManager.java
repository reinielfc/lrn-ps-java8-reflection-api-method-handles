package orm;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface EntityManager<T> {

    static <T> EntityManager<T> of(Class<T> tClass) {
        return new H2EntityManager<>();
    }

    void persist(T t) throws SQLException, IllegalAccessException;

    T find(Class<T> tClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
