
package Model;

import java.util.Date;

public class Reserva {
    private int id_reserva;
    private Persona persona;
    private Paquete paquete;
    private Date fecha;
    private int cantidad;
    private EstadoReserva estadoReserva;
    private Date fecha_programada;

    public Reserva() {
    }

    public Reserva(int id_reserva, Persona persona, Paquete paquete, Date fecha, int cantidad, EstadoReserva estadoReserva, Date fecha_programada) {
        this.id_reserva = id_reserva;
        this.persona = persona;
        this.paquete = paquete;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.estadoReserva = estadoReserva;
        this.fecha_programada = fecha_programada;
    }

    public int getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(int id_reserva) {
        this.id_reserva = id_reserva;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Paquete getPaquete() {
        return paquete;
    }

    public void setPaquete(Paquete paquete) {
        this.paquete = paquete;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public Date getFecha_programada() {
        return fecha_programada;
    }

    public void setFecha_programada(Date fecha_programada) {
        this.fecha_programada = fecha_programada;
    }

}
