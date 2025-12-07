package EditorTexto;

import Sistema.MiniWindowsClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import  java.util.List;

/**
 *
 * @author marye
 */
public class EditorGUI extends JFrame{

 private MiniWindowsClass sistema;
    private JPanel panelEditor;
    private JToolBar barraOpciones;
    private JComboBox<String> fontCombo, styleCombo;
    private JSpinner tamanio;
    private JButton btnColor, btnAbrir, btnGuardar, btnNuevo, btnGuardarComo;
    private JTabbedPane archivosAbiertos;
    private List<PestaniaEditor> tabs = new ArrayList<>();
    private int contandorArchivos = 1;

    public EditorGUI(MiniWindowsClass sistema) {
        this.sistema = sistema;
        initComponents();
    }

    private void initComponents() {
        setTitle("Editor de Texto");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);

        panelEditor = new JPanel(new BorderLayout());
        add(panelEditor, BorderLayout.CENTER);

        barraOpciones = new JToolBar();
        barraOpciones.setFloatable(false);
        barraOpciones.setBackground(new Color(255, 182, 193));
        barraOpciones.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        barraOpciones.setPreferredSize(new Dimension(0, 48));

        String[] nombres = {"Nuevo", "Abrir", "Guardar", "Guardar como"};
        for (String nombre : nombres) {
            JButton b = new JButton(nombre);
            b.setFocusPainted(false);
            b.setBackground(new Color(255, 210, 220));
            b.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            barraOpciones.add(b);
            switch (nombre) {
                case "Nuevo" -> btnNuevo = b;
                case "Abrir" -> btnAbrir = b;
                case "Guardar" -> btnGuardar = b;
                case "Guardar como" -> btnGuardarComo = b;
            }
        }

        barraOpciones.addSeparator(new Dimension(12, 0));

        fontCombo = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontCombo.setSelectedItem("SansSerif");
        fontCombo.setPreferredSize(new Dimension(160, 26));
        fontCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        tamanio = new JSpinner(new SpinnerNumberModel(14, 8, 72, 1));
        tamanio.setPreferredSize(new Dimension(60, 26));
        tamanio.setMaximumSize(new Dimension(60, 26));
        tamanio.setMinimumSize(new Dimension(40, 26));
        if (tamanio.getEditor() instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) tamanio.getEditor()).getTextField().setColumns(3);
        }

        styleCombo = new JComboBox<>(new String[]{"Normal", "Negrita", "Cursiva"});
        styleCombo.setSelectedIndex(0);
        styleCombo.setPreferredSize(new Dimension(160, 26));
        styleCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        barraOpciones.add(new JLabel(" Fuente: "));
        barraOpciones.add(fontCombo);
        barraOpciones.add(Box.createHorizontalStrut(8));
        barraOpciones.add(new JLabel("Tamaño:"));
        barraOpciones.add(tamanio);
        barraOpciones.add(Box.createHorizontalStrut(8));
        barraOpciones.add(new JLabel("Estilo:"));
        barraOpciones.add(styleCombo);

        barraOpciones.addSeparator(new Dimension(12, 0));

        btnColor = new JButton("Color");
        btnColor.setFocusPainted(false);
        btnColor.setBackground(new Color(255, 210, 220));
        btnColor.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        btnColor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        barraOpciones.add(btnColor);

        add(barraOpciones, BorderLayout.NORTH);

        archivosAbiertos = new JTabbedPane(JTabbedPane.BOTTOM);
        archivosAbiertos.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        archivosAbiertos.addChangeListener(e -> {
            int indice = archivosAbiertos.getSelectedIndex();
            if (indice >= 0) {
                showTabInCenter(indice);
            }
        });
       add(archivosAbiertos, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> nuevoArchivo());
        btnAbrir.addActionListener(e -> abrirArchivo());
        btnGuardar.addActionListener(e -> {
            try {
                guardarArchivo(false);
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });

        btnGuardarComo.addActionListener(e -> {
            try {
                guardarArchivo(true);
            } catch (Exception ex) {
                mostrarError(ex);
            }
        });

        btnColor.addActionListener(e -> {
            PestaniaEditor dt = getActivarPestania();
            if (dt == null) {
                return;
            }

            Color c = JColorChooser.showDialog(this, "Selecciona color", dt.getDefaultColor());
            if (c != null) {
                dt.setDefaultColor(c);
                aplicarCambioColor();
            }
        });

        fontCombo.addActionListener(e -> aplicarCambioFuente());
        tamanio.addChangeListener(e -> aplicarCambioTamano());
        styleCombo.addActionListener(e -> aplicarCambioEstilo());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (salirEditorTexto()) {
                    dispose();
                }
            }
        });

        nuevoArchivo();
        setVisible(true);
    }

    private void nuevoArchivo() {
        DocumentModel dm = new DocumentModel("");
        dm.setDefaultFont("SansSerif");
        dm.setDefaultSize(14);
        dm.setDefaultStyle(Font.PLAIN);
        dm.setDefaultColor(FormatoTexto.colorToHex(Color.BLACK));

        PestaniaEditor dt = new PestaniaEditor(dm, null);
        String title = "Nuevo " + contandorArchivos++;
        tabs.add(dt);
        archivosAbiertos.addTab(title, null);

        int indice = archivosAbiertos.getTabCount() - 1;
        archivosAbiertos.setTabComponentAt(indice, tituloPestania(title));
        setDocumentTabAtIndex(indice, dt);
        archivosAbiertos.setSelectedIndex(indice);
        showTabInCenter(indice);
        actualizarArchivos();
    }

    private void abrirArchivo() {
        JFileChooser fc = createTxtFileChooser();
        try {
            if (sistema != null && sistema.getSistemaArchivos() != null) {
                File dir = sistema.getSistemaArchivos().getDirectorioActualFisico();
                if (dir != null && dir.exists()) {
                    fc.setCurrentDirectory(dir);
                }
            }
        } catch (Exception ex) {}

        int opcion = fc.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (f != null) {
                if (!f.getName().toLowerCase().endsWith(".txt")) {
                    JOptionPane.showMessageDialog(this, "Seleccione un archivo con extensión .txt", "Archivo no permitido", JOptionPane.WARNING_MESSAGE);
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

    public void openFile(File f) throws Exception {
        DocumentModel dm = GestionTexto.cargarTxt(f);
        PestaniaEditor dt = new PestaniaEditor(dm, f);
        tabs.add(dt);
        archivosAbiertos.addTab(f.getName(), null);

        int indice = archivosAbiertos.getTabCount() - 1;
        archivosAbiertos.setTabComponentAt(indice, tituloPestania(f.getName()));
        setDocumentTabAtIndex(indice, dt);
        archivosAbiertos.setSelectedIndex(indice);
        showTabInCenter(indice);
        actualizarArchivos();
    }

    private void guardarArchivo(boolean guardado) throws Exception {
        int indice = archivosAbiertos.getSelectedIndex();
        if (indice < 0) {
            return;
        }

        PestaniaEditor dt = getNuevoArchivo(indice);
        if (dt == null) {
            return;
        }
        if (dt.getFile() == null || guardado) {
            JFileChooser fc = createTxtFileChooser();
            try {
                if (sistema != null && sistema.getSistemaArchivos() != null) {
                    File dir = sistema.getSistemaArchivos().getDirectorioActualFisico();
                    if (dir != null && dir.exists()) {
                        fc.setCurrentDirectory(dir);
                    }
                }
            } catch (Exception ex) {}

            int op = fc.showSaveDialog(this);
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
                int resp = JOptionPane.showConfirmDialog(this,
                        "El archivo ya existe. ¿Desea sobrescribirlo?",
                        "Confirmar sobrescritura",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (resp != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            dt.setFile(chosen);
            Component tc = archivosAbiertos.getTabComponentAt(indice);
            if (tc instanceof JPanel && ((JPanel) tc).getComponentCount() > 0 && ((JPanel) tc).getComponent(0) instanceof JLabel) {
                ((JLabel) ((JPanel) tc).getComponent(0)).setText(chosen.getName() + "  ");
            } else {
                archivosAbiertos.setTitleAt(indice, chosen.getName());
            }
        }
        dt.guardarCambios();
        GestionTexto.guardarTxt(dt.getFile(), dt.getModel());
        dt.setModified(false);
        actualizarArchivos();
        try {
            if (sistema != null) {
                sistema.guardarSistema();
            }
        } catch (Exception ex) {
        }
    }

    private int styleComboCase() {
        int indice = styleCombo.getSelectedIndex();
        switch (indice) {
            case 1:
                return Font.BOLD;
            case 2:
                return Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }

    private void aplicarCambioFuente() {
        PestaniaEditor dt = getActivarPestania();
        if (dt == null) {
            return;
        }
        String font = (String) fontCombo.getSelectedItem();
        int inicio = dt.textPane.getSelectionStart();
        int fin = dt.textPane.getSelectionEnd();
        if (inicio == fin && dt.lastSelEnd > dt.lastSelStart) {
            inicio = dt.lastSelStart;
            fin = dt.lastSelEnd;
        }

        if (inicio == fin) {
            dt.getModel().setDefaultFont(font);
            dt.aplicarDocumentModel();
            dt.setModified(true);
            return;
        }

        dt.getModel().setText(dt.textPane.getText());
        boolean seleccionado = dt.textPane.isFocusOwner();
        dt.getModel().aplicarFormato(inicio, fin, font, null, null, null);
        dt.aplicarDocumentModel();
        if (seleccionado) {
            dt.textPane.setSelectionStart(inicio);
            dt.textPane.setSelectionEnd(fin);
            dt.textPane.requestFocusInWindow();
        } else {
            dt.textPane.setCaretPosition(Math.min(fin, dt.textPane.getDocument().getLength()));
        }
        dt.setModified(true);
    }

    private void aplicarCambioTamano() {
        PestaniaEditor dt = getActivarPestania();
        if (dt == null) {
            return;
        }

        int size = (Integer) tamanio.getValue();
        int inicio = dt.textPane.getSelectionStart();
        int fin = dt.textPane.getSelectionEnd();
        if (inicio == fin && dt.lastSelEnd > dt.lastSelStart) {
            inicio = dt.lastSelStart;
            fin = dt.lastSelEnd;
        }

        if (inicio == fin) {
            dt.getModel().setDefaultSize(size);
            dt.aplicarDocumentModel();
            dt.setModified(true);
            return;
        }

        dt.getModel().setText(dt.textPane.getText());
        boolean seleccionado = dt.textPane.isFocusOwner();
        dt.getModel().aplicarFormato(inicio, fin, null, Integer.valueOf(size), null, null);
        final int i = inicio;
        final int f = fin;
        SwingUtilities.invokeLater(() -> {
            dt.aplicarDocumentModel();
            if (seleccionado) {
                dt.textPane.setSelectionStart(i);
                dt.textPane.setSelectionEnd(f);
                dt.textPane.requestFocusInWindow();
            } else {
                dt.textPane.setCaretPosition(Math.min(f, dt.textPane.getDocument().getLength()));
            }
            dt.textPane.repaint();
        });
        dt.setModified(true);
    }

    private void aplicarCambioEstilo() {
        PestaniaEditor dt = getActivarPestania();
        if (dt == null) {
            return;
        }

        int style = styleComboCase();
        int inicio = dt.textPane.getSelectionStart();
        int fin = dt.textPane.getSelectionEnd();
        if (inicio == fin && dt.lastSelEnd > dt.lastSelStart) {
            inicio = dt.lastSelStart;
            fin = dt.lastSelEnd;
        }

        if (inicio == fin) {
            dt.getModel().setDefaultStyle(style);
            dt.aplicarDocumentModel();
            dt.setModified(true);
            return;
        }

        dt.getModel().setText(dt.textPane.getText());
        boolean seleccionado = dt.textPane.isFocusOwner();
        dt.getModel().aplicarFormato(inicio, fin, null, null, Integer.valueOf(style), null);
        dt.aplicarDocumentModel();
        if (seleccionado) {
            dt.textPane.setSelectionStart(inicio);
            dt.textPane.setSelectionEnd(fin);
            dt.textPane.requestFocusInWindow();
        } else {
            dt.textPane.setCaretPosition(Math.min(fin, dt.textPane.getDocument().getLength()));
        }

        dt.setModified(true);
    }

    private void aplicarCambioColor() {
        PestaniaEditor dt = getActivarPestania();
        if (dt == null) {
            return;
        }

        Color color = dt.getDefaultColor();
        String colorHex = FormatoTexto.colorToHex(color);
        int inicio = dt.textPane.getSelectionStart();
        int fin = dt.textPane.getSelectionEnd();
        if (inicio == fin && dt.lastSelEnd > dt.lastSelStart) {
            inicio = dt.lastSelStart;
            fin = dt.lastSelEnd;
        }

        if (inicio == fin) {
            dt.getModel().setDefaultColor(colorHex);
            dt.aplicarDocumentModel();
            dt.setModified(true);
            return;
        }

        dt.getModel().setText(dt.textPane.getText());
        boolean seleccionado = dt.textPane.isFocusOwner();
        dt.getModel().aplicarFormato(inicio, fin, null, null, null, colorHex);
        dt.aplicarDocumentModel();
        if (seleccionado) {
            dt.textPane.setSelectionStart(inicio);
            dt.textPane.setSelectionEnd(fin);
            dt.textPane.requestFocusInWindow();
        } else {
            dt.textPane.setCaretPosition(Math.min(fin, dt.textPane.getDocument().getLength()));
        }
        dt.setModified(true);
    }

    private PestaniaEditor getNuevoArchivo(int indice) {
        if (indice < 0 || indice >= tabs.size()) {
            return null;
        }
        return tabs.get(indice);
    }

    private PestaniaEditor getActivarPestania() {
        int idx = archivosAbiertos.getSelectedIndex();
        return getNuevoArchivo(idx);
    }

    private void setDocumentTabAtIndex(int indice, PestaniaEditor dt) {
        JScrollPane sp = dt.scroll;
    }

    private void showTabInCenter(int idx) {
        panelEditor.removeAll();
        PestaniaEditor dt = getNuevoArchivo(idx);
        if (dt != null) {
            panelEditor.add(dt.scroll, BorderLayout.CENTER);
        } else {
            panelEditor.add(new JPanel(), BorderLayout.CENTER);
        }
        panelEditor.revalidate();
        panelEditor.repaint();
        actualizarArchivos();
    }

    private JPanel tituloPestania(String titulo) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl.setOpaque(false);
        JLabel lbl = new JLabel(titulo + "  ");
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JButton btnClose = new JButton("x");
        btnClose.setMargin(new Insets(2, 4, 2, 4));
        btnClose.setBorderPainted(false);
        btnClose.setOpaque(false);
        btnClose.setToolTipText("Cerrar pestaña");
        btnClose.addActionListener(e -> {
            int indice = obtenerIndiceArchivo(pnl);
            if (indice >= 0) {
                cerrarArchivosAbiertos(indice);
            }
        });
        pnl.add(lbl);
        pnl.add(btnClose);
        return pnl;
    }

    private int obtenerIndiceArchivo(Component c) {
        for (int i = 0; i < archivosAbiertos.getTabCount(); i++) {
            if (archivosAbiertos.getTabComponentAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    private void cerrarArchivosAbiertos(int indice) {
        PestaniaEditor dt = getNuevoArchivo(indice);
        if (dt != null && dt.isModified() && dt.getModel().getTexto() != null && !dt.getModel().getTexto().isEmpty()) {
            int opt = JOptionPane.showOptionDialog(this,
                    "El documento tiene cambios sin guardar. ¿Desea guardar antes de cerrar?",
                    "Guardar cambios",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{"Guardar", "No guardar", "Cancelar"},
                    "Guardar");
            if (opt == JOptionPane.CLOSED_OPTION || opt == 2) {
                return;
            }
            if (opt == 0) {
                try {
                    archivosAbiertos.setSelectedIndex(indice);
                    guardarArchivo(false);
                } catch (Exception ex) {
                    mostrarError(ex);
                    return;
                }
            }
        }
        
        tabs.remove(indice);
        archivosAbiertos.remove(indice);
        if (archivosAbiertos.getTabCount() > 0) {
            archivosAbiertos.setSelectedIndex(Math.max(0, indice - 1));
        } else {
            panelEditor.removeAll();
            panelEditor.revalidate();
            panelEditor.repaint();
        }
        actualizarArchivos();
    }

    private boolean salirEditorTexto() {
        for (PestaniaEditor dt : tabs) {
            if (dt != null && dt.isModified() && dt.getModel().getTexto() != null && !dt.getModel().getTexto().isEmpty()) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Hay pestañas con cambios sin guardar. ¿Desea salir y perder cambios no guardados?",
                        "Salir",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                return opcion == JOptionPane.NO_OPTION;
            }
        }
        return true;
    }

    private void actualizarArchivos() {
        boolean x = tabs.size() > 0;
        btnGuardar.setEnabled(x);
        btnGuardarComo.setEnabled(x);
        btnColor.setEnabled(x);
        fontCombo.setEnabled(x);
        tamanio.setEnabled(x);
        styleCombo.setEnabled(x);
    }

    private JFileChooser createTxtFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
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
            } catch (Exception e) {
                try {
                    fc.setCurrentDirectory(raizPermitida);
                } catch (Exception ignored) {
                }
            }
        }
        return fc;
    }

    private void mostrarError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }    
}
