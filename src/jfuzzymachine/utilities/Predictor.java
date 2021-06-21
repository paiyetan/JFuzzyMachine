/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.Rule;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;

/**
 *
 * @author aiyetanpo
 */
public class Predictor {
    
    private Table predictionsTable;
    private Table exprsTable;
    private HashMap<String, Model> outputToModelMap;

    public Predictor(Table exprsTable, 
                        HashMap<Vertex, LinkedList<Model>> outputsToModelsMap) {
        this.exprsTable = exprsTable;
        this.outputToModelMap = new HashMap();
        Set<Vertex> vertices = outputsToModelsMap.keySet();
        vertices.forEach((vertex) -> {
            String id = vertex.getId();
            Model model = outputsToModelsMap.get(vertex).getFirst();
            outputToModelMap.put(id, model);
        });
        
        predictionsTable = new Table(exprsTable.getRowIds(),
                                     exprsTable.getColumnIds(),
                                     new double[exprsTable.getRowIds().length][exprsTable.getColumnIds().length] );
    }
    
    public void runPredictions(){
        FitEvaluator fe = new FitEvaluator();
        Fuzzifier fu = new Fuzzifier();
        double[][] mat = new double[exprsTable.getRowIds().length][exprsTable.getColumnIds().length];
        for(int i = 0; i < exprsTable.getRowIds().length; i++){
            for(int j =0; j < exprsTable.getColumnIds().length; j++){
                //getOutput node...
                String output = exprsTable.getRowIds()[i];
                Model model = outputToModelMap.get(output);
                if(model != null){
                    LinkedList<Rule> rules = model.getRulesLinkedList();
                    String[] inputNodes = model.getInputNodesStringArray();
                    double[] expValues = exprsTable.getColumn(j, Table.TableType.DOUBLE); //get currently being considered column exprs values...               
                    double[] inputsExpValues = new double[inputNodes.length]; //get expression value of input features..
                    for(int k = 0; k < inputsExpValues.length; k++){
                        inputsExpValues[k] = expValues[exprsTable.getRowIndex(inputNodes[k])];             
                    }
                    mat[i][j] = fe.getPrediction(inputsExpValues, rules, fu);    
                }else{
                    mat[i][j] = exprsTable.getDoubleMatrix()[i][j];
                }                          
            }
        }
        predictionsTable.setMatrix(mat);
    }
    
    public void printPredictionsTable(String outputFile) throws FileNotFoundException{
        predictionsTable.print(outputFile, Table.TableType.DOUBLE);
    }
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("Starting...");
        String exprsFile = args[0];
        String fitFile = args[1];
        double fitCutOff = Double.parseDouble(args[2]);
        String predictionsFile = args[3];
        
        Table exprsTable = new Table(exprsFile, Table.TableType.DOUBLE);
        HashMap<Vertex, LinkedList<Model>> outputsToModelsMap = ModelFitFileReader.read(fitFile, fitCutOff);
        
        Predictor predictor = new Predictor(exprsTable, outputsToModelsMap);      
        System.out.println("Predicting...");
        predictor.runPredictions();
        System.out.println("Printing...");
        predictor.printPredictionsTable(predictionsFile);       
        System.out.println("...Done!");
    }
    
}
