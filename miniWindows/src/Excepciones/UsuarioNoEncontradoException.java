/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Excepciones;

/**
 *
 * @author najma
 */
public class UsuarioNoEncontradoException extends Exception {
    
    public UsuarioNoEncontradoException() {
        super("Usuario no encontrado en el sistema");
    }
    
    public UsuarioNoEncontradoException(String username) {
        super("El usuario ["+username+"] no existe en el sistema");
    }
}