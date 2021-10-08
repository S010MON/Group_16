package Bamboo.view.startup;

import Bamboo.view.resources.ResourceLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

public class HelpPanel extends JPanel
{
    public HelpPanel()
    {
        setBackground(Color.WHITE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        int x = 0;
        int y = 0;
        int imgWidth = 640;
        int imgHeight = 660
                ;
        BufferedImage img = ResourceLoader.getImage("rules.png");
        g2d.drawImage(img, x, y, imgWidth, imgHeight,null);
    }
}
