package Sistema;

import Modelo.Usuario;
import Excepciones.*;

/**
 *
 * @author najma
 */
public class MiniWindowsClass {
    
    private SistemaArchivos sistemaArchivos;
    private GestorUsuarios gestorUsuarios;
    private static MiniWindowsClass instancia;
    
    private MiniWindowsClass() {
        this.gestorUsuarios = new GestorUsuarios();
        this.sistemaArchivos = new SistemaArchivos();
        
        if (!sistemaArchivos.cargar()) {
            try {
                sistemaArchivos.crearCarpetaUsuario("admin");
            } catch (ArchivoNoValidoException e) {
                System.err.println("Error al crear carpeta admin: "+e.getMessage());
            }
        }
    }
    
    public static MiniWindowsClass getInstance() {
        if (instancia == null) {
            instancia = new MiniWindowsClass();
        }
        return instancia;
    }
    
    public Usuario login(String username, String password) 
            throws UsuarioNoEncontradoException, ArchivoNoValidoException, PermisosDenegadosException {
        
        Usuario usuario = gestorUsuarios.login(username, password);
        sistemaArchivos.establecerUsuario(usuario);
        return usuario;
    }
    
    public void logout() {
        gestorUsuarios.logout();
        guardarSistema();
    }
    
    public Usuario crearUsuario(String nombreCompleto, String username, String password) 
            throws ArchivoNoValidoException {
        
        if (gestorUsuarios.haySesionActiva() && !gestorUsuarios.getUsuarioActual().esAdmin()) {
            throw new ArchivoNoValidoException("Solo el administrador puede crear usuarios");
        }
        
        Usuario nuevoUsuario = gestorUsuarios.crearUsuario(nombreCompleto, username, password);
        
        sistemaArchivos.crearCarpetaUsuario(username);
        
        guardarSistema();
        
        return nuevoUsuario;
    }
    
    public SistemaArchivos getSistemaArchivos() {
        return sistemaArchivos;
    }
    
    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }
    
    public boolean haySesionActiva() {
        return gestorUsuarios.haySesionActiva();
    }
    
    public Usuario getUsuarioActual() {
        return gestorUsuarios.getUsuarioActual();
    }
    
    public void guardarSistema() {
        sistemaArchivos.guardar();
        gestorUsuarios.guardarUsuarios();
    }
    
    public void reiniciarSistema() {
        java.io.File archivoSistema = new java.io.File("sistema_archivos.sop");
        java.io.File archivoUsuarios = new java.io.File("usuarios.sop");
        
        if (archivoSistema.exists()) archivoSistema.delete();
        if (archivoUsuarios.exists()) archivoUsuarios.delete();
        
        instancia = null;
        getInstance();
    }
}