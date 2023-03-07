package provider;

import annotation.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider {
    @Provides
    public Connection buildConnection() throws SQLException {
        String connectionUrl = "jdbc:h2:/media/data/project/lrn-ps-java8-reflection-api-method-handles/db/pluralsight-db";
        String username = "sa";
        String password = "";

        return DriverManager.getConnection(connectionUrl, username, password);
    }
}
