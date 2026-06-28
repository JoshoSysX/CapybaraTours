
package Model;

import java.util.Date;

public class Pago {
    private int id_pago;
    private Reserva reserva;
    private double monto;
    private Date fecha_pago;
    private String metodo_pago;
    private EstadoPago estadopago;

    public Pago() {
    }

    public Pago(int id_pago, Reserva reserva, double monto, Date fecha_pago, String metodo_pago, EstadoPago estadopago) {
        this.id_pago = id_pago;
        this.reserva = reserva;
        this.monto = monto;
        this.fecha_pago = fecha_pago;
        this.metodo_pago = metodo_pago;
        this.estadopago = estadopago;
    }

    public int getId_pago() {
        return id_pago;
    }

    public void setId_pago(int id_pago) {
        this.id_pago = id_pago;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Date getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(Date fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public EstadoPago getEstadopago() {
        return estadopago;
    }

    public void setEstadopago(EstadoPago estadopago) {
        this.estadopago = estadopago;
    }
    
}
