package util;

import annotation.Column;
import annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Metamodel<T> {

    private final Class<T> _class;

    public Metamodel(Class<T> _class) {
        this._class = _class;
    }

    public static <T> Metamodel<T> of(Class<T> _class) {
        return new Metamodel<>(_class);
    }

    public PrimaryKeyField getPrimaryKey() {
        Field[] fields = _class.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                .findFirst()
                .map(PrimaryKeyField::new)
                .orElseThrow(() -> new IllegalArgumentException("No primary key found in class" + _class.getSimpleName()));
    }

    public List<ColumnField> getColumns() {
        Field[] fields = _class.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> field.getAnnotation(Column.class) != null)
                .map(ColumnField::new)
                .collect(Collectors.toList());
    }
}
