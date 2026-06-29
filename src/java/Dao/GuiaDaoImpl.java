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
            String query = "SELECT * FROM GUIA ORDER BY id_guia";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Guia g = new Guia();
                g.setIdGuia(rs.getInt("ID_GUIA"));
                g.setNombre(rs.getString("NOMBRE"));
                g.setTelefono(rs.getString("TELEFONO"));
                lista.add(g);
            }
        } catch (Exception e) {
            System.out.println("Error al listar guias: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Guia g) {
        // El ID lo asigna el trigger TG_ID_GUIA (MAX(id_guia)+1), no se envia desde Java
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO GUIA (NOMBRE, TELEFONO) "
                         + "VALUES (?,?)";
            st = cn.prepareStatement(query);
            st.setString(1, g.getNombre());
            st.setString(2, g.getTelefono());

            int r = st.executeUpdate();
            resultado = r > 0;

            if (resultado) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_guia) FROM GUIA");
                rs = st.executeQuery();
                if (rs.next()) {
                    g.setIdGuia(rs.getInt(1));
                }
                System.out.println("Guia registrada correctamente con ID: " + g.getIdGuia());
            }
        } catch (Exception e) {
            System.out.println("Error al insertar guia: " + e.getMessage());
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
            String query = "UPDATE GUIA SET NOMBRE=?, TELEFONO=? WHERE ID_GUIA=?";
            st = cn.prepareStatement(query);
            st.setString(1, g.getNombre());
            st.setString(2, g.getTelefono());
            st.setInt(3, g.getIdGuia());

            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar guia: " + e.getMessage());
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
            String query = "SELECT * FROM GUIA WHERE ID_GUIA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                g = new Guia();
                g.setIdGuia(rs.getInt("ID_GUIA"));
                g.setNombre(rs.getString("NOMBRE"));
                g.setTelefono(rs.getString("TELEFONO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar guia: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return g;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM GUIA WHERE ID_GUIA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar guia: " + e.getMessage());
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