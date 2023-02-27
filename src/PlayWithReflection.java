import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class PlayWithReflection {
    public static void main(String[] args) throws ClassNotFoundException {
        String personClassName = "model.Person";
        Class<?> personClass = Class.forName(personClassName);

        System.out.println("personClass = " + personClass);

        Field[] fields = personClass.getFields();
        System.out.println("fields = " + Arrays.toString(fields));

        Field[] declaredFields = personClass.getDeclaredFields();
        System.out.println("declaredFields = " + Arrays.toString(declaredFields));

        Method[] methods = personClass.getMethods();
        System.out.println("methods = ");
        Arrays.stream(methods)
                .map(m -> "\t" + m)
                .forEach(System.out::println);

        Method[] declaredMethods = personClass.getDeclaredMethods();
        System.out.println("declaredMethods = ");
        Arrays.stream(declaredMethods)
                .map(m -> "\t" + m)
                .forEach(System.out::println);

        System.out.println("declaredMethods (static) = ");
        Arrays.stream(declaredMethods)
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .map(m -> "\t" + m)
                .forEach(System.out::println);
    }
}
