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

    private final SistemaArchivos archivo;
    private final String usernameActual;
    private boolean error = false;

    public CMD(Usuario usuario, SistemaArchivos archivo) {
        this.archivo = archivo != null ? archivo : new SistemaArchivos();
        this.usernameActual = usuario != null ? usuario.getUsername() : null;
        inicializarUsuario(usuario);
    }

    public boolean isError() {
        return error;
    }

    private String mostarError(String mensaje) {
        this.error = true;
        return mensaje;
    }

    private void inicializarUsuario(Usuario usuario) {
        try {
            archivo.cargar();
            if (usuario != null) {
                try {
                    archivo.establecerUsuario(usuario);
                } catch (PermisosDenegadosException e) {
                    archivo.crearCarpetaUsuario(usuario.getUsername());
                    archivo.establecerUsuario(usuario);
                }
                try {
                    if (usuario.esAdmin()) {
                        boolean existeAdmin = (archivo.obtenerArchivoEnRuta(usuario.getUsername()) != null);
                        if (!existeAdmin) {
                            archivo.crearCarpetaEnRuta(usuario.getUsername(), "");
                        }
                        archivo.navegarARuta("Z:\\" + usuario.getUsername());
                    }
                } catch (Exception e) {
                }
            } else {
                archivo.navegarARuta("Z:\\");
            }
        } catch (Exception e) {
        }
    }

    public String getPrompt() {
        String ruta = archivo.getRutaActual();
        String rel = ruta.replace("Z:\\", "").replace("/", "\\");
        if (rel.startsWith("\\")) {
            rel = rel.substring(1);
        }

        if (rel.endsWith("\\")) {
            rel = rel.substring(0, rel.length() - 1);
        }

        String user = usernameActual != null ? usernameActual : "";
        if (rel.isEmpty()) {
            return "Z:\\" + user + "> ";
        }

        if (rel.toLowerCase().startsWith(user.toLowerCase())) {
            return "Z:\\" + rel + "> ";
        }
        return "Z:\\" + user + "\\" + rel + "> ";
    }

    public String Ejecutar(String entrada) {
        error = false;

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

        if (comando.equals("date") && !parametro.isEmpty()) {
            return mostarError("El comando 'date' no puede ejecutarse con parametro.\nUse el comando sin añadir texto o números.");
        }
        if (comando.equalsIgnoreCase("time") && !parametro.isEmpty()) {
            return mostarError("El comando 'time' no puede ejecutarse con parametro.\nUse el comando sin añadir texto o números.");
        }
        if (comando.equalsIgnoreCase("cd..") && !parametro.isEmpty()) {
            return mostarError("El comando 'cd..' no utiliza nombres de carpeta ni rutas.\nEste comando únicamente retrocede a la carpeta anterior.");
        }
        if (comando.equals("mkdir") && parametro.isEmpty()) {
            return mostarError("El comando 'mkdir' requiere el nombre de la carpeta que desea crear.");
        }
        if (comando.equals("rm") && parametro.isEmpty()) {
            return mostarError("El comando 'rm' necesita el nombre del archivo o carpeta que desea eliminar.");
        }
        if (comando.equals("cd") && parametro.isEmpty()) {
            return mostarError("El comando 'cd' debe acompañarse de una ruta válida.\nIndique la ubicación a la que desea acceder.");
        }

        switch (comando) {
            case "mkdir":
                return ejecutarMkdir(parametro);
            case "rm":
                return ejecutarRm(parametro);
            case "cd":
                return ejecutarCd(parametro);
            case "cd..":
                return ejecutarRegresar();
            case "dir":
                if (parametro.isEmpty()) {
                    String resultado = dir();
                    error = false;
                    return resultado;
                } else {
                    String rutaOriginal = archivo.getRutaActual();
                    try {
                        String ruta = parametro.replace("/", "\\");
                        boolean cambiado;
                        if (ruta.toUpperCase().startsWith("Z:")) {
                            archivo.navegarARuta(ruta);
                            cambiado = true;
                        } else {
                            cambiado = archivo.cambiarDirectorio(parametro);
                        }
                        if (!cambiado) {
                            return mostarError("El directorio no ha sido encontrado. Intente de nuevo.");
                        }
                        String resultado = dir();
                        try {
                            archivo.navegarARuta(rutaOriginal);
                        } catch (Exception ignore) {
                        }
                        this.error = false;
                        return resultado;
                    } catch (Exception e) {
                        try {
                            archivo.navegarARuta(rutaOriginal);
                        } catch (Exception ignore) {
                        }
                        return mostarError("El directorio o archivo no ha sido encontrado. Intente de nuevo.");
                    }
                }
            case "date":
                this.error = false;
                return fechaActual();
            case "time":
                this.error = false;
                return horaActual();
            default:
                return mostarError("El comando no es valido. Intente de nuevo.");
        }
    }

    private String dir() {
        try {
            ArrayList<Archivo> lista = archivo.listarContenido();
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            StringBuilder salida = new StringBuilder();
            salida.append("\nDirectorio de: ").append(archivo.getRutaActual()).append("\n\n");
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

            error = false;
            return salida.toString();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return mostarError("No se pudo listar el directorio.");
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

    private String ejecutarMkdir(String parametro) {
        try {
            boolean creado = archivo.crearCarpeta(parametro);

            if (creado) {
                error = false;
                return "Carpeta creada: " + parametro;
            }

        } catch (ArchivoNoValidoException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return mostarError("Carpeta existente. No se pudo crear la carpeta '" + parametro + "'.");
    }

    private String ejecutarRm(String parametro) {
        String nombre = parametro;

        try {
            boolean force = false;
            if (parametro.endsWith(" -f") || parametro.endsWith(" -r")) {
                force = true;
                nombre = parametro.substring(0, parametro.length() - 3).trim();
            }

            boolean eliminado = archivo.eliminar(nombre, force);

            if (eliminado) {
                error = false;
                return "Eliminado: " + nombre;
            }

        } catch (ArchivoNoValidoException e) {
            System.out.println("ERROR" + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return mostarError("Carpeta o archivo existente. No se pudo eliminar '" + nombre + "'");
    }

    private String ejecutarCd(String parametro) {
        try {
            String ruta = parametro.replace("/", "\\");

            if (ruta.toUpperCase().startsWith("Z:")) {
                archivo.navegarARuta(ruta);
                error = false;
                return "";
            }

            boolean encontrado = archivo.cambiarDirectorio(parametro);

            if (encontrado) {
                error = false;
                return "";
            }

        } catch (ArchivoNoValidoException e) {
            System.out.println("ERROR" + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR" + e.getMessage());
        }

        return mostarError("El directorio o archivo no ha sido encontrado. Intente de nuevo.");
    }

    private String ejecutarRegresar() {
        try {
            boolean ruta = archivo.regresarCarpeta();

            if (ruta) {
                this.error = false;
                return "";
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return mostarError("Ya te encuntras en la raíz.");
    }

    private String fechaActual() {
        this.error = false;
        return "La fecha actual es: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private String horaActual() {
        this.error = false;
        return "La hora actual es: " + new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
