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
            String query = "SELECT p.* FROM PAGO p ORDER BY p.id_pago";
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
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(Pago p) {
        // El id_pago lo asigna el trigger TG_ID_PAGO (MAX(id_pago)+1)
        PreparedStatement st = null;
        ResultSet rs = null;
        int idPago = 0;
        int resultado = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO PAGO (ID_RESERVA, MONTO, FECHA_PAGO, METODO_PAGO) "
                    + "VALUES (?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, p.getReserva().getId_reserva());
            st.setDouble(2, p.getMonto());
            st.setDate(3, new java.sql.Date(p.getFecha_pago().getTime()));
            st.setString(4, p.getMetodo_pago());

            resultado = st.executeUpdate();

            if (resultado > 0) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_pago) FROM PAGO");
                rs = st.executeQuery();
                if (rs.next()) {
                    idPago = rs.getInt(1);
                }
                p.setId_pago(idPago);
                System.out.println("Pago registrado correctamente con ID: " + idPago);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar pago: " + e.getMessage());
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
            String query = "SELECT p.* FROM PAGO p WHERE p.ID_PAGO = ?";
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
            String query = "DELETE FROM PAGO WHERE ID_PAGO = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar pago: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public List<Pago> listaPorReserva(int idReserva) {
        List<Pago> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT p.* FROM PAGO p WHERE p.ID_RESERVA = ? ORDER BY p.id_pago";
            st = cn.prepareStatement(query);
            st.setInt(1, idReserva);
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
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar pagos por reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    public boolean actualizarMontoPorReserva(int idReserva, double monto) {
        PreparedStatement st = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE PAGO SET MONTO=? WHERE ID_RESERVA=?";
            st = cn.prepareStatement(query);
            st.setDouble(1, monto);
            st.setInt(2, idReserva);
            return st.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar monto por reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return false;
    }


    @Override
    public double totalPagosGeneral() {
        PreparedStatement st = null;
        ResultSet rs = null;
        double total = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT NVL(SUM(MONTO), 0) AS TOTAL FROM PAGO";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("TOTAL");
            }
        } catch (Exception e) {
            System.out.println("Error al calcular total general de pagos: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return total;
    }

    @Override
    public double totalPagosPorAnio(int anio) {
        PreparedStatement st = null;
        ResultSet rs = null;
        double total = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT NVL(SUM(MONTO), 0) AS TOTAL FROM PAGO "
                    + "WHERE EXTRACT(YEAR FROM FECHA_PAGO) = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, anio);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("TOTAL");
            }
        } catch (Exception e) {
            System.out.println("Error al calcular total de pagos por año: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return total;
    }

    @Override
    public double totalPagosPorMes(int anio, int mes) {
        PreparedStatement st = null;
        ResultSet rs = null;
        double total = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT NVL(SUM(MONTO), 0) AS TOTAL FROM PAGO "
                    + "WHERE EXTRACT(YEAR FROM FECHA_PAGO) = ? "
                    + "AND EXTRACT(MONTH FROM FECHA_PAGO) = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, anio);
            st.setInt(2, mes);
            rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("TOTAL");
            }
        } catch (Exception e) {
            System.out.println("Error al calcular total de pagos por mes: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return total;
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