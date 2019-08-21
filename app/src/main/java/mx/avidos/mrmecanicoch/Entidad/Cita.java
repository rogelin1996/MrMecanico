package mx.avidos.mrmecanicoch.Entidad;

public class Cita {

    private String uidCita;
    private String automovil;
    private long fecha;
    private int status;
    private String talleruid;
    private String uidPaquete;
    private String uidUsuario;

    public Cita() { }

    public Cita(String automovil, long fecha, int status, String talleruid, String uidPaquete, String uidUsuario) {
        this.automovil = automovil;
        this.fecha = fecha;
        this.status = status;
        this.talleruid = talleruid;
        this.uidPaquete = uidPaquete;
        this.uidUsuario = uidUsuario;
    }

    public String getUidCita() {
        return uidCita;
    }

    public void setUidCita(String uidCita) {
        this.uidCita = uidCita;
    }

    public String getAutomovil() {
        return automovil;
    }

    public void setAutomovil(String automovil) {
        this.automovil = automovil;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTalleruid() {
        return talleruid;
    }

    public void setTalleruid(String talleruid) {
        this.talleruid = talleruid;
    }

    public String getUidPaquete() {
        return uidPaquete;
    }

    public void setUidPaquete(String uidPaquete) {
        this.uidPaquete = uidPaquete;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }
}
