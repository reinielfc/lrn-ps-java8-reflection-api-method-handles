package util;

import annotation.PrimaryKey;

import java.lang.reflect.Field;

public class PrimaryKeyField {
    private final Field field;
    private final PrimaryKey primaryKey;

    public PrimaryKeyField(Field field) {
        this.field = field;
        this.primaryKey = this.field.getAnnotation(PrimaryKey.class);
    }

    public String getName() {
        return primaryKey.name();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Field getField() {
        return this.field;
    }
}
