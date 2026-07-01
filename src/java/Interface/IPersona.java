package Interface;

import Model.Persona;
import Model.Usuario;
import java.util.List;

public interface IPersona {
    
    public List<Persona> lista();
    public int insert(Persona p, Usuario u);   // Registrar persona + usuario
    public int insertSoloPersona(Persona p); // Registrar solo persona, sin usuario
    public boolean update(Persona p);
    public Persona SearchById(int id);
    public boolean delete(int id);
    public boolean existeCorreo(String correo);
    public boolean existeDocumento(String numeroDoc);
    
}