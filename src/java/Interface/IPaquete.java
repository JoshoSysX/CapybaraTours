package Interface;

import Model.Paquete;
import java.util.List;

public interface IPaquete {
    
    public List<Paquete> lista();
    public boolean insert(Paquete p);
    public boolean update(Paquete p);
    public Paquete SearchById(int id);
    public boolean delete(int id);
    
}