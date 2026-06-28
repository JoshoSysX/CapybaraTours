package Dao;

import Interface.IGuia;
import Model.Guia;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuiaDaoImpl implements IGuia {

    private Connection cn;

    @Override
    public List<Guia> lista() {
        List<Guia> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM GUÍA ORDER BY id_guia";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Guia g = new Guia();
                g.setIdGuia(rs.getInt("ID_GUIA"));
                g.setNombre(rs.getString("NOMBRE"));
                g.setTelefono(rs.getString("TELÉFONO"));
                lista.add(g);
            }
        } catch (Exception e) {
            System.out.println("Error al listar guías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Guia g) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int idGuia = 0;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            
            // Obtener siguiente ID de Guía
            st = cn.prepareStatement("SELECT SEQ_GUIA.NEXTVAL FROM DUAL");
            rs = st.executeQuery();
            if (rs.next()) {
                idGuia = rs.getInt(1);
            }
            rs.close();
            st.close();

            // Insertar Guía
            String query = "INSERT INTO GUÍA (ID_GUIA, NOMBRE, TELÉFONO) "
                         + "VALUES (?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, idGuia);
            st.setString(2, g.getNombre());
            st.setString(3, g.getTelefono());
            
            int r = st.executeUpdate();
            resultado = r > 0;
            
            if (resultado) {
                System.out.println("Guía registrada correctamente con ID: " + idGuia);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar guía: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return resultado;
    }

    @Override
    public boolean update(Guia g) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE GUÍA SET NOMBRE=?, TELÉFONO=? WHERE ID_GUIA=?";
            st = cn.prepareStatement(query);
            st.setString(1, g.getNombre());
            st.setString(2, g.getTelefono());
            st.setInt(3, g.getIdGuia());
            
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar guía: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Guia SearchById(int id) {
        Guia g = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM GUÍA WHERE ID_GUIA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                g = new Guia();
                g.setIdGuia(rs.getInt("ID_GUIA"));
                g.setNombre(rs.getString("NOMBRE"));
                g.setTelefono(rs.getString("TELÉFONO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar guía: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return g;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM GUÍA WHERE ID_GUIA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar guía: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, st);
        }
    }

    private void cerrarRecursos(ResultSet rs, PreparedStatement st) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
        } catch (Exception ex) {
            System.out.println("Error cerrando recursos: " + ex.getMessage());
        }
    }
}