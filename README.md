# Learn With Pluralsight: [Java 8 Fundamentals: The Java Reflection API Method Handles][course]

1. Course Overview [[GITHUB][m1.gh]]
2. Introducing Java Reflection [[GITHUB][m2.gh]]
3. Creating an Object Metamodel Using Annotations and Reflection [[GITHUB][m3.gh]]
4. Creating an Object Relational Mapper Using an Object Metamodel [[GITHUB][m4.gh]]
5. Injecting Dependencies in an Object Using an Object Metamodel [[GITHUB][m5.gh]]
6. **Improving Performance Using Method Handles** [[GITHUB][m6.gh]]

## 6. Improving Performance Using Method Handles

### Setting up the Problem

How does this code work?

`Person person = Person.class.getConstructor().getInstance();`

```java
SecurityManager sm = System.getSecurityManager();
if (sm != null) {
    checkMemberAccess(sm, Member.PUBLIC,
        Reflection.getCallerClass(), false); // check if the caller has right to access reflection
}
// some code
Reflection.ensureMemberAccess(caller, this, this, modifiers); // apply some more security
// some more code
```

- there are several security checks when accessing a class using reflection
- all those checks are checked again each time access is made
- this is costly, has a noticeable impact on performance
- main reason the Reflection API is considered **slow**

### Introducing MethodHandles VarHandle and the Lookup Object

- the entrypoint of the MethodHandle API is the `Lookup` Object
- it encapsulates the security information
  - will check it once
- from it, you can create
  - `MethodHandle` instances to access the class, its methods and fields
  - `VarHandle` instances to access to fields in a concurrent way

### Getting a Private Trusted Lookup Object

`Lookup lookup = MethodHandles.lookup();`

- entry point is the object returned by the lookup static call
- the lookup object carries the security information
- it is a different instance for each caller
- _should not be shared w/ untrusted code!_

### Using a Method Handle to Get a Reference on a Class

```java
Person person = ...;

Class<?> personClass =
    MethodHandles.lookup() // returns a Lookup instance
            .findClass(Person.class.getName());
```

- entry point is the object returned by the lookup call
- one can call `findClass()` to get a reference on that class
- works almost the same as `Class.forName(String)`

### Creating MethodType Object to Get a Reference on a Method

- how to get a handle on a method
  - get a MethodType object
  - call the right method on the lookup object
  - there is also a bridge from method to `MethodHandle`

```java
Lookup lookup = MethodHandles.lookup();

// public String getName() { ... }
MethodType getterType =
        MethodType.methodType(String.class);

// public void setName(String name) { ... }
MethodType setterType =
    MethodType.methodType(void.class, String.class);

// public void Person(String name, int age) { ... }
MethodType constructorType =
        MethodType.methodType(void.class, String.class, int.class);

// public void Person() { ... }
MethodType emptyConstructorType =
        MethodType.methodType(void.class);
```

- first we create a method type for a method that returns a String and takes no argument
- then we create a method type for a method that returns void and returns a String argument
- in the case of a constructor, the returned type is `void.class`

### Getting a Method Handle on a Method or Constructor

several methods to find a handle

- `findVirtual()`
- `findConstructor()`
- `findStatic()`

```java
Lookup lookup = MethodHandles.lookup();

// public void setName(String name) { ... }
MethodType getterType =
        MethodType.methodType(String.class);

MethodHandle getterHandle =
        lookup.findVirtual(Person.class, "getName", getterType);
```

```java
Lookup lookup = MethodHandles.lookup();

// public void setName(String name) { ... }
MethodType setterType =
        MethodType.methodType(void.class, String.class);

MethodHandle setterHandle =
        lookup.findVirtual(Person.class, "setName", setterType);
```

```java
Lookup lookup = MethodHandles.lookup();

// public Person(String name, int age) { ... }
MethodType constructorType =
        MethodType.methodType(void.class, String.class, int.class);

MethodHandle constructorHandle =
        lookup.findConstuctor(Person.class, constructorType);
```

```java
Lookup lookup = MethodHandles.lookup();

// public Person() { ... }
MethodType emptyConstructorType =
        MethodType.methodType(void.class);

MethodHandle emptyConstructorHandle =
        lookup.findConstuctor(Person.class, emptyConstructorType);
```

### Getting a Method Handle to Read and Write a Field

lookup class has also these 2 methods:

- `findGetter()`
- `findSetter()`

they return handles on a field not on a getter/setter!

```java
Lookup lookup = MethodHandles.lookup();

MethodHandle nameReader =
        lookup.findGetter(Person.class, "name", String.class); // reads name

MethodHandle nameWriter =
        lookup.findSetter(Person.class, "name", String.class); // writes name
```

- a handle returned by a `findGetter` gives read access on a field and does not call the getter of that field
- a handle returned by a `findSetter` gives write access on a field and odes not call the setter of that field

### Using a Method Handle to Invoke a Method of a Class

- same as w/ an instance of `Method`
- 1st arg is object that holds the invoked method
- following args, if any, are passed to the method

```java
Person person = ...;

MethodHandle nameGetter = ...;
String name = (String) nameGetter.invoke(person);
```

### Accessing Public and Private Fields Using Method Handles

- a method handle gives access to non-private members

```java
Person person = ...;

Field nameField = Person.class.getDeclaredField("name");
nameField.setAccessible(true);

// pre-Java 9 solutions
MethodHandle privateNameReader = lookup.unreflectGetter(field);
String name (String) privateNameReader.invoke(person);

MethodHandle privateNameWriter = lookup.unreflectWriter(field);
privateNameWriter.invoke(person, "John");

// post-Java 9
Lookup privateLookup =
        MethodHandles.privateLookupIn(Person.class, lookup);

MethodHandle privateNameReader =
        privateLookup.findGetter(Person.class, "name", String.class);

String name = (String) privateNameReader.invoke(person);
```

### Adding Concurrent Field Access Using the Var Handle API

- has been added in Java 9
- looks like MethodHandle for fields
- but MethodHandle can already access a field...
- why has it been added? it gives you 3 typed of access
  - plain, regular access: read/write
  - volatile access
  - compare and set
- last 2 deal w/ concurrent access

### Using Var Handle to Add Concurrent Access to Fields

- the `get()` method invokes a var handle in normal mode
- the `getVolatile()` method invokes a var handle in volatile mode

```java
Lookup lookup = ...;
Person person = ...;

VarHandle nameVarHandle =
        MethodHandles.privateLookupIn(Person.class, lookup)
        .findVarHandle(Person.class, "name", String.class);
```

```java
String name = (String) nameVarHandle.get(person);
```

```java
String name = (String) nameVarHandle.getVolatile(person); // concurrent
```

the `getAndAdd()` method atomically adds the value passed and returns the previous value

```java
VarHandle ageVarHandle =
        MethodHandles.privateLookupIn(Person.class, lookup)
        .findVarHandle(Person.class, "age", int.class);

int newAge = (int) ageVarHandle.getAndAdd(person, 1); // concurrent
```

[course]: https://app.pluralsight.com/library/courses/java-fundamentals-reflection-api-method-handles
[m1.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/main
[m2.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/2-IntroducingJavaReflection
[m3.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/3-CreatingAnObjectMetamodelUsingAnnotationsAndReflection
[m4.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/4-CreatingAnObjectRelationalMapperUsingAnObjectMetamodel
[m5.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/5-InjectingDependenciesInAnObjectUsingAnObjectMetamodel
[m6.gh]: https://github.com/reinielfc/lrn-ps-java8-reflection-api-method-handles/tree/6-ImprovingPerformanceUsingMethodHandles
