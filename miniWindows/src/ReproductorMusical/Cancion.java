package ReproductorMusical;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author marye
 */
public class Cancion {

    private String titulo;
    private String direccion;
    private ImageIcon imgDefault;
    private long duracion;

    public Cancion(String titulo, String direccion) {
        this.titulo = titulo;
        this.direccion = direccion;
        this.duracion = 0;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

     public void setDuracion(long duracion) {
         this.duracion=duracion;
    }
    public long getDuracion() {
        return duracion;
    }

    public ImageIcon getImgDefault() {
        if (imgDefault == null) {
            imgDefault = imgDefault();
        }
        return imgDefault;
    }

    public String DuracionFormateada() {
        if (duracion <= 0) {
            return "0:00";
        }

        long minutos = duracion / 60;
        long segundos = duracion % 60;

        if (minutos >= 60) {
            long horas = minutos / 60;
            minutos %= 60;
            return String.format("%d:%02d:%02d", horas, minutos, segundos);
        }
        return String.format("%d:%02d", minutos, segundos);
    }

    private ImageIcon imgDefault() {
        BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(255, 20, 147));
        g2d.fillRect(0, 0, 50, 50);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", Font.BOLD, 20));
        g2d.drawString("♪", 18, 32);
        g2d.dispose();
        return new ImageIcon(img);
    }

    public String toString() {
        return titulo + " (" + DuracionFormateada() + ")";
    }
}
