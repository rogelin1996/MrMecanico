package mx.avidos.mrmecanicoch.Entidad;

public class Paquete {

    private String uidAutomovil;
    private String uidPaquete;
    private String paquete;
    private String descripcion_corta;
    private String descripcion_larga;
    private Double precio;
    private String url;
    private String kilometraje;
    private int tipo;

    public Paquete() { }

    public String getUidPaquete() {
        return uidPaquete;
    }

    public void setUidPaquete(String uidPaquete) {
        this.uidPaquete = uidPaquete;
    }

    public String getPaquete() {
        return paquete;
    }

    public void setPaquete(String paquete) {
        this.paquete = paquete;
    }

    public String getDescripcion_corta() {
        return descripcion_corta;
    }

    public void setDescripcion_corta(String descripcion_corta) {
        this.descripcion_corta = descripcion_corta;
    }

    public String getDescripcion_larga() {
        return descripcion_larga;
    }

    public void setDescripcion_larga(String descripcion_larga) {
        this.descripcion_larga = descripcion_larga;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getUidAutomovil() {
        return uidAutomovil;
    }

    public void setUidAutomovil(String uidAutomovil) {
        this.uidAutomovil = uidAutomovil;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
