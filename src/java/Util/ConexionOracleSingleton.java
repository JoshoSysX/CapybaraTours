
package Util;
import java.sql.*;

public class ConexionOracleSingleton {

    public static Connection connection;

    public static Connection getConnection() {

        try {

            if (connection == null || connection.isClosed()) {

                Runtime.getRuntime().addShutdownHook(new GetClose());

                Class.forName("oracle.jdbc.OracleDriver");

                connection = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:xe",
                        "TOURBD",
                        "T123"
                );

                System.out.println("Conexión Oracle establecida");
            }

            return connection;

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Conexion con Oracle Fallida", e);
        }
    }

    static class GetClose extends Thread {

        @Override
        public void run() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

