package Sistema;

import Modelo.Usuario;
import Excepciones.UsuarioNoEncontradoException;
import Excepciones.ArchivoNoValidoException;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author najma
 */
public class GestorUsuarios implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private ArrayList<Usuario> usuarios;
    private Usuario usuarioActual;
    private static final String ARCHIVO_USUARIOS = "usuarios.sop";
    
    public GestorUsuarios() {
        this.usuarios = new ArrayList<>();
        cargarUsuarios();
        
        if (usuarios.isEmpty()) {
            usuarios.add(Usuario.crearAdmin());
            guardarUsuarios();
        }
    }
    
    // iniciar Sesión
    public Usuario login(String username, String password) throws UsuarioNoEncontradoException {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                if (!usuario.isActivo()) {
                    throw new UsuarioNoEncontradoException("La cuenta está desactivada");
                }
                
                if (usuario.verificarPassword(password)) {
                    this.usuarioActual = usuario;
                    return usuario;
                } else {
                    throw new UsuarioNoEncontradoException("Contraseña incorrecta");
                }
            }
        }
        throw new UsuarioNoEncontradoException(username);
    }
    
    // cerrar Sesión
    public void logout() {
        this.usuarioActual = null;
    }
    
    // crear nuevo usuario
    public Usuario crearUsuario(String nombreCompleto, String username, String password) 
            throws ArchivoNoValidoException {
        
        // evaluaciones
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new ArchivoNoValidoException("El nombre completo no puede estar vacío");
        }
        
        if (username == null || username.trim().isEmpty()) {
            throw new ArchivoNoValidoException("El nombre de usuario no puede estar vacío");
        }
        
        if (password == null || password.length() < 4) {
            throw new ArchivoNoValidoException("La contraseña debe tener al menos 4 caracteres");
        }
        
        if (existeUsuario(username)) {
            throw new ArchivoNoValidoException("El nombre de usuario ["+username+"] ya existe");
        }
        
        if (username.contains(" ") || username.contains("\\") || username.contains("/")) {
            throw new ArchivoNoValidoException("El nombre de usuario contiene caracteres inválidos");
        }
        
        // crear el usuario
        Usuario nuevoUsuario = new Usuario(nombreCompleto, username, password, false);
        usuarios.add(nuevoUsuario);
        guardarUsuarios();
        
        return nuevoUsuario;
    }
    
    // verificar si existe un usuario
    public boolean existeUsuario(String username) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    // obtener dicho usuario por "username"
    public Usuario obtenerUsuario(String username) throws UsuarioNoEncontradoException {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                return usuario;
            }
        }
        throw new UsuarioNoEncontradoException(username);
    }
    
    // obtener una lista de todos los usuarios registrados
    public ArrayList<Usuario> obtenerTodosLosUsuarios() {
        return new ArrayList<>(usuarios);
    }
    
    // obtener una lista de los usuarios activos
    public ArrayList<Usuario> obtenerUsuariosActivos() {
        ArrayList<Usuario> activos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario.isActivo()) {
                activos.add(usuario);
            }
        }
        return activos;
    }
    
    // activar o desactivar un usuario
    public void cambiarEstadoUsuario(String username, boolean activo) throws UsuarioNoEncontradoException {
        Usuario usuario = obtenerUsuario(username);
        usuario.setActivo(activo);
        guardarUsuarios();
    }
    
    // cambiar de contraseña
    public void cambiarPassword(String username, String passwordActual, String passwordNueva) 
            throws UsuarioNoEncontradoException, ArchivoNoValidoException {
        
        Usuario usuario = obtenerUsuario(username);
        
        if (!usuario.verificarPassword(passwordActual)) {
            throw new ArchivoNoValidoException("La contraseña actual es incorrecta");
        }
        
        if (passwordNueva == null || passwordNueva.length() < 4) {
            throw new ArchivoNoValidoException("La nueva contraseña debe tener al menos 4 caracteres");
        }
        
        usuario.setPassword(passwordNueva);
        guardarUsuarios();
    }
    
    // eliminar usuario 
    public boolean eliminarUsuario(String username) throws UsuarioNoEncontradoException, ArchivoNoValidoException {
        if (usuarioActual == null || !usuarioActual.esAdmin()) {
            throw new ArchivoNoValidoException("Solo el administrador puede eliminar usuarios");
        }
        
        if (username.equalsIgnoreCase("admin")) {
            throw new ArchivoNoValidoException("No se puede eliminar al usuario administrador");
        }
        
        Usuario usuario = obtenerUsuario(username);
        usuarios.remove(usuario);
        guardarUsuarios();
        return true;
    }
    
    // obtener usuario actualmente logueado
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    // verificar si hay sesión activa
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
    
    // guardar usuarios en archivo binario
    public void guardarUsuarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_USUARIOS))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " +e.getMessage());
        }
    }
    
    // cargar usuarios desde archivo binario
    private void cargarUsuarios() {
        File archivo = new File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_USUARIOS))) {
            this.usuarios = (ArrayList<Usuario>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar usuarios: "+e.getMessage());
            this.usuarios = new ArrayList<>();
        }
    }
    
    // obtener cantidad de usuarios registrados
    public int getCantidadUsuarios() {
        return usuarios.size();
    }
}