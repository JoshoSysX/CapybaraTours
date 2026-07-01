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
        CallableStatement cs = null;
        ResultSet rs = null;

        int idPersona = 0;
        int resultado = 0;

        try {
            cn = ConexionOracleSingleton.getConnection();
            cn.setAutoCommit(false);

            String queryPersona
                    = "BEGIN "
                    + "INSERT INTO PERSONA "
                    + "(NOMBRES, APELLIDOS, DOCUMENTO, NUMERO_DOC, TELEFONO, CORREO) "
                    + "VALUES (?,?,?,?,?,?) "
                    + "RETURNING ID_PERSONA INTO ?; "
                    + "END;";

            cs = cn.prepareCall(queryPersona);

            cs.setString(1, p.getNombre());
            cs.setString(2, p.getApellido());
            cs.setString(3, p.getDocumento());
            cs.setString(4, p.getNumeroDoc());
            cs.setString(5, p.getTelefono());
            cs.setString(6, p.getEmail());
            cs.registerOutParameter(7, Types.INTEGER);

            cs.execute();

            idPersona = cs.getInt(7);
            p.setId_persona(idPersona);

            cs.close();
            cs = null;

            u.setRol(Rol.CLIENTE);
            String hashedPassword = u.HashPassword(u.getContraseña());

            String queryUsuario
                    = "INSERT INTO USUARIO "
                    + "(ID_PERSONA, USUARIO, \"CONTRASEÑA\", ROL) "
                    + "VALUES (?,?,?,?)";

            st = cn.prepareStatement(queryUsuario);
            st.setInt(1, idPersona);
            st.setString(2, p.getEmail());
            st.setString(3, hashedPassword);
            st.setString(4, u.getRol().name());

            int rUsuario = st.executeUpdate();

            if (rUsuario > 0) {
                cn.commit();
                resultado = 1;
                System.out.println("Persona y Usuario creados correctamente");
            } else {
                cn.rollback();
            }

        } catch (Exception e) {
            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Error al insertar persona y usuario:");
            e.printStackTrace();

        } finally {
            try {
                if (cn != null) {
                    cn.setAutoCommit(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            cerrarRecursos(rs, st);

            try {
                if (cs != null) {
                    cs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultado;
    }


    @Override
    public int insertSoloPersona(Persona p) {
        CallableStatement cs = null;
        int idPersona = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String queryPersona = "BEGIN "
                    + "INSERT INTO PERSONA "
                    + "(NOMBRES, APELLIDOS, DOCUMENTO, NUMERO_DOC, TELEFONO, CORREO) "
                    + "VALUES (?,?,?,?,?,?) "
                    + "RETURNING ID_PERSONA INTO ?; "
                    + "END;";
            cs = cn.prepareCall(queryPersona);
            cs.setString(1, p.getNombre());
            cs.setString(2, p.getApellido());
            cs.setString(3, p.getDocumento());
            cs.setString(4, p.getNumeroDoc());
            cs.setString(5, p.getTelefono());
            cs.setString(6, p.getEmail());
            cs.registerOutParameter(7, Types.INTEGER);
            cs.execute();
            idPersona = cs.getInt(7);
            p.setId_persona(idPersona);
        } catch (Exception e) {
            System.out.println("Error al insertar solo persona: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (cs != null) cs.close(); } catch (Exception e) { e.printStackTrace(); }
        }
        return idPersona;
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

    @Override
    public boolean existeCorreo(String correo) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT COUNT(*) FROM PERSONA WHERE LOWER(CORREO) = LOWER(?)";
            st = cn.prepareStatement(query);
            st.setString(1, correo);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error al validar correo: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return false;
    }

    @Override
    public boolean existeDocumento(String numeroDoc) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT COUNT(*) FROM PERSONA WHERE NUMERO_DOC = ?";
            st = cn.prepareStatement(query);
            st.setString(1, numeroDoc);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error al validar documento: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return false;
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
