package Modelo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author najma
 */
public class Archivo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private boolean esCarpeta;
    private long tamanio;
    private java.util.Calendar fechaModificacion;
    private String rutaRelativa;
    private String rutaAbsoluta;

    public Archivo(String nombre, boolean esCarpeta, long tamanio,
          Calendar fechaModificacion, String rutaRelativa, String rutaAbsoluta) {
        this.nombre = nombre;
        this.esCarpeta = esCarpeta;
        this.tamanio = tamanio;
        this.fechaModificacion = fechaModificacion;
        this.rutaRelativa = rutaRelativa;
        this.rutaAbsoluta = rutaAbsoluta;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isEsCarpeta() {
        return esCarpeta;
    }

    public long getTamanio() {
        return tamanio;
    }

    public java.util.Calendar getFechaModificacion() {
        return fechaModificacion;
    }

    public String getRutaRelativa() {
        return rutaRelativa;
    }

    public String getRutaAbsoluta() {
        return rutaAbsoluta;
    }

    @Override
    public String toString() {
        return esCarpeta ? "[DIR] " + nombre : nombre + " (" + tamanio + " bytes)";
    }
}
