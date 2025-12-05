/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Excepciones;

/**
 *
 * @author najma
 */
public class PermisosDenegadosException extends Exception {
    
    public PermisosDenegadosException() {
        super("No tiene permisos para realizar esta acción");
    }
    
    public PermisosDenegadosException(String accion) {
        super("No tiene permisos para: "+accion);
    }
    
    public PermisosDenegadosException(String usuario, String recurso) {
        super("El usuario ["+usuario+"] no tiene permisos para acceder a ["+recurso+"]");
    }
}
