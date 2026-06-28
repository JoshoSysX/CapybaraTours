
package Model;

public class Transporte {
    private int idTransporte;
    private String vehiculo;
    private int capacidad;
    private String placa;

    public Transporte() {
    }

    public Transporte(int idTransporte, String vehiculo, int capacidad, String placa) {
        this.idTransporte = idTransporte;
        this.vehiculo = vehiculo;
        this.capacidad = capacidad;
        this.placa = placa;
    }

    public int getIdTransporte() {
        return idTransporte;
    }

    public void setIdTransporte(int idTransporte) {
        this.idTransporte = idTransporte;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
}
