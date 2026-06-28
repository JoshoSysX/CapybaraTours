package Dao;

import Interface.ITransporte;
import Model.Transporte;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransporteDaoImpl implements ITransporte {

    private Connection cn;

    @Override
    public List<Transporte> lista() {
        List<Transporte> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM TRANSPORTE ORDER BY id_transporte";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Transporte t = new Transporte();
                t.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
                t.setVehiculo(rs.getString("VEHICULO"));
                t.setCapacidad(rs.getInt("CAPACIDAD"));
                t.setPlaca(rs.getString("PLACA"));
                lista.add(t);
            }
        } catch (Exception e) {
            System.out.println("Error al listar transportes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Transporte t) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int idTransporte = 0;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            
            // Obtener siguiente ID
            st = cn.prepareStatement("SELECT SEQ_TRANSPORTE.NEXTVAL FROM DUAL");
            rs = st.executeQuery();
            if (rs.next()) {
                idTransporte = rs.getInt(1);
            }
            rs.close();
            st.close();

            String query = "INSERT INTO TRANSPORTE (ID_TRANSPORTE, VEHICULO, CAPACIDAD, PLACA) "
                         + "VALUES (?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, idTransporte);
            st.setString(2, t.getVehiculo());
            st.setInt(3, t.getCapacidad());
            st.setString(4, t.getPlaca());
            
            int r = st.executeUpdate();
            resultado = r > 0;
            
            if (resultado) {
                System.out.println("Transporte registrado correctamente con ID: " + idTransporte);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar transporte: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return resultado;
    }

    @Override
    public boolean update(Transporte t) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE TRANSPORTE SET VEHICULO=?, CAPACIDAD=?, PLACA=? WHERE ID_TRANSPORTE=?";
            st = cn.prepareStatement(query);
            st.setString(1, t.getVehiculo());
            st.setInt(2, t.getCapacidad());
            st.setString(3, t.getPlaca());
            st.setInt(4, t.getIdTransporte());
            
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar transporte: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Transporte SearchById(int id) {
        Transporte t = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT * FROM TRANSPORTE WHERE ID_TRANSPORTE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                t = new Transporte();
                t.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
                t.setVehiculo(rs.getString("VEHICULO"));
                t.setCapacidad(rs.getInt("CAPACIDAD"));
                t.setPlaca(rs.getString("PLACA"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar transporte: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return t;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM TRANSPORTE WHERE ID_TRANSPORTE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar transporte: " + e.getMessage());
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