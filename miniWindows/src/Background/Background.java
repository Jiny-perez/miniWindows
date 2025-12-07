package Background;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author marye
 */
public class Background extends JPanel {

    private Image img;

    public Background(String path) {
        ImageIcon icon = new ImageIcon(path);
        img = icon.getImage();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }
}
