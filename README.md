# Learn With Pluralsight: [Java 8 Fundamentals: The Java Reflection API Method Handles][course]

1. Course Overview [[GITHUB][m1.gh]]
2. **Introducing Java Reflection** [[GITHUB][m2.gh]]
3. Creating an Object Metamodel Using Annotations and Reflection [[GITHUB][m3.gh]]
4. Creating an Object Relational Mapper Using an Object Metamodel [[GITHUB][m4.gh]]
5. Injecting Dependencies in an Object Using an Object Metamodel [[GITHUB][m5.gh]]
6. Improving Performance Using Method Handles [[GITHUB][m6.gh]]

## 2. Introducing Java Reflection

- Reflection API introduced in Java 1
- Method Handles introduced in Java 7
- read and modify the content of an object
- w/o knowing its class or structure
- how to discover the content of an object

Why is this API so important?

- all the major java frameworks use it
- Hibernate, EclipseLink
- JAX-B, JASON-B
- Spring, CDI, Guice
- JAX-RS, JAX-WS
- JUnit, TestNG, Mockito, ACID J, ...

### Introducing the Java Reflection API: Class, Field, and Method

several classes that provide a model for each fundamental part of a class:

- `Class`
- `Field`
- `Method`
- `Constructor`
- `Annotation`

### The Class Named Class

- cannot create a class instance, but you can get one by
- querying an existing object for its class: _`<object>`_`.getclass()`
- you can get a class by its name, known at compile time: _`<ClassName>`_`.class`
- you can get a class by its name, known at runtime: `Class.forName(`_`"className"`_`)`

### Getting a Class Instance

```java
public class Main {
    public static void main(String[] args) {
        String hello = "Hello";
        Class helloClass = hello.getClass(); // `getClass()` is declared on the Object class
        String world = "World";
        Class worldClass = world.getClass(); // there is only one instance of Class for a given class

        System.out.println(helloClass == worldClass); // true
    }
}
```

- `Class` is a class w/ a parameter
- so, some affectations do compile
- and some do not!

```java
public class Main {
    public static void main(String[] args) {
        //Class<?> getClass();
        Class<?> helloClass = "Hello".getClass(); // Class is a class w/ a parameter
        Class<String> worldClass = "World".getClass(); // COMPILE ERROR!!!
        Class<Object> helloWorldClass = "Hello World".getClass(); // COMPILE ERROR!!!
        
        // should be written like this:
        Class<?> helloClass = "Hello".getClass();
        Class<? extends String> worldClass = "World".getClass(); 
        Class<? extends Object> helloWorldClass = "Hello World".getClass(); // (same as Class<?>)
    }
}
```

- get a Class object from a known class: `Class<?> stringClass = String.class`
- and from the name of a class:

```java
public class Main {
    public static void main(String[] args) {
        String className = "java.lang.String";
        Class<?> stringClass = Class.forName(className);
    }
}
```

- beware of exceptions
    - security-related exceptions (trying to access a class w/o permissions)
    - name-related exception (no class w/ that name)

### Getting the Super Class and the Implemented Interfaces of a Class

- from the Class object, we can:
    - get the super classes
    - get the implemented interfaces, if any
        - interfaces are seen as types by the Reflection API, seen as Class object
- the super class of Object is _null_
- `getInterfaces()`: returns the _interfaces,_ or an empty array

```java
public class Main {
    public static void main(String[] args) {
        Class<?> theClass = "Hello".getClass();
        Class<?> superClass = theClass.getSuperclass(); // returns only super class
        Class<?>[] interfaces = theClass.getInterfaces(); // returns the interfaces or an empty array
    }
}
```

### Getting the Fields of a Class

methods in class, methods to get references on the:

- fields
- methods and constructors
- many more

the _"non-declared"_ elements of a class

- elements declared in this class and all super classes
- but only the public ones

the **"declared"** elements of a class

- elements declared in this class
- private
- protected
- public
- w/ no inherited element

```java
public class Main {
    public static void main(String[] args) {
        Class<?> personClass = Person.class;
        Field ageField = personClass.getField("age"); // specific field
        Field[] declaredFields = personClass.getDeclaredFields(); // all declared
        Field[] fields = personClass.getFields(); // only public + public fields of super class
    }
}
```

Example:

```java
public class Person {
    private int age;
    private String name;
    // getters and setters
}
```
```java
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {
        Class<?> personClass = Person.class;
        Field[] fields = personClass.getFields(); // [] empty array
        Field[] declaredFields = personClass.getDeclaredFields(); // [...] age and name fields 
    }
}  
```

### Getting the Methods and the Constructors of a Class

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        Class<?> personClass = Person.class;

        // 3 methods
        Method method = personClass.getMethod("setName", String.class);
        Method[] declaredMethods = personClass.getDeclaredMethods();
        Method[] methods = personClass.getMethods();

        // 3 method for constructors
        Constructor constructor = personClass.getConstructor(); // takes the types of the parameters
        Constructor[] declaredConstructors = personClass.getDeclaredConstructors();
        Constructor[] constructors = personClass.getConstructors();
    }
}
```

### Reading The Modifier to Tell if a Class Member is Public

the modifiers tell if a field/method is

- static or not
- abstract or not
- final or not
- public/private/protected
- synchronized/volatile
- native

`getModifiers()` returns an int (32 bits), by bit #:

1. public or not
2. private or not
3. protected or not
4. static or not
5. so on...

```java
import java.lang.reflect.Modifier;

public class Main {
  public static void main(String[] args) {
    Class<?> personClass = Person.class;

    Field field = personClass.getField("name");
    int modifiers = field.getModifiers();

    // check if a field is public by using the correct bit mask
    boolean isPublic = modifiers & 0x00000001; // is tedious or error-prone
    
    // right pattern to use, Modifier factory class:
    isPublic = Modifier.isPublic(modifiers);

  }
}
```

[course]: https://app.pluralsight.com/library/courses/java-fundamentals-reflection-api-method-handles
[m1.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/main
[m2.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/2-IntroducingJavaReflection
[m3.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/3-CreatingAnObjectMetamodelUsingAnnotationsAndReflection
[m4.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/4-CreatingAnObjectRelationalMapperUsingAnObjectMetamodel
[m5.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/5-InjectingDependenciesInAnObjectUsingAnObjectMetamodel
[m6.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/6-ImprovingPerformanceUsingMethodHandles