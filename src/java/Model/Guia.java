
package Model;

public class Guia {
    private int idGuia;
    private String nombre;
    private String telefono;

    public Guia() {
    }

    public Guia(int idGuia, String nombre, String telefono) {
        this.idGuia = idGuia;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(int idGuia) {
        this.idGuia = idGuia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
}
