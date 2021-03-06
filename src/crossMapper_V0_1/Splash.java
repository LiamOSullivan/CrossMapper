package crossMapper_V0_1;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Splash extends JPanel {
    BufferedImage image;
    Dimension size = new Dimension();

    public Splash(BufferedImage image) {
        this.image = image;
        size.setSize(image.getWidth(), image.getHeight());
        String path = "splash.gif";
        BufferedImage splashImage = null;
		try {
			splashImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Splash test = new Splash(splashImage);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new JScrollPane(test));
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
      //showIcon(image);
    }

    /**
     * Drawing an image can allow for more
     * flexibility in processing/editing.
     */
    protected void paintComponent(Graphics g) {
        // Center image in this component.
        int x = (getWidth() - size.width)/2;
        int y = (getHeight() - size.height)/2;
        g.drawImage(image, x, y, this);
    }

    public Dimension getPreferredSize() { return size; }

    //public static void main(String[] args) throws IOException {
        
    //    
    //}

    /**
     * Easy way to show an image: load it into a JLabel
     * and add the label to a container in your gui.
     */
    private static void showIcon(BufferedImage image) {
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon, JLabel.CENTER);
        JOptionPane.showMessageDialog(null, label, "icon", -1);
    }
}

