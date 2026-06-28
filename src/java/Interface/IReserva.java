package Interface;

import Model.Reserva;
import java.util.List;

public interface IReserva {
    
    public List<Reserva> lista();
    public int insert(Reserva r);           // Registrar reserva
    public boolean update(Reserva r);
    public Reserva SearchById(int id);
    public boolean delete(int id);
    public List<Reserva> listaPorPersona(int idPersona);
    
}