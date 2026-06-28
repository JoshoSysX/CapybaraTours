package Test;

import Dao.DetalleReservaDaoImpl;
import Interface.IDetalleReserva;
import Model.DetalleReserva;
import Model.Reserva;
import Model.Guia;
import Model.Transporte;

public class TestDetalleReserva {

    static IDetalleReserva dao = new DetalleReservaDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD DETALLE RESERVA - ORACLE");
        System.out.println("====================================");

        // insertarDetalle();
        // listarDetalles();
        // listarDetallesPorReserva(1);
        // buscarDetalle(1);
        eliminarDetalle(1);
    }

    // ================= INSERT =================
    public static void insertarDetalle() {
        System.out.println("\n=== INSERTAR DETALLE DE RESERVA ===");
        DetalleReserva dr = new DetalleReserva();
        
        Reserva r = new Reserva();
        r.setId_reserva(1);        
        
        Guia g = new Guia();
        g.setIdGuia(1);           
        
        Transporte t = new Transporte();
        t.setIdTransporte(1);      
        
        dr.setReserva(r);
        dr.setGuia(g);
        dr.setTransporte(t);
        dr.setFecha_salida(new java.util.Date(System.currentTimeMillis() + 864000000L)); 

        int resultado = dao.insert(dr);
        if (resultado > 0) {
            System.out.println(" Detalle de reserva insertado correctamente");
        } else {
            System.out.println(" Error al insertar detalle");
        }
    }

    // ================= LISTAR TODOS =================
    public static void listarDetalles() {
        System.out.println("\n=== LISTA DE DETALLES DE RESERVA ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-10s %-10s %-10s %-15s%n", 
                    "ID", "ID_RESERVA", "ID_GUIA", "ID_TRANSP", "FECHA SALIDA");
            System.out.println("-----------------------------------------------------");
            for (DetalleReserva dr : lista) {
                System.out.printf("%-5d %-10d %-10d %-10d %-15s%n",
                        dr.getId_detalleReserva(),
                        dr.getReserva().getId_reserva(),
                        dr.getGuia().getIdGuia(),
                        dr.getTransporte().getIdTransporte(),
                        dr.getFecha_salida());
            }
        }
    }

    // ================= LISTAR POR RESERVA =================
    public static void listarDetallesPorReserva(int idReserva) {
        System.out.println("\n=== DETALLES POR RESERVA (ID: " + idReserva + ") ===");
        var lista = dao.listaPorReserva(idReserva);
        if (lista.isEmpty()) {
            System.out.println("No existen detalles para esta reserva");
        } else {
            System.out.printf("%-5s %-10s %-10s %-10s%n", 
                    "ID_DETALLE", "ID_GUIA", "ID_TRANSPORTE", "FECHA SALIDA");
            System.out.println("---------------------------------------------");
            for (DetalleReserva dr : lista) {
                System.out.printf("%-10d %-10d %-10d %-15s%n",
                        dr.getId_detalleReserva(),
                        dr.getGuia() != null ? dr.getGuia().getIdGuia() : 0,
                        dr.getTransporte() != null ? dr.getTransporte().getIdTransporte() : 0,
                        dr.getFecha_salida());
            }
        }
    }

    // ================= BUSCAR =================
    public static void buscarDetalle(int id) {
        System.out.println("\n=== BUSCAR DETALLE DE RESERVA ===");
        DetalleReserva dr = dao.SearchById(id);
        if (dr != null) {
            System.out.println("ID Detalle: " + dr.getId_detalleReserva());
            System.out.println("ID Reserva: " + dr.getReserva().getId_reserva());
            System.out.println("ID Guía: " + dr.getGuia().getIdGuia());
            System.out.println("ID Transporte: " + dr.getTransporte().getIdTransporte());
            System.out.println("Fecha de Salida: " + dr.getFecha_salida());
        } else {
            System.out.println(" Detalle no encontrado");
        }
    }

    // ================= ELIMINAR =================
    public static void eliminarDetalle(int id) {
        System.out.println("\n=== ELIMINAR DETALLE DE RESERVA ===");
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Detalle de reserva eliminado correctamente");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}