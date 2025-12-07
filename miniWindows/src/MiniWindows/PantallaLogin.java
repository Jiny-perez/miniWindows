package MiniWindows;

import Background.Background;
import Sistema.MiniWindowsClass;
import Modelo.Usuario;
import Excepciones.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 *
 * @author marye
 */
public class PantallaLogin extends JFrame {

    private MiniWindowsClass sistema;
    private JPasswordField txtPassword;
    private JLabel lblNombre, lblInfo;
    private JPanel panelPrincipal, panelOtroUsuario, panelPassword, panelInferior;
    private String selectedUsername = "";
    private AvatarPanel inicialUserG, inicialUserP;
    private JButton btnFlecha, btnLogin;
    private JLabel lblSalir;

    public PantallaLogin() {
        this.sistema = MiniWindowsClass.getInstance();
        setUndecorated(true);
        setResizable(true);    
        inicializarComponentes();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void inicializarComponentes() {
        panelPrincipal = new Background("src/Background/BgLogIn.jpg");
        panelPrincipal.setLayout(new BorderLayout());
        setContentPane(panelPrincipal);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(180, 160, 120, 160)); 

        inicialUserG = new AvatarPanel(220);
        inicialUserG.setInitials(generarIniciales("Administrador"));
        inicialUserG.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(Box.createVerticalGlue());
        centro.add(inicialUserG);
        centro.add(Box.createRigidArea(new Dimension(0, 18)));

        lblNombre = new JLabel("Administrador", SwingConstants.CENTER);
        lblNombre.setForeground(new Color(255, 255, 255, 230));
        lblNombre.setFont(new Font("Segoe UI Semibold", Font.BOLD, 40));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(lblNombre);
        centro.add(Box.createRigidArea(new Dimension(0, 18)));

        panelPassword = new JPanel(null);
        panelPassword.setOpaque(false);
        panelPassword.setBorder(null);
        panelPassword.setBackground(new Color(0, 0, 0, 0));
        panelPassword.setBorder(null);
        panelPassword.setPreferredSize(new Dimension(420, 150));
        panelPassword.setMaximumSize(new Dimension(420, 150));
        panelPassword.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(14, 12, 404, 22);
        lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
        panelPassword.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        txtPassword.setBounds(14, 38, 334, 36);
        panelPassword.add(txtPassword);

        btnFlecha = new JButton("\u2192");
        btnFlecha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnFlecha.setFocusPainted(false);
        btnFlecha.setBorderPainted(false);
        btnFlecha.setBackground(new Color(230, 230, 230));
        btnFlecha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFlecha.addActionListener(e -> iniciarSesion());
        btnFlecha.setBounds(356, 38, 50, 36);

        btnFlecha.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnFlecha.setBackground(new Color(200, 200, 200));
            }

            public void mouseExited(MouseEvent e) {
                btnFlecha.setBackground(new Color(230, 230, 230));
            }
        });

        panelPassword.add(btnFlecha);

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(14, 86, 392, 42);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 100, 190));
            }

            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 120, 215));
            }
        });

        btnLogin.addActionListener(e -> iniciarSesion());
        panelPassword.add(btnLogin);

        centro.add(panelPassword);
        centro.add(Box.createRigidArea(new Dimension(0, 12)));

        lblInfo = new JLabel("Ingrese su contraseña para acceder", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(lblInfo);
        centro.add(Box.createVerticalGlue());
        panelPrincipal.add(centro, BorderLayout.CENTER);

        panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        izquierda.setOpaque(false);
        panelOtroUsuario = new JPanel();
        panelOtroUsuario.setOpaque(false);
        panelOtroUsuario.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panelOtroUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inicialUserP = new AvatarPanel(52);
        inicialUserP.setInitials(generarIniciales("Administrador"));
        panelOtroUsuario.add(inicialUserP);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("Ingresar con otro usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Seleccionar cuenta");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(220, 220, 220));

        textos.add(lblTitulo);
        textos.add(lblSubtitulo);
        panelOtroUsuario.add(textos);
        izquierda.add(panelOtroUsuario);
        panelInferior.add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        derecha.setOpaque(false);
        lblSalir = new JLabel("\u23FB");
        lblSalir.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        lblSalir.setForeground(Color.WHITE);
        lblSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblSalir.setToolTipText("Salir");

        lblSalir.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                System.exit(0);
            }

            public void mouseEntered(MouseEvent e) {
                lblSalir.setForeground(new Color(220, 100, 100));
            }

            public void mouseExited(MouseEvent e) {
                lblSalir.setForeground(Color.WHITE);
            }
        });

        derecha.add(lblSalir);
        panelInferior.add(derecha, BorderLayout.EAST);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        MouseAdapter abrirPopup = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                mostrarListaUsuarios(panelOtroUsuario);
            }

            public void mouseEntered(MouseEvent e) {
                lblTitulo.setForeground(new Color(200, 200, 255));
                lblSubtitulo.setForeground(new Color(200, 200, 255));
            }

            public void mouseExited(MouseEvent e) {
                lblTitulo.setForeground(Color.WHITE);
                lblSubtitulo.setForeground(new Color(220, 220, 220));
            }
        };

        panelOtroUsuario.addMouseListener(abrirPopup);
        txtPassword.addActionListener(e -> iniciarSesion());
        selectedUsername = "";
    }

    private void iniciarSesion() {
        String password = new String(txtPassword.getPassword());
        String usernameToUse = selectedUsername.isEmpty() ? "admin" : selectedUsername;
        if (password.isEmpty()) {
            txtPassword.requestFocus();
            return;
        }
        try {
            Usuario usuario = sistema.login(usernameToUse, password);
            abrirVentanaPrincipal(usuario);
            dispose();
        } catch (UsuarioNoEncontradoException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("contraseña")) {
                if (lblInfo != null) {
                    lblInfo.setText("Contraseña incorrecta. Intenta de nuevo.");
                    lblInfo.setForeground(Color.WHITE);
                }
            }
            txtPassword.setText("");
            txtPassword.requestFocus();
        } catch (ArchivoNoValidoException | PermisosDenegadosException ex) {
        }
    }

    private void abrirVentanaPrincipal(Usuario usuario) {
        VentanaPrincipal ventana = new VentanaPrincipal(usuario, sistema);
        ventana.setVisible(true);
    }

    private void actualizarIcono(String username, String nombreCompleto) {
        if (username == null || username.trim().isEmpty()) {
            inicialUserP.setInitials(generarIniciales("Administrador"));
        } else {
            inicialUserP.setInitials(generarIniciales(nombreCompleto != null ? nombreCompleto : username));
        }
        inicialUserP.repaint();
    }

    private void mostrarListaUsuarios(Component invoker) {
        try {
            ArrayList<Usuario> usuario = sistema.getGestorUsuarios().obtenerUsuariosActivos();
            ArrayList<Usuario> lista = new ArrayList<>();
            for (Usuario u : usuario) {
                if (u.getUsername() == null) {
                    continue;
                }

                if (selectedUsername.isEmpty() && u.getUsername().equalsIgnoreCase("admin")) {
                    continue;
                }

                if (!selectedUsername.isEmpty() && u.getUsername().equalsIgnoreCase(selectedUsername)) {
                    continue;
                }

                lista.add(u);
            }

            JWindow popup = new JWindow(this);
            popup.setBackground(new Color(0, 0, 0, 0));
            JPanel cont = new JPanel();
            cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
            cont.setBackground(new Color(255, 255, 255, 140));
            cont.setBorder(BorderFactory.createLineBorder(new Color(140, 140, 140, 200)));
            cont.setOpaque(true);

            for (Usuario u : lista) {
                JPanel fila = new JPanel(new BorderLayout(8, 0));
                fila.setPreferredSize(new Dimension(250, 50));
                fila.setMaximumSize(new Dimension(250, 50));
                fila.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
                fila.setOpaque(false);

                AvatarPanel small = new AvatarPanel(44);
                small.setPreferredSize(new Dimension(44, 44));
                small.setInitials(generarIniciales(u.getNombreCompleto()));
                fila.add(small, BorderLayout.WEST);

                JLabel txt = new JLabel("<html><b>" + u.getNombreCompleto() + "</b><br><small>" + u.getUsername() + "</small></html>");
                txt.setBorder(new EmptyBorder(4, 0, 4, 0));
                fila.add(txt, BorderLayout.CENTER);
                fila.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (u.getUsername().equalsIgnoreCase("admin")) {
                            selectedUsername = "";
                            lblNombre.setText("Administrador");
                            inicialUserG.setInitials(generarIniciales("Administrador"));
                            actualizarIcono("", "Administrador");
                        } else {
                            selectedUsername = u.getUsername();
                            lblNombre.setText(u.getNombreCompleto());
                            inicialUserG.setInitials(generarIniciales(u.getNombreCompleto()));
                            actualizarIcono(selectedUsername, u.getNombreCompleto());
                        }
                        popup.setVisible(false);
                        popup.dispose();
                        txtPassword.requestFocus();
                    }

                    public void mouseEntered(MouseEvent e) {
                        fila.setOpaque(true);
                        fila.setBackground(new Color(230, 240, 255, 120));
                    }

                    public void mouseExited(MouseEvent e) {
                        fila.setOpaque(false);
                    }
                });
                cont.add(fila);
            }

            JScrollPane scroll = new JScrollPane(cont, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.getViewport().setBackground(new Color(0, 0, 0, 0));

            if (scroll.getVerticalScrollBar() != null) {
                scroll.getVerticalScrollBar().setOpaque(false);
            }

            popup.getContentPane().setBackground(new Color(0, 0, 0, 0));
            popup.getContentPane().add(scroll);
            popup.pack();
            Point invScreen;

            try {
                invScreen = invoker.getLocationOnScreen();
            } catch (IllegalComponentStateException ex) {
                return;
            }

            int x = invScreen.x;
            int y = invScreen.y - popup.getHeight();
            Rectangle screen = getGraphicsConfiguration().getBounds();

            if (x + popup.getWidth() > screen.x + screen.width) {
                x = screen.x + screen.width - popup.getWidth() - 8;
            }

            if (x < screen.x) {
                x = screen.x + 8;
            }

            popup.setLocation(x, y);
            popup.setVisible(true);

            final AWTEventListener[] listenerRef = new AWTEventListener[1];
            AWTEventListener listener = new AWTEventListener() {
                public void eventDispatched(AWTEvent event) {
                    if (!(event instanceof MouseEvent)) {
                        return;
                    }

                    MouseEvent me = (MouseEvent) event;
                    if (me.getID() != MouseEvent.MOUSE_PRESSED) {
                        return;
                    }

                    try {
                        if (!popup.isVisible()) {
                            Toolkit.getDefaultToolkit().removeAWTEventListener(listenerRef[0]);
                            return;
                        }

                        Point p = me.getLocationOnScreen();
                        Rectangle popupRect;

                        try {
                            popupRect = new Rectangle(popup.getLocationOnScreen(), popup.getSize());
                        } catch (IllegalComponentStateException ex) {
                            popup.setVisible(false);
                            popup.dispose();
                            Toolkit.getDefaultToolkit().removeAWTEventListener(listenerRef[0]);
                            return;
                        }

                        if (popupRect.contains(p)) {
                            return;
                        }

                        if (invoker.isShowing()) {
                            try {
                                Rectangle invRect = new Rectangle(invoker.getLocationOnScreen(), invoker.getSize());
                                if (invRect.contains(p)) {
                                    return;
                                }
                            } catch (IllegalComponentStateException ignored) {
                            }
                        }
                        popup.setVisible(false);
                        popup.dispose();
                        Toolkit.getDefaultToolkit().removeAWTEventListener(listenerRef[0]);
                    } catch (Exception ex) {
                        popup.setVisible(false);
                        popup.dispose();
                        Toolkit.getDefaultToolkit().removeAWTEventListener(listenerRef[0]);
                    }
                }
            };

            listenerRef[0] = listener;
            Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
            popup.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
                }
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener la lista de usuarios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String generarIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            return "";
        }
        String[] parts = nombreCompleto.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    static class AvatarPanel extends JPanel {

        private final int diameter;
        private String initials = null;

        AvatarPanel(int diameter) {
            this.diameter = diameter;
            setOpaque(false);
        }

        void setInitials(String initials) {
            this.initials = initials == null ? null : initials.trim();
            repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            int d = Math.min(w, h);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = w / 2, cy = h / 2;
            int circleX = cx - d / 2, circleY = cy - d / 2;

            if (initials != null && !initials.isEmpty()) {
                g2.setColor(new Color(255, 20, 147));
                g2.fillOval(circleX, circleY, d, d);
                g2.setColor(Color.WHITE);
                Font font = new Font("Segoe UI", Font.PLAIN, Math.max(10, d / 2));
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int wText = fm.stringWidth(initials);
                int hAscent = fm.getAscent();
                g2.drawString(initials, cx - wText / 2, cy + hAscent / 3);
            }

            g2.dispose();
        }

        public Dimension getPreferredSize() {
            return new Dimension(diameter, diameter);
        }
    }

    public static void main(String[] args) {
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            PantallaLogin login = new PantallaLogin();
            login.setVisible(true);
        });
    }
}
