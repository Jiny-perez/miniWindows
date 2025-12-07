package EditorTexto;

import java.awt.*;
import java.util.Objects;

/**
 *
 * @author marye
 */
public class FormatoTexto {

    public int inicio;
    public int fin;

    public String fontFamily;
    public int fontSize;
    public int fontStyle;
    public String color;

    public FormatoTexto(int inicio, int fin, String fontFamily, int fontSize, int fontStyle, String color) {
        this.inicio = inicio;
        this.fin = fin;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        this.color = color;
    }

    public boolean superposicionTexto(FormatoTexto otro) {
        if (otro == null) {
            return false;
        }
        return this.inicio < otro.fin && this.fin > otro.inicio;
    }

    public boolean mismoFormato(FormatoTexto otro) {
        if (otro == null) {
            return false;
        }

        return Objects.equals(this.fontFamily, otro.fontFamily)
                && this.fontSize == otro.fontSize
                && this.fontStyle == otro.fontStyle
                && Objects.equals(this.color, otro.color);
    }

    public void normalize() {
        if (this.inicio > this.fin) {
            int t = this.inicio;
            this.inicio = this.fin;
            this.fin = t;
        }
    }

    public static String colorToHex(java.awt.Color c) {
        if (c == null) {
            return "#000000";
        }
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static java.awt.Color hexToColor(String hex) {
        if (hex == null) {
            return Color.BLACK;
        }
        try {
            return Color.decode(hex);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    @Override
    public String toString() {
        return inicio + ";" + fin + ";" + escape(fontFamily) + ";" + fontSize + ";" + fontStyle + ";" + color;
    }

    public static FormatoTexto fromString(String line) {
        if (line == null) {
            return null;
        }
        String[] parts = line.split(";", 6);
        if (parts.length < 6) {
            return null;
        }
        try {
            int inicio = Integer.parseInt(parts[0]);
            int fin = Integer.parseInt(parts[1]);
            String font = unescape(parts[2]);
            int size = Integer.parseInt(parts[3]);
            int style = Integer.parseInt(parts[4]);
            String color = parts[5];

            FormatoTexto f = new FormatoTexto(inicio, fin, font, size, style, color);
            f.normalize();
            return f;
        } catch (Exception ex) {
            return null;
        }
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace(";", "\\;");
    }

    private static String unescape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\;", ";").replace("\\\\", "\\");
    }
}

