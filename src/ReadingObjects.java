
import beanmanager.BeanManager;
import model.Person;
import orm.EntityManager;
import orm.ManagedEntityManager;

import java.util.Arrays;
import java.util.List;

public class ReadingObjects {
    public static void main(String[] args) throws Exception {
        BeanManager beanManager = BeanManager.getInstance();
        EntityManager<Person> em = beanManager.getInstance(ManagedEntityManager.class);

        Person linda = em.find(Person.class, 1L);
        Person james = em.find(Person.class, 2L);
        Person susan = em.find(Person.class, 3L);
        Person john = em.find(Person.class, 4L);

        List<Person> people = Arrays.asList(linda, james, susan, john);
        people.forEach(System.out::println);


    }
}
