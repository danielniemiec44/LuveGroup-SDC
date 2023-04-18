package luvegroup.sdc;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;

public class DatabaseConnection {
    public static Connection getConnection(String password) {
        // Connection to PostgreSQL DB
        Connection connection = PostgreSQLConnectionBuilder.createConnectionPool(
                "jdbc:postgresql://10.2.0.11:5432/postgres?user=postgres&password=" + password);

        return connection;
    }
}
