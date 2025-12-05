package VisorImagenes;

import Modelo.Archivo;
import Sistema.SistemaArchivos;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author najma
 */
public class VisorImagenes {
  private final ArrayList<File> listaImagenes;
    private int indiceActual;
    private BufferedImage imagenOriginal;
    private BufferedImage imagenTransformada;
    private double zoomActual;
    private int rotacionActual;

    public VisorImagenes(SistemaArchivos sa, int indiceInicial) {
        listaImagenes = new ArrayList<>();
        indiceActual = 0;
        zoomActual = 1.0;
        rotacionActual = 0;
        if (sa != null) {
            ArrayList<Archivo> contenidos = sa.listarContenido();
            if (contenidos != null) {
                for (Archivo a : contenidos) {
                    if (a != null && !a.isEsCarpeta() && esImagen(a)) {
                        File f = new File(a.getRutaAbsoluta());
                        if (f.exists() && f.isFile()) {
                            listaImagenes.add(f);
                        }
                    }
                }
            }
        }
        if (!listaImagenes.isEmpty()) {
            this.indiceActual = Math.max(0, Math.min(indiceInicial, listaImagenes.size() - 1));
            cargarImagen(this.indiceActual);
        }
    }

    public VisorImagenes(SistemaArchivos sa, Archivo archivoInicial) {
        this(sa, 0);
        if (archivoInicial != null && esImagen(archivoInicial)) {
            String rutaInicial = archivoInicial.getRutaAbsoluta();
            for (int i = 0; i < listaImagenes.size(); i++) {
                File f = listaImagenes.get(i);
                if (f != null && f.getAbsolutePath().equals(rutaInicial)) {
                    cargarImagen(i);
                    break;
                }
            }
        }
    }

    public VisorImagenes(File archivoInicial) {
        listaImagenes = new ArrayList<>();
        indiceActual = 0;
        zoomActual = 1.0;
        rotacionActual = 0;
        if (archivoInicial != null && archivoInicial.exists() && archivoInicial.isFile()) {
            File carpeta = archivoInicial.getParentFile();
            if (carpeta != null && carpeta.exists() && carpeta.isDirectory()) {
                File[] hijos = carpeta.listFiles();
                if (hijos != null) {
                    for (File f : hijos) {
                        if (f != null && f.isFile() && esImagen(f)) {
                            listaImagenes.add(f);
                        }
                    }
                }
            }
            for (int i = 0; i < listaImagenes.size(); i++) {
                if (listaImagenes.get(i).getAbsolutePath().equals(archivoInicial.getAbsolutePath())) {
                    indiceActual = i;
                    break;
                }
            }
            if (!listaImagenes.isEmpty()) cargarImagen(indiceActual);
        }
    }

    public boolean esImagen(Archivo archivo) {
        if (archivo == null || archivo.isEsCarpeta()) return false;
        String nombre = archivo.getNombre().toLowerCase();
        return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") ||
               nombre.endsWith(".png") || nombre.endsWith(".gif") ||
               nombre.endsWith(".bmp") || nombre.endsWith(".webp");
    }

    public boolean esImagen(File archivo) {
        if (archivo == null || !archivo.isFile()) return false;
        String nombre = archivo.getName().toLowerCase();
        return nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") ||
               nombre.endsWith(".png") || nombre.endsWith(".gif") ||
               nombre.endsWith(".bmp") || nombre.endsWith(".webp");
    }

    public boolean cargarImagen(int indice) {
        if (indice < 0 || indice >= listaImagenes.size()) return false;
        File archivo = listaImagenes.get(indice);
        try {
            BufferedImage img = ImageIO.read(archivo);
            if (img == null) return false;
            this.imagenOriginal = img;
            this.zoomActual = 1.0;
            this.rotacionActual = 0;
            this.indiceActual = indice;
            actualizarImagenTransformada();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void actualizarImagenTransformada() {
        if (imagenOriginal == null) {
            imagenTransformada = null;
            return;
        }
        BufferedImage temp = imagenOriginal;
        if (rotacionActual % 360 != 0) {
            temp = rotarImagen(temp, rotacionActual);
        }
        if (Math.abs(zoomActual - 1.0) > 1e-9) {
            temp = escalarImagen(temp, zoomActual);
        }
        imagenTransformada = temp;
    }

    private BufferedImage rotarImagen(BufferedImage imagen, int grados) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        boolean swap = ((grados / 90) % 2 != 0);
        int nuevoAncho = swap ? alto : ancho;
        int nuevoAlto = swap ? ancho : alto;
        int tipo = imagen.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : imagen.getType();
        BufferedImage rotada = new BufferedImage(nuevoAncho, nuevoAlto, tipo);
        Graphics2D g2d = rotada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        AffineTransform transform = new AffineTransform();
        transform.translate(nuevoAncho / 2.0, nuevoAlto / 2.0);
        transform.rotate(Math.toRadians(grados));
        transform.translate(-ancho / 2.0, -alto / 2.0);
        g2d.setTransform(transform);
        g2d.drawImage(imagen, 0, 0, null);
        g2d.dispose();
        return rotada;
    }

    private BufferedImage escalarImagen(BufferedImage imagen, double escala) {
        int nuevoAncho = Math.max(1, (int)Math.round(imagen.getWidth() * escala));
        int nuevoAlto  = Math.max(1, (int)Math.round(imagen.getHeight() * escala));
        int tipo = imagen.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : imagen.getType();
        BufferedImage escalada = new BufferedImage(nuevoAncho, nuevoAlto, tipo);
        Graphics2D g2d = escalada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(imagen, 0, 0, nuevoAncho, nuevoAlto, null);
        g2d.dispose();
        return escalada;
    }

    public boolean imagenAnterior() {
        if (indiceActual > 0) return cargarImagen(indiceActual - 1);
        return false;
    }

    public boolean imagenSiguiente() {
        if (indiceActual < listaImagenes.size() - 1) return cargarImagen(indiceActual + 1);
        return false;
    }

    public void zoomIn() {
        zoomActual = Math.min(zoomActual * 1.25, 5.0);
        actualizarImagenTransformada();
    }

    public void zoomOut() {
        zoomActual = Math.max(zoomActual / 1.25, 0.1);
        actualizarImagenTransformada();
    }

    public void tamañoReal() {
        zoomActual = 1.0;
        actualizarImagenTransformada();
    }

    public void ajustarPantalla(int anchoVentana, int altoVentana) {
        if (imagenOriginal == null || anchoVentana <= 0 || altoVentana <= 0) return;
        int anchoImagen = imagenOriginal.getWidth();
        int altoImagen = imagenOriginal.getHeight();
        if (rotacionActual == 90 || rotacionActual == 270) {
            int temp = anchoImagen; anchoImagen = altoImagen; altoImagen = temp;
        }
        double escalaAncho = (double) anchoVentana / (double) anchoImagen;
        double escalaAlto  = (double) altoVentana / (double) altoImagen;
        zoomActual = Math.min(escalaAncho, escalaAlto);
        if (zoomActual <= 0) zoomActual = 1.0;
        actualizarImagenTransformada();
    }

    public void rotarIzquierda() {
        rotacionActual = (rotacionActual - 90 + 360) % 360;
        actualizarImagenTransformada();
    }

    public void rotarDerecha() {
        rotacionActual = (rotacionActual + 90) % 360;
        actualizarImagenTransformada();
    }

    public BufferedImage getImagenTransformada() {
        return imagenTransformada;
    }

    public BufferedImage getImagenOriginal() {
        return imagenOriginal;
    }

    public File getArchivoActual() {
        if (indiceActual >= 0 && indiceActual < listaImagenes.size()) return listaImagenes.get(indiceActual);
        return null;
    }

    public int getIndiceActual() {
        return indiceActual;
    }

    public int getTotalImagenes() {
        return listaImagenes.size();
    }

    public double getZoomActual() {
        return zoomActual;
    }

    public int getRotacionActual() {
        return rotacionActual;
    }

    public boolean hayAnterior() {
        return indiceActual > 0;
    }

    public boolean haySiguiente() {
        return indiceActual < listaImagenes.size() - 1;
    }

    public int getAnchoOriginal() {
        return imagenOriginal != null ? imagenOriginal.getWidth() : 0;
    }

    public int getAltoOriginal() {
        return imagenOriginal != null ? imagenOriginal.getHeight() : 0;
    }
}