package EditorTexto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author marye
 */
public class DocumentModel {

    private final List<FormatoTexto> formato = new ArrayList<>();

    private String texto;
    private String defaultFont = "SansSerif";
    private int defaultSize = 14;
    private int defaultStyle = 0;
    private String defaultColor = "#000000";

    public DocumentModel(String texto) {
        this.texto = texto == null ? "" : texto;
    }

    public List<FormatoTexto> getFormato() {
        return formato;
    }

    public String getTexto() {
        return texto;
    }

    public void setText(String texto) {
        this.texto = texto == null ? "" : texto;
    }

    public String getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(String defaultFont) {
        if (defaultFont != null && !defaultFont.isEmpty()) {
            this.defaultFont = defaultFont;
        }
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
        if (defaultSize > 0) {
            this.defaultSize = defaultSize;
        }
    }

    public int getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(int defaultStyle) {
        if (defaultStyle >= 0) {
            this.defaultStyle = defaultStyle;
        }
    }

    public String getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(String defaultColor) {
        if (defaultColor != null && !defaultColor.isEmpty()) {
            this.defaultColor = defaultColor;
        }
    }

    public void AgregarFormato(FormatoTexto ft) {
        if (ft == null) {
            return;
        }
        ft.normalize();
        if (ft.inicio < 0) {
            ft.inicio = 0;
        }
        if (ft.fin > texto.length()) {
            ft.fin = texto.length();
        }
        if (ft.inicio >= ft.fin) {
            return;
        }
        formato.add(ft);
    }

    public void limpiarFormato() {
        formato.clear();
    }

    public void normalizeFormato() {
        Collections.sort(formato, Comparator.comparingInt(a -> a.inicio));

        List<FormatoTexto> salida = new ArrayList<>();
        for (FormatoTexto f : formato) {
            if (salida.isEmpty()) {
                salida.add(new FormatoTexto(f.inicio, f.fin, f.fontFamily, f.fontSize, f.fontStyle, f.color));
            } else {
                FormatoTexto last = salida.get(salida.size() - 1);
                if (last.fin >= f.inicio && last.mismoFormato(f)) {
                    last.fin = Math.max(last.fin, f.fin);
                } else {
                    salida.add(new FormatoTexto(f.inicio, f.fin, f.fontFamily, f.fontSize, f.fontStyle, f.color));
                }
            }
        }
        formato.clear();
        formato.addAll(salida);
    }
}
