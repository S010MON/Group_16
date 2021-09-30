package Bamboo.view.resources;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Color;

public class Label extends JLabel
{
    public Label(String iconName)
    {
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setBackground(new Color(158, 208, 239));
        this.setIcon(ResourceLoader.getIcon(iconName));
    }
}
