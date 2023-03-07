
import beanmanager.BeanManager;
import model.Person;
import orm.EntityManager;
import orm.ManagedEntityManager;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class WritingObjects {
    public static void main(String[] args) throws SQLException, IllegalAccessException {
        BeanManager beanManager = BeanManager.getInstance();
        EntityManager<Person> em = beanManager.getInstance(ManagedEntityManager.class);

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
