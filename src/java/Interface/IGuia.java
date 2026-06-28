package Interface;

import Model.Guia;
import java.util.List;

public interface IGuia {
    
    public List<Guia> lista();
    public boolean insert(Guia g);
    public boolean update(Guia g);
    public Guia SearchById(int id);
    public boolean delete(int id);
    
}