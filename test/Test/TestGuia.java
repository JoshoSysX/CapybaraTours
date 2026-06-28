package Test;

import Dao.GuiaDaoImpl;
import Interface.IGuia;
import Model.Guia;

public class TestGuia {

    static IGuia dao = new GuiaDaoImpl();

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println(" TEST CRUD GUÍA - ORACLE");
        System.out.println("====================================");

//         insertarGuia();
        // listarGuias();
        // buscarGuia(1);
//         editarGuia(1);
        // listarGuias();
        eliminarGuia(1);
    }

    // ================= INSERT =================
    public static void insertarGuia() {
        System.out.println("\n=== INSERTAR GUÍA ===");
        Guia g = new Guia();
        g.setNombre("Carlos Mendoza");
        g.setTelefono("987654321");

        boolean resultado = dao.insert(g);
        if (resultado) {
            System.out.println(" Guía insertada correctamente");
        } else {
            System.out.println(" Error al insertar guía");
        }
    }

    // ================= LISTAR =================
    public static void listarGuias() {
        System.out.println("\n=== LISTA DE GUÍAS ===");
        var lista = dao.lista();
        if (lista.isEmpty()) {
            System.out.println("No existen registros");
        } else {
            System.out.printf("%-5s %-25s %-15s%n", "ID", "NOMBRE", "TELÉFONO");
            System.out.println("---------------------------------------------");
            for (Guia g : lista) {
                System.out.printf("%-5d %-25s %-15s%n",
                        g.getIdGuia(),
                        g.getNombre(),
                        g.getTelefono());
            }
        }
    }

    // ================= BUSCAR =================
    public static void buscarGuia(int id) {
        System.out.println("\n=== BUSCAR GUÍA ===");
        Guia g = dao.SearchById(id);
        if (g != null) {
            System.out.println("ID: " + g.getIdGuia());
            System.out.println("Nombre: " + g.getNombre());
            System.out.println("Teléfono: " + g.getTelefono());
        } else {
            System.out.println(" Guía no encontrada");
        }
    }

    // ================= EDITAR =================
    public static void editarGuia(int id) {
        System.out.println("\n=== EDITAR GUÍA ===");
        Guia g = dao.SearchById(id);
        if (g == null) {
            System.out.println(" Guía no encontrada");
            return;
        }
        
        g.setNombre("Carlos Mendoza Torres");
        g.setTelefono("999888777");

        boolean actualizado = dao.update(g);
        if (actualizado) {
            System.out.println(" Guía actualizada correctamente");
            Guia actualizada = dao.SearchById(id);
            System.out.println("ID: " + actualizada.getIdGuia());
            System.out.println("Nombre: " + actualizada.getNombre());
            System.out.println("Teléfono: " + actualizada.getTelefono());
        } else {
            System.out.println(" Error al actualizar");
        }
    }

    // ================= ELIMINAR =================
    public static void eliminarGuia(int id) {
        System.out.println("\n=== ELIMINAR GUÍA ===");
        Guia g = dao.SearchById(id);
        if (g == null) {
            System.out.println(" Guía no encontrada");
            return;
        }
        System.out.println("Eliminando: " + g.getNombre());
        
        boolean eliminado = dao.delete(id);
        if (eliminado) {
            System.out.println(" Guía eliminada correctamente");
        } else {
            System.out.println(" Error al eliminar");
        }
    }
}