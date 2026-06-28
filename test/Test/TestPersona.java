package Test;

import Dao.PersonaDaoImpl;
import Interface.IPersona;
import Model.Persona;
import Model.Usuario;

public class TestPersona {

    static IPersona dao = new PersonaDaoImpl();

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("   TEST CRUD PERSONA - ORACLE");
        System.out.println("====================================");

        // INSERTAR
        insertarNuevaPersona();

//        // LISTAR
//        listarPersonas();
//
//        // BUSCAR
//        buscarPersona(1);
//
//        // EDITAR
//        editarPersona(22);
//
//        // LISTAR NUEVAMENTE
//        listarPersonas();

        // ELIMINAR
         eliminarPersona(21);
    }

    // ================= INSERT =================

    public static void insertarNuevaPersona() {

        System.out.println("\n=== INSERTAR PERSONA ===");

        Persona p = new Persona();

        p.setNombre("María");
        p.setApellido("López");
        p.setDocumento("D");
        p.setNumeroDoc("76543210");
        p.setTelefono("987654321");
        p.setEmail("maria.lopez@gmail.com");

        Usuario u = new Usuario();
        u.setContraseña("password123");

        int resultado = dao.insert(p, u);

        if (resultado > 0) {
            System.out.println(" Persona insertada correctamente");
            System.out.println("ID: " + p.getId_persona());
        } else {
            System.out.println(" Error al insertar");
        }
    }

    // ================= LISTAR =================

    public static void listarPersonas() {

        System.out.println("\n=== LISTA DE PERSONAS ===");

        var lista = dao.lista();

        if (lista.isEmpty()) {

            System.out.println("No existen registros");

        } else {

            System.out.printf(
                    "%-5s %-15s %-15s %-12s %-15s %-25s%n",
                    "ID",
                    "NOMBRE",
                    "APELLIDO",
                    "DOC",
                    "TELEFONO",
                    "EMAIL"
            );

            System.out.println(
                    "--------------------------------------------------------------------------");

            for (Persona p : lista) {

                System.out.printf(
                        "%-5d %-15s %-15s %-12s %-15s %-25s%n",
                        p.getId_persona(),
                        p.getNombre(),
                        p.getApellido(),
                        p.getNumeroDoc(),
                        p.getTelefono(),
                        p.getEmail()
                );
            }
        }
    }

    // ================= BUSCAR =================

    public static void buscarPersona(int id) {

        System.out.println("\n=== BUSCAR PERSONA ===");

        Persona p = dao.SearchById(id);

        if (p != null) {

            System.out.println("ID: " + p.getId_persona());
            System.out.println("Nombre: " + p.getNombre());
            System.out.println("Apellido: " + p.getApellido());
            System.out.println("Documento: " + p.getDocumento());
            System.out.println("N° Documento: " + p.getNumeroDoc());
            System.out.println("Teléfono: " + p.getTelefono());
            System.out.println("Correo: " + p.getEmail());

        } else {

            System.out.println(" Persona no encontrada");

        }
    }

    // ================= EDITAR =================

    public static void editarPersona(int id) {

        System.out.println("\n=== EDITAR PERSONA ===");

        Persona p = dao.SearchById(id);

        if (p == null) {

            System.out.println(" Persona no encontrada");
            return;

        }

        // Nuevos datos
        p.setNombre("Fernanda");
        p.setApellido("Gonzales Pérez");
        p.setDocumento("D");
        p.setNumeroDoc("98765432");
        p.setTelefono("999888777");
        p.setEmail("mariafernanda@gmail.com");

        boolean actualizado = dao.update(p);

        if (actualizado) {

            System.out.println(" Persona actualizada");

            Persona nueva = dao.SearchById(id);

            System.out.println("ID: " + nueva.getId_persona());
            System.out.println("Nombre: " + nueva.getNombre());
            System.out.println("Apellido: " + nueva.getApellido());
            System.out.println("Documento: " + nueva.getDocumento());
            System.out.println("N° Documento: " + nueva.getNumeroDoc());
            System.out.println("Teléfono: " + nueva.getTelefono());
            System.out.println("Correo: " + nueva.getEmail());

        } else {

            System.out.println(" Error al actualizar");

        }
    }

    // ================= ELIMINAR =================

    public static void eliminarPersona(int id) {

        System.out.println("\n=== ELIMINAR PERSONA ===");

        Persona p = dao.SearchById(id);

        if (p == null) {

            System.out.println(" Persona no encontrada");
            return;

        }

        System.out.println(
                "Eliminando: "
                + p.getNombre()
                + " "
                + p.getApellido()
        );

        boolean eliminado = dao.delete(id);

        if (eliminado) {

            System.out.println(" Persona eliminada");

        } else {

            System.out.println(" Error al eliminar");

        }
    }
}