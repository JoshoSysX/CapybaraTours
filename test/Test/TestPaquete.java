package Test;

import Dao.PaqueteDaoImpl;
import Interface.IPaquete;
import Model.Paquete;

public class TestPaquete {

    static IPaquete dao = new PaqueteDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD PAQUETE - ORACLE");
        System.out.println("====================================");

         insertarPaquete();
        // listarPaquetes();
        // buscarPaquete(1);
//         editarPaquete(3);
        // listarPaquetes();
//        eliminarPaquete(5);
    }

    // ================= INSERT =================
    public static void insertarPaquete() {
        System.out.println("\n=== INSERTAR PAQUETE ===");
        Paquete p = new Paquete();
        p.setNombre("Machu Picchu Express");
        p.setDescripcion("Tour de 4 días por Machu Picchu y Valle Sagrado");
        p.setDuracion("4 días / 3 noches");
        p.setPrecio(1250.00);

        boolean resultado = dao.insert(p);
        if (resultado) {
            System.out.println(" Paquete insertado correctamente");
        } else {
            System.out.println(" Error al insertar");
        }
    }

    // ================= LISTAR =================
    public static void listarPaquetes() {
        System.out.println("\n=== LISTA DE PAQUETES ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-30s %-15s %-12s%n", "ID", "NOMBRE", "DURACION", "PRECIO");
            System.out.println("-------------------------------------------------------------");
            for (Paquete p : lista) {
                System.out.printf("%-5d %-30s %-15s $%-12.2f%n",
                        p.getId_paquete(),
                        p.getNombre(),
                        p.getDuracion(),
                        p.getPrecio());
            }
        }
    }

    // ================= BUSCAR =================
    public static void buscarPaquete(int id) {
        System.out.println("\n=== BUSCAR PAQUETE ===");
        Paquete p = dao.SearchById(id);
        if (p != null) {
            System.out.println("ID: " + p.getId_paquete());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Descripción: " + p.getDescripcion());
            System.out.println("Duración: " + p.getDuracion());
            System.out.println("Precio: $" + p.getPrecio());
        } else {
            System.out.println(" Paquete no encontrado");
        }
    }

    // ================= EDITAR =================
    public static void editarPaquete(int id) {
        System.out.println("\n=== EDITAR PAQUETE ===");
        Paquete p = dao.SearchById(id);
        if (p == null) {
            System.out.println(" Paquete no encontrado");
            return;
        }
        p.setNombre("Machu Picchu Premium");
        p.setDescripcion("Tour mejorado con hotel 5 estrellas");
        p.setDuracion("5 días / 4 noches");
        p.setPrecio(1890.00);

        boolean actualizado = dao.update(p);
        if (actualizado) {
            System.out.println(" Paquete actualizado correctamente");
        } else {
            System.out.println(" Error al actualizar");
        }
    }

    // ================= ELIMINAR =================
    public static void eliminarPaquete(int id) {
        System.out.println("\n=== ELIMINAR PAQUETE ===");
        Paquete p = dao.SearchById(id);
        if (p == null) {
            System.out.println(" Paquete no encontrado");
            return;
        }
        System.out.println("Eliminando: " + p.getNombre());
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Paquete eliminado");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}