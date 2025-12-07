package EditorTexto;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author marye
 */
public class PestaniaEditor {

    public final JTextPane textPane;
    public final JScrollPane scroll;
    private final DocumentModel model;
    private File file;

    private boolean modified;
    private Color defaultColor;
    public int lastSelStart = 0;
    public int lastSelEnd = 0;

    public PestaniaEditor(DocumentModel model, File file) {
        this.model = model == null ? new DocumentModel("") : model;
        this.file = file;
        this.modified = false;
        this.defaultColor = FormatoTexto.hexToColor(model.getDefaultColor());

        this.textPane = new JTextPane();
        this.textPane.setText(model.getTexto());
        this.scroll = new JScrollPane(textPane);

        aplicarDocumentModel();

        this.textPane.addCaretListener(e -> {
            int a = Math.min(e.getDot(), e.getMark());
            int b = Math.max(e.getDot(), e.getMark());
            if (b > a) {
                this.lastSelStart = a;
                this.lastSelEnd = b;
            }
        });

        this.textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                setModified(true);
            }

            public void removeUpdate(DocumentEvent e) {
                setModified(true);
            }

            public void changedUpdate(DocumentEvent e) {
                setModified(true);
            }
        });
    }


    public void setModified(boolean m) {
        if (this.modified == m) {
            return;
        }
        this.modified = m;
    }

    public boolean isModified() {
        return modified;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public DocumentModel getModel() {
        return model;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color color) {
        if (color != null) {
            this.defaultColor = color;
            this.model.setDefaultColor(FormatoTexto.colorToHex(color));
        }
    }

    public void aplicarDocumentModel() {
        StyledDocument doc = textPane.getStyledDocument();
        String modelText = model.getTexto();
        try {
            if (!modelText.equals(doc.getText(0, doc.getLength()))) {
                textPane.setText(modelText);
                doc = textPane.getStyledDocument();
            }
        } catch (BadLocationException ignored) {
        }

        SimpleAttributeSet def = new SimpleAttributeSet();
        StyleConstants.setFontFamily(def, model.getDefaultFont());
        StyleConstants.setFontSize(def, model.getDefaultSize());
        StyleConstants.setBold(def, (model.getDefaultStyle() & Font.BOLD) != 0);
        StyleConstants.setItalic(def, (model.getDefaultStyle() & Font.ITALIC) != 0);
        StyleConstants.setForeground(def, FormatoTexto.hexToColor(model.getDefaultColor()));
        if (doc.getLength() > 0) {
            doc.setCharacterAttributes(0, doc.getLength(), def, true);
        }

        for (FormatoTexto ft : model.getFormato()) {
            int inicio = Math.max(0, ft.inicio);
            int fin = Math.min(doc.getLength(), ft.fin);
            if (inicio >= fin) {
                continue;
            }
            
            SimpleAttributeSet sas = new SimpleAttributeSet();
            if (ft.fontFamily != null && !ft.fontFamily.isEmpty()) {
                StyleConstants.setFontFamily(sas, ft.fontFamily);
            }
            
            if (ft.fontSize > 0) {
                StyleConstants.setFontSize(sas, ft.fontSize);
            }
            
            StyleConstants.setBold(sas, (ft.fontStyle & Font.BOLD) != 0);
            StyleConstants.setItalic(sas, (ft.fontStyle & Font.ITALIC) != 0);
            if (ft.color != null && !ft.color.isEmpty()) {
                StyleConstants.setForeground(sas, FormatoTexto.hexToColor(ft.color));
            }
            
            doc.setCharacterAttributes(inicio, fin - inicio, sas, false);
        }
    }

  
    public void ExtraerDocumentModel() throws BadLocationException {
        StyledDocument doc = textPane.getStyledDocument();
        String fullText = doc.getText(0, doc.getLength());
        model.setText(fullText);
        model.limpiarFormato();

        if (fullText.length() == 0) {
            model.normalizeFormato();
            return;
        }

        int posicion = 0;
        while (posicion < fullText.length()) {
            Element e = doc.getCharacterElement(posicion);
            AttributeSet as = e.getAttributes();

            String font = StyleConstants.getFontFamily(as);
            int size = StyleConstants.getFontSize(as);
            boolean isBold = StyleConstants.isBold(as);
            boolean isItalic = StyleConstants.isItalic(as);
            int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
            Color col = StyleConstants.getForeground(as);
            String colorHex = FormatoTexto.colorToHex(col);

            int runEnd = e.getEndOffset();
            if (runEnd > fullText.length()) {
                runEnd = fullText.length();
            }

            FormatoTexto ft = new FormatoTexto(posicion, runEnd, font, size, style, colorHex);
            model.AgregarFormato(ft);

            posicion = runEnd;
        }

        model.normalizeFormato();
    }

    public void guardarCambios() {
        try {
            ExtraerDocumentModel();
            model.setDefaultColor(FormatoTexto.colorToHex(defaultColor));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
}
