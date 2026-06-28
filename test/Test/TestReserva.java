package Test;

import Dao.ReservaDaoImpl;
import Interface.IReserva;
import Model.Persona;
import Model.Paquete;
import Model.Reserva;
import Model.EstadoReserva;  // Asegúrate que exista este enum

public class TestReserva {

    static IReserva dao = new ReservaDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD RESERVA - ORACLE");
        System.out.println("====================================");

         insertarReserva();
        // listarReservas();
        // buscarReserva(1);
        // editarReserva(1);
//        eliminarReserva(1);
    }

    public static void insertarReserva() {
        System.out.println("\n=== INSERTAR RESERVA ===");
        Reserva r = new Reserva();
        
        Persona p = new Persona();
        p.setId_persona(27);  
        
        Paquete paq = new Paquete();
        paq.setId_paquete(5); 
        
        r.setPersona(p);
        r.setPaquete(paq);
        r.setFecha(new java.util.Date());
        r.setCantidad(2);
        r.setEstadoReserva(EstadoReserva.PENDIENTE); 
        r.setFecha_programada(new java.util.Date(System.currentTimeMillis() + 864000000L)); 

        int resultado = dao.insert(r);
        if (resultado > 0) {
            System.out.println(" Reserva insertada correctamente");
        } else {
            System.out.println(" Error al insertar reserva");
        }
    }

    public static void listarReservas() {
        System.out.println("\n=== LISTA DE RESERVAS ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-15s %-25s %-12s %-10s%n", 
                    "ID", "PERSONA", "PAQUETE", "CANTIDAD", "ESTADO");
            System.out.println("---------------------------------------------------------------");
            for (Reserva r : lista) {
                System.out.printf("%-5d %-15s %-25s %-12d %-10s%n",
                        r.getId_reserva(),
                        r.getPersona().getNombre() + " " + r.getPersona().getApellido(),
                        r.getPaquete().getNombre(),
                        r.getCantidad(),
                        r.getEstadoReserva());
            }
        }
    }

    public static void buscarReserva(int id) {
        System.out.println("\n=== BUSCAR RESERVA ===");
        Reserva r = dao.SearchById(id);
        if (r != null) {
            System.out.println("ID Reserva: " + r.getId_reserva());
            System.out.println("Cliente: " + r.getPersona().getNombre() + " " + r.getPersona().getApellido());
            System.out.println("Paquete: " + r.getPaquete().getNombre());
            System.out.println("Cantidad: " + r.getCantidad());
            System.out.println("Fecha Programada: " + r.getFecha_programada());
        } else {
            System.out.println(" Reserva no encontrada");
        }
    }

    public static void editarReserva(int id) {
        System.out.println("\n=== EDITAR RESERVA ===");
        Reserva r = dao.SearchById(id);
        if (r == null) {
            System.out.println(" Reserva no encontrada");
            return;
        }
        r.setCantidad(4);
        r.setEstadoReserva(EstadoReserva.FINALIZADA);
        
        boolean actualizado = dao.update(r);
        if (actualizado) {
            System.out.println("Reserva actualizada correctamente");
        } else {
            System.out.println("Error al actualizar");
        }
    }

    public static void eliminarReserva(int id) {
        System.out.println("\n=== ELIMINAR RESERVA ===");
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Reserva eliminada");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}