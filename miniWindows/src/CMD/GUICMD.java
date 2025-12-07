package CMD;

import Modelo.Usuario;
import Sistema.SistemaArchivos;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author marye
 */
public class GUICMD {

    private final CMD consola;
    private int posPrompt;

    private JTextPane txtPane = new JTextPane();
    private SimpleAttributeSet attrDefault = new SimpleAttributeSet();
    private SimpleAttributeSet attrCommand = new SimpleAttributeSet();
    private SimpleAttributeSet attrError = new SimpleAttributeSet();

    public GUICMD(Usuario usuario, SistemaArchivos sistemaCompartido) {
        this.consola = new CMD(usuario, sistemaCompartido);
        initStyles();
        initComponents();
    }

    public GUICMD(Usuario usuario) {
        this.consola = new CMD(usuario, null);
        initStyles();
        initComponents();
    }

    private void initStyles() {
        StyleConstants.setForeground(attrDefault, Color.WHITE);
        StyleConstants.setFontFamily(attrDefault, Font.MONOSPACED);
        StyleConstants.setFontSize(attrDefault, 14);
        StyleConstants.setBold(attrDefault, false);

        StyleConstants.setForeground(attrCommand, new Color(255, 200, 0));
        StyleConstants.setBold(attrCommand, true);
        StyleConstants.setFontFamily(attrCommand, Font.MONOSPACED);
        StyleConstants.setFontSize(attrCommand, 14);

        StyleConstants.setForeground(attrError, Color.RED);
        StyleConstants.setBold(attrError, false);
        StyleConstants.setFontFamily(attrError, Font.MONOSPACED);
        StyleConstants.setFontSize(attrError, 14);
    }

    private void initComponents() {
        JFrame ventana = new JFrame("Administrador: Command Prompt");
        ventana.setSize(820, 520);
        ventana.setLocationRelativeTo(null);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.setLayout(new BorderLayout());

        txtPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        txtPane.setBackground(Color.BLACK);
        txtPane.setForeground(Color.WHITE);
        txtPane.setCaretColor(Color.WHITE);
        txtPane.setEditable(true);
        txtPane.setFocusable(true);
        txtPane.setOpaque(true);

        JScrollPane scroll = new JScrollPane(txtPane);
        ventana.add(scroll, BorderLayout.CENTER);

        imprimirLinea("Microsoft Windows [Version 10.0.22621.521]", attrDefault);
        imprimirLinea("(c) Microsoft Corporation. All rights reserved.", attrDefault);
        imprimirLinea("", attrDefault);

        imprimirPrompt();

        AbstractDocument doc = (AbstractDocument) txtPane.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
                if (off < posPrompt) {
                    return;
                }
                super.remove(fb, off, len);
                SwingUtilities.invokeLater(() -> aplicarEstilos());
            }

            public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attrs) throws BadLocationException {
                if (off < posPrompt) {
                    return;
                }
                super.replace(fb, off, len, text, attrs);
                SwingUtilities.invokeLater(() -> aplicarEstilos());
            }

            public void insertString(FilterBypass fb, int off, String str, AttributeSet attr) throws BadLocationException {
                if (off < posPrompt) {
                    txtPane.setCaretPosition(txtPane.getDocument().getLength());
                    return;
                }
                super.insertString(fb, off, str, attr);
                SwingUtilities.invokeLater(() -> aplicarEstilos());
            }
        });

        txtPane.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_HOME) {
                    txtPane.setCaretPosition(posPrompt);
                    e.consume();
                } else if (code == KeyEvent.VK_ENTER) {
                    e.consume();
                    ejecutarComando();
                } else if (code == KeyEvent.VK_LEFT) {
                    if (txtPane.getCaretPosition() <= posPrompt) {
                        e.consume();
                        txtPane.setCaretPosition(posPrompt);
                    }
                } else if (code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
                    e.consume();
                }
            }
        });

        ventana.setVisible(true);
        SwingUtilities.invokeLater(this::aplicarEstilos);
    }

    private void imprimirLinea(String linea, AttributeSet estilo) {
        try {
            StyledDocument doc = txtPane.getStyledDocument();
            doc.insertString(doc.getLength(), linea + "\n", estilo);
        } catch (BadLocationException ex) {
        }
    }

    private void imprimirPrompt() {
        try {
            StyledDocument doc = txtPane.getStyledDocument();
            String prompt = consola.getPrompt();
            doc.insertString(doc.getLength(), prompt, attrDefault);
            posPrompt = doc.getLength();
            txtPane.setCaretPosition(posPrompt);
            SwingUtilities.invokeLater(this::aplicarEstilos);
        } catch (BadLocationException ex) {
        }
    }

    private void ejecutarComando() {
        try {
            StyledDocument doc = txtPane.getStyledDocument();
            int len = doc.getLength() - posPrompt;
            String linea = doc.getText(posPrompt, len).trim();

            doc.insertString(doc.getLength(), "\n", attrDefault);

            if (linea.isEmpty()) {
                imprimirPrompt();
                return;
            }

            String salida = consola.Ejecutar(linea);
            if (salida == null) {
                imprimirLinea("Error interno en la ejecución del comando.", attrError);
                doc.insertString(doc.getLength(), "\n", attrDefault);
            } else if (!salida.isEmpty()) {
                if (consola.isError()) {
                    imprimirLinea(salida, attrError);
                    doc.insertString(doc.getLength(), "\n", attrDefault);
                } else {
                    imprimirLinea(salida, attrDefault);
                    doc.insertString(doc.getLength(), "\n", attrDefault);

                }
            }
            imprimirPrompt();
        } catch (BadLocationException ex) {
            try {
                txtPane.getStyledDocument().insertString(txtPane.getStyledDocument().getLength(), "Error interno en la consola\n", attrError);
            } catch (BadLocationException ignored) {
            }
            imprimirPrompt();
        }

    }

    private void aplicarEstilos() {
        StyledDocument doc = txtPane.getStyledDocument();
        int inicio = posPrompt;
        int fin = doc.getLength();
        if (inicio > fin) {
            return;
        }

        doc.setCharacterAttributes(inicio, fin - inicio, attrDefault, true);

        try {
            String texto = doc.getText(inicio, fin - inicio);
            if (texto.isEmpty()) {
                return;
            }

            int siguienteLinea = texto.indexOf('\n');
            if (siguienteLinea != -1) {
                texto = texto.substring(0, siguienteLinea);
            }

            int inicioPalabra = 0;
            while (inicioPalabra < texto.length() && Character.isWhitespace(texto.charAt(inicioPalabra))) {
                inicioPalabra++;
            }

            if (inicioPalabra >= texto.length()) {
                return;
            }

            int limitePalabra = inicioPalabra;
            while (limitePalabra < texto.length() && !Character.isWhitespace(texto.charAt(limitePalabra))) {
                limitePalabra++;
            }

            String comando = texto.substring(inicioPalabra, limitePalabra).toLowerCase();
            String[] comandosValidos = {"mkdir", "rm", "cd", "cd..", "dir", "date", "time"};
            boolean existe = false;
            for (String c : comandosValidos) {
                if (comando.equals(c)) {
                    existe = true;
                    break;
                }
            }

            if (existe) {
                int absPos = posPrompt + inicioPalabra;
                doc.setCharacterAttributes(absPos, comando.length(), attrCommand, true);
            }
        } catch (BadLocationException ex) {
        }
    }
}
