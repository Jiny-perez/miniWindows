package Main;

import MiniWindows.PantallaLogin;
import javax.swing.SwingUtilities;

/**
 *
 * @author marye
 */
public class Main {

   public static void main(String[] args) {       
        SwingUtilities.invokeLater(() -> {
            PantallaLogin login = new PantallaLogin();
            login.setVisible(true);
        });
    }
}
