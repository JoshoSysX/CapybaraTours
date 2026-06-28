package Test;

import Dao.TransporteDaoImpl;
import Interface.ITransporte;
import Model.Transporte;

public class TestTransporte {

    static ITransporte dao = new TransporteDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD TRANSPORTE - ORACLE");
        System.out.println("====================================");

        // insertarTransporte();
        // listarTransportes();
        // buscarTransporte(1);
        eliminarTransporte(1);
    }

    public static void insertarTransporte() {
        System.out.println("\n=== INSERTAR TRANSPORTE ===");
        Transporte t = new Transporte();
        t.setVehiculo("Bus");
        t.setCapacidad(40);
        t.setPlaca("ABC-123");

        boolean resultado = dao.insert(t);
        if (resultado) {
            System.out.println(" Transporte insertado correctamente");
        } else {
            System.out.println(" Error al insertar");
        }
    }

    public static void listarTransportes() {
        System.out.println("\n=== LISTA DE TRANSPORTES ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-12s %-10s %-12s%n", "ID", "VEHICULO", "CAPACIDAD", "PLACA");
            System.out.println("------------------------------------------");
            for (Transporte t : lista) {
                System.out.printf("%-5d %-12s %-10d %-12s%n",
                        t.getIdTransporte(), t.getVehiculo(), t.getCapacidad(), t.getPlaca());
            }
        }
    }

    public static void buscarTransporte(int id) {
        System.out.println("\n=== BUSCAR TRANSPORTE ===");
        Transporte t = dao.SearchById(id);
        if (t != null) {
            System.out.println("ID: " + t.getIdTransporte());
            System.out.println("Vehículo: " + t.getVehiculo());
            System.out.println("Capacidad: " + t.getCapacidad());
            System.out.println("Placa: " + t.getPlaca());
        } else {
            System.out.println(" Transporte no encontrado");
        }
    }

    public static void eliminarTransporte(int id) {
        System.out.println("\n=== ELIMINAR TRANSPORTE ===");
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Transporte eliminado");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}