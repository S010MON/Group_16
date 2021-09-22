package Bamboo.view;

import Bamboo.controller.Settings;
import Bamboo.model.Game;
import Bamboo.view.startup.StartupPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;

public class MainFrame extends JFrame
{
    private Game currentGame;
    private GamePanel gamePanel;
    private StartupPanel startupPanel = new StartupPanel();
    private Dimension screenSize;
    private MenuBar fileMenu;

    public MainFrame()
    {
        buildFrame();

        // Settings Panel
        setCurrentPanel(null, startupPanel);
        while (startupPanel.isNotSettingsReady()) {
            sleep(1000);
        }
        Settings settings = startupPanel.getSettings();
        startupPanel.reset();

        // Set the current game
        currentGame = new Game(settings);
        setCurrentPanel(startupPanel, new GamePanel(screenSize, currentGame));
    }

    private void setCurrentPanel(JPanel outgoing, JPanel incoming) {
        if(outgoing != null)
            remove(outgoing);
        add(incoming);
        setVisible(true);
    }

    /** All frame settings detailed here */
    private void buildFrame()
    {
        screenSize  = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Bamboo");
        setSize(screenSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setJMenuBar(new MenuBar(this));
    }


    public void quit() {

    }

    public void save() {

    }

    public void load() {

    }

    private void sleep(int amount) {
        try {
            Thread.sleep(amount);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
