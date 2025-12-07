package Main;

import MiniWindows.VentanaInicio;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author marye
 */
public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            SwingUtilities.invokeLater(() -> new VentanaInicio().MostrarVenatanaInicio());

        });
    }
}
