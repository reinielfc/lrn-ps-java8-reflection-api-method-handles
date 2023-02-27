# 3. Creating an Object Metamodel Using Annotations and Reflection

## Creating a Metamodel By Adding Annotations to Fields

- we need to tell what to save from the objects
- how to save it

Example:

- we need metadata added to the Person class
- we need to tell that id is a primary key
- that age and name are regular fields

```java
public class Person {
    @PrimaryKey
    private long id; 
    @Column
    private int age;
    @Column
    private String name;
    // getters & setters
}
```

creating custom annotations is easy:

```java
public @interface PrimaryKey {
}
public @interface Column {
}
```

stages where an annotation will be made available:

1. Compile time `@Retention(RetentionPolicy.SOURCE)`
2. Class loading time `@Retention(RetentionPolicy.CLASS)`
3. Runtime `@Retention(RetentionPolicy.RUNTIME)`


## Using the Reflection API to Read and Write a Field

the problem:

- reading a person bean w/o knowing it is an instance of the Person class
- write its content in storage (file, database, etc)
- read fields w/ reflection

setting the value of a field:  _`field`_`.setValue(`_`instance, `_`"Sarah")`

```java
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
        Person person = new Person(21, "Jane");
        Class<?> personClass = person.getClass();
        Field field = personClass.getField("name");

        field.setAccessible(true);
        field.setValue(person, "Sarah");

        String name = (String) field.getValue(person);
    }
}
```

- what about private fields
- does the Reflection API break encapsulation?
- yes and no
  - if the field is private, an IllegalAccessException is thrown
  - no, the encapsulation is not completely broken
  - yes, there is a security check if you want to access a private member
- but there is a `setAccessible(true)` call
  - does not make a private field public
  - it just suppresses the access control on that field
  - security checks can be made to prevent this kind of thing (if unwanted in app)

## Designing an EntityManager for Reading and Writing to Database

- `EntityManager` interface models the writing and reading of instances of `T` to any storage file or media
- w/o knowing what `T` is at compile time

```java
public interface EntityManager<T> {
    void persist(T t);
    T read(Class<?> _class, long primaryKey);
}
```

given an instance of T

1. read the fields of T
2. check for the annotations
3. find the primary key
4. find the fields to read/write

```java
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
        Person person = new Person(21, "Jane");

        Class<?> personClass = person.getClass();
        Field[] fields = personClass.getDeclaredFields();

        // loop through the fields of the class
        for (Field field : fields) {
            if (field.getAnnotation(PrimaryKey.class) != null) {
                // this is the primary key
            }
            if (field.getAnnotation(Column.class) != null) {
                // this is an element to read/write
            }
        }
    }
}
```
