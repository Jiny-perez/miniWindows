package MiniWindows;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author marye
 */
public class VentanaInicio {

    private JWindow window;

    public VentanaInicio() {
        window = new JWindow();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        window.setBounds(0, 0, screen.width, screen.height);
        window.getContentPane().add(new SplashPanel());
        window.validate();
    }

    public void MostrarVenatanaInicio() {
   window.setVisible(true);

    Timer t = new Timer(1400, e -> {
        PantallaLogin login = new PantallaLogin();

        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        login.setBounds(0, 0, scr.width, scr.height);
        login.toFront();

        login.repaint();

        window.dispose();
    });

    t.setRepeats(false);
    t.start();
    }

    private static class SplashPanel extends JPanel {

        public SplashPanel() {
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 20, 147));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int x = (getWidth() - 280) / 2;
            int y = (getHeight() - 300) / 2 - 40;

            int gap = 12;
            int w = (260 - gap) / 2;
            int h = (260 - gap) / 2;

            g2.setColor(Color.WHITE);

            g2.fillRect(x, y, w, h);
            g2.fillRect(x + w + gap, y, w, h);
            g2.fillRect(x, y + h + gap, w, h);
            g2.fillRect(x + w + gap, y + h + gap, w, h);

            String titulo = "Mini-Windows";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 50));
            int tw = g2.getFontMetrics().stringWidth(titulo);
            g2.drawString(titulo, (getWidth() - tw) / 2, y + 330);

            String subtitulo = "Sistema Operativo";
            g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 30));
            int sw = g2.getFontMetrics().stringWidth(subtitulo);
            g2.drawString(subtitulo, (getWidth() - sw) / 2, y + 380);

            g2.dispose();
        }
    }
}
