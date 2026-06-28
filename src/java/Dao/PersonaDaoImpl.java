package Dao;

import Interface.IPersona;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDaoImpl implements IPersona {

    private Connection cn;

    @Override
    public List<Persona> lista() {
        List<Persona> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM PERSONA ORDER BY id_persona";

            st = cn.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Persona p = new Persona();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("NOMBRES"));
                p.setApellido(rs.getString("APELLIDOS"));
                p.setDocumento(rs.getString("DOCUMENTO"));
                p.setNumeroDoc(rs.getString("NUMERO_DOC"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("CORREO"));
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar personas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(Persona p, Usuario u) {

        PreparedStatement st = null;
        ResultSet rs = null;

        int idPersona = 0;
        int idUsuario = 0;
        int r = 0;

        try {

            cn = ConexionOracleSingleton.getConnection();

            // ===== OBTENER ID PERSONA =====
            st = cn.prepareStatement(
                    "SELECT SEQ_PERSONA.NEXTVAL FROM DUAL");

            rs = st.executeQuery();

            if (rs.next()) {
                idPersona = rs.getInt(1);
            }

            rs.close();
            st.close();

            // ===== INSERTAR PERSONA =====
            String query = "INSERT INTO PERSONA "
                    + "(ID_PERSONA, NOMBRES, APELLIDOS, DOCUMENTO, "
                    + "NUMERO_DOC, TELEFONO, CORREO) "
                    + "VALUES (?,?,?,?,?,?,?)";

            st = cn.prepareStatement(query);

            st.setInt(1, idPersona);
            st.setString(2, p.getNombre());
            st.setString(3, p.getApellido());
            st.setString(4, p.getDocumento());
            st.setString(5, p.getNumeroDoc());
            st.setString(6, p.getTelefono());
            st.setString(7, p.getEmail());

            r = st.executeUpdate();

            if (r > 0) {

                p.setId_persona(idPersona);

                // ===== OBTENER ID USUARIO =====
                st.close();

                st = cn.prepareStatement(
                        "SELECT SEQ_USUARIO.NEXTVAL FROM DUAL");

                rs = st.executeQuery();

                if (rs.next()) {
                    idUsuario = rs.getInt(1);
                }

                rs.close();
                st.close();

                // ===== INSERTAR USUARIO =====
                u.setRol(Rol.CLIENTE);

                String hashedPassword
                        = u.HashPassword(u.getContraseña());

                query = "INSERT INTO USUARIO "
                        + "(ID_USUARIO, ID_PERSONA, USUARIO, CONTRASEÑA, ROL) "
                        + "VALUES (?,?,?,?,?)";

                st = cn.prepareStatement(query);

                st.setInt(1, idUsuario);
                st.setInt(2, idPersona);
                st.setString(3, p.getEmail());
                st.setString(4, hashedPassword);
                st.setString(5, u.getRol().name());

                r = st.executeUpdate();

                if (r > 0) {
                    System.out.println("Persona y User creada");
                    System.out.println("Usuario: " + p.getEmail());
                    System.out.println("Rol asignado: " + u.getRol());
                }
            }

        } catch (Exception e) {

            System.out.println("Error al insertar persona: "
                    + e.getMessage());

            e.printStackTrace();

        } finally {

            cerrarRecursos(rs, st);

        }

        return r;
    }

    @Override
    public boolean update(Persona p) {
        PreparedStatement st = null;
        boolean resultado = false;

        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE PERSONA SET NOMBRES=?, APELLIDOS=?, DOCUMENTO=?, "
                    + "NUMERO_DOC=?, TELEFONO=?, CORREO=? WHERE id_persona=?";

            st = cn.prepareStatement(query);
            st.setString(1, p.getNombre());
            st.setString(2, p.getApellido());
            st.setString(3, p.getDocumento());
            st.setString(4, p.getNumeroDoc());
            st.setString(5, p.getTelefono());
            st.setString(6, p.getEmail());
            st.setInt(7, p.getId_persona());

            int r = st.executeUpdate();
            resultado = r > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Persona SearchById(int id) {
        Persona p = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM PERSONA WHERE id_persona = ?";

            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                p = new Persona();
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("NOMBRES"));
                p.setApellido(rs.getString("APELLIDOS"));
                p.setDocumento(rs.getString("DOCUMENTO"));
                p.setNumeroDoc(rs.getString("NUMERO_DOC"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("CORREO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return p;
    }

    @Override
    public boolean delete(int id) {

        PreparedStatement st = null;

        try {

            cn = ConexionOracleSingleton.getConnection();

            // Eliminar usuario asociado
            st = cn.prepareStatement(
                    "DELETE FROM USUARIO WHERE ID_PERSONA = ?"
            );
            st.setInt(1, id);
            st.executeUpdate();

            st.close();

            // Eliminar persona
            st = cn.prepareStatement(
                    "DELETE FROM PERSONA WHERE ID_PERSONA = ?"
            );
            st.setInt(1, id);

            return st.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Error al eliminar: " + e.getMessage());
            e.printStackTrace();

        } finally {

            cerrarRecursos(null, st);

        }

        return false;
    }

    // Método auxiliar para cerrar recursos
    private void cerrarRecursos(ResultSet rs, PreparedStatement st) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
        } catch (Exception ex) {
            System.out.println("Error cerrando recursos: " + ex.getMessage());
        }
    }
}
