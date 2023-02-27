import model.Person;
import util.ColumnField;
import util.Metamodel;
import util.PrimaryKeyField;

import java.util.List;

public class PlayWithMetamodel {
    public static void main(String[] args) {
        Metamodel<Person> metamodel = Metamodel.of(Person.class);
        PrimaryKeyField primaryKeyField = metamodel.getPrimaryKey();
        List<ColumnField> columnFields = metamodel.getColumns();

        System.out.println("primaryKeyField = ");
        System.out.println("\tprimaryKeyField.getName() = " + primaryKeyField.getName());
        System.out.println("\tprimaryKeyField.getType().getSimpleName() = " + primaryKeyField.getType().getSimpleName());

        System.out.println("columnFields = ");
        columnFields.forEach(columnField -> {
            System.out.println("\tcolumnField = ");
            System.out.println("\t\tcolumnField.getName() = " + columnField.getName());
            System.out.println("\t\tcolumnField.getType().getSimpleName() = " + columnField.getType().getSimpleName());
        });
    }
}
