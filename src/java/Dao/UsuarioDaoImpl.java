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
        Persona p;
        PreparedStatement st;
        ResultSet rs;
        String query;

        try {
            String hashedPassword = new Usuario().HashPassword(password);

            query = " SELECT u.id_usuario, u.usuario, u.rol, p.id_persona, p.nombres "
                    + " FROM persona p, usuario u "
                    + " WHERE u.id_persona = p.id_persona "
                    + " AND u.usuario = ? "
                    + " AND u.contraseña = ? ";

            cn = ConexionOracleSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setString(1, usuario);
            st.setString(2, hashedPassword);
            rs = st.executeQuery();
            while (rs.next()) {
                u = new Usuario();
                p = new Persona();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setUsuario(rs.getString("usuario"));
                u.setRol(Rol.valueOf(rs.getString("rol").toUpperCase()));
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombres"));
                u.setPersona(p);
            }

        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.getMessage());
        } finally {
            if (cn != null) {
                try {
                } catch (Exception ex) {
                }
            }
        }
        return u;
    }

}