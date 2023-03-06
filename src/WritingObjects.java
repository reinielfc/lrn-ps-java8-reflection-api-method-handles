
import model.Person;
import orm.EntityManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class WritingObjects {
    public static void main(String[] args) throws SQLException, IllegalAccessException {
        EntityManager<Person> em = EntityManager.of(Person.class);
        Person linda = new Person("Linda", 31);
        Person james = new Person("Janes", 24);
        Person susan = new Person("Susan", 34);
        Person john = new Person("John", 33);

        List<Person> personList = Arrays.asList(linda, james, susan, john);

        personList.forEach(System.out::println);

        System.out.println("Writing to DB...");

        for (Person person : personList) {
            em.persist(person);
        }

        personList.forEach(System.out::println);
    }
}
