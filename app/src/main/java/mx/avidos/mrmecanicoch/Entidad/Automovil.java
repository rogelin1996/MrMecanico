package mx.avidos.mrmecanicoch.Entidad;

public class Automovil {

    private String uidAutomovil;
    private String placa;
    private String marca;
    private String modelo;
    private String anio;
    private String kilometraje;

    public Automovil() { }

    public Automovil(String placa, String marca, String modelo, String anio, String kilometraje) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometraje = kilometraje;
    }

    public String getUidAutomovil() {
        return uidAutomovil;
    }

    public void setUidAutomovil(String uidAutomovil) {
        this.uidAutomovil = uidAutomovil;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }
}
