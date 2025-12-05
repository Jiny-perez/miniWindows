package EditorTexto;

import Sistema.MiniWindowsClass;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 *
 * @author marye
 */
public class GUIEditor {

    private JFrame VEditor;
    private JTextPane textPane;
    private JToolBar formatBar;
    private JComboBox<String> fontCombo, styleCombo;
    private JSpinner sizeSpinner;
    private JButton btnColor, btnAbrir, btnGuardar, btnNuevo, btnGuardarComo;
    private DocumentModel defaultDoc;
    private File defaultFile;
    private Color defaultColor = Color.BLACK;

    private MiniWindowsClass sistema;

    public GUIEditor() {
        this(null);
    }

    public GUIEditor(MiniWindowsClass sistema) {
        this.sistema = sistema;
        initComponents();
    }

    private void initComponents() {
        VEditor = new JFrame("Editor de Texto");
        VEditor.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        VEditor.setSize(800, 600);
        VEditor.setLocationRelativeTo(null);
        VEditor.setResizable(false);
        VEditor.setLayout(new BorderLayout());
        textPane = new JTextPane();
        textPane.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(textPane);
        VEditor.add(scroll, BorderLayout.CENTER);
        formatBar = new JToolBar();
        formatBar.setFloatable(false);
        btnNuevo = new JButton("Nuevo");
        btnAbrir = new JButton("Abrir");
        btnGuardar = new JButton("Guardar");
        btnGuardarComo = new JButton("Guardar como");
        formatBar.add(btnNuevo);
        formatBar.add(btnAbrir);
        formatBar.add(btnGuardar);
        formatBar.add(btnGuardarComo);
        formatBar.addSeparator();
        fontCombo = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontCombo.setSelectedItem("SansSerif");
        formatBar.add(fontCombo);
        sizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1));
        formatBar.add(sizeSpinner);
        styleCombo = new JComboBox<>(new String[]{"Normal", "Negrita", "Cursiva"});
        styleCombo.setSelectedIndex(0);
        formatBar.add(styleCombo);
        btnColor = new JButton("Color");
        formatBar.add(btnColor);
        VEditor.add(formatBar, BorderLayout.NORTH);
        btnNuevo.addActionListener(e -> newFile());
        btnAbrir.addActionListener(e -> chooseOpen());
        btnGuardar.addActionListener(e -> {
            try {
                saveFile(false);
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });
        btnGuardarComo.addActionListener(e -> {
            try {
                saveFile(true);
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });
        btnColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(VEditor, "Selecciona color", defaultColor);
            if (c != null) {
                defaultColor = c;
                aplicarFormatoSeleccionado();
            }
        });
        fontCombo.addActionListener(e -> aplicarFormatoSeleccionado());
        sizeSpinner.addChangeListener(e -> aplicarFormatoSeleccionado());
        styleCombo.addActionListener(e -> aplicarFormatoSeleccionado());
        VEditor.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control S"), "save");
        VEditor.getRootPane().getActionMap().put("save", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveFile(false);
                } catch (Exception ex) {
                    mostrarError(ex);
                }
            }
        });
        VEditor.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control O"), "open");
        VEditor.getRootPane().getActionMap().put("open", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                chooseOpen();
            }
        });
        defaultDoc = new DocumentModel("");
        actualizarEditor();
        VEditor.setVisible(true);
    }

    private void newFile() {
        defaultFile = null;
        defaultDoc = new DocumentModel("");
        actualizarEditor();
        VEditor.setTitle("Nuevo - Editor de Texto");
    }

    private JFileChooser createTxtFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Archivos de texto (*.txt)";
            }
        });

        File raizPermitida = null;
        try {
            if (sistema != null && sistema.getSistemaArchivos() != null) {
                raizPermitida = sistema.getSistemaArchivos().getDirectorioActualFisico();
            }
        } catch (Exception ex) {
            raizPermitida = null;
        }

        if (raizPermitida != null) {
            try {
                final File raizCanonical = raizPermitida.getCanonicalFile();
                fc.setCurrentDirectory(raizCanonical);

                final File[] ultimo = new File[1];
                ultimo[0] = raizCanonical;

                fc.addPropertyChangeListener(evt -> {
                    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                        Object newVal = evt.getNewValue();
                        if (newVal instanceof File) {
                            File nuevoDir = (File) newVal;
                            try {
                                File nuevoCanon = nuevoDir.getCanonicalFile();
                                String raizPath = raizCanonical.getAbsolutePath();
                                String nuevoPath = nuevoCanon.getAbsolutePath();
                                if (!nuevoPath.equals(raizPath) && !nuevoPath.startsWith(raizPath + File.separator)) {
                                    SwingUtilities.invokeLater(() -> {
                                        JOptionPane.showMessageDialog(fc,
                                                "No puede salir de la carpeta del usuario.",
                                                "Acceso denegado",
                                                JOptionPane.WARNING_MESSAGE);
                                        fc.setCurrentDirectory(ultimo[0]);
                                    });
                                } else {
                                    ultimo[0] = nuevoCanon;
                                }
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(() -> fc.setCurrentDirectory(ultimo[0]));
                            }
                        }
                    }
                });

            } catch (Exception ex) {
                try {
                    fc.setCurrentDirectory(raizPermitida);
                } catch (Exception ignored) {
                }
            }
        }

        return fc;
    }

    private void chooseOpen() {
        JFileChooser fc = createTxtFileChooser();

        try {
            if (sistema != null && sistema.getSistemaArchivos() != null) {
                File dir = sistema.getSistemaArchivos().getDirectorioActualFisico();
                if (dir != null && dir.exists()) {
                    fc.setCurrentDirectory(dir);
                }
            }
        } catch (Exception ex) {
        }

        int op = fc.showOpenDialog(VEditor);
        if (op == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (f != null) {
                if (!f.getName().toLowerCase().endsWith(".txt")) {
                    JOptionPane.showMessageDialog(VEditor, "Seleccione un archivo con extensión .txt", "Archivo no permitido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    openFile(f);
                } catch (Exception ex) {
                    mostrarError(ex);
                }
            }
        }
    }

    private void openFile(File f) throws Exception {
        defaultFile = f;
        defaultDoc = GestionTexto.cargarTxt(f);
        actualizarEditor();
        VEditor.setTitle(f.getName() + " - Editor de Texto");
    }

    private void saveFile(boolean saveAs) throws Exception {
        if (defaultFile == null || saveAs) {
            JFileChooser fc = createTxtFileChooser();
            try {
                if (sistema != null && sistema.getSistemaArchivos() != null) {
                    File dir = sistema.getSistemaArchivos().getDirectorioActualFisico();
                    if (dir != null && dir.exists()) {
                        fc.setCurrentDirectory(dir);
                    }
                }
            } catch (Exception ex) {
            }

            int op = fc.showSaveDialog(VEditor);
            if (op != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File chosen = fc.getSelectedFile();
            if (chosen == null) {
                return;
            }
            if (!chosen.getName().toLowerCase().endsWith(".txt")) {
                chosen = new File(chosen.getParentFile(), chosen.getName() + ".txt");
            }
            if (chosen.exists()) {
                int resp = JOptionPane.showConfirmDialog(VEditor,
                        "El archivo ya existe. ¿Desea sobrescribirlo?",
                        "Confirmar sobrescritura",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (resp != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            defaultFile = chosen;
        }
        guardarCambios();
        GestionTexto.guardarTxt(defaultFile, defaultDoc);
        VEditor.setTitle(defaultFile.getName() + " - Editor de Texto");

        try {
            if (sistema != null) {
                sistema.guardarSistema();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(VEditor, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private int styleComboToInt() {
        int idx = styleCombo.getSelectedIndex();
        switch (idx) {
            case 1:
                return Font.BOLD;
            case 2:
                return Font.ITALIC;
            case 3:
                return Font.BOLD | Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }

    private void aplicarFormatoSeleccionado() {
        String font = (String) fontCombo.getSelectedItem();
        int size = (Integer) sizeSpinner.getValue();
        Color color = defaultColor;
        int style = styleComboToInt();
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StyleConstants.setFontFamily(sas, font);
        StyleConstants.setFontSize(sas, size);
        StyleConstants.setBold(sas, (style & Font.BOLD) != 0);
        StyleConstants.setItalic(sas, (style & Font.ITALIC) != 0);
        StyleConstants.setForeground(sas, color);
        if (start == end) {
            textPane.setCharacterAttributes(sas, false);
            return;
        }
        doc.setCharacterAttributes(start, end - start, sas, false);
    }

    private void guardarCambios() {
        try {
            StyledDocument doc = textPane.getStyledDocument();
            String fullText = doc.getText(0, doc.getLength());
            defaultDoc.setText(fullText);
            defaultDoc.limpiarFormato();
            if (fullText.length() == 0) {
                return;
            }
            int posicion = 0;
            while (posicion < fullText.length()) {
                Element e = doc.getCharacterElement(posicion);
                AttributeSet as = e.getAttributes();
                String font = StyleConstants.getFontFamily(as);
                int size = StyleConstants.getFontSize(as);
                Boolean isBold = StyleConstants.isBold(as);
                Boolean isItalic = StyleConstants.isItalic(as);
                int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
                Color col = StyleConstants.getForeground(as);
                String colorHex = FormatoTexto.colorToHex(col);
                int runEnd = e.getEndOffset();
                if (runEnd > fullText.length()) {
                    runEnd = fullText.length();
                }
                FormatoTexto ft = new FormatoTexto(posicion, runEnd, font, size, style, colorHex);
                defaultDoc.AgregarFormato(ft);
                posicion = runEnd;
            }
            defaultDoc.normalizeFormato();
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarEditor() {
        textPane.setText(defaultDoc.getTexto());
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet def = new SimpleAttributeSet();
        StyleConstants.setFontFamily(def, defaultDoc.getDefaultFont());
        StyleConstants.setFontSize(def, defaultDoc.getDefaultSize());
        StyleConstants.setBold(def, (defaultDoc.getDefaultStyle() & Font.BOLD) != 0);
        StyleConstants.setItalic(def, (defaultDoc.getDefaultStyle() & Font.ITALIC) != 0);
        StyleConstants.setForeground(def, FormatoTexto.hexToColor(defaultDoc.getDefaultColor()));
        doc.setCharacterAttributes(0, doc.getLength(), def, true);
        for (FormatoTexto ft : defaultDoc.getFormato()) {
            if (ft.inicio < 0) {
                ft.inicio = 0;
            }
            if (ft.fin > doc.getLength()) {
                ft.fin = doc.getLength();
            }
            if (ft.inicio >= ft.fin) {
                continue;
            }
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setFontFamily(sas, ft.fontFamily);
            StyleConstants.setFontSize(sas, ft.fontSize);
            StyleConstants.setBold(sas, (ft.fontStyle & Font.BOLD) != 0);
            StyleConstants.setItalic(sas, (ft.fontStyle & Font.ITALIC) != 0);
            StyleConstants.setForeground(sas, FormatoTexto.hexToColor(ft.color));
            doc.setCharacterAttributes(ft.inicio, ft.fin - ft.inicio, sas, false);
        }
    }
}
