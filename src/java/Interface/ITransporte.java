package Interface;

import Model.Transporte;
import java.util.List;

public interface ITransporte {
    
    public List<Transporte> lista();
    public boolean insert(Transporte t);
    public boolean update(Transporte t);
    public Transporte SearchById(int id);
    public boolean delete(int id);
    
}