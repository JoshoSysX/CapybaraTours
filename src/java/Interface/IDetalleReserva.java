package Interface;

import Model.DetalleReserva;
import java.util.List;

public interface IDetalleReserva {
    
    public List<DetalleReserva> lista();
    public int insert(DetalleReserva dr);
    public boolean update(DetalleReserva dr);
    public DetalleReserva SearchById(int id);
    public boolean delete(int id);
    public List<DetalleReserva> listaPorReserva(int idReserva);
    
}