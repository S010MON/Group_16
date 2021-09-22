package Bamboo.controller;

import Bamboo.model.Game;
import Bamboo.model.Tile;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.util.HashMap;

public class FileManager
{
    private static final String internalPath = "/app/src/main/java/saved/";
    private static final String fileHeader = "# Any lines that start with a '#' are ignored\n#\n# Settings\n#\nSOS";

    public static Settings load()
    {
        try {
            File file = promptUserForFile();
            Settings settings = readSettingsFromFile(file);
            settings.addTiles(readTilesFromFile(file));
            return settings;

        } catch (Exception e) {

            showDialogIOError("Unable to load file");
        }
        return null;
    }

    public static void save(Game game)
    {
        try {
            String filePath = getFilePath(createFileName());
            File file = new File(filePath);
            if (!file.exists())
                file.createNewFile();
            writeToFile(file, game);

        } catch (Exception e) {

            showDialogIOError("Unable to save file");
        }
    }

    private static File promptUserForFile() throws FileNotFoundException
    {
        String openAtPath = getFilePath("");
        JFileChooser chooser = new JFileChooser(openAtPath);
        chooser.setDialogTitle("Choose a saved game: ");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        String chosenFilePath = null;
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            chosenFilePath = chooser.getSelectedFile().getPath();
            System.out.println(chosenFilePath);
        }

        File file = new File(chosenFilePath);
        if(!file.exists())
            throw new FileNotFoundException("File: " + chosenFilePath + " not found");
        return file;
    }

    public static Settings readSettingsFromFile(File file) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        // Look for start of settings (SOS)
        while(!line.equalsIgnoreCase("SOS"))
        {
            line = reader.readLine();
        }

        // Read in each line for the settings
        String[] data = new String[7];
        for(int i = 0; i < data.length; i++)
        {
            data[i] = reader.readLine();
        }

        // Remove the human readable parts (i.e. stuff before '=')
        for(int i = 0; i < data.length; i++)
        {
            String[] subString = data[i].split("=");
            data[i] = subString[1];
        }

        // Parse the settings and make a new object
        int radius = Integer.parseInt(data[0]);
        String player1Name = data[1];
        Color player1Colour = parseColour(data[2]);
        Agent player1 = parseAgent(data[3], player1Name, player1Colour);
        String player2Name = data[4];
        Color player2Colour = parseColour(data[5]);
        Agent player2 = parseAgent(data[6], player2Name, player2Colour);
        return new Settings(player1, player2, radius);
    }

    public static HashMap<Vector, Color> readTilesFromFile(File file) throws IOException
    {
        HashMap<Vector, Color> tiles = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        // Look for the "Start of Tiles" tag
        while(!line.equalsIgnoreCase("SOT"))
        {
            line = reader.readLine();
        }
        line = reader.readLine();

        // Continue until the "End of Tiles" tag is reached
        while(!line.equalsIgnoreCase("EOT"))
        {
            String[] subString = line.split(",");
            int x = Integer.parseInt(subString[0]);
            int y = Integer.parseInt(subString[1]);
            int z = Integer.parseInt(subString[2]);
            Color color = parseColour(subString[3]);
            tiles.put(new Vector(x,y,z), color);
            line = reader.readLine();
        }
        return tiles;
    }

    private static void writeToFile(File file, Game game) throws IOException
    {
        FileWriter writer = new FileWriter(file,false);
        writer.write(fileHeader);
        writer.write("radius=" + game.getSettings().boardSize);
        writer.write("name=" + game.getSettings().player1.getName());
        writer.write("colour=" + colourAsString(game.getSettings().player1.getColor()));
        writer.write("type=" + game.getSettings().player1.getType());
        writer.write("name=" + game.getSettings().player2.getName());
        writer.write("colour=" + colourAsString(game.getSettings().player2.getColor()));
        writer.write("type=" + game.getSettings().player2.getType());
        writer.write("EOS\n#\n# Start of tiles\n#  Layout: x,y,z,colour\nSOT");
        for (Tile tile: game.getGrid().getAllTiles())
        {
            if(tile.getColour() != Color.WHITE)
                writer.write(tile.toCSV());
        }
        writer.write("EOS\n");
    }

    /**
     * Creates a name with the LocalDateTime of the saved game
     */
    private static String createFileName()
    {
        StringBuilder fileName = new StringBuilder();
        fileName.append("bamboo");
        fileName.append("_");
        fileName.append(LocalDateTime.now().toString());
        return fileName.toString();
    }

    /**
     * Creates a file path to saved games with the correct file name appended
     */
    private static String getFilePath(String fileName)
    {
        FileSystem fileSystem = FileSystems.getDefault();
        String path = fileSystem.getPath("").toAbsolutePath().toString();
        return path.concat(internalPath + fileName);
    }

    private static void showDialogIOError(String message)
    {
        JOptionPane.showMessageDialog(null,
                message,
                "File Input/Output Error",
                JOptionPane.WARNING_MESSAGE);
    }

    private static Color parseColour(String colour)
    {
        if(colour.equalsIgnoreCase("RED"))
            return Color.RED;
        else if (colour.equalsIgnoreCase("BLUE"))
            return Color.BLUE;
        else
            return Color.WHITE;
    }

    private static String colourAsString(Color colour)
    {
        if(colour.equals(Color.RED))
            return "RED";
        else if (colour.equals(Color.BLUE))
            return "BLUE";
        else
            return "WHITE";
    }

    private static Agent parseAgent(String agent, String name, Color color)
    {
        if(agent.equalsIgnoreCase("HUMAN"))
            return new Human(name, color);
        else
            return null; // TODO Extend here for AI
    }
}
