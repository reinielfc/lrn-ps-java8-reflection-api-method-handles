package util;

import annotation.Column;
import annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Metamodel {

    private final Class<?> _class;

    public Metamodel(Class<?> _class) {
        this._class = _class;
    }

    public static <T> Metamodel of(Class<T> _class) {
        return new Metamodel(_class);
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

    public String buildInsertRequest() {
        // insert into Person (id, name, age) value (?, ?, ?)
        String columnElement = buildColumnElement();
        String questionMarksElement = buildQuestionMarksElement();

        return String.format(
                "insert into %s(%s) values (%s)",
                this._class.getSimpleName(), columnElement, questionMarksElement
        );
    }

    public String buildSelectRequest() {
        // select id, name, age from Person where id = ?
        String columnElement = buildColumnElement();
        return String.format(
                "select %s from %s where %s = ?",
                columnElement, this._class.getSimpleName(), getPrimaryKey().getName());
    }

    private String buildQuestionMarksElement() {
        int numberOFColumns = getColumns().size() + 1;
        return IntStream.range(0, numberOFColumns)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));
    }

    private String buildColumnElement() {
        String primaryKeyColumnName = getPrimaryKey().getName();
        return Stream.concat(
                Stream.of(primaryKeyColumnName),
                getColumns().stream().map(ColumnField::getName)
        ).collect(Collectors.joining(", "));
    }

}
