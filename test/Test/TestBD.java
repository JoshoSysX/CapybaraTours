
package Test;
import Util.ConexionOracleSingleton;
import java.sql.*;

public class TestBD {

    public static void main(String[] args) {
        TestBD t = new TestBD();
        t.testConexion();
        
    }
    public static void testConexion(){
        ConexionOracleSingleton conn = new ConexionOracleSingleton();
        try {
            Connection connection = conn.getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexion a Oracle satisfactoria!!!");
            } else {
                System.out.println("No se puede establecer conexion");
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
            e.printStackTrace();
        }
    }
    
}
