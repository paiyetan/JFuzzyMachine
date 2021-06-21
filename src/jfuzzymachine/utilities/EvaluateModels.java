/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;

/**
 *
 * @author aiyetanpo
 */
public class EvaluateModels {
    
    private String outputFile;
    private HashMap<Vertex, LinkedList<Model>> outputsToModelsMap;
    private Table exprs;

    public EvaluateModels(String outputFile, 
                                HashMap<Vertex, LinkedList<Model>> outputsToModelsMap, 
                                Table exprs) {
        this.outputFile = outputFile;
        this.outputsToModelsMap = outputsToModelsMap;
        this.exprs = exprs;
        
        //evaluate models...
        Set<Vertex> outputs = outputsToModelsMap.keySet();
        outputs.forEach((output) -> {
            LinkedList<Model> mappedModels = outputsToModelsMap.get(output);
            mappedModels.forEach((model) -> {
                model.computeFit(exprs); // re-compute fit against current exprs (expression matrix)
            });
            outputsToModelsMap.replace(output, mappedModels);
        });
    }
       
    public void printEvaluatedModels() throws FileNotFoundException{
        // how is this different from an edge(s) table....we could describe with filename _OutputFile.fit
        PrintWriter printer = new PrintWriter(outputFile);
        printer.println("Output\tNumberOfFittedModels\tInputNodes(BestFit)\tRules\tFit");
        Set<Vertex> outputs = outputsToModelsMap.keySet();
        
        outputs.forEach((output) -> {
            LinkedList<Model> mappedModels = outputsToModelsMap.get(output);
            Collections.sort(mappedModels); // sort mapped models in ascending order                   
            Model bestFitModel = mappedModels.getLast();             
            printer.println(output.getId() + "\t" +
                            mappedModels.size() + "\t" +
                            bestFitModel.getInputNodesString() + "\t" +
                            bestFitModel.getRulesString() + "\t" +
                            bestFitModel.getFit());                      
        });
        printer.close();       
    }
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("Starting...");       
        Date start = new Date();
        long start_time = start.getTime();
        
        // args[0] = "pathToFitFile"
        // args[1] = "pathToExprsMatFile"
        // args[2] = "pathToEvalOutputFile"
        String fitFilePath = args[0]; 
                //file containing models info: //Output NumberOfFittedModels  InputNodes(BestFit)  Rules  Fit
        String exprsFilePath = args[1]; // expression matrix file against which to evaluate models...
        String evalOutputFile = args[2]; // evaluation output file...
        HashMap<Vertex, LinkedList<Model>> outputsToModelsMap = 
                ModelFitFileReader.read(fitFilePath, 0);
        Table exprs = new Table(exprsFilePath, Table.TableType.DOUBLE);
        
        System.out.println("Evaluating..."); 
        EvaluateModels modelsEvaluator = new EvaluateModels(evalOutputFile, outputsToModelsMap, exprs);
        System.out.println("Printing..."); 
        modelsEvaluator.printEvaluatedModels();
        
        System.out.println("\n...Done!");        
        Date end = new Date();
        long end_time = end.getTime();
        
        System.out.println("\n     Started: " + start.toString());
        System.out.println("     Ended: " + end.toString());
        System.out.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + 
                                                      " seconds.");
               
        
    }

    
}
