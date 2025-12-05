package Modelo;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author najma
 */
public class Usuario implements Serializable {
   
    // atributo constante
    private static final long serialVersionUID = 1L;
    
    // atributoss
    private String nombreCompleto;
    private String username;
    private String password;
    private boolean esAdmin;
    private Calendar fechaCreacion;
    private boolean activo;
    
    // constructor
    public Usuario(String nombreCompleto, String username, String password, boolean esAdmin) {
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.password = password;
        this.esAdmin = esAdmin;
        this.fechaCreacion = Calendar.getInstance();
        this.activo = true;
    }
    
    // constructor para usuario admin
    public static Usuario crearAdmin() {
        return new Usuario("Administrador", "admin", "admin123", true);
    }
    
    // método para verificar contraseña
    public boolean verificarPassword(String password) {
        return this.password.equals(password);
    }
    
    // getters
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean esAdmin() {
        return esAdmin;
    }
    
    public Calendar getFechaCreacion() {
        return fechaCreacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    // setters
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public String toString() {
        return username + (esAdmin ? " [ADMIN]" : "");
    }
} 