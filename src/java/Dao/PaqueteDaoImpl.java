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
                p.setImagen(rs.getString("IMAGEN"));
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al listar paquetes: " + e.getMessage());
        } finally {
            cerrarRecursos(rs, st);
        }
        return lista;
    }

    @Override
    public boolean insert(Paquete p) {
        // El ID lo asigna el trigger TG_ID_PAQUETE (MAX(id_paquete)+1), no se envia desde Java
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean resultado = false;
        try {
            cn = ConexionOracleSingleton.getConnection();

            String query = "INSERT INTO PAQUETE (NOMBRE, DESCRIPCION, DURACION, PRECIO, IMAGEN) "
                         + "VALUES (?,?,?,?,?)";
            st = cn.prepareStatement(query);
            st.setString(1, p.getNombre());
            st.setString(2, p.getDescripcion());
            st.setString(3, p.getDuracion());
            st.setDouble(4, p.getPrecio());
            st.setString(5, p.getImagen());

            int r = st.executeUpdate();
            resultado = r > 0;

            if (resultado) {
                // Por si se necesita el id generado en el frontend
                st.close();
                st = cn.prepareStatement("SELECT MAX(id_paquete) FROM PAQUETE");
                rs = st.executeQuery();
                if (rs.next()) {
                    p.setId_paquete(rs.getInt(1));
                }
                System.out.println("Paquete registrado correctamente con ID: " + p.getId_paquete());
            }
        } catch (Exception e) {
            System.out.println("Error al insertar paquete: " + e.getMessage());
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
            String query = "UPDATE PAQUETE SET NOMBRE=?, DESCRIPCION=?, DURACION=?, PRECIO=?, IMAGEN=? WHERE ID_PAQUETE=?";
            st = cn.prepareStatement(query);
            st.setString(1, p.getNombre());
            st.setString(2, p.getDescripcion());
            st.setString(3, p.getDuracion());
            st.setDouble(4, p.getPrecio());
            st.setString(5, p.getImagen());
            st.setInt(6, p.getId_paquete());

            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar paquete: " + e.getMessage());
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
                p.setImagen(rs.getString("IMAGEN"));
            }
        } catch (Exception e) {
            System.out.println("Error al buscar paquete: " + e.getMessage());
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
            String query = "DELETE FROM PAQUETE WHERE ID_PAQUETE = ?";
            st = cn.prepareStatement(query);
            st.setInt(1, id);
            int r = st.executeUpdate();
            resultado = r > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar paquete: " + e.getMessage());
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