package Dao;

import Interface.ILog;
import Model.Log;
import Util.ConexionOracleSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDaoImpl implements ILog {
    private Connection cn;

    @Override
    public List<Log> lista() {
        List<Log> lista = new ArrayList<>();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cn = ConexionOracleSingleton.getConnection();
            String query = "SELECT ID_LOG, USUARIO, ACCION, TABLA_AFECTADA, DETALLE, FECHA "
                    + "FROM LOGS ORDER BY FECHA DESC, ID_LOG DESC";
            st = cn.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Log l = new Log();
                l.setId_log(rs.getInt("ID_LOG"));
                l.setUsuario(rs.getString("USUARIO"));
                l.setAccion(rs.getString("ACCION"));
                l.setTabla_afectada(rs.getString("TABLA_AFECTADA"));
                l.setDetalle(rs.getString("DETALLE"));
                l.setFecha(rs.getTimestamp("FECHA"));
                lista.add(l);
            }
        } catch (Exception e) {
            System.out.println("Error al listar logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
        return lista;
    }
}
