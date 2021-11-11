import Bamboo.controller.Agent;
import Bamboo.controller.AgentFactory;
import Bamboo.controller.AgentType;
import Bamboo.controller.MCTS.UCB;
import Bamboo.controller.Settings;
import Bamboo.model.GameWithoutGUI;

import java.awt.*;
import java.util.ArrayList;

public class MCTS_experiment
{
    public static void main(String[] args) {
        double startC = 0.05;
        double stepSize = 0.05;
        double maxC = 1;
        int replications = 1;
        int boardSize = 5;
        ArrayList<Double> winRates = new ArrayList<>();

        //I think:
        // MCTS iterations <= 1000
        // iterations here >= 100
        // C in (0,1)

        for(double C = startC; C <= maxC; C+=stepSize){
            int[] wins = new int[replications];
            UCB.C = C;
            System.out.println(C);
            for(int i = 0; i < replications; i++){
                GameWithoutGUI.MCTSiterations = 100;//just an idea
                Agent agent1 = AgentFactory.makeAgent(AgentType.MCTS, Color.RED);
                Agent agent2 = AgentFactory.makeAgent(AgentType.RANDOM, Color.BLUE);
                Settings settings = new Settings(agent1, agent2, boardSize);
                GameWithoutGUI game = new GameWithoutGUI(settings);
                Agent winner = game.playout();//make this return winning agent in the end
                if(winner == agent1)
                    wins[i] = 1;
                else
                    wins[i] = 0;
            }
            winRates.add(mean(wins));
        }
        for(double p : winRates)
            System.out.println(p);
        //todo complete setup and write to file
    }

    private static double mean(int[] wins){return 0;}//return mean of array

    int[] runLoop(){return new int[]{1};}
}
