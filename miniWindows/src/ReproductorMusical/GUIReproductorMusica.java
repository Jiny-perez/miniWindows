package ReproductorMusical;

import Sistema.MiniWindowsClass;
import Sistema.SistemaArchivos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author marye
 */
public class GUIReproductorMusica {

    ListaCanciones listaCanciones;
    Reproductor reproductor;
    JList<Cancion> JListaCanciones;
    DefaultListModel<Cancion> listModel;
    JButton btnAgregarCancion;
    JLabel lblListaVacia;

    public GUIReproductorMusica() {
        listaCanciones = new ListaCanciones();
        reproductor = new Reproductor();
        initComponents();
        cargarCanciones();
    }

    public void initComponents() {
        JFrame VReproductorMusica = new JFrame("Reproductor de Música");
        VReproductorMusica.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        VReproductorMusica.setSize(900, 700);
        VReproductorMusica.setLocationRelativeTo(null);
        VReproductorMusica.setLayout(new BorderLayout());
        VReproductorMusica.getContentPane().setBackground(new Color(18, 18, 18));
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(18, 18, 18));
        JLabel lblTitulo = new JLabel("Biblioteca de Música", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.white);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        JListaCanciones = new JList<>(listModel);
        JListaCanciones.setCellRenderer(new CancionListCellRenderer());
        JListaCanciones.setBackground(new Color(18, 18, 18));
        JListaCanciones.setForeground(Color.WHITE);
        JListaCanciones.setSelectionBackground(new Color(40, 40, 40));
        JListaCanciones.setSelectionForeground(new Color(29, 185, 84));
        JListaCanciones.setFont(new Font("Arial", Font.PLAIN, 12));
        JListaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Cancion seleccion = JListaCanciones.getSelectedValue();
                if (seleccion != null) {
                    try {
                        reproductor.cargarCancion(seleccion);
                        reproductor.play();
                        JListaCanciones.repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al reproducir: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        reproductor.setOnFinished(() -> SwingUtilities.invokeLater(() -> {
            if (JListaCanciones != null) {
                JListaCanciones.clearSelection();
            }
        }));

        JScrollPane scrollPane = new JScrollPane(JListaCanciones);
        scrollPane.getViewport().setBackground(new Color(18, 18, 18));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel panelListaContenedor = new JPanel(new CardLayout());
        panelListaContenedor.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lblListaVacia = new JLabel("No hay canciones en la carpeta Musica", SwingConstants.CENTER);
        lblListaVacia.setForeground(new Color(179, 179, 179));
        lblListaVacia.setFont(new Font("Arial", Font.ITALIC, 16));
        lblListaVacia.setBackground(new Color(18, 18, 18));
        lblListaVacia.setOpaque(true);
        panelListaContenedor.add(scrollPane, "Lista");
        panelListaContenedor.add(lblListaVacia, "Vacia");
        panelPrincipal.add(panelListaContenedor, BorderLayout.CENTER);

        btnAgregarCancion = new JButton("Agregar Cancion");
        btnAgregarCancion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregarCancion.setBackground(new Color(29, 185, 84));
        btnAgregarCancion.setForeground(Color.WHITE);
        btnAgregarCancion.setFocusPainted(false);
        btnAgregarCancion.setBorderPainted(false);
        btnAgregarCancion.setContentAreaFilled(true);
        btnAgregarCancion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregarCancion.setPreferredSize(new Dimension(890, 40));
        btnAgregarCancion.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAgregarCancion.setBackground(new Color(29, 185, 84).darker());
            }

            public void mouseExited(MouseEvent e) {
                btnAgregarCancion.setBackground(new Color(29, 185, 84));
            }
        });
        btnAgregarCancion.addActionListener(e -> agregarCancionSO());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBoton.setBackground(new Color(18, 18, 18));
        panelBoton.add(btnAgregarCancion);

        JPanel panelBarraReproduccion = new JPanel();
        panelBarraReproduccion.setLayout(new BoxLayout(panelBarraReproduccion, BoxLayout.Y_AXIS));
        panelBarraReproduccion.setBackground(new Color(18, 18, 18));
        JPanel panelReproductor = reproductor.getPanel();
        panelReproductor.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBarraReproduccion.add(panelReproductor);
        panelBarraReproduccion.add(Box.createRigidArea(new Dimension(0, 8)));
        panelBarraReproduccion.add(panelBoton);
        panelPrincipal.add(panelBarraReproduccion, BorderLayout.SOUTH);

        VReproductorMusica.add(panelPrincipal);
        VReproductorMusica.setVisible(true);
        actualizarVista();
    }

    private void agregarCancionSO() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".mp3");
            }

            public String getDescription() {
                return "MP3";
            }
        });

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File[] seleccion = chooser.getSelectedFiles();
        if (seleccion == null || seleccion.length == 0) {
            return;
        }

        MiniWindowsClass sistema = MiniWindowsClass.getInstance();
        if (sistema == null || sistema.getUsuarioActual() == null) {
            return;
        }

        String username = sistema.getUsuarioActual().getUsername();
        File dirMusica = getUserMusicDir(sistema.getSistemaArchivos(), username);
        if (!dirMusica.exists()) {
            dirMusica.mkdirs();
        }

        for (File src : seleccion) {
            try {
                File dest = new File(dirMusica, src.getName());

                if (dest.exists()) {
                    JOptionPane.showMessageDialog(null,
                            "El archivo '" + src.getName() + "' ya existe en la carpeta Música.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                java.nio.file.Files.copy(src.toPath(), dest.toPath());

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "No se pudo agregar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        cargarCanciones();
    }

    private void cargarCanciones() {
        listaCanciones = new ListaCanciones();
        MiniWindowsClass sistema = MiniWindowsClass.getInstance();
        if (sistema == null || sistema.getUsuarioActual() == null) {
            actualizarVista();
            return;
        }
        String username = sistema.getUsuarioActual().getUsername();
        File dirMusica = getUserMusicDir(sistema.getSistemaArchivos(), username);
        if (!dirMusica.exists() || !dirMusica.isDirectory()) {
            actualizarVista();
            return;
        }

        File[] mp3s = dirMusica.listFiles((d, n) -> n.toLowerCase().endsWith(".mp3"));
        if (mp3s != null) {
            for (File f : mp3s) {
                Cancion c = new Cancion(f.getName().replaceFirst("[.][^.]+$", ""), f.getAbsolutePath());
                try {
                    reproductor.cargarCancion(c);
                    if (reproductor.getCancionActual() != null) {
                        c.setDuracion(reproductor.getCancionActual().getDuracion());
                    }
                } catch (Exception e) {
                    c.setDuracion(0);
                } finally {
                    try {
                        reproductor.limpiar();
                    } catch (Exception ignored) {
                    }
                }
                listaCanciones.agregarListaCanciones(c);
            }
        }
        actualizarVista();
    }

    private void actualizarVista() {
        listModel.clear();
        if (listaCanciones != null) {
            int total = listaCanciones.tamanio();
            for (int i = 0; i < total; i++) {
                Cancion c = listaCanciones.getCancion(i);
                if (c != null) {
                    listModel.addElement(c);
                }
            }
        }
        CardLayout cl = (CardLayout) (lblListaVacia.getParent().getLayout());
        if (listModel.isEmpty()) {
            cl.show(lblListaVacia.getParent(), "Vacia");
        } else {
            cl.show(lblListaVacia.getParent(), "Lista");
        }
    }

    private File getUserMusicDir(SistemaArchivos sistemaArchivos, String username) {
        String base = System.getProperty("user.dir") + File.separator + "Z";
        File dir = new File(base, username + File.separator + "Musica");
        return dir;
    }

    private class CancionListCellRenderer extends DefaultListCellRenderer {

        private Cancion getCancionActual() {
            return reproductor != null ? reproductor.getCancionActual() : null;
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Cancion) {
                Cancion cancion = (Cancion) value;
                JPanel panel = new JPanel(new BorderLayout());
                panel.setOpaque(true);
                panel.setPreferredSize(new Dimension(0, 60));

                boolean isCurrentlyPlaying = false;
                Cancion actual = getCancionActual();
                if (actual != null && actual.equals(cancion)) {
                    isCurrentlyPlaying = true;
                }
                if (isSelected) {
                    panel.setBackground(new Color(40, 40, 40));
                } else if (isCurrentlyPlaying) {
                    panel.setBackground(new Color(25, 25, 25));
                } else {
                    panel.setBackground(new Color(18, 18, 18));
                }

                JLabel lblImgDefault = new JLabel(cancion.getImgDefault());
                lblImgDefault.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                JPanel panelInfo = new JPanel(new GridLayout(2, 1));
                panelInfo.setOpaque(false);
                JLabel lblTitulo = new JLabel(cancion.getTitulo());
                lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
                lblTitulo.setForeground((isSelected || isCurrentlyPlaying) ? new Color(29, 185, 84) : Color.WHITE);
                panelInfo.add(lblTitulo);
                JLabel lblDuracion = new JLabel(cancion.DuracionFormateada());
                lblDuracion.setForeground(new Color(179, 179, 179));
                lblDuracion.setFont(new Font("Arial", Font.PLAIN, 12));
                lblDuracion.setHorizontalAlignment(SwingConstants.RIGHT);
                lblDuracion.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
                panel.add(lblImgDefault, BorderLayout.WEST);
                panel.add(panelInfo, BorderLayout.CENTER);
                panel.add(lblDuracion, BorderLayout.EAST);
                return panel;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
