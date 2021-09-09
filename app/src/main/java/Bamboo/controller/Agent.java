package Bamboo.controller;

import Bamboo.model.Game;

import java.awt.*;

public interface Agent
{
    public String getName();
    public boolean isHuman();
    public CubeVector getNextMove(Game game);
    public Color getColor();
}
