package Dao;

import Interface.IReserva;
import Model.Reserva;
import Model.Persona;
import Model.Paquete;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDaoImpl implements IReserva {

    private Connection cn;

    @Override
    public List<Reserva> lista() {
        List<Reserva> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT r.*, p.NOMBRES, p.APELLIDOS, pa.NOMBRE_PAQUETE " +
                          "FROM RESERVA r " +
                          "JOIN PERSONA p ON r.ID_PERSONA = p.ID_PERSONA " +
                          "JOIN PAQUETE pa ON r.ID_PAQUETE = pa.ID_PAQUETE " +
                          "ORDER BY r.id_reserva";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId_reserva(rs.getInt("ID_RESERVA"));
                
                Persona per = new Persona();
                per.setId_persona(rs.getInt("ID_PERSONA"));
                per.setNombre(rs.getString("NOMBRES"));
                per.setApellido(rs.getString("APELLIDOS"));
                r.setPersona(per);
                
                Paquete paq = new Paquete();
                paq.setId_paquete(rs.getInt("ID_PAQUETE"));
                paq.setNombre(rs.getString("NOMBRE"));
                r.setPaquete(paq);
                
                r.setFecha(rs.getDate("FECHA"));
                r.setCantidad(rs.getInt("CANTIDAD_PERSONAS"));
                // r.setEstadoReserva(...) → según tu enum
                r.setFecha_programada(rs.getDate("FECHA_PRGRAMADA"));
                
                lista.add(r);
            }
        } catch (Exception e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(Reserva r) {
        PreparedStatement st = null;
        ResultSet rs = null;
        int idReserva = 0;
        int rResultado = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();
            
            // Obtener siguiente ID de Reserva
            st = cn.prepareStatement("SELECT SEQ_RESERVA.NEXTVAL FROM DUAL");
            rs = st.executeQuery();
            if (rs.next()) {
                idReserva = rs.getInt(1);
            }
            rs.close();
            st.close();

            String query = "INSERT INTO RESERVA (ID_RESERVA, ID_PERSONA, ID_PAQUETE, FECHA, "
                         + "CANTIDAD_PERSONAS, ESTADO, FECHA_PRGRAMADA) "
                         + "VALUES (?,?,?,?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, idReserva);
            st.setInt(2, r.getPersona().getId_persona());
            st.setInt(3, r.getPaquete().getId_paquete());
            st.setDate(4, new java.sql.Date(r.getFecha().getTime()));
            st.setInt(5, r.getCantidad());
            st.setString(6, r.getEstadoReserva().name());  // Asumiendo que es enum
            st.setDate(7, new java.sql.Date(r.getFecha_programada().getTime()));
            
            rResultado = st.executeUpdate();
            
            if (rResultado > 0) {
                System.out.println("Reserva registrada correctamente con ID: " + idReserva);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar reserva: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return rResultado;
    }

    @Override
    public boolean update(Reserva r) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE RESERVA SET ID_PERSONA=?, ID_PAQUETE=?, FECHA=?, "
                         + "CANTIDAD_PERSONAS=?, ESTADO=?, FECHA_PRGRAMADA=? "
                         + "WHERE ID_RESERVA=?";
            st = cn.prepareStatement(query);
            st.setInt(1, r.getPersona().getId_persona());
            st.setInt(2, r.getPaquete().getId_paquete());
            st.setDate(3, new java.sql.Date(r.getFecha().getTime()));
            st.setInt(4, r.getCantidad());
            st.setString(5, r.getEstadoReserva().name());
            st.setDate(6, new java.sql.Date(r.getFecha_programada().getTime()));
            st.setInt(7, r.getId_reserva());
            
            int filas = st.executeUpdate();
            resultado = filas > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar reserva: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public Reserva SearchById(int id) {
        Reserva r = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT r.*, p.NOMBRES, p.APELLIDOS, pa.NOMBRE_PAQUETE " +
                          "FROM RESERVA r " +
                          "JOIN PERSONA p ON r.ID_PERSONA = p.ID_PERSONA " +
                          "JOIN PAQUETE pa ON r.ID_PAQUETE = pa.ID_PAQUETE " +
                          "WHERE r.ID_RESERVA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                r = new Reserva();
                r.setId_reserva(rs.getInt("ID_RESERVA"));
                
                Persona per = new Persona();
                per.setId_persona(rs.getInt("ID_PERSONA"));
                per.setNombre(rs.getString("NOMBRES"));
                per.setApellido(rs.getString("APELLIDOS"));
                r.setPersona(per);
                
                Paquete paq = new Paquete();
                paq.setId_paquete(rs.getInt("ID_PAQUETE"));
                paq.setNombre(rs.getString("NOMBRE"));
                r.setPaquete(paq);
                
                r.setFecha(rs.getDate("FECHA"));
                r.setCantidad(rs.getInt("CANTIDAD_PERSONAS"));
                // r.setEstadoReserva...
                r.setFecha_programada(rs.getDate("FECHA_PRGRAMADA"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar reserva: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }
        return r;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM RESERVA WHERE ID_RESERVA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            return r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar reserva: " + e.getMessage());
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
    public List<Reserva> listaPorPersona(int idPersona) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}