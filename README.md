# Learn With Pluralsight: [Java 8 Fundamentals: The Java Reflection API Method Handles][course]

1. Course Overview [[GITHUB][m1.gh]]
2. Introducing Java Reflection [[GITHUB][m2.gh]]
3. Creating an Object Metamodel Using Annotations and Reflection [[GITHUB][m3.gh]]
4. Creating an Object Relational Mapper Using an Object Metamodel [[GITHUB][m4.gh]]
5. **Injecting Dependencies in an Object Using an Object Metamodel** [[GITHUB][m5.gh]]
6. Improving Performance Using Method Handles [[GITHUB][m6.gh]]

## 5. Injecting Dependencies in an Object Using an Object Metamodel

The SOLID principles of OOP:

- Single Responsibility Principle
    - each module/class should have one and only one reason to change
- Open-Closed Principle
    - each module/class should be open for extension but closed for modification
- Liskov Substitution Principle (LSP)
    - you should be able to use any derived class instead of a base class w/o modification
- Interface Segregation Principle (ISP)
    - clients should not be forced to use an interface which is not relevant to it
- **Dependency Inversion Principle (DIP)**
    - _high level classes should not depend on low level classes, instead both should depend upon abstraction_

### Designing a BeanManager to Perform Dependency Injection

```java
import java.sql.Connection;

public class EntityManager {
  @Inject
  private Connection connection;
}
```

```java
import java.sql.Connection;

public class H2ConnectionProvider {
  @Provides
  public static Connection createConnection(/*...*/) {/*...*/}
}
```

#### Understanding Injection at Runtime

- **BeanManager:** can wire objects together
    - **EntityManager:** needs a DB connection
    - **ConnectionProvider:** provides a DB connection

```java
class Main {
  public static void main(String[] args) {
    BeanManager beanManager = BeanManager.getInstance();
    EntityManager em = beanManager.get(EntityManager.class);
  }
}
```

#### Getting and Invoking Methods Using the Reflection API

```java
import java.sql.Connection;

public class ConnectionProvider {
    Connection createConnection(String uri) {
        //...
    }
}
```
```java
public class Main {
    public static void main(String[] args) {
        Class<?> _class = ConnectionProvider.class;
        Object connectionProvider = _class.getConstructor().getInstance();
        Method method = _class.getMethod("createConnection", String.class);
        
        method.invoke(connectionProvider, "jdbc:h2:mem:db_reflection");
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