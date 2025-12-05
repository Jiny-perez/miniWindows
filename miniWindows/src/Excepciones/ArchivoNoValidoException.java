package Excepciones;

/**
 *
 * @author najma
 */
public class ArchivoNoValidoException extends Exception {
    
    public ArchivoNoValidoException() {
        super("El archivo o carpeta no es válido");
    }
    
    public ArchivoNoValidoException(String mensaje) {
        super(mensaje);
    }
    
    public ArchivoNoValidoException(String nombre, String razon) {
        super("El archivo/carpeta ["+nombre+"] no es válido: "+razon);
    }
}