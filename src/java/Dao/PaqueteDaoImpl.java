package Dao;

import Interface.IPaquete;
import Model.Paquete;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaqueteDaoImpl implements IPaquete {

    private Connection cn;

    @Override
    public List<Paquete> lista() {
        List<Paquete> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM PAQUETE ORDER BY id_paquete";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Paquete p = new Paquete();
                p.setId_paquete(rs.getInt("ID_PAQUETE"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setDescripcion(rs.getString("DESCRIPCION"));
                p.setDuracion(rs.getString("DURACION"));
                p.setPrecio(rs.getDouble("PRECIO"));
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar paquetes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Paquete p) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int idPaquete = 0;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            
            // Obtener siguiente ID
            st = cn.prepareStatement("SELECT SEQ_PAQUETE.NEXTVAL FROM DUAL");
            rs = st.executeQuery();
            if (rs.next()) {
                idPaquete = rs.getInt(1);
            }
            rs.close();
            st.close();

            String query = "INSERT INTO PAQUETE (ID_PAQUETE, NOMBRE, DESCRIPCION, DURACION, PRECIO) "
                         + "VALUES (?,?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, idPaquete);
            st.setString(2, p.getNombre());
            st.setString(3, p.getDescripcion());
            st.setString(4, p.getDuracion());
            st.setDouble(5, p.getPrecio());
            
            int r = st.executeUpdate();
            resultado = r > 0;
            
            if (resultado) {
                System.out.println("Paquete registrado correctamente con ID: " + idPaquete);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar paquete: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return resultado;
    }

    @Override
    public boolean update(Paquete p) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE PAQUETE SET NOMBRE=?, DESCRIPCION=?, DURACION=?, PRECIO=? WHERE ID_PAQUETE=?";
            st = cn.prepareStatement(query);
            st.setString(1, p.getNombre());
            st.setString(2, p.getDescripcion());
            st.setString(3, p.getDuracion());
            st.setDouble(4, p.getPrecio());
            st.setInt(5, p.getId_paquete());
            
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar paquete: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Paquete SearchById(int id) {
        Paquete p = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM PAQUETE WHERE ID_PAQUETE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                p = new Paquete();
                p.setId_paquete(rs.getInt("ID_PAQUETE"));
                p.setNombre(rs.getString("NOMBRE"));
                p.setDescripcion(rs.getString("DESCRIPCION"));
                p.setDuracion(rs.getString("DURACION"));
                p.setPrecio(rs.getDouble("PRECIO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar paquete: " + e.getMessage());
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
            String query = "DELETE FROM PAQUETE WHERE ID_PAQUETE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar paquete: " + e.getMessage());
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