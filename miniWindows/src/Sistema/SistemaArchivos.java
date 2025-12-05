package Sistema;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import Modelo.Usuario;
import Excepciones.*;
import Modelo.Archivo;
import java.nio.file.attribute.BasicFileAttributes;

public class SistemaArchivos implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String BASE_FISICO;
    private String rutaActualRelativa;
    private Usuario usuarioActual;
    private final String ARCHIVO_SISTEMA = "sistema_archivos.sop";

    public SistemaArchivos() {
        this.BASE_FISICO = System.getProperty("user.dir") + File.separator + "Z";
        this.rutaActualRelativa = "";
        File base = new File(BASE_FISICO);
        if (!base.exists()) {
            base.mkdirs();
        }
    }

    private File fileFromRel(String rutaRel) {
        if (rutaRel == null || rutaRel.trim().isEmpty()) {
            return new File(BASE_FISICO);
        }
        String r = rutaRel.replace("/", File.separator).replace("\\", File.separator);
        return new File(BASE_FISICO + File.separator + r);
    }

    private String normalizeRel(String rutaRel) {
        if (rutaRel == null) {
            return "";
        }
        String r = rutaRel.trim().replace("\\", "/");
        r = r.replaceAll("^/+|/+$", "");
        return r;
    }

    private String buildRelativePath(File f) {
        String abs = f.getAbsolutePath();
        String base = new File(BASE_FISICO).getAbsolutePath();
        if (abs.startsWith(base)) {
            String rel = abs.substring(base.length());
            if (rel.startsWith(File.separator)) {
                rel = rel.substring(1);
            }
            return rel.replace(File.separatorChar, '/');
        } else {
            return f.getAbsolutePath();
        }
    }

    private Calendar fileTimeToCalendar(File f) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(f.lastModified());
        return cal;
    }

    public String getRutaActual() {
        if (rutaActualRelativa == null || rutaActualRelativa.isEmpty()) {
            return "Z:\\";
        }
        return "Z:\\" + rutaActualRelativa.replace("/", "\\") + "\\";
    }

    public File getDirectorioActualFisico() {
        return fileFromRel(rutaActualRelativa);
    }

    public Archivo getRaiz() {
        File base = fileFromRel("");
        return new Archivo("Z:", true, 0L, Calendar.getInstance(), "", base.getAbsolutePath());
    }

    public ArrayList<Archivo> listarContenido() {
        return listarContenidoEnRuta(rutaActualRelativa);
    }

    public ArrayList<Archivo> listarContenidoEnRuta(String rutaRelativa) {
        String rnorm = normalizeRel(rutaRelativa);
        File dir = fileFromRel(rnorm);
        ArrayList<Archivo> lista = new ArrayList<>();
        File[] hijos = dir.listFiles();
        if (hijos == null) {
            return lista;
        }
        for (File f : hijos) {
            Calendar cal = fileTimeToCalendar(f);
            String rel = buildRelativePath(f);
            lista.add(new Archivo(f.getName(), f.isDirectory(), f.isFile() ? f.length() : 0L, cal, rel, f.getAbsolutePath()));
        }
        return lista;
    }

    public Archivo obtenerArchivoEnRuta(String nombre, String rutaRelativa) {
        if (nombre == null) {
            return null;
        }
        String rnorm = normalizeRel(rutaRelativa);
        File padre = fileFromRel(rnorm);
        File f = new File(padre, nombre);
        if (!f.exists()) {
            return null;
        }
        return new Archivo(f.getName(), f.isDirectory(), f.isFile() ? f.length() : 0L, fileTimeToCalendar(f), buildRelativePath(f), f.getAbsolutePath());
    }

    public Archivo obtenerArchivoEnRuta(String nombre) {
        return obtenerArchivoEnRuta(nombre, rutaActualRelativa);
    }

    public boolean crearCarpeta(String nombre) throws ArchivoNoValidoException {
        return crearCarpetaEnRuta(nombre, rutaActualRelativa);
    }

    public boolean crearCarpetaEnRuta(String nombre, String rutaVirtualPadre) throws ArchivoNoValidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoNoValidoException("El nombre de la carpeta no puede estar vacío");
        }
        if (nombre.contains("/") || nombre.contains("\\")) {
            throw new ArchivoNoValidoException("El nombre de carpeta no puede contener separadores");
        }
        String padreRel = normalizeRel(rutaVirtualPadre);
        File padre = fileFromRel(padreRel);
        if (!padre.exists() || !padre.isDirectory()) {
            throw new ArchivoNoValidoException(rutaVirtualPadre, "la ruta padre no existe");
        }
        File nueva = new File(padre, nombre);
        if (nueva.exists()) {
            throw new ArchivoNoValidoException(nombre, "ya existe una carpeta o archivo con ese nombre");
        }
        try {
            if (!nueva.mkdirs()) {
                throw new ArchivoNoValidoException("No se pudo crear la carpeta física: " + nueva.getAbsolutePath());
            }
        } catch (SecurityException se) {
            throw new ArchivoNoValidoException("Permisos denegados al crear carpeta física: " + se.getMessage());
        }
        return true;
    }

    public boolean crearArchivo(String nombre, String tipo, String contenido) throws ArchivoNoValidoException {
        return crearArchivoEnRuta(nombre, tipo, contenido, rutaActualRelativa);
    }

    public boolean crearArchivoEnRuta(String nombre, String tipo, String contenido, String rutaVirtualPadre) throws ArchivoNoValidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoNoValidoException("El nombre del archivo no puede estar vacío");
        }
        if (nombre.contains("/") || nombre.contains("\\")) {
            throw new ArchivoNoValidoException("Nombre de archivo inválido");
        }
        String padreRel = normalizeRel(rutaVirtualPadre);
        File padre = fileFromRel(padreRel);
        if (!padre.exists() || !padre.isDirectory()) {
            throw new ArchivoNoValidoException(rutaVirtualPadre, "la ruta padre no existe");
        }
        File archivo = new File(padre, nombre);
        if (archivo.exists()) {
            throw new ArchivoNoValidoException(nombre, "ya existe");
        }
        try {
            File parent = archivo.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"))) {
                if (contenido != null) {
                    bw.write(contenido);
                }
            }
        } catch (IOException | SecurityException e) {
            if (archivo.exists()) {
                archivo.delete();
            }
            throw new ArchivoNoValidoException("No se pudo crear el archivo físico: " + e.getMessage());
        }
        return true;
    }

    public boolean renombrar(String nombreActual, String nombreNuevo) throws ArchivoNoValidoException {
        if (nombreActual == null || nombreNuevo == null || nombreNuevo.trim().isEmpty()) {
            throw new ArchivoNoValidoException("Nombre inválido");
        }
        File padre = fileFromRel(rutaActualRelativa);
        File a = new File(padre, nombreActual);
        if (!a.exists()) {
            throw new ArchivoNoValidoException(nombreActual, "no existe");
        }
        File b = new File(padre, nombreNuevo);
        if (b.exists()) {
            throw new ArchivoNoValidoException(nombreNuevo, "ya existe un archivo o carpeta con ese nombre");
        }
        boolean ok = a.renameTo(b);
        if (!ok) {
            if (a.isFile()) {
                try {
                    Files.copy(a.toPath(), b.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.deleteIfExists(a.toPath());
                } catch (IOException ex) {
                    throw new ArchivoNoValidoException("Error al renombrar: " + ex.getMessage());
                }
            } else {
                throw new ArchivoNoValidoException("No se pudo renombrar la carpeta");
            }
        }
        return true;
    }

    public boolean eliminar(String nombre, boolean force) throws ArchivoNoValidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoNoValidoException("Debe especificar un nombre");
        }
        File padre = fileFromRel(rutaActualRelativa);
        File objetivo = new File(padre, nombre);
        if (!objetivo.exists()) {
            throw new ArchivoNoValidoException(nombre, "no existe");
        }
        try {
            if (objetivo.isFile()) {
                Files.deleteIfExists(objetivo.toPath());
            } else {
                boolean fisicoTieneContenido = objetivo.isDirectory() && (objetivo.list() != null && objetivo.list().length > 0);
                if (fisicoTieneContenido && !force) {
                    throw new ArchivoNoValidoException(nombre, "no está vacía. Usa force=true para eliminar recursivamente");
                }
                borrarFisicoRecursivo(objetivo);
            }
        } catch (IOException | SecurityException e) {
            throw new ArchivoNoValidoException("No se pudo eliminar físicamente: " + e.getMessage());
        }
        return true;
    }

    public boolean eliminar(String nombre) throws ArchivoNoValidoException {
        return eliminar(nombre, false);
    }

    public boolean cambiarDirectorio(String nombre) throws ArchivoNoValidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ArchivoNoValidoException("Debe especificar un nombre de carpeta");
        }
        String nuevaRel = (rutaActualRelativa == null || rutaActualRelativa.isEmpty()) ? nombre : rutaActualRelativa + "/" + nombre;
        File f = fileFromRel(nuevaRel);
        if (!f.exists() || !f.isDirectory()) {
            throw new ArchivoNoValidoException(nombre, "no existe o no es una carpeta");
        }
        rutaActualRelativa = normalizeRel(nuevaRel);
        return true;
    }

    public boolean regresarCarpeta() {
        if (rutaActualRelativa == null || rutaActualRelativa.isEmpty()) {
            return false;
        }
        int last = rutaActualRelativa.lastIndexOf('/');
        if (last <= 0) {
            rutaActualRelativa = "";
        } else {
            rutaActualRelativa = rutaActualRelativa.substring(0, last);
        }
        return true;
    }

    public boolean navegarARuta(String ruta) throws ArchivoNoValidoException {
        if (ruta == null) {
            return false;
        }
        String r = ruta.replaceFirst("(?i)^Z:[:\\\\/]*", "");
        r = normalizeRel(r);
        File f = fileFromRel(r);
        if (!f.exists() || !f.isDirectory()) {
            throw new ArchivoNoValidoException("Ruta no existe: " + ruta);
        }
        rutaActualRelativa = r;
        return true;
    }

    public ArrayList<Archivo> listarOrdenadoPorNombre(boolean ascendente) {
        ArrayList<Archivo> lista = listarContenido();
        lista.sort((a, b) -> ascendente ? a.getNombre().compareToIgnoreCase(b.getNombre())
                : b.getNombre().compareToIgnoreCase(a.getNombre()));
        return lista;
    }

    public ArrayList<Archivo> listarOrdenadoPorFecha(boolean ascendente) {
        ArrayList<Archivo> lista = listarContenido();
        lista.sort((a, b) -> ascendente ? Long.compare(a.getFechaModificacion().getTimeInMillis(), b.getFechaModificacion().getTimeInMillis())
                : Long.compare(b.getFechaModificacion().getTimeInMillis(), a.getFechaModificacion().getTimeInMillis()));
        return lista;
    }

    public ArrayList<Archivo> listarOrdenadoPorTamanio(boolean ascendente) {
        ArrayList<Archivo> lista = listarContenido();
        lista.sort((a, b) -> ascendente ? Long.compare(a.getTamanio(), b.getTamanio())
                : Long.compare(b.getTamanio(), a.getTamanio()));
        return lista;
    }

    public ArrayList<Archivo> listarOrdenadoPorTipo(boolean ascendente) {
        ArrayList<Archivo> lista = listarContenido();
        lista.sort((a, b) -> {
            if (a.isEsCarpeta() && !b.isEsCarpeta()) {
                return -1;
            }
            if (!a.isEsCarpeta() && b.isEsCarpeta()) {
                return 1;
            }
            String ta = obtenerExtension(a.getNombre());
            String tb = obtenerExtension(b.getNombre());
            int comp = ta.compareToIgnoreCase(tb);
            return ascendente ? comp : -comp;
        });
        return lista;
    }

    private String obtenerExtension(String nombre) {
        int ix = nombre.lastIndexOf('.');
        if (ix > 0 && ix < nombre.length() - 1) {
            return nombre.substring(ix + 1).toLowerCase();
        }
        return "";
    }

    public void crearCarpetaUsuario(String username) throws ArchivoNoValidoException {
        if (username == null || username.trim().isEmpty()) {
            throw new ArchivoNoValidoException("Usuario inválido");
        }
        String anterior = rutaActualRelativa;
        try {
            rutaActualRelativa = "";
            if (!fileFromRel(username).exists()) {
                crearCarpetaEnRuta(username, "");
            }
            cambiarDirectorio(username);
            if (!fileFromRel("Mis Documentos").exists()) {
                crearCarpeta("Mis Documentos");
            }
            if (!fileFromRel("Musica").exists()) {
                crearCarpeta("Musica");
            }
            if (!fileFromRel("Mis Imagenes").exists()) {
                crearCarpeta("Mis Imagenes");
            }
        } finally {
            rutaActualRelativa = anterior;
        }
    }

    public void establecerUsuario(Usuario usuario) throws ArchivoNoValidoException, PermisosDenegadosException {
        if (usuario == null) {
            this.usuarioActual = null;
            this.rutaActualRelativa = "";
            return;
        }
        this.usuarioActual = usuario;
        if (usuario.esAdmin()) {
            this.rutaActualRelativa = "";
        } else {
            File carpetaUsuario = fileFromRel(usuario.getUsername());
            if (carpetaUsuario.exists() && carpetaUsuario.isDirectory()) {
                this.rutaActualRelativa = usuario.getUsername();
            } else {
                throw new PermisosDenegadosException("No se encontró la carpeta del usuario");
            }
        }
    }

    public void guardar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_SISTEMA))) {
            oos.writeObject(this.rutaActualRelativa);
        } catch (IOException e) {
            System.err.println("Warning: no se pudo guardar archivo de sistema: " + e.getMessage());
        }
    }

    public boolean cargar() {
        File archivo = new File(ARCHIVO_SISTEMA);
        if (!archivo.exists()) {
            return false;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_SISTEMA))) {
            Object o = ois.readObject();
            if (o instanceof String) {
                this.rutaActualRelativa = (String) o;
            }
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: no se pudo cargar archivo de sistema: " + e.getMessage());
            return false;
        }
    }

    private void borrarFisicoRecursivo(File fichero) throws IOException {
        if (fichero == null || !fichero.exists()) {
            return;
        }
        final Path root = fichero.toPath();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    try {
                        File f = file.toFile();
                        f.setWritable(true);
                        Files.deleteIfExists(file);
                    } catch (IOException ex) {
                        throw new IOException("No se pudo eliminar el archivo: " + file.toAbsolutePath() + " -> " + ex.getMessage(), ex);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                try {
                    Files.deleteIfExists(dir);
                } catch (IOException e) {
                    try {
                        File d = dir.toFile();
                        d.setWritable(true);
                        Files.deleteIfExists(dir);
                    } catch (IOException ex) {
                        String pathStr = dir.toAbsolutePath().toString();
                        String cmdOutput = ejecutarRmdirWindows(pathStr);
                        if (new File(pathStr).exists()) {
                            throw new IOException("No se pudo eliminar la carpeta (rmdir fallback falló): " + pathStr + " -> " + cmdOutput);
                        } else {
                            return FileVisitResult.CONTINUE;
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private String ejecutarRmdirWindows(String ruta) {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "rmdir", "/s", "/q", ruta);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = p.getInputStream()) {
                byte[] buf = new byte[4096];
                int r;
                while ((r = is.read(buf)) != -1) {
                    baos.write(buf, 0, r);
                }
            }
            int code = p.waitFor();
            String output = baos.toString("UTF-8");
            return "exit=" + code + " output=" + output;
        } catch (IOException | InterruptedException ie) {
            Thread.currentThread().interrupt();
            return "Exception al ejecutar rmdir: " + ie.getMessage();
        }
    }
}
