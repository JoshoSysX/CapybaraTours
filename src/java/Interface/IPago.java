package Interface;

import Model.Pago;
import java.util.List;

public interface IPago {
    
    public List<Pago> lista();
    public int insert(Pago p);
    public boolean update(Pago p);
    public Pago SearchById(int id);
    public boolean delete(int id);
    public List<Pago> listaPorReserva(int idReserva);
    
}