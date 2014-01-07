/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Libra
 */
public class PngBean extends JPanel {

    private BufferedImage image;
    final private ChartSave chart = new ChartSave();

    public void updateImage(File f) throws Exception{
        try {
            image = ImageIO.read(f);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 2, 2, this.getWidth() - 3, this.getHeight() - 3, this);
    }

    public void updateChart(List<int[]> fireList, String pathToFile) throws Throwable {
        updateImage(chart.updateChart(fireList, pathToFile));
        this.repaint();
    }
}
