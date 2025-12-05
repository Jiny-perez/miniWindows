package CMD;

import Excepciones.ArchivoNoValidoException;
import Excepciones.PermisosDenegadosException;
import Modelo.Archivo;
import Modelo.Usuario;
import Sistema.SistemaArchivos;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author marye
 */
public class CMD {
   private final SistemaArchivos sistema;
    private final String usernameActual;

    public CMD(Usuario usuario, SistemaArchivos sistemaCompartido) {
        this.sistema = sistemaCompartido != null ? sistemaCompartido : new SistemaArchivos();
        this.usernameActual = usuario != null ? usuario.getUsername() : null;
        inicializarUsuario(usuario);
    }

    private void inicializarUsuario(Usuario usuario) {
        try {
            try {
                sistema.cargar();
            } catch (Exception ignored) {
            }
            if (usuario != null) {
                try {
                    sistema.establecerUsuario(usuario);
                } catch (PermisosDenegadosException pex) {
                    try {
                        sistema.crearCarpetaUsuario(usuario.getUsername());
                        sistema.establecerUsuario(usuario);
                    } catch (Exception ex) {
                        System.err.println("No fue posible crear/la carpeta de usuario: " + ex.getMessage());
                    }
                }
                try {
                    if (usuario.esAdmin()) {
                        boolean existeAdmin = (sistema.obtenerArchivoEnRuta(usuario.getUsername()) != null);
                        if (!existeAdmin) {
                            sistema.crearCarpetaEnRuta(usuario.getUsername(), "");
                        }
                        sistema.navegarARuta("Z:\\" + usuario.getUsername());
                    }
                } catch (Exception e) {
                    System.err.println("Advertencia al posicionar admin en su carpeta: " + e.getMessage());
                }
            } else {
                try {
                    sistema.navegarARuta("Z:\\");
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar usuario en CMD: " + e.getMessage());
        }
    }

    public String getPrompt() {
        String ruta = sistema.getRutaActual();
        String rel = ruta.replace("Z:\\", "").replace("/", "\\");
        if (rel.startsWith("\\")) {
            rel = rel.substring(1);
        }
        if (rel.endsWith("\\")) {
            rel = rel.substring(0, rel.length() - 1);
        }
        String user = usernameActual != null ? usernameActual : "";
        if (rel.isEmpty()) {
            return "Z:\\" + user + ">";
        }
        if (rel.toLowerCase().startsWith(user.toLowerCase())) {
            return "Z:\\" + rel + ">";
        }
        return "Z:\\" + user + "\\" + rel + ">";
    }

    public String Ejecutar(String entrada) {
        if (entrada == null) {
            entrada = "";
        }
        entrada = entrada.trim();
        if (entrada.isEmpty()) {
            return "";
        }
        String[] datos = entrada.split("\\s+", 2);
        String comando = datos[0].toLowerCase();
        String parametro = (datos.length > 1) ? datos[1].trim() : "";
        try {
            switch (comando) {
                case "mkdir":
                    return ejecutarMkdir(parametro);
                case "rm":
                    return ejecutarRm(parametro);
                case "cd":
                    return ejecutarCd(parametro);
                case "cd..":
                case "..":
                    return ejecutarRegresar();
                case "dir":
                    return dir();
                case "date":
                    return fechaActual();
                case "time":
                    return horaActual();
                default:
                    return "Comando no valido";
            }
        } catch (ArchivoNoValidoException ex) {
            return "Error: " + ex.getMessage();
        } catch (Exception ex) {
            return "Error inesperado: " + ex.getMessage();
        }
    }

    private String dir() {
        ArrayList<Archivo> lista = sistema.listarContenido();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder salida = new StringBuilder();
        salida.append("\nDirectorio de: ").append(sistema.getRutaActual()).append("\n\n");
        salida.append(String.format("%-19s  %-6s  %-10s  %s%n",
                "Modificacion", "Tipo", "Tamano", "Nombre"));
        salida.append("--------------------------------------------------------------\n");
        if (lista != null) {
            for (Archivo f : lista) {
                Date fechaDate = f.getFechaModificacion().getTime();
                String fecha = formato.format(fechaDate);
                String tipo = f.isEsCarpeta() ? "<DIR>" : "FILE";
                String tam = f.isEsCarpeta() ? "-" : convertirTam(f.getTamanio());
                String nombre = f.getNombre();
                salida.append(String.format("%-19s  %-6s  %-10s  %s%n",
                        fecha, tipo, tam, nombre));
            }
        }
        return salida.toString();
    }

    private String convertirTam(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }
        double mb = kb / 1024.0;
        if (mb < 1024) {
            return String.format("%.2f MB", mb);
        }
        double gb = mb / 1024.0;
        return String.format("%.2f GB", gb);
    }

    private String ejecutarMkdir(String parametro) throws ArchivoNoValidoException {
        if (parametro == null || parametro.isBlank()) {
            return "Comando no valido";
        }
        boolean ok = sistema.crearCarpeta(parametro);
        return ok ? "Carpeta creada: " + parametro : "No se pudo crear la carpeta: " + parametro;
    }

    private String ejecutarRm(String parametro) throws ArchivoNoValidoException {
        if (parametro == null || parametro.isBlank()) {
            return "Comando no valido";
        }
        boolean force = false;
        String nombre = parametro;
        if (parametro.endsWith(" -f") || parametro.endsWith(" -r")) {
            force = true;
            nombre = parametro.substring(0, parametro.length() - 3).trim();
        }
        boolean ok = sistema.eliminar(nombre, force);
        return ok ? "Eliminado: " + nombre : "No se pudo eliminar: " + nombre;
    }

    private String ejecutarCd(String parametro) throws ArchivoNoValidoException {
        if (parametro == null || parametro.isBlank()) {
            return "Comando no valido";
        }
        String ruta = parametro.replace("/", "\\");
        if (ruta.toUpperCase().startsWith("Z:")) {
            sistema.navegarARuta(ruta);
            return "";
        } else {
            boolean ok = sistema.cambiarDirectorio(parametro);
            return ok ? "" : "Directorio no encontrado";
        }
    }

    private String ejecutarRegresar() {
        boolean ok = sistema.regresarCarpeta();
        return ok ? "" : "Ya estás en la raíz";
    }

    private String fechaActual() {
        return "La fecha actual es: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String horaActual() {
        return "La hora actual es: " + new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
