
package Model;

import java.util.Date;

public class DetalleReserva {
    private int id_detalleReserva;
    private Reserva reserva;
    private Guia guia;
    private Transporte transporte;
    private Date fecha_salida;

    public DetalleReserva() {
    }

    public DetalleReserva(int id_detalleReserva, Reserva reserva, Guia guia, Transporte transporte, Date fecha_salida) {
        this.id_detalleReserva = id_detalleReserva;
        this.reserva = reserva;
        this.guia = guia;
        this.transporte = transporte;
        this.fecha_salida = fecha_salida;
    }

    public int getId_detalleReserva() {
        return id_detalleReserva;
    }

    public void setId_detalleReserva(int id_detalleReserva) {
        this.id_detalleReserva = id_detalleReserva;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public Date getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(Date fecha_salida) {
        this.fecha_salida = fecha_salida;
    }
    
}
