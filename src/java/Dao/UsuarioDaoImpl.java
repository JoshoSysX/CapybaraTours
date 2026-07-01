package Dao;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionOracleSingleton;
import java.sql.*;

public class UsuarioDaoImpl implements IUsuario {

    private Connection cn;

    @Override
    public Usuario validate(String usuario, String password) {
        Usuario u = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            String hashedPassword = new Usuario().HashPassword(password);

            String query = "SELECT u.ID_USUARIO, u.USUARIO, u.ROL, "
                    + "p.ID_PERSONA, p.NOMBRES, p.APELLIDOS, p.DOCUMENTO, "
                    + "p.NUMERO_DOC, p.TELEFONO, p.CORREO "
                    + "FROM PERSONA p INNER JOIN USUARIO u ON u.ID_PERSONA = p.ID_PERSONA "
                    + "WHERE u.USUARIO = ? AND u.\"CONTRASEÑA\" = ?";

            cn = ConexionOracleSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setString(1, usuario);
            st.setString(2, hashedPassword);
            rs = st.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                Persona p = new Persona();

                u.setId_usuario(rs.getInt("ID_USUARIO"));
                u.setUsuario(rs.getString("USUARIO"));
                u.setRol(Rol.valueOf(rs.getString("ROL").toUpperCase()));

                p.setId_persona(rs.getInt("ID_PERSONA"));
                p.setNombre(rs.getString("NOMBRES"));
                p.setApellido(rs.getString("APELLIDOS"));
                p.setDocumento(rs.getString("DOCUMENTO"));
                p.setNumeroDoc(rs.getString("NUMERO_DOC"));
                p.setTelefono(rs.getString("TELEFONO"));
                p.setEmail(rs.getString("CORREO"));

                u.setPersona(p);
            }
        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (st != null) st.close(); } catch (Exception e) {}
        }
        return u;
    }
}
