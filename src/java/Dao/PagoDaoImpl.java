package Dao;

import Interface.IPago;
import Model.Pago;
import Model.Reserva;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDaoImpl implements IPago {

    private Connection cn;

    @Override
    public List<Pago> lista() {
        List<Pago> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT p.*, r.ID_RESERVA FROM PAGO p "
                         + "JOIN RESERVA r ON p.ID_RESERVA = r.ID_RESERVA "
                         + "ORDER BY p.id_pago";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Pago p = new Pago();
                p.setId_pago(rs.getInt("ID_PAGO"));
                
                Reserva res = new Reserva();
                res.setId_reserva(rs.getInt("ID_RESERVA"));
                p.setReserva(res);
                
                p.setMonto(rs.getDouble("MONTO"));
                p.setFecha_pago(rs.getDate("FECHA_PAGO"));
                p.setMetodo_pago(rs.getString("METODO_PAGO"));
                // p.setEstadopago(...) → según tu enum
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(Pago p) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int idPago = 0;
        int resultado = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            
            // Obtener siguiente ID de Pago
            st = cn.prepareStatement("SELECT SEQ_PAGO.NEXTVAL FROM DUAL");
            rs = st.executeQuery();
            if (rs.next()) {
                idPago = rs.getInt(1);
            }
            rs.close();
            st.close();

            String query = "INSERT INTO PAGO (ID_PAGO, ID_RESERVA, MONTO, FECHA_PAGO, METODO_PAGO) "
                         + "VALUES (?,?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, idPago);
            st.setInt(2, p.getReserva().getId_reserva());
            st.setDouble(3, p.getMonto());
            st.setDate(4, new java.sql.Date(p.getFecha_pago().getTime()));
            st.setString(5, p.getMetodo_pago());
            
            resultado = st.executeUpdate();
            
            if (resultado > 0) {
                System.out.println("Pago registrado correctamente con ID: " + idPago);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar pago: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return resultado;
    }

    @Override
    public boolean update(Pago p) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE PAGO SET ID_RESERVA=?, MONTO=?, FECHA_PAGO=?, METODO_PAGO=? "
                         + "WHERE ID_PAGO=?";
            st = cn.prepareStatement(query);
            st.setInt(1, p.getReserva().getId_reserva());
            st.setDouble(2, p.getMonto());
            st.setDate(3, new java.sql.Date(p.getFecha_pago().getTime()));
            st.setString(4, p.getMetodo_pago());
            st.setInt(5, p.getId_pago());
            
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar pago: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Pago SearchById(int id) {
        Pago p = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT p.*, r.ID_RESERVA FROM PAGO p "
                         + "JOIN RESERVA r ON p.ID_RESERVA = r.ID_RESERVA "
                         + "WHERE p.ID_PAGO = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                p = new Pago();
                p.setId_pago(rs.getInt("ID_PAGO"));
                
                Reserva res = new Reserva();
                res.setId_reserva(rs.getInt("ID_RESERVA"));
                p.setReserva(res);
                
                p.setMonto(rs.getDouble("MONTO"));
                p.setFecha_pago(rs.getDate("FECHA_PAGO"));
                p.setMetodo_pago(rs.getString("METODO_PAGO"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar pago: " + e.getMessage());
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
            String query = "DELETE FROM PAGO WHERE ID_PAGO = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar pago: " + e.getMessage());
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

    @Override
    public List<Pago> listaPorReserva(int idReserva) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}