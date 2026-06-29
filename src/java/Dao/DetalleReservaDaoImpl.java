package Dao;

import Interface.IDetalleReserva;
import Model.DetalleReserva;
import Model.Reserva;
import Model.Guia;
import Model.Transporte;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleReservaDaoImpl implements IDetalleReserva {

    private Connection cn;

    @Override
    public List<DetalleReserva> lista() {
        List<DetalleReserva> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT d.*, g.NOMBRE AS NOMBRE_GUIA, t.PLACA "
                    + "FROM DETALLE d "
                    + "JOIN GUIA g ON d.ID_GUIA = g.ID_GUIA "
                    + "JOIN TRANSPORTE t ON d.ID_TRANSPORTE = t.ID_TRANSPORTE "
                    + "ORDER BY d.id_detalle";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                DetalleReserva dr = new DetalleReserva();
                dr.setId_detalleReserva(rs.getInt("ID_DETALLE"));

                Reserva res = new Reserva();
                res.setId_reserva(rs.getInt("ID_RESERVA"));
                dr.setReserva(res);

                Guia gui = new Guia();
                gui.setIdGuia(rs.getInt("ID_GUIA"));
                gui.setNombre(rs.getString("NOMBRE_GUIA"));
                dr.setGuia(gui);

                Transporte trans = new Transporte();
                trans.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
                trans.setPlaca(rs.getString("PLACA"));
                dr.setTransporte(trans);

                dr.setFecha_salida(rs.getDate("FECHA_SALIDA"));

                lista.add(dr);
            }
        } catch (Exception e) {
            System.out.println("Error al listar detalles de reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(DetalleReserva dr) {
        // El id_detalle lo asigna el trigger TG_ID_DETALLE (MAX(id_detalle)+1)
        PreparedStatement st = null;
        ResultSet rs = null;
        int idDetalle = 0;
        int resultado = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO DETALLE (ID_RESERVA, ID_GUIA, ID_TRANSPORTE, FECHA_SALIDA) "
                    + "VALUES (?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, dr.getReserva().getId_reserva());
            st.setInt(2, dr.getGuia().getIdGuia());
            st.setInt(3, dr.getTransporte().getIdTransporte());
            st.setDate(4, new java.sql.Date(dr.getFecha_salida().getTime()));

            resultado = st.executeUpdate();

            if (resultado > 0) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_detalle) FROM DETALLE");
                rs = st.executeQuery();
                if (rs.next()) {
                    idDetalle = rs.getInt(1);
                }
                dr.setId_detalleReserva(idDetalle);
                System.out.println("Detalle de reserva registrado correctamente con ID: " + idDetalle);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar detalle de reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return resultado;
    }

    @Override
    public boolean update(DetalleReserva dr) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE DETALLE SET ID_RESERVA=?, ID_GUIA=?, ID_TRANSPORTE=?, FECHA_SALIDA=? "
                    + "WHERE ID_DETALLE=?";
            st = cn.prepareStatement(query);
            st.setInt(1, dr.getReserva().getId_reserva());
            st.setInt(2, dr.getGuia().getIdGuia());
            st.setInt(3, dr.getTransporte().getIdTransporte());
            st.setDate(4, new java.sql.Date(dr.getFecha_salida().getTime()));
            st.setInt(5, dr.getId_detalleReserva());

            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar detalle de reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public DetalleReserva SearchById(int id) {
        DetalleReserva dr = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT d.*, g.NOMBRE AS NOMBRE_GUIA, t.PLACA "
                    + "FROM DETALLE d "
                    + "JOIN GUIA g ON d.ID_GUIA = g.ID_GUIA "
                    + "JOIN TRANSPORTE t ON d.ID_TRANSPORTE = t.ID_TRANSPORTE "
                    + "WHERE d.ID_DETALLE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                dr = new DetalleReserva();
                dr.setId_detalleReserva(rs.getInt("ID_DETALLE"));

                Reserva res = new Reserva();
                res.setId_reserva(rs.getInt("ID_RESERVA"));
                dr.setReserva(res);

                Guia gui = new Guia();
                gui.setIdGuia(rs.getInt("ID_GUIA"));
                gui.setNombre(rs.getString("NOMBRE_GUIA"));
                dr.setGuia(gui);

                Transporte trans = new Transporte();
                trans.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
                trans.setPlaca(rs.getString("PLACA"));
                dr.setTransporte(trans);

                dr.setFecha_salida(rs.getDate("FECHA_SALIDA"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar detalle de reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return dr;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM DETALLE WHERE ID_DETALLE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar detalle de reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public List<DetalleReserva> listaPorReserva(int idReserva) {
        List<DetalleReserva> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT d.*, g.NOMBRE AS NOMBRE_GUIA, t.PLACA "
                    + "FROM DETALLE d "
                    + "JOIN GUIA g ON d.ID_GUIA = g.ID_GUIA "
                    + "JOIN TRANSPORTE t ON d.ID_TRANSPORTE = t.ID_TRANSPORTE "
                    + "WHERE d.ID_RESERVA = ? ORDER BY d.ID_DETALLE";
            st = cn.prepareStatement(query);
            st.setInt(1, idReserva);
            rs = st.executeQuery();
            while (rs.next()) {
                DetalleReserva dr = new DetalleReserva();
                dr.setId_detalleReserva(rs.getInt("ID_DETALLE"));

                Guia gui = new Guia();
                gui.setIdGuia(rs.getInt("ID_GUIA"));
                gui.setNombre(rs.getString("NOMBRE_GUIA"));
                dr.setGuia(gui);

                Transporte trans = new Transporte();
                trans.setIdTransporte(rs.getInt("ID_TRANSPORTE"));
                trans.setPlaca(rs.getString("PLACA"));
                dr.setTransporte(trans);

                dr.setFecha_salida(rs.getDate("FECHA_SALIDA"));

                lista.add(dr);
            }
        } catch (Exception e) {
            System.out.println("Error al listar detalles por reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
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