package MiniWindows;

import EditorTexto.EditorGUI;
import Sistema.MiniWindowsClass;
import Sistema.SistemaArchivos;
import Modelo.Archivo;
import Modelo.Usuario;
import Excepciones.*;
import ReproductorMusical.GUIReproductorMusica;
import VisorImagenes.GUIVisorImagenes;
import VisorImagenes.VisorImagenes;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.io.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Calendar;

/**
 *
 * @author najma
 */
public class NavegadorArchivos extends JFrame {

    private MiniWindowsClass sistema;
    private SistemaArchivos sistemaArchivos;
    private Usuario usuarioActual;
    private EditorGUI editorTextoInstancia = null;
    private GUIReproductorMusica reproductorInstancia = null;

    private JTree arbolArchivos;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode nodoRaiz;
    private JTable tablaArchivos;
    private DefaultTableModel modeloTabla;
    private JLabel lblRutaActual;
    private JComboBox<String> comboOrden;
    private JLabel lblInfoEstado;
    private JToolBar barraHerramientas;
    private JButton btnNuevaCarpeta;
    private JButton btnSubirArchivo;
    private JButton btnEliminar;
    private JButton btnRenombrar;
    private JButton btnActualizar;

    private ArrayList<Archivo> contenidoActual;
    private ArrayList<Archivo> archivosMostrados = new ArrayList<>();

    public NavegadorArchivos(JFrame parent, Usuario usuario, MiniWindowsClass sistema) {
        super("Navegador de Archivos - Mini-Windows");
        this.usuarioActual = usuario;
        this.sistema = sistema;
        this.sistemaArchivos = sistema.getSistemaArchivos();
        initComponents();
        configurarVentana();
        cargarArbol();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        JSplitPane splitPane = crearPanelCentral();
        add(splitPane, BorderLayout.CENTER);

        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        barraHerramientas = new JToolBar();
        barraHerramientas.setFloatable(false);
        barraHerramientas.setBackground(Color.WHITE);
        barraHerramientas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnNuevaCarpeta = crearBotonHerramienta("Nueva Carpeta");
        btnNuevaCarpeta.addActionListener(e -> crearNuevaCarpeta());
        btnSubirArchivo = crearBotonHerramienta("Subir Archivo");
        btnSubirArchivo.addActionListener(e -> subirArchivo());

        btnEliminar = crearBotonHerramienta("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnRenombrar = crearBotonHerramienta("Renombrar");
        btnRenombrar.addActionListener(e -> renombrarSeleccionado());
        btnActualizar = crearBotonHerramienta("Actualizar");

        btnActualizar.addActionListener(e -> actualizarVista());

        barraHerramientas.add(btnNuevaCarpeta);
        barraHerramientas.add(btnSubirArchivo);
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnEliminar);
        barraHerramientas.add(btnRenombrar);
        barraHerramientas.addSeparator();
        barraHerramientas.add(btnActualizar);

        JPanel panelRuta = new JPanel(new BorderLayout());
        panelRuta.setBackground(Color.WHITE);
        panelRuta.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lblRuta = new JLabel("Ubicación:");
        lblRuta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRuta.setForeground(new Color(100, 100, 100));
        lblRutaActual = new JLabel(sistemaArchivos.getRutaActual());
        lblRutaActual.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRutaActual.setForeground(new Color(0, 102, 204));

        JPanel panelRutaTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelRutaTexto.setBackground(Color.WHITE);
        panelRutaTexto.add(lblRuta);
        panelRutaTexto.add(lblRutaActual);

        JPanel panelOrden = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelOrden.setBackground(Color.WHITE);
        JLabel lblOrden = new JLabel("Ordenar por:");
        lblOrden.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        String[] opciones = {"Nombre", "Fecha", "Tipo", "Tamaño"};
        comboOrden = new JComboBox<>(opciones);
        comboOrden.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        comboOrden.addActionListener(e -> ordenarTabla());

        JButton btnAscendente = new JButton("A-Z");
        btnAscendente.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnAscendente.setPreferredSize(new Dimension(45, 25));
        btnAscendente.setFocusPainted(false);
        btnAscendente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAscendente.setToolTipText("Orden Ascendente");
        btnAscendente.setBackground(Color.WHITE);
        btnAscendente.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        btnAscendente.addActionListener(e -> ordenarTabla(true));

        JButton btnDescendente = new JButton("Z-A");
        btnDescendente.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnDescendente.setPreferredSize(new Dimension(45, 25));
        btnDescendente.setFocusPainted(false);
        btnDescendente.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDescendente.setToolTipText("Orden Descendente");
        btnDescendente.setBackground(Color.WHITE);
        btnDescendente.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        btnDescendente.addActionListener(e -> ordenarTabla(false));

        panelOrden.add(lblOrden);
        panelOrden.add(comboOrden);
        panelOrden.add(btnAscendente);
        panelOrden.add(btnDescendente);
        panelRuta.add(panelRutaTexto, BorderLayout.WEST);
        panelRuta.add(panelOrden, BorderLayout.EAST);
        panel.add(barraHerramientas, BorderLayout.NORTH);
        panel.add(panelRuta, BorderLayout.SOUTH);

        return panel;
    }

    private JButton crearBotonHerramienta(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(230, 240, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 215), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        return btn;
    }

    private JSplitPane crearPanelCentral() {
        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.setBackground(Color.WHITE);
        nodoRaiz = new DefaultMutableTreeNode("Cargando...");
        modeloArbol = new DefaultTreeModel(nodoRaiz);
        arbolArchivos = new JTree(modeloArbol);
        arbolArchivos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        arbolArchivos.setShowsRootHandles(true);
        arbolArchivos.setCellRenderer(new RenderizadorArbol());
        arbolArchivos.setBackground(Color.WHITE);
        arbolArchivos.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolArchivos.getLastSelectedPathComponent();
            if (nodo != null && nodo.getUserObject() instanceof Archivo) {
                Archivo archivo = (Archivo) nodo.getUserObject();
                if (archivo.isEsCarpeta()) {
                    navegarACarpeta(archivo);
                }
            }
        });

        JScrollPane scrollArbol = new JScrollPane(arbolArchivos);
        scrollArbol.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
                "Carpetas",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 11)
        ));

        scrollArbol.setBackground(Color.WHITE);
        panelArbol.add(scrollArbol, BorderLayout.CENTER);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBackground(Color.WHITE);
        String[] columnas = {"Nombre", "Tipo", "Tamaño", "Fecha Modificación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaArchivos = new JTable(modeloTabla);
        tablaArchivos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaArchivos.setRowHeight(24);
        tablaArchivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaArchivos.setShowVerticalLines(false);
        tablaArchivos.setGridColor(new Color(240, 240, 240));
        tablaArchivos.setSelectionBackground(new Color(230, 240, 255));
        tablaArchivos.setSelectionForeground(Color.BLACK);
        tablaArchivos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private ImageIcon iconoCarpeta = crearIconoCarpetaTabla();

            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String texto = value.toString();
                if (texto.startsWith("\u25B6 ")) {
                    setText(texto.substring(2).trim());
                    setIcon(iconoCarpeta);
                } else {
                    setText(texto);
                    setIcon(null);
                }
                return this;
            }
        });

        JTableHeader header = tablaArchivos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        tablaArchivos.getColumnModel().getColumn(0).setPreferredWidth(250);
        tablaArchivos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaArchivos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaArchivos.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaArchivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSeleccionado();
                }
            }
        });

        JPopupMenu menuContextual = crearMenuContextual();
        tablaArchivos.setComponentPopupMenu(menuContextual);
        arbolArchivos.setComponentPopupMenu(menuContextual);
        JScrollPane scrollTabla = new JScrollPane(tablaArchivos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "Contenido",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 11)
        ));

        scrollTabla.setBackground(Color.WHITE);
        panelTabla.add(scrollTabla, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelArbol, panelTabla);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        return splitPane;
    }

    private JPopupMenu crearMenuContextual() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JMenuItem itemNuevaCarpeta = new JMenuItem("Nueva Carpeta");
        itemNuevaCarpeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemNuevaCarpeta.addActionListener(e -> crearNuevaCarpeta());

        JMenuItem itemSubirArchivo = new JMenuItem("Subir Archivo");
        itemSubirArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemSubirArchivo.addActionListener(e -> subirArchivo());

        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        itemEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemEliminar.addActionListener(e -> eliminarSeleccionado());

        JMenuItem itemRenombrar = new JMenuItem("Renombrar");
        itemRenombrar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemRenombrar.addActionListener(e -> renombrarSeleccionado());

        menu.add(itemNuevaCarpeta);
        menu.add(itemSubirArchivo);
        menu.addSeparator();
        menu.add(itemRenombrar);
        menu.add(itemEliminar);
        return menu;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        lblInfoEstado = new JLabel("Usuario: " + usuarioActual.getUsername() + " | 0 elementos");
        lblInfoEstado.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblInfoEstado.setForeground(new Color(100, 100, 100));
        panel.add(lblInfoEstado);
        return panel;
    }

    private void configurarVentana() {
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private ImageIcon crearIconoCarpetaTabla() {
        int ancho = 16;
        int alto = 16;
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 193, 7));
        g2d.fillRect(1, 4, 6, 2);
        g2d.fillRect(1, 6, 14, 8);
        g2d.setColor(new Color(230, 170, 0));
        g2d.drawRect(1, 4, 6, 2);
        g2d.drawRect(1, 6, 14, 8);
        g2d.setColor(new Color(255, 220, 100));
        g2d.drawLine(2, 7, 13, 7);
        g2d.dispose();
        return new ImageIcon(imagen);
    }

    private boolean esAdmin() {
        try {
            return usuarioActual != null && "admin".equalsIgnoreCase(usuarioActual.getUsername());
        } catch (Exception e) {
            return false;
        }
    }

    private void cargarArbol() {
        DefaultMutableTreeNode raizNode = new DefaultMutableTreeNode("Z:");
        modeloArbol.setRoot(raizNode);
        construirArbolRecursivo(raizNode, "");

        String user = usuarioActual.getUsername().toLowerCase();  
        String rutaInicial = "Z:\\" + user;

        lblRutaActual.setText(rutaInicial);

        arbolArchivos.expandRow(0);

        try {
            sistemaArchivos.navegarARuta(rutaInicial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cargarTabla();
    }

    private void construirArbolRecursivo(DefaultMutableTreeNode nodo, String ruta) {
        String rutaNorm = normalizeRuta(ruta);
        ArrayList<Archivo> hijos = sistemaArchivos.listarContenidoEnRuta(rutaNorm);
        if (hijos == null) {
            return;
        }

        for (Archivo fi : hijos) {
            if (!fi.isEsCarpeta()) {
                continue;
            }

            if (!esAdmin() && (rutaNorm == null || rutaNorm.isEmpty())) {
                String usuario = (usuarioActual != null) ? usuarioActual.getUsername() : "";
                if (!fi.getNombre().equalsIgnoreCase(usuario)) {
                    continue;
                }
            }

            DefaultMutableTreeNode child = new DefaultMutableTreeNode(fi);
            nodo.add(child);
            String nuevaRuta = rutaNorm.isEmpty() ? fi.getNombre() : rutaNorm + File.separator + fi.getNombre();
            construirArbolRecursivo(child, nuevaRuta);
        }
    }

    private void navegarACarpeta(Archivo carpeta) {
        if (carpeta == null) {
            return;
        }

        try {
            String rutaRel = normalizeRuta(carpeta.getRutaRelativa());
            if (rutaRel.isEmpty()) {
                rutaRel = normalizeRuta(carpeta.getNombre());
            }

            String rutaAUsar;
            if (esAdmin()) {
                rutaAUsar = "Z:\\" + rutaRel;
            } else {
                String usuario = (usuarioActual != null) ? usuarioActual.getUsername() : "";

                if (rutaRel.toLowerCase().startsWith(usuario.toLowerCase())) {
                    rutaAUsar = "Z:\\" + rutaRel;
                } else {
                    rutaAUsar = "Z:\\" + usuario + (rutaRel.isEmpty() ? "" : File.separator + rutaRel);
                }
            }

            sistemaArchivos.navegarARuta(rutaAUsar);
            String rutaSis = sistemaArchivos.getRutaActual();
            if (rutaSis == null || rutaSis.trim().isEmpty()) {
                lblRutaActual.setText(rutaAUsar);
            } else {
                if (!rutaSis.toLowerCase().startsWith("z:\\")) {
                    lblRutaActual.setText("Z:\\" + rutaSis.replaceFirst("^\\\\+", ""));
                } else {
                    lblRutaActual.setText(rutaSis);
                }
            }
            cargarTabla();
        } catch (ArchivoNoValidoException e) {
            mostrarError("Error al navegar", e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al navegar", ex.getMessage());
        }

    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        archivosMostrados.clear();

        if (esAdmin()) {
            contenidoActual = sistemaArchivos.listarContenido();
            lblRutaActual.setText(sistemaArchivos.getRutaActual());
        } else {
            String userFolder = usuarioActual != null ? usuarioActual.getUsername() : "";
            String rutaSis = "";
            try {
                rutaSis = sistemaArchivos.getRutaActual();
            } catch (Exception ignored) {
            }
            if (rutaSis != null && rutaSis.toLowerCase().startsWith(("z:\\" + userFolder).toLowerCase())) {
                contenidoActual = sistemaArchivos.listarContenido();
                lblRutaActual.setText(rutaSis);
            } else {
                contenidoActual = sistemaArchivos.listarContenidoEnRuta(userFolder);
                lblRutaActual.setText("Z:");
            }
            if (contenidoActual == null) {
                contenidoActual = new ArrayList<>();
            }
        }

        for (Archivo fi : contenidoActual) {
            if (fi.isEsCarpeta()) {
                Object[] fila = new Object[]{"\u25B6 " + fi.getNombre(), "Carpeta de archivos", "", formatearFecha(fi.getFechaModificacion())};
                modeloTabla.addRow(fila);
                archivosMostrados.add(fi);
                continue;
            }

            String ext = obtenerExtension(fi.getNombre()).toLowerCase();
            boolean esTxt = ext.equals("txt");
            boolean esImagen = ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png");
            boolean esAudio = ext.equals("mp3");
            if (!(esTxt || esImagen || esAudio)) {
                continue;
            }

            String tipo;
            if (esImagen) {
                tipo = "Imagen";
            } else if (esAudio) {
                tipo = "Audio";
            } else {
                tipo = "Documento de texto";
            }

            Object[] fila = new Object[]{fi.getNombre(), tipo, formatearTamanio(fi.getTamanio()), formatearFecha(fi.getFechaModificacion())};
            modeloTabla.addRow(fila);
            archivosMostrados.add(fi);
        }
        lblInfoEstado.setText(
                "Usuario: " + usuarioActual.getUsername() + " | " + archivosMostrados.size() + " elemento(s)"
        );
    }

    private void ordenarTabla() {
        ordenarTabla(true);
    }

    private void ordenarTabla(boolean ascendente) {
        String opcion = (String) comboOrden.getSelectedItem();
        ArrayList<Archivo> lista;

        switch (opcion) {
            case "Nombre" ->
                lista = sistemaArchivos.listarOrdenadoPorNombre(ascendente);
            case "Fecha" ->
                lista = sistemaArchivos.listarOrdenadoPorFecha(ascendente);
            case "Tipo" ->
                lista = sistemaArchivos.listarOrdenadoPorTipo(ascendente);
            case "Tamaño" ->
                lista = sistemaArchivos.listarOrdenadoPorTamanio(ascendente);
            default ->
                lista = sistemaArchivos.listarContenido();
        }

        contenidoActual = lista != null ? lista : new ArrayList<>();
        modeloTabla.setRowCount(0);
        archivosMostrados.clear();

        for (Archivo fi : contenidoActual) {

            if (fi.isEsCarpeta()) {
                Object[] fila = new Object[]{ "\u25B6 " + fi.getNombre(), "Carpeta de archivos", "",
                    formatearFecha(fi.getFechaModificacion())
                };
                
                modeloTabla.addRow(fila);
                archivosMostrados.add(fi);
                continue;
            }

            String ext = obtenerExtension(fi.getNombre()).toLowerCase();

            boolean esTxt = ext.equals("txt");
            boolean esImg = ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png");
            boolean esAudio = ext.equals("mp3");

            if (!(esTxt || esImg || esAudio)) {
                continue;
            }

            String tipo;
            if (esImg) {
                tipo = "Imagen";
            } else if (esAudio) {
                tipo = "Audio";
            } else {
                tipo = "Documento de texto";
            }

            Object[] fila = new Object[]{
                fi.getNombre(),
                tipo,
                formatearTamanio(fi.getTamanio()),
                formatearFecha(fi.getFechaModificacion())
            };

            modeloTabla.addRow(fila);
            archivosMostrados.add(fi);
        }

        lblInfoEstado.setText(
                "Usuario: " + usuarioActual.getUsername() + " | " + archivosMostrados.size() + " elemento(s)"
        );
    }

    private void crearNuevaCarpeta() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva carpeta:", "Nueva Carpeta", JOptionPane.PLAIN_MESSAGE);
        if (nombre != null && !nombre.trim().isEmpty()) {
            try {
                sistemaArchivos.crearCarpeta(nombre.trim());
                actualizarVista();
                mostrarExito("Carpeta creada exitosamente");
            } catch (ArchivoNoValidoException e) {
                mostrarError("Error al crear carpeta", e.getMessage());
            }
        }
    }

    private void subirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivoSeleccionado = fileChooser.getSelectedFile();
        if (archivoSeleccionado == null || !archivoSeleccionado.exists() || !archivoSeleccionado.isFile()) {
            mostrarError("Error", "Archivo seleccionado inválido");
            return;
        }
        try {
            File destinoDir = sistemaArchivos.getDirectorioActualFisico();
            if (destinoDir == null) {
                mostrarError("Error", "Directorio destino inválido");
                return;
            }
            
            if (!destinoDir.exists() && !destinoDir.mkdirs()) {
                mostrarError("Error", "No se pudo crear la carpeta destino: " + destinoDir.getAbsolutePath());
                return;
            }
            
            File destino = new File(destinoDir, archivoSeleccionado.getName());
            try {
                if (archivoSeleccionado.getCanonicalPath().equals(destino.getCanonicalPath())) {
                    mostrarError("Error", "El archivo seleccionado ya está en la carpeta actual.");
                    return;
                }
            } catch (IOException io) {}
            
            if (destino.exists()) {
                mostrarError("Error", "El archivo '" + destino.getName() + "' ya existe en la carpeta actual.");
                return;
            }
            
            try {
                Files.copy(archivoSeleccionado.toPath(), destino.toPath(), java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
            } catch (FileAlreadyExistsException faee) {
                mostrarError("Error", "El archivo destino ya existe.");
                return;
            } catch (IOException io) {
                mostrarError("Error al copiar archivo", io.getMessage());
                io.printStackTrace();
                return;
            }
            
            try {
                destino.setLastModified(archivoSeleccionado.lastModified());
            } catch (SecurityException se) {
            }
            actualizarVista();
            mostrarExito("Archivo '" + destino.getName() + "' subido exitosamente");
        } catch (Exception e) {
            mostrarError("Error al subir archivo", e.getMessage());
            e.printStackTrace();
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
        }
        return "";
    }

    private void eliminarSeleccionado() {
        int filaSeleccionada = tablaArchivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Seleccione un elemento para eliminar");
            return;
        }

        if (filaSeleccionada >= contenidoActual.size()) {
            return;
        }

        Archivo archivo = contenidoActual.get(filaSeleccionada);
        String nombre = archivo.getNombre();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar '" + nombre + "'?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                sistemaArchivos.eliminar(nombre);
                actualizarVista();
                mostrarExito("Elemento eliminado exitosamente");
            } catch (ArchivoNoValidoException e) {
                if (e.getMessage() != null && e.getMessage().contains("no está vacía")) {
                    int r = JOptionPane.showConfirmDialog(this, "La carpeta no está vacía. ¿Eliminar recursivamente?",
                            "Eliminar recursivamente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (r == JOptionPane.YES_OPTION) {
                        try {
                            sistemaArchivos.eliminar(nombre, true);
                            actualizarVista();
                            mostrarExito("Elemento eliminado exitosamente");
                        } catch (ArchivoNoValidoException ex) {
                            mostrarError("Error al eliminar", ex.getMessage());
                        }
                    }
                } else {
                    mostrarError("Error al eliminar", e.getMessage());
                }
            }
        }
    }

    private void renombrarSeleccionado() {
        int filaSeleccionada = tablaArchivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            mostrarAdvertencia("Seleccione un elemento para renombrar");
            return;
        }

        if (filaSeleccionada >= archivosMostrados.size()) {
            return;
        }

        Archivo archivo = archivosMostrados.get(filaSeleccionada);
        if (archivo == null) {
            return;
        }

        String nombreActual = archivo.getNombre();
        if (archivo.isEsCarpeta()) {
            String nombreNuevo = JOptionPane.showInputDialog(this, "Nuevo nombre:", nombreActual);
            if (nombreNuevo != null) {
                nombreNuevo = nombreNuevo.trim();
                if (!nombreNuevo.isEmpty() && !nombreNuevo.equals(nombreActual)) {
                    try {
                        sistemaArchivos.renombrar(nombreActual, nombreNuevo);
                        actualizarVista();
                        mostrarExito("Elemento renombrado exitosamente");
                    } catch (ArchivoNoValidoException e) {
                        mostrarError("Error al renombrar", e.getMessage());
                    }
                }
            }
            return;
        }

        String ext = obtenerExtension(nombreActual);
        String baseActual = nombreActual;
        if (!ext.isEmpty()) {
            baseActual = nombreActual.substring(0, nombreActual.length() - ext.length() - 1); 
        }

        String entrada = (String) JOptionPane.showInputDialog(
                this, "Nuevo nombre:", "Renombrar archivo", JOptionPane.PLAIN_MESSAGE,
                null, null, baseActual);

        if (entrada == null) {
            return;
        }

        String nombreNuevo = entrada.trim();
        if (nombreNuevo.isEmpty()) {
            mostrarAdvertencia("El nombre no puede estar vacío.");
            return;
        }

        int punto = nombreNuevo.lastIndexOf('.');
        if (punto > 0) {
            nombreNuevo = nombreNuevo.substring(0, punto);
        }

        String nombreFinal = nombreNuevo + (ext.isEmpty() ? "" : "." + ext);
        if (nombreFinal.equals(nombreActual)) {
            return;
        }

        try {
            sistemaArchivos.renombrar(nombreActual, nombreFinal);

            if ("txt".equalsIgnoreCase(ext)) {
                try {
                    File dir = sistemaArchivos.getDirectorioActualFisico();
                    if (dir != null && dir.exists()) {
                        File oldHtml = new File(dir, baseActual + ".html");
                        File newHtml = new File(dir, nombreNuevo + ".html");

                        if (oldHtml.exists() && oldHtml.isFile()) {
                            try {
                                try {
                                    Files.move(oldHtml.toPath(), newHtml.toPath(), StandardCopyOption.ATOMIC_MOVE);
                                } catch (AtomicMoveNotSupportedException ex) {
                                    Files.move(oldHtml.toPath(), newHtml.toPath());
                                }
                            } catch (Exception moveEx) {
                                moveEx.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            actualizarVista();
            mostrarExito("Archivo renombrado exitosamente");

        } catch (ArchivoNoValidoException e) {
            mostrarError("Error al renombrar", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error al renombrar", e.getMessage());
        }
    }

    private void abrirSeleccionado() {
        int filaSeleccionada = tablaArchivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            return;
        }

        if (filaSeleccionada >= contenidoActual.size()) {
            return;
        }

        Archivo archivo = archivosMostrados.get(filaSeleccionada);

        if (archivo == null) {
            return;
        }

        if (archivo.isEsCarpeta()) {
            navegarACarpeta(archivo);
            return;
        }

        String rutaAbsoluta = archivo.getRutaAbsoluta();
        if (rutaAbsoluta == null || rutaAbsoluta.trim().isEmpty()) {
            File dirActual = sistemaArchivos.getDirectorioActualFisico();
            if (dirActual != null) {
                File posible = new File(dirActual, archivo.getNombre());
                rutaAbsoluta = posible.getAbsolutePath();
            }
        }

        if (rutaAbsoluta == null || rutaAbsoluta.trim().isEmpty()) {
            mostrarError("Error", "No se pudo determinar la ruta del archivo");
            return;
        }

        File archivoFisico = new File(rutaAbsoluta);
        if (!archivoFisico.exists() || !archivoFisico.isFile()) {
            mostrarError("Error", "El archivo no existe: " + archivo.getNombre());
            return;
        }

        String ext = obtenerExtension(archivo.getNombre()).toLowerCase();
        try {
            VisorImagenes comprobador = new VisorImagenes(sistemaArchivos, 0);
            if (comprobador.esImagen(archivo)) {
                GUIVisorImagenes visor = new GUIVisorImagenes(archivoFisico);
                visor.setVisible(true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ("txt".equalsIgnoreCase(ext)) {
            try {
                if (editorTextoInstancia == null || !editorTextoInstancia.isVisible()) {
                    editorTextoInstancia = new EditorTexto.EditorGUI(this.sistema);
                }
                editorTextoInstancia.openFile(archivoFisico);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarError("Error al abrir en editor", ex.getMessage());
            }
            return;
        }

        if (ext.equals("mp3")) {
            try {
                if (reproductorInstancia == null || !reproductorInstancia.isVisible()) {
                    reproductorInstancia = new ReproductorMusical.GUIReproductorMusica();
                }
                reproductorInstancia.reproducirDesdeArchivo(archivoFisico);
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarError("Error al abrir en reproductor", ex.getMessage());
            }
            return;
        }

        mostrarError("No hay aplicación interna", "No hay una aplicación interna registrada para abrir archivos de tipo: "
                + (ext.isEmpty() ? "(sin extensión)" : ext.toUpperCase()));
    }

    private void actualizarVista() {
        try {
            cargarArbol();
            if (sistema != null) {
                sistema.guardarSistema();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAdvertencia("Advertencia: no se pudo guardar el sistema: " + e.getMessage());
        }
    }

    private String formatearTamanio(long bytes) {
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

    private String formatearFecha(Calendar cal) {
        return String.format("%02d/%02d/%04d %02d:%02d",
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));
    }

    private String normalizeRuta(String ruta) {
        if (ruta == null) {
            return "";
        }
        String r = ruta.trim();
        r = r.replace('/', File.separatorChar).replace('\\', File.separatorChar);

        if (r.toLowerCase().startsWith("z:")) {
            r = r.length() > 2 ? r.substring(2) : "";
        }

        while (r.startsWith(File.separator)) {
            r = r.substring(1);
        }
        return r;
    }

    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private class RenderizadorArbol implements TreeCellRenderer {

        private JLabel label;
        private ImageIcon iconoCarpetaCerrada;
        private ImageIcon iconoCarpetaAbierta;

        public RenderizadorArbol() {
            label = new JLabel();
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            iconoCarpetaCerrada = crearIconoCarpeta(false);
            iconoCarpetaAbierta = crearIconoCarpeta(true);
        }

        private ImageIcon crearIconoCarpeta(boolean abierta) {
            int ancho = 16;
            int alto = 16;
            BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imagen.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (abierta) {
                g2d.setColor(new Color(255, 193, 7));
                g2d.fillRect(1, 3, 6, 3);
                int[] xPoints = {1, 15, 14, 2};
                int[] yPoints = {6, 6, 14, 14};
                g2d.fillPolygon(xPoints, yPoints, 4);
                g2d.setColor(new Color(230, 170, 0));
                g2d.drawPolygon(xPoints, yPoints, 4);
                g2d.drawRect(1, 3, 6, 3);
                g2d.setColor(new Color(245, 200, 50));
                g2d.drawLine(2, 7, 14, 7);
            } else {
                g2d.setColor(new Color(255, 193, 7));
                g2d.fillRect(1, 4, 6, 2);
                g2d.fillRect(1, 6, 14, 8);
                g2d.setColor(new Color(230, 170, 0));
                g2d.drawRect(1, 4, 6, 2);
                g2d.drawRect(1, 6, 14, 8);
                g2d.setColor(new Color(255, 220, 100));
                g2d.drawLine(2, 7, 13, 7);
            }

            g2d.dispose();
            return new ImageIcon(imagen);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            String textoNodo = "";
            ImageIcon icono = null;
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) value;
                Object obj = nodo.getUserObject();

                if (obj instanceof Archivo) {
                    Archivo archivo = (Archivo) obj;
                    textoNodo = archivo.getNombre();
                    if (archivo.isEsCarpeta()) {
                        icono = expanded ? iconoCarpetaAbierta : iconoCarpetaCerrada;
                    }
                } else {
                    textoNodo = obj.toString();
                }
            } else {
                textoNodo = value.toString();
            }
            label.setText(textoNodo);
            label.setIcon(icono);

            if (selected) {
                label.setBackground(new Color(230, 240, 255));
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            return label;
        }
    }
}
