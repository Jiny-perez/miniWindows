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

    public void aplicarFormato(int start, int end,
            String newFontFamily, Integer newFontSize,
            Integer newFontStyle, String newColor) {
        if (this.texto == null) {
            this.texto = "";
        }

        // Limitar rangos
        if (start < 0) {
            start = 0;
        }
        if (end > this.texto.length()) {
            end = this.texto.length();
        }
        if (start >= end) {
            return;
        }

        // Asegurar formato ordenado por inicio
        Collections.sort(formato, Comparator.comparingInt(a -> a.inicio));

        List<FormatoTexto> nuevos = new ArrayList<>();

        // 1) Conserva las porciones de formatos que no se solapan con la selección
        for (FormatoTexto f : formato) {
            if (f.fin <= start || f.inicio >= end) {
                // no se solapa con la selección: lo conservamos tal cual
                nuevos.add(new FormatoTexto(f.inicio, f.fin, f.fontFamily, f.fontSize, f.fontStyle, f.color));
            } else {
                // se solapa: añadimos la porción izquierda (si existe)
                if (f.inicio < start) {
                    nuevos.add(new FormatoTexto(f.inicio, start, f.fontFamily, f.fontSize, f.fontStyle, f.color));
                }
                // la porción central se procesará en el bloque de subtramos más abajo
                // añadiremos la porción derecha (si existe) después del proceso de selección
                if (f.fin > end) {
                    // guardamos la porción derecha para añadirla más tarde (evita duplicados)
                    nuevos.add(new FormatoTexto(end, f.fin, f.fontFamily, f.fontSize, f.fontStyle, f.color));
                }
            }
        }

        // 2) Ahora rellenamos la selección (start..end) por subtramos:
        // Recorremos los formatos existentes y cubrimos gaps con defaults.
        int cursor = start;
        int idx = 0;
        // Usamos la lista original (ordenada) para localizar formatos que intersectan [start,end)
        while (idx < formato.size() && formato.get(idx).fin <= start) {
            idx++;
        }
        while (cursor < end) {
            // buscar siguiente formato que se solape con cursor
            FormatoTexto f = null;
            if (idx < formato.size()) {
                f = formato.get(idx);
            }
            if (f == null || f.inicio >= end) {
                // no hay más formatos solapando la selección: crear tramo [cursor, end) con defaults
                String font = (newFontFamily != null && !newFontFamily.isEmpty()) ? newFontFamily : this.defaultFont;
                int fsize = (newFontSize != null && newFontSize > 0) ? newFontSize : this.defaultSize;
                int fstyle = (newFontStyle != null && newFontStyle >= 0) ? newFontStyle : this.defaultStyle;
                String fcolor = (newColor != null && !newColor.isEmpty()) ? newColor : this.defaultColor;
                nuevos.add(new FormatoTexto(cursor, end, font, fsize, fstyle, fcolor));
                cursor = end;
            } else if (f.fin <= cursor) {
                // formato ya terminado, avanzar índice
                idx++;
            } else if (f.inicio > cursor) {
                // hay un gap entre cursor y f.inicio -> usar defaults para [cursor, min(f.inicio,end))
                int gapEnd = Math.min(f.inicio, end);
                String font = (newFontFamily != null && !newFontFamily.isEmpty()) ? newFontFamily : this.defaultFont;
                int fsize = (newFontSize != null && newFontSize > 0) ? newFontSize : this.defaultSize;
                int fstyle = (newFontStyle != null && newFontStyle >= 0) ? newFontStyle : this.defaultStyle;
                String fcolor = (newColor != null && !newColor.isEmpty()) ? newColor : this.defaultColor;
                nuevos.add(new FormatoTexto(cursor, gapEnd, font, fsize, fstyle, fcolor));
                cursor = gapEnd;
            } else {
                // f.inicio <= cursor < f.fin -> hay solapamiento con formato existente
                int overlapStart = cursor;
                int overlapEnd = Math.min(f.fin, end);

                // heredar atributos de f y sobrescribir solo los que no son null
                String font = (newFontFamily != null && !newFontFamily.isEmpty()) ? newFontFamily : (f.fontFamily != null && !f.fontFamily.isEmpty() ? f.fontFamily : this.defaultFont);
                int fsize = (newFontSize != null && newFontSize > 0) ? newFontSize : (f.fontSize > 0 ? f.fontSize : this.defaultSize);
                int fstyle = (newFontStyle != null && newFontStyle >= 0) ? newFontStyle : (f.fontStyle >= 0 ? f.fontStyle : this.defaultStyle);
                String fcolor = (newColor != null && !newColor.isEmpty()) ? newColor : (f.color != null && !f.color.isEmpty() ? f.color : this.defaultColor);

                nuevos.add(new FormatoTexto(overlapStart, overlapEnd, font, fsize, fstyle, fcolor));

                cursor = overlapEnd;
                if (f.fin <= cursor) {
                    idx++;
                }
            }
        }

        // 3) Reemplazar lista de formatos y normalizar (fusiona intervalos contiguos con mismo formato)
        formato.clear();
        formato.addAll(nuevos);
        normalizeFormato();
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
