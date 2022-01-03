package Bamboo.controller.MCTS;

import Bamboo.controller.Agent;
import Bamboo.controller.Vector;
import Bamboo.model.GameWithGUI;
import Bamboo.model.GameWithoutGUI;

import java.awt.Color;

public class MCTS implements Agent
{
    private Color colour;
    private NodeMCTS root;
    private int iterations = 10000;

    public MCTS(Color colour)
    {
        this.colour = colour;
    }

    @Override
    public String getName() {
        return "MCTS";
    }

    @Override
    public String getType() {
        return "AI";
    }

    @Override
    public boolean isHuman()
    {
        return false;
    }

    @Override
    public Vector getNextMove(GameWithGUI game)
    {
        root = new NodeMCTS(game.getGrid(), null, game.getCurrentPlayer().getColor(), null);
        for(int i = 0; i < iterations; i++)
        {
            NodeMCTS next = root.select();
            root.expand(next);
            next.backProp(next.playout());
        }
        NodeMCTS bestMove = root.selectBest();
        return bestMove.getMove();
    }

    @Override
    public Vector getNextMove(GameWithoutGUI game)
    {
        root = new NodeMCTS(game.getGrid(), null, game.getCurrentPlayer().getColor(), null);
        for(int i = 0; i < GameWithoutGUI.MCTSiterations; i++)
        {
            NodeMCTS next = root.select();
            root.expand(next);
            next.backProp(next.playout());
        }
        NodeMCTS bestMove = root.selectBest();
        return bestMove.getMove();
    }

    @Override
    public Color getColor() {
        return colour;
    }
}