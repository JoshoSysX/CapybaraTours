package Dao;

import Interface.IReserva;
import Model.EstadoReserva;
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
            String query = "SELECT r.*, p.NOMBRES, p.APELLIDOS, pa.NOMBRE AS NOMBRE_PAQUETE "
                    + "FROM RESERVA r "
                    + "JOIN PERSONA p ON r.ID_PERSONA = p.ID_PERSONA "
                    + "JOIN PAQUETE pa ON r.ID_PAQUETE = pa.ID_PAQUETE "
                    + "ORDER BY r.id_reserva";
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
                paq.setNombre(rs.getString("NOMBRE_PAQUETE"));
                r.setPaquete(paq);

                r.setFecha(rs.getDate("FECHA"));
                r.setCantidad(rs.getInt("CANTIDAD_PERSONAS"));
                r.setFecha_programada(rs.getDate("FECHA_PRGRAMADA"));
                r.setEstadoReserva(codeToEstado(rs.getString("ESTADO")));

                lista.add(r);
            }
        } catch (Exception e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public int insert(Reserva r) {
        // El id_reserva lo asigna el trigger TG_ID_RESERVA (MAX(id_reserva)+1)
        PreparedStatement st = null;
        ResultSet rs = null;
        int idReserva = 0;
        int rResultado = 0;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO RESERVA (ID_PERSONA, ID_PAQUETE, FECHA, "
                    + "CANTIDAD_PERSONAS, ESTADO, FECHA_PRGRAMADA) "
                    + "VALUES (?,?,?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setInt(1, r.getPersona().getId_persona());
            st.setInt(2, r.getPaquete().getId_paquete());
            st.setDate(3, new java.sql.Date(r.getFecha().getTime()));
            st.setInt(4, r.getCantidad());
            st.setString(5, estadoToCode(r.getEstadoReserva()));
            st.setDate(6, new java.sql.Date(r.getFecha_programada().getTime()));

            rResultado = st.executeUpdate();

            if (rResultado > 0) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_reserva) FROM RESERVA");
                rs = st.executeQuery();
                if (rs.next()) {
                    idReserva = rs.getInt(1);
                }
                r.setId_reserva(idReserva);
                System.out.println("Reserva registrada correctamente con ID: " + idReserva);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar reserva: " + e.getMessage());
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
            st.setString(5, estadoToCode(r.getEstadoReserva()));
            st.setDate(6, new java.sql.Date(r.getFecha_programada().getTime()));
            st.setInt(7, r.getId_reserva());

            int filas = st.executeUpdate();
            resultado = filas > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }


    public boolean updateEstado(int idReserva, EstadoReserva estado) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "UPDATE RESERVA SET ESTADO=? WHERE ID_RESERVA=?";
            st = cn.prepareStatement(query);
            st.setString(1, estadoToCode(estado));
            st.setInt(2, idReserva);
            resultado = st.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar estado de reserva: " + e.getMessage());
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
            String query = "SELECT r.*, p.NOMBRES, p.APELLIDOS, pa.NOMBRE AS NOMBRE_PAQUETE "
                    + "FROM RESERVA r "
                    + "JOIN PERSONA p ON r.ID_PERSONA = p.ID_PERSONA "
                    + "JOIN PAQUETE pa ON r.ID_PAQUETE = pa.ID_PAQUETE "
                    + "WHERE r.ID_RESERVA = ?";
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
                paq.setNombre(rs.getString("NOMBRE_PAQUETE"));
                r.setPaquete(paq);

                r.setFecha(rs.getDate("FECHA"));
                r.setCantidad(rs.getInt("CANTIDAD_PERSONAS"));
                r.setFecha_programada(rs.getDate("FECHA_PRGRAMADA"));
                r.setEstadoReserva(codeToEstado(rs.getString("ESTADO")));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return r;
    }

    @Override
    public boolean delete(int id) {
        PreparedStatement st = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "DELETE FROM RESERVA WHERE ID_RESERVA = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar reserva: " + e.getMessage());
        } finally {
            cerrarRecursos(null, st);
        }
        return resultado;
    }

    @Override
    public List<Reserva> listaPorPersona(int idPersona) {
        List<Reserva> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT r.*, pa.NOMBRE AS NOMBRE_PAQUETE "
                    + "FROM RESERVA r "
                    + "JOIN PAQUETE pa ON r.ID_PAQUETE = pa.ID_PAQUETE "
                    + "WHERE r.ID_PERSONA = ? ORDER BY r.id_reserva DESC";
            st = cn.prepareStatement(query);
            st.setInt(1, idPersona);
            rs = st.executeQuery();
            while (rs.next()) {
                Reserva r = new Reserva();
                r.setId_reserva(rs.getInt("ID_RESERVA"));

                Paquete paq = new Paquete();
                paq.setId_paquete(rs.getInt("ID_PAQUETE"));
                paq.setNombre(rs.getString("NOMBRE_PAQUETE"));
                r.setPaquete(paq);

                r.setFecha(rs.getDate("FECHA"));
                r.setCantidad(rs.getInt("CANTIDAD_PERSONAS"));
                r.setFecha_programada(rs.getDate("FECHA_PRGRAMADA"));
                r.setEstadoReserva(codeToEstado(rs.getString("ESTADO")));

                lista.add(r);
            }
        } catch (Exception e) {
            System.out.println("Error al listar reservas por persona: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }


    // La columna ESTADO de la BD es char(3); mapeamos el enum Java a un codigo corto
    private String estadoToCode(EstadoReserva e) {
        if (e == null) return "PEN";
        switch (e) {
            case PENDIENTE:  return "PEN";
            case CONFIRMADO: return "CNF";
            case PAGADA:     return "PAG";
            case EN_CURSO:   return "CUR";
            case FINALIZADA: return "FIN";
            case CANCELADA:  return "CAN";
            default:         return "PEN";
        }
    }

    private EstadoReserva codeToEstado(String c) {
        if (c == null) return EstadoReserva.PENDIENTE;
        switch (c.trim()) {
            case "PEN": return EstadoReserva.PENDIENTE;
            case "CNF": return EstadoReserva.CONFIRMADO;
            case "PAG": return EstadoReserva.PAGADA;
            case "ABO": return EstadoReserva.CONFIRMADO; // abonado parcial (trigger de pagos)
            case "CUR": return EstadoReserva.EN_CURSO;
            case "FIN": return EstadoReserva.FINALIZADA;
            case "CAN": return EstadoReserva.CANCELADA;
            default:    return EstadoReserva.PENDIENTE;
        }
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