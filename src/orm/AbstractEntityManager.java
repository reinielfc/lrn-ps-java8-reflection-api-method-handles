package orm;

import util.ColumnField;
import util.Metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEntityManager<T> implements EntityManager<T> {
    private final AtomicLong idGenerator = new AtomicLong(0L);

    @Override
    public void persist(T t) throws SQLException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(t.getClass());
        String sql = metamodel.buildInsertRequest();
        PreparedStatement statement = prepareStatementWith(sql).andParameters(t);
        statement.executeUpdate();
    }

    @Override
    public T find(Class<T> tClass, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(tClass);
        String sql = metamodel.buildSelectRequest();
        PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
        ResultSet resultSet = statement.executeQuery();
        return buildInstanceFrom(tClass, resultSet);
    }

    private T buildInstanceFrom(Class<T> tClass, ResultSet resultSet)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        Metamodel metamodel = Metamodel.of(tClass);

        //tClass.newInstance() // deprecated
        T t = tClass.getConstructor().newInstance();
        Field primaryKeyField = metamodel.getPrimaryKey().getField();
        String primaryKeyColumnName = metamodel.getPrimaryKey().getName();
        Class<?> primaryKeyType = primaryKeyField.getType();

        resultSet.next();
        if (primaryKeyType == long.class) {
            long primaryKey = resultSet.getInt(primaryKeyColumnName);
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(t, primaryKey);
        }

        for (ColumnField columnField : metamodel.getColumns()) {
            Field field = columnField.getField();
            field.setAccessible(true);

            Class<?> columnType = columnField.getType();
            String columnName = columnField.getName();

            if (columnType == int.class) {
                int value = resultSet.getInt(columnName);
                field.set(t, value);
            } else if (columnType == String.class) {
                String value = resultSet.getString(columnName);
                field.set(t, value);
            }
        }

       return t;
    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
        Connection connection = buildConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        return new PreparedStatementWrapper(statement);
    }

    public abstract Connection buildConnection() throws SQLException;
    private class PreparedStatementWrapper {

        private final PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }

        public PreparedStatement andParameters(T t) throws SQLException, IllegalArgumentException, IllegalAccessException {
            Metamodel metamodel = Metamodel.of(t.getClass());
            Class<?> primaryKeyType = metamodel.getPrimaryKey().getType();

            if (primaryKeyType == long.class) {
                long id = idGenerator.incrementAndGet();
                statement.setLong(1, id);
                Field field = metamodel.getPrimaryKey().getField();
                field.setAccessible(true);
                field.set(t, id);
            }

            List<ColumnField> columns = metamodel.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                ColumnField columnField = columns.get(i);
                Class<?> fieldType = columnField.getType();

                Field field = columnField.getField();
                field.setAccessible(true);

                Object value = field.get(t);

                int columnIndex = i + 2;
                if (fieldType.equals(int.class)) {
                    statement.setInt(columnIndex, (int) value);
                } else if (fieldType.equals(String.class)) {
                    statement.setString(columnIndex, (String) value);
                }
            }

            return statement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            if (primaryKey.getClass() == Long.class) {
                statement.setLong(1, (Long) primaryKey);
            }
            return statement;
        }
    }
}
