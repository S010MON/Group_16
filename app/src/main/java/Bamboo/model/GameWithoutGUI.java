package Bamboo.model;

import Bamboo.controller.Agent;
import Bamboo.controller.Settings;
import Bamboo.controller.Vector;

import java.util.ArrayList;
import java.util.List;

public class GameWithoutGUI implements Game
{
    public static int MCTSiterations = 1000;

    private Grid grid;
    private ArrayList<Tile> remainingTiles = new ArrayList<>();
    Agent a1, a2, currentPlayer;

    public GameWithoutGUI(Settings settings){
        a1 = settings.player1;
        a2 = settings.player2;
        currentPlayer = a1;
        grid = new GridGraphImp(settings.boardSize);
        remainingTiles = new ArrayList<>(grid.getAllTiles());
    }

    private void toggleTurn()
    {
        if(currentPlayer == a1)
            currentPlayer = a2;
        else
            currentPlayer = a1;
    }

    public Agent turnLogic(){
        while(!remainingTiles.isEmpty() && !grid.isFinished(currentPlayer.getColor())){
            makeTurn();
        }
        return otherPlayer();
    }

    private void makeTurn(){
        Vector move = currentPlayer.getNextMove(this);
        remainingTiles.remove(grid.getTile(move));
        this.grid.setTile(move,currentPlayer.getColor());
        //System.out.println("Agent " + currentPlayer.getName() + " placed color " + currentPlayer.getColor() + " at " + move.toString());
        toggleTurn();
    }

    @Override
    public Grid getGrid() {
        return this.grid;
    }

    @Override
    public Agent getCurrentPlayer() {
        return currentPlayer;
    }

    private Agent otherPlayer(){
        if(currentPlayer == a1)
            return a2;
        else
            return a1;
    }

    @Override
    public List<Tile> getAllTiles() {
        return grid.getAllTiles();
    }
}