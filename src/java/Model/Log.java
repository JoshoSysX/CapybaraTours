package Model;

import java.util.Date;

public class Log {
    private int id_log;
    private String usuario;
    private String accion;
    private String tabla_afectada;
    private String detalle;
    private Date fecha;

    public Log() {}

    public int getId_log() { return id_log; }
    public void setId_log(int id_log) { this.id_log = id_log; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getTabla_afectada() { return tabla_afectada; }
    public void setTabla_afectada(String tabla_afectada) { this.tabla_afectada = tabla_afectada; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
