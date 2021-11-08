package Bamboo.controller.nNet;

import Bamboo.controller.*;
import Bamboo.model.Game;
import Bamboo.model.Grid;
import Bamboo.model.GridGraphImp;
import deepnetts.data.DataSets;
import deepnetts.data.MLDataItem;
import deepnetts.data.TabularDataSet;
import deepnetts.eval.Evaluators;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.opt.OptimizerType;
import deepnetts.util.FileIO;
import org.apache.logging.log4j.core.appender.SyslogAppender;

import javax.visrec.ml.data.Column;
import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NeuralNetwork implements Agent
{

    private FeedForwardNetwork neuralNet;
    private Color color;
    private String nNetSavePath = FilePath.getNNetPath("networkSave.json");

    public NeuralNetwork(Color color) throws IOException {
        this.color = color;
        try{
            this.neuralNet = (FeedForwardNetwork) FileIO.createFromJson(new File(nNetSavePath));
            NetworkManager.fillNN(neuralNet);
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public String getName()
    {
        return "Nathan";
    }

    @Override
    public String getType()
    {
        return "Neural Net";
    }

    @Override
    public boolean isHuman()
    {
        return false;
    }

    @Override
    public Vector getNextMove(Game game)
    {
        Grid grid = game.getGrid();
        int[] input = DataManager.flatten(grid,color);
        float[] floatInputs = new float[input.length];
        for(int i = 0; i < input.length; i++)
            floatInputs[i] = (float)input[i];
        float[] output = neuralNet.predict(floatInputs);
        Vector move = getMoveFromPrediction(output,game);
        if(grid.isLegalMove(move,color))
            System.out.println("NNet chooses move " + move);
        else
            System.out.println("At " + move + " NNet tried to break rules");
        return move;
    }

    @Override
    public Color getColor()
    {
        return color;
    }

    public void train()
    {
        int inputsNum = 91;
        int outputsNum = 91;

        try {
            String filePath = FilePath.getNNetPath("data.csv");
            TabularDataSet<MLDataItem> data = DataSets.readCsv(filePath, inputsNum, outputsNum,true);
            data.shuffle();
            DataSet<MLDataItem>[] dataSplit = data.split(0.7,0.3);
            TabularDataSet<MLDataItem> trainData = (TabularDataSet) dataSplit[0];
            TabularDataSet<MLDataItem> testData = (TabularDataSet) dataSplit[1];
            buildColumns(trainData,inputsNum,outputsNum);
            buildColumns(testData,inputsNum,outputsNum);
            int hiddenSize = (int) Math.round(Math.sqrt(inputsNum*outputsNum));
            if(neuralNet == null){
                neuralNet = FeedForwardNetwork.builder()
                        .addInputLayer(inputsNum)
                        .addFullyConnectedLayer(hiddenSize,ActivationType.RELU)
                        .addFullyConnectedLayer(hiddenSize,ActivationType.RELU)
                        .addFullyConnectedLayer(hiddenSize,ActivationType.RELU)
                        .addOutputLayer(outputsNum, ActivationType.SOFTMAX)
                        .lossFunction(LossType.CROSS_ENTROPY)
                        .randomSeed(123)
                        .build();
            }

            BackpropagationTrainer trainer = neuralNet.getTrainer();
            trainer.setOptimizer(OptimizerType.MOMENTUM);
            trainer.setShuffle(true);
            trainer.setTestSet(testData);
            trainer.setMaxEpochs(100);
            trainer.setEarlyStopping(true);
            trainer.setEarlyStoppingPatience(15);
            neuralNet.train(trainData);

            System.out.println("---------TRAIN DATA--------");
            EvaluationMetrics em = Evaluators.evaluateClassifier(neuralNet,trainData);
            System.out.println(em);
            System.out.println("---------TEST DATA--------");
            EvaluationMetrics em2 = Evaluators.evaluateClassifier(neuralNet,testData);
            System.out.println(em2);

            FileIO.writeToFileAsJson(neuralNet,FilePath.getNNetPath("networkSave.json"));
            NetworkManager.save(neuralNet,"");
        }
        catch (IOException e) { e.printStackTrace();}

    }

    void buildColumns(TabularDataSet dataSet, int numInputs, int numOutputs){
        ArrayList<Column> cols = new ArrayList<>();
        for(int in = 0; in < numInputs; in++){
            Column temp = new Column("i"+in,Column.Type.DECIMAL,false);
            cols.add(temp);
        }
        for(int out = 0; out < numOutputs; out++){
            Column temp = new Column("o"+out,Column.Type.BINARY,true);
            cols.add(temp);
        }
        dataSet.setColumns(cols);
    }

    Vector getMoveFromPrediction(float[] prediction){
        Grid grid = new GridGraphImp(5);
        ArrayList<Vector> vectors = new ArrayList<Vector>(grid.getAllVectors());
        int iterator = 0;
        int bestID = 0;
        float bestPred = 0;
        for(float pred : prediction){
            if(pred > bestPred){
                bestPred = pred;
                bestID = iterator;
            }
            iterator++;
        }
        return vectors.get(bestID);
    }

    Vector getMoveFromPrediction(float[] prediction,Game game){
        Grid grid = game.getGrid();
        ArrayList<Vector> vectors = new ArrayList<Vector>(grid.getAllVectors());
        int iterator = 0;
        int bestID = 0;
        float bestPred = 0;
        for(float pred : prediction){
            if(pred > bestPred && grid.isLegalMove(vectors.get(iterator),color)){
                bestPred = pred;
                bestID = iterator;
            }
            iterator++;
        }
        return vectors.get(bestID);
    }
}
