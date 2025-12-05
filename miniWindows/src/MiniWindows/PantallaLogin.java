package MiniWindows;

import Sistema.MiniWindowsClass;
import Modelo.Usuario;
import Excepciones.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author najma
 */
public class PantallaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCrearCuenta;
    private JLabel lblMensaje;
    private JPanel panelPrincipal;

    private MiniWindowsClass sistema;

    public PantallaLogin() {
        this.sistema = MiniWindowsClass.getInstance();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {

        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(new Color(25, 25, 112));

        JLabel lblTitulo = new JLabel("Mini-Windows");
        lblTitulo.setFont(new Font("Segoe UI Light", Font.BOLD, 48));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 50, 500, 60);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Sistema Operativo Virtual");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setBounds(0, 110, 500, 25);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblSubtitulo);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setBounds(75, 170, 350, 280);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(new Color(60, 60, 60));
        lblUsuario.setBounds(30, 30, 290, 25);
        panelFormulario.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBounds(30, 55, 290, 35);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtUsuario);

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(new Color(60, 60, 60));
        lblPassword.setBounds(30, 100, 290, 25);
        panelFormulario.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(30, 125, 290, 35);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtPassword);

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBounds(30, 180, 290, 40);
        btnLogin.setBackground(new Color(0, 120, 215));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> iniciarSesion());

        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 95, 184));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 120, 215));
            }
        });
        panelFormulario.add(btnLogin);

        btnCrearCuenta = new JButton("Crear nueva cuenta");
        btnCrearCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCrearCuenta.setBounds(30, 230, 290, 30);
        btnCrearCuenta.setBackground(Color.WHITE);
        btnCrearCuenta.setForeground(new Color(0, 120, 215));
        btnCrearCuenta.setFocusPainted(false);
        btnCrearCuenta.setBorderPainted(true);
        btnCrearCuenta.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 1));
        btnCrearCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCrearCuenta.addActionListener(e -> abrirCrearCuenta());

        btnCrearCuenta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCrearCuenta.setBackground(new Color(240, 248, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCrearCuenta.setBackground(Color.WHITE);
            }
        });
        panelFormulario.add(btnCrearCuenta);

        panelPrincipal.add(panelFormulario);

        lblMensaje = new JLabel("");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setBounds(0, 460, 500, 25);
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblMensaje);

        JLabel lblInfo = new JLabel("Usuario por defecto: admin / Contraseña: admin123");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(220, 220, 220));
        lblInfo.setBounds(0, 500, 500, 20);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblInfo);

        txtPassword.addActionListener(e -> iniciarSesion());
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());

        add(panelPrincipal);
    }

    private void configurarVentana() {
        setTitle("Mini-Windows - Iniciar Sesión");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void iniciarSesion() {
        String username = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            mostrarError("Por favor ingrese su usuario");
            txtUsuario.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mostrarError("Por favor ingrese su contraseña");
            txtPassword.requestFocus();
            return;
        }

        try {

            Usuario usuario = sistema.login(username, password);

            mostrarExito("Bienvenido, " + usuario.getNombreCompleto());

            Timer timer = new Timer(1000, e -> {
                abrirVentanaPrincipal(usuario);
                dispose();
            });
            timer.setRepeats(false);
            timer.start();

        } catch (UsuarioNoEncontradoException e) {
            mostrarError(e.getMessage());
            txtPassword.setText("");
            txtPassword.requestFocus();
        } catch (Exception e) {
            mostrarError("Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirCrearCuenta() {

        JDialog dialogo = new JDialog(this, "Crear Nueva Cuenta", true);
        dialogo.setLayout(null);
        dialogo.setSize(400, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.getContentPane().setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setBounds(30, 20, 340, 25);
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dialogo.add(lblNombre);

        JTextField txtNombre = new JTextField();
        txtNombre.setBounds(30, 45, 340, 30);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialogo.add(txtNombre);

        JLabel lblUsuarioNuevo = new JLabel("Nombre de Usuario:");
        lblUsuarioNuevo.setBounds(30, 85, 340, 25);
        lblUsuarioNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dialogo.add(lblUsuarioNuevo);

        JTextField txtUsuarioNuevo = new JTextField();
        txtUsuarioNuevo.setBounds(30, 110, 340, 30);
        txtUsuarioNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsuarioNuevo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialogo.add(txtUsuarioNuevo);

        JLabel lblPasswordNueva = new JLabel("Contraseña:");
        lblPasswordNueva.setBounds(30, 150, 340, 25);
        lblPasswordNueva.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dialogo.add(lblPasswordNueva);

        JPasswordField txtPasswordNueva = new JPasswordField();
        txtPasswordNueva.setBounds(30, 175, 340, 30);
        txtPasswordNueva.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPasswordNueva.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialogo.add(txtPasswordNueva);

        JLabel lblInfo2 = new JLabel("La contraseña debe tener al menos 4 caracteres");
        lblInfo2.setBounds(30, 210, 340, 20);
        lblInfo2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo2.setForeground(Color.GRAY);
        dialogo.add(lblInfo2);

        JButton btnCrear = new JButton("Crear Cuenta");
        btnCrear.setBounds(30, 245, 165, 40);
        btnCrear.setBackground(new Color(0, 120, 215));
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCrear.setFocusPainted(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dialogo.add(btnCrear);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(205, 245, 165, 40);
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(new Color(100, 100, 100));
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        dialogo.add(btnCancelar);

        btnCrear.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String usuario = txtUsuarioNuevo.getText().trim();
            String pass = new String(txtPasswordNueva.getPassword());

            if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,
                        "Por favor complete todos los campos",
                        "Campos Incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                sistema.crearUsuario(nombre, usuario, pass);
                JOptionPane.showMessageDialog(dialogo,
                        "Cuenta creada exitosamente!\nYa puede iniciar sesión.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();

                txtUsuario.setText(usuario);
                txtPassword.setText("");
                txtPassword.requestFocus();

            } catch (ArchivoNoValidoException ex) {
                JOptionPane.showMessageDialog(dialogo,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.setVisible(true);
    }

    private void abrirVentanaPrincipal(Usuario usuario) {
        VentanaPrincipal ventana = new VentanaPrincipal(usuario, sistema);
        ventana.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(new Color(220, 53, 69));
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(new Color(40, 167, 69));
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
