package Test;

import Dao.PagoDaoImpl;
import Interface.IPago;
import Model.Pago;
import Model.Reserva;

public class TestPago {

    static IPago dao = new PagoDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD PAGO - ORACLE");
        System.out.println("====================================");

        // insertarPago();
        // listarPagos();
        // buscarPago(1);
        eliminarPago(1);
    }

    public static void insertarPago() {
        System.out.println("\n=== INSERTAR PAGO ===");
        Pago p = new Pago();
        
        Reserva r = new Reserva();
        r.setId_reserva(1); // Cambia por ID existente
        
        p.setReserva(r);
        p.setMonto(2500.00);
        p.setFecha_pago(new java.util.Date());
        p.setMetodo_pago("TAR");

        int resultado = dao.insert(p);
        if (resultado > 0) {
            System.out.println(" Pago registrado correctamente");
        } else {
            System.out.println(" Error al registrar pago");
        }
    }

    public static void listarPagos() {
        System.out.println("\n=== LISTA DE PAGOS ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-10s %-12s %-15s%n", "ID", "ID_RESERVA", "MONTO", "METODO");
            System.out.println("---------------------------------------------");
            for (Pago p : lista) {
                System.out.printf("%-5d %-10d $%-12.2f %-15s%n",
                        p.getId_pago(),
                        p.getReserva().getId_reserva(),
                        p.getMonto(),
                        p.getMetodo_pago());
            }
        }
    }

    public static void buscarPago(int id) {
        System.out.println("\n=== BUSCAR PAGO ===");
        Pago p = dao.SearchById(id);
        if (p != null) {
            System.out.println("ID Pago: " + p.getId_pago());
            System.out.println("ID Reserva: " + p.getReserva().getId_reserva());
            System.out.println("Monto: $" + p.getMonto());
            System.out.println("Método: " + p.getMetodo_pago());
            System.out.println("Fecha: " + p.getFecha_pago());
        } else {
            System.out.println(" Pago no encontrado");
        }
    }

    public static void eliminarPago(int id) {
        System.out.println("\n=== ELIMINAR PAGO ===");
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Pago eliminado");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}