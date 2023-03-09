import model.Person;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public class PlayWithMethodHandle {
    public static void main(String[] args) throws Throwable {
        Lookup lookup = MethodHandles.lookup();

        MethodType emptyConstructorMethodType = MethodType.methodType(void.class);
        MethodHandle emptyConstructor = lookup.findConstructor(Person.class, emptyConstructorMethodType);

        Person p = (Person) emptyConstructor.invoke();
        System.out.println(p);

        MethodType constructorMethodType = MethodType.methodType(void.class, String.class, int.class);
        MethodHandle constructor = lookup.findConstructor(Person.class, constructorMethodType);

        Person p2 = (Person) constructor.invoke( "James", 28);
        System.out.println(p2);

        MethodType nameGetterMethodType = MethodType.methodType(String.class);
        MethodHandle getName = lookup.findVirtual(Person.class, "getName", nameGetterMethodType);

        String name = (String) getName.invoke(p2);
        System.out.println(name);

        MethodType nameSetterMethodType = MethodType.methodType(void.class, String.class);
        MethodHandle setName = lookup.findVirtual(Person.class, "setName", nameSetterMethodType);

        setName.invoke(p2, "Linda");
        System.out.println(p2);

        Lookup privateLookup = MethodHandles.privateLookupIn(Person.class, lookup);
        MethodHandle nameReader =
                //lookup.findGetter(Person.class, "name", String.class);
                privateLookup.findGetter(Person.class, "name", String.class);
        name = (String) nameReader.invoke(p2);
        System.out.println(name);
    }
}
