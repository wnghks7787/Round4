import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {

    private JLabel openImage;
    Graphics g;

    public ImagePanel()
    {
        setBounds(0, 70, 800, 630);
        setLayout(null);
        setBackground(Color.orange);
    }

    public JLabel getOpenImage() {
        return openImage;
    }

    public void setOpenImage(JLabel openImage) {
        this.openImage = openImage;
    }

    public void setGraphics(Graphics g)
    {
        this.g = g;
    }
}
