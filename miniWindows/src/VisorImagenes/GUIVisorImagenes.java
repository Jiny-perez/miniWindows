package VisorImagenes;

import Sistema.SistemaArchivos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 *
 * @author najma
 */
public class GUIVisorImagenes extends JFrame {
    
    private final VisorImagenes visor;

    private JLabel lblImagen;
    private JScrollPane scrollPane;
    private JLabel lblNombreArchivo;
    private JLabel lblInfoImagen;
    private JLabel lblPosicion;

    private JButton btnAnterior;
    private JButton btnSiguiente;

    private JLabel lblZoomIn;
    private JLabel lblZoomOut;
    private JLabel lblAjustar;
    private JLabel lblTamañoReal;
    private JLabel lblRotarIzq;
    private JLabel lblRotarDer;

    private boolean ajusteAutomatico = true;

    public GUIVisorImagenes(File archivoInicial) {
        super("Visor de Imágenes - Mini-Windows");
        this.visor = new VisorImagenes(archivoInicial);

        inicializarComponentes();
        configurarVentana();

        if (visor.getTotalImagenes() > 0) {
            visor.cargarImagen(visor.getIndiceActual());
            actualizarVista();

            // Auto-ajustar al abrir
            SwingUtilities.invokeLater(this::ajustarPantalla);
        }
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 25, 25));

        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);

        JPanel panelHerramientas = crearPanelHerramientas();
        add(panelHerramientas, BorderLayout.EAST);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
        panel.setPreferredSize(new Dimension(0, 60));

        lblNombreArchivo = new JLabel("Sin imagen");
        lblNombreArchivo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombreArchivo.setForeground(Color.WHITE);
        lblNombreArchivo.setHorizontalAlignment(SwingConstants.CENTER);
        lblNombreArchivo.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

        lblInfoImagen = new JLabel(" ");
        lblInfoImagen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfoImagen.setForeground(new Color(180, 180, 180));
        lblInfoImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfoImagen.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        panel.add(lblNombreArchivo, BorderLayout.NORTH);
        panel.add(lblInfoImagen, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 25, 25));

        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setVerticalAlignment(SwingConstants.CENTER);

        scrollPane = new JScrollPane(lblImagen);
        scrollPane.setBackground(new Color(25, 25, 25));
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)));
        panel.setPreferredSize(new Dimension(0, 65));

        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelNavegacion.setBackground(new Color(35, 35, 35));

        btnAnterior = crearBotonNavegacion("Anterior");
        btnAnterior.addActionListener(e -> imagenAnterior());

        lblPosicion = new JLabel("0 / 0");
        lblPosicion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPosicion.setForeground(Color.WHITE);
        lblPosicion.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));

        btnSiguiente = crearBotonNavegacion("Siguiente");
        btnSiguiente.addActionListener(e -> imagenSiguiente());

        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(lblPosicion);
        panelNavegacion.add(btnSiguiente);

        panel.add(panelNavegacion, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHerramientas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(60, 60, 60)));
        panel.setPreferredSize(new Dimension(150, 0));

        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Sección Zoom
        JLabel lblTituloZoom = new JLabel("ZOOM");
        lblTituloZoom.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTituloZoom.setForeground(new Color(150, 150, 150));
        lblTituloZoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTituloZoom);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        lblZoomIn = crearOpcionInteractiva("    + Acercar");
        lblZoomIn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                ajusteAutomatico = false;
                zoomIn();
            }
        });
        panel.add(lblZoomIn);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblZoomOut = crearOpcionInteractiva("    − Alejar");
        lblZoomOut.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                ajusteAutomatico = false;
                zoomOut();
            }
        });
        panel.add(lblZoomOut);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblAjustar = crearOpcionInteractiva("      Ajustar");
        lblAjustar.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                ajusteAutomatico = true;
                ajustarPantalla();
            }
        });
        panel.add(lblAjustar);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblTamañoReal = crearOpcionInteractiva("    1️⃣  Real");
        lblTamañoReal.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                ajusteAutomatico = false;
                tamañoReal();
            }
        });
        panel.add(lblTamañoReal);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        JLabel lblTituloRotacion = new JLabel("ROTACIÓN");
        lblTituloRotacion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTituloRotacion.setForeground(new Color(150, 150, 150));
        lblTituloRotacion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTituloRotacion);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        lblRotarIzq = crearOpcionInteractiva("    90° Izquierda");
        lblRotarIzq.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { rotarIzquierda(); }
        });
        panel.add(lblRotarIzq);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblRotarDer = crearOpcionInteractiva("    90° Derecha");
        lblRotarDer.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { rotarDerecha(); }
        });
        panel.add(lblRotarDer);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel crearOpcionInteractiva(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(140, 32));
        lbl.setPreferredSize(new Dimension(140, 32));
        lbl.setOpaque(false);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { lbl.setForeground(new Color(100, 180, 255)); }
            @Override public void mouseExited(MouseEvent e)  { lbl.setForeground(new Color(200, 200, 200)); }
        });

        return lbl;
    }

    private JButton crearBotonNavegacion(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 120, 215));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(0, 95, 184));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 120, 215));
            }
        });

        return btn;
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                if (ajusteAutomatico) SwingUtilities.invokeLater(() -> ajustarPantalla());
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:  imagenAnterior(); break;
                    case KeyEvent.VK_RIGHT: imagenSiguiente(); break;
                    case KeyEvent.VK_PLUS:
                    case KeyEvent.VK_ADD:
                        ajusteAutomatico = false; zoomIn(); break;
                    case KeyEvent.VK_MINUS:
                    case KeyEvent.VK_SUBTRACT:
                        ajusteAutomatico = false; zoomOut(); break;
                    case KeyEvent.VK_0:
                        ajusteAutomatico = false; tamañoReal(); break;
                    case KeyEvent.VK_F:
                        ajusteAutomatico = true; ajustarPantalla(); break;
                }
            }
        });

        setFocusable(true);
    }

    private void imagenAnterior() {
        if (visor.imagenAnterior()) {
            actualizarVista();
            if (ajusteAutomatico) SwingUtilities.invokeLater(() -> ajustarPantalla());
        }
    }

    private void imagenSiguiente() {
        if (visor.imagenSiguiente()) {
            actualizarVista();
            if (ajusteAutomatico) SwingUtilities.invokeLater(() -> ajustarPantalla());
        }
    }

    private void zoomIn() {
        visor.zoomIn();
        actualizarImagen();
        actualizarInformacion();
    }

    private void zoomOut() {
        visor.zoomOut();
        actualizarImagen();
        actualizarInformacion();
    }

    private void tamañoReal() {
        visor.tamañoReal();
        actualizarImagen();
        actualizarInformacion();
    }

    private void ajustarPantalla() {
        int anchoVentana = Math.max(1, scrollPane.getViewport().getWidth() - 40);
        int altoVentana  = Math.max(1, scrollPane.getViewport().getHeight() - 40);

        visor.ajustarPantalla(anchoVentana, altoVentana);
        actualizarImagen();
        actualizarInformacion();
    }

    private void rotarIzquierda() {
        visor.rotarIzquierda();
        actualizarImagen();
        if (ajusteAutomatico) SwingUtilities.invokeLater(() -> ajustarPantalla());
    }

    private void rotarDerecha() {
        visor.rotarDerecha();
        actualizarImagen();
        if (ajusteAutomatico) SwingUtilities.invokeLater(() -> ajustarPantalla());
    }

    private void actualizarVista() {
        actualizarImagen();
        actualizarInformacion();
        actualizarBotonesNavegacion();
    }

    private void actualizarImagen() {
        if (visor.getImagenTransformada() != null) {
            lblImagen.setIcon(new ImageIcon(visor.getImagenTransformada()));
            lblImagen.revalidate();
            lblImagen.repaint();
        } else {
            lblImagen.setIcon(null);
            lblImagen.repaint();
        }
    }

    private void actualizarInformacion() {
        File archivo = visor.getArchivoActual();

        if (archivo != null) {
            lblNombreArchivo.setText(archivo.getName());

            int ancho = visor.getAnchoOriginal();
            int alto  = visor.getAltoOriginal();
            long tamanio = archivo.length();

            String info = String.format("%d × %d px  •  %s  •  Zoom: %.0f%%",
                    ancho, alto, formatearTamanio(tamanio), visor.getZoomActual() * 100.0);

            lblInfoImagen.setText(info);
            lblPosicion.setText((visor.getIndiceActual() + 1) + " / " + visor.getTotalImagenes());
        } else {
            lblNombreArchivo.setText("Sin imagen");
            lblInfoImagen.setText(" ");
            lblPosicion.setText("0 / 0");
        }
    }

    private void actualizarBotonesNavegacion() {
        btnAnterior.setEnabled(visor.hayAnterior());
        btnSiguiente.setEnabled(visor.haySiguiente());
    }

    private String formatearTamanio(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f KB", kb);
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }
}