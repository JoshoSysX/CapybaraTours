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
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Transporte t) {
        // El ID lo asigna el trigger TG_ID_TRANSPORTE (MAX(id_transporte)+1), no se envia desde Java
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO TRANSPORTE (VEHICULO, CAPACIDAD, PLACA) "
                         + "VALUES (?,?,?)";
            st = cn.prepareStatement(query);
            st.setString(1, t.getVehiculo());
            st.setInt(2, t.getCapacidad());
            st.setString(3, t.getPlaca());

            int r = st.executeUpdate();
            resultado = r > 0;

            if (resultado) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_transporte) FROM TRANSPORTE");
                rs = st.executeQuery();
                if (rs.next()) {
                    t.setIdTransporte(rs.getInt(1));
                }
                System.out.println("Transporte registrado correctamente con ID: " + t.getIdTransporte());
            }
        } catch (Exception e) {
            System.out.println("Error al insertar transporte: " + e.getMessage());
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
        } finally {
            cerrarRecursos(rs, st);
        }
        return t;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM TRANSPORTE WHERE ID_TRANSPORTE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar transporte: " + e.getMessage());
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