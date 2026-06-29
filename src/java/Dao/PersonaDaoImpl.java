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
                p.setId_persona(rs.getInt("ID_PERSONA"));
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
        int r = 0;

        try {
            cn = ConexionOracleSingleton.getConnection();

            // ===== INSERTAR PERSONA =====
            // El id_persona lo asigna el trigger TG_ID_PERSONA (MAX(id_persona)+1)
            String query = "INSERT INTO PERSONA "
                    + "(NOMBRES, APELLIDOS, DOCUMENTO, "
                    + "NUMERO_DOC, TELEFONO, CORREO) "
                    + "VALUES (?,?,?,?,?,?)";

            st = cn.prepareStatement(query);

            st.setString(1, p.getNombre());
            st.setString(2, p.getApellido());
            st.setString(3, p.getDocumento());
            st.setString(4, p.getNumeroDoc());
            st.setString(5, p.getTelefono());
            st.setString(6, p.getEmail());

            r = st.executeUpdate();

            if (r > 0) {

                // ===== RECUPERAR ID GENERADO POR EL TRIGGER =====
                // Necesario porque USUARIO depende de id_persona como FK
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_persona) FROM PERSONA");
                rs = st.executeQuery();
                if (rs.next()) {
                    idPersona = rs.getInt(1);
                }
                p.setId_persona(idPersona);
                st.close();

                // ===== INSERTAR USUARIO =====
                // El trigger TG_ID_USUARIO asigna id_usuario = id_persona automaticamente
                u.setRol(Rol.CLIENTE);

                String hashedPassword = u.HashPassword(u.getContraseña());

                query = "INSERT INTO USUARIO "
                        + "(ID_PERSONA, USUARIO, CONTRASEÑA, ROL) "
                        + "VALUES (?,?,?,?)";

                st = cn.prepareStatement(query);

                st.setInt(1, idPersona);
                st.setString(2, p.getEmail());
                st.setString(3, hashedPassword);
                st.setString(4, u.getRol().name());

                r = st.executeUpdate();

                if (r > 0) {
                    System.out.println("Persona y Usuario creados");
                    System.out.println("Usuario: " + p.getEmail());
                    System.out.println("Rol asignado: " + u.getRol());
                }
            }

        } catch (Exception e) {
            System.out.println("Error al insertar persona: " + e.getMessage());
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
                p.setId_persona(rs.getInt("ID_PERSONA"));
                p.setNombre(rs.getString("NOMBRES"));
                p.setApellido(rs.getString("APELLIDOS"));
                p.setDocumento(rs.getString("DOCUMENTO"));
                p.setNumeroDoc(rs.getString("NUMERO_DOC"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("CORREO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar persona: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return p;
    }

    @Override
    public boolean delete(int id) {

        PreparedStatement st = null;
        boolean resultado = false;

        try {
            cn = ConexionOracleSingleton.getConnection();

            // Eliminar usuario asociado primero (FK)
            st = cn.prepareStatement("DELETE FROM USUARIO WHERE ID_PERSONA = ?");
            st.setInt(1, id);
            st.executeUpdate();
            st.close();

            // Eliminar persona
            st = cn.prepareStatement("DELETE FROM PERSONA WHERE ID_PERSONA = ?");
            st.setInt(1, id);

            int r = st.executeUpdate();
            resultado = r > 0;

        } catch (Exception e) {
            System.out.println("Error al eliminar persona: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }

        return resultado;
    }

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