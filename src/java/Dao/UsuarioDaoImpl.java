
package Dao;

import Interface.IUsuario;
import Model.Persona;
import Model.Rol;
import Model.Usuario;
import Util.ConexionOracleSingleton;
import java.sql.*;

public class UsuarioDaoImpl implements IUsuario{
    private Connection cn;

    @Override
    public Usuario validate(String usuario, String password) {
        Usuario u = null;
        Persona p = null;
        
        PreparedStatement st;
        ResultSet rs;
        String query = null;
        
        try {
            u = new Usuario();
            p = new Persona();
            String hashedPassword = u.HashPassword(password);
            query = 
                    "SELECT u.id_usuario,"
                    + "       u.usuario, "
                    + "       p.id_persona, "
                    + "       p.nombres "
                    + "FROM persona p, usuario u "
                    + "WHERE u.id_persona = p.id_persona "
                    + "  AND u.usuario = ? "
                    + "  AND u.contraseña = ? ";
            cn = ConexionOracleSingleton.getConnection();
            st = cn.prepareStatement(query);
            st.setString(1, usuario);
            st.setString(2, hashedPassword);
            
            rs = st.executeQuery();
            while (rs.next()) {
                p = new Persona();
                u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setUsuario(rs.getString("usuario"));
                p.setId_persona(rs.getInt("id_persona"));
                p.setNombre(rs.getString("nombres"));
                u.setPersona(p);
            }
        } catch (Exception e) {
            System.out.println("Error al validar usuario:"+e.getMessage());
            try {
                cn.rollback();
            } catch (Exception ex) {
            }
            System.out.println("No se pudo validar el usuario");
        } finally {
            if (cn != null) {
        try { cn.close(); } catch (Exception ex) { }
    }
        }
      return u;
    }
    
}
