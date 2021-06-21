/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Random;
import jfuzzymachine.Rule;
import jfuzzymachine.exceptions.TableBindingException;
import jfuzzymachine.tables.RuleTable;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import jfuzzymachine.utilities.simulation.Simulation;
import jfuzzymachine.utilities.simulation.Simulator;

/**
 *
 * @author paiyetan
 */
public class ProbabilityEngine {
    
    private final RuleTable ruleTable = new RuleTable();
    private final Random random = new Random();
    
    public void getRandomFits(int iterations,
                              String outputNodeId, 
                              Table exprsTable,
                              String outputFile) throws FileNotFoundException{
        FitEvaluator fe = new FitEvaluator();
        PrintWriter printer = new PrintWriter(outputFile);
        //print header....
        printer.println("Output\tInputs\tRules\tFit");
        while(iterations > 0){
            //get random inputs (up to three inputs[current default])
            int number = random.nextInt(3) + 1; //number of inputs
            String[] features = exprsTable.getRowIds();
            String[] possibleInputs = exprsTable.removeItem(features, outputNodeId);
            String[] inputs = this.getRandomInputs(number, possibleInputs);
            LinkedList<Rule> rules = this.getRandomRulesList(number);
            double fit = fe.evaluateFit(outputNodeId, inputs, rules, exprsTable);
            printer.println(outputNodeId + "\t" + 
                            Arrays.toString(inputs) + "\t" +
                            rules.toString() + "\t" +
                            fit);
            iterations--;
        }        
        printer.close();
    }
            
    /* 
     recall that the fit is computed by 
    E = \frac{\sum_{j=1}^M(x_i - \tilde{x)_i)^2}{\sum_{j=1}^M(x_i - \bar{x)_i)^2}    
    */    
    public double computeFitPValue(double estimatedFit,
                                    String outputNode,
                                        int maxNoOfInputs,
                                            Table expMat
                                                ){
        double fitPValue = 0;
        //RuleTable ruleTable = new RuleTable();
                
        return fitPValue;
    }
    
    public LinkedList<Double> getRandomDynamicPredictions(
            HashMap<Vertex, LinkedList<Model>> outputToModelsMap,
                      Simulator.INIT initType,
                          double alpha,
                              int maxIterations,
                                double eCutOff,
                                String outputNode,
                                    Table exprs,
                                        int sampleSize, //number of randomization
                                            boolean outputIsPheno,
                                                String phenotypeId,
                                                Table phenoExprs,
                                                    boolean tanTransform,
                                                        boolean logitTransform, 
                                                        double k,
                                                            PrintWriter printer,
                                                            boolean nullPrinter) throws TableBindingException{    
    LinkedList<Double> randomDynPreds = new LinkedList();
        Simulator simulator = new Simulator();
        double[] initialValues;
        final int maxNoOfInputs = 5;
        
        //get output nodes in network nodes...
        Set<Vertex> outputVertices = outputToModelsMap.keySet();
        System.out.println("Number of Network Nodes: " + outputVertices.size());
        
        
        for(int i = 0; i < sampleSize; i++){
            
            if((i > 0) && ((i % 20)==0))
               System.out.println("[" + new Date() + "]: " +  i  +  " sampling already perfomed...");
            
            HashMap<Vertex, LinkedList<Model>> outputToRandomModelsMap = new HashMap();
            //for each output node...
            for(Vertex outputVertex : outputVertices){
                //get a random number of inputs or get the number of inputs
                int vertexModelsNumber = outputToModelsMap.get(outputVertex).size();
                //int randomVertexModelsNumber = random.nextInt(maxNoOfInputs);
                
                LinkedList<Model> randomModels = new LinkedList();
                for(int j = 0; j < vertexModelsNumber; j++){ //for each input, associate a random rule
                    Model vertexModel = outputToModelsMap.get(outputVertex).get(j);
                    //LinkedList<Vertex> inputVertices = vertexModel.getInputNodes();
                    LinkedList<Vertex> inputVertices = new LinkedList();
                    int numberOfInputVertices = random.nextInt((maxNoOfInputs + 1));
                    // for each of the input vertices, scramble the input nodes Id...
                    for(int m = 0; m < numberOfInputVertices; m++){
                        String vertexId = exprs.getRowIds()[random.nextInt(exprs.getRowIds().length)];
                        Vertex vertex = new Vertex(vertexId);
                        inputVertices.add(vertex);
                    }
                    LinkedList<String> rulesList = getRandomRules(inputVertices.size());
                    double fit = random.nextDouble();
                    
                    Model randomModel = new Model(outputVertex, inputVertices, rulesList, fit);
                    randomModels.add(randomModel);
                }               
                outputToRandomModelsMap.put(outputVertex, randomModels);                
            }
            //run network simulation x (number of iterations)
            //LinkedList<Double> simulationValues = runSimulations();
            boolean includesPheno;
            includesPheno = outputIsPheno;
            Table samplingExprs = exprs;
            if(includesPheno){            
                samplingExprs = exprs.bind(phenoExprs, Table.BindType.ROW);
            }
            initialValues = simulator.getInitialValues(samplingExprs, initType);
                    
            Simulation simulation = new Simulation(outputToRandomModelsMap,
                                                      initialValues,
                                                      samplingExprs,
                                                      alpha,
                                                      maxIterations,
                                                      eCutOff,
                                                      includesPheno,
                                                      phenotypeId,
                                                      tanTransform,
                                                      logitTransform, 
                                                      k);
            simulation.run();
            //get the derived desired output value at the end of the simulation
            LinkedList<double[]> simulatedValuesList = simulation.getSimulatedValues();
            double[] simulatedValues = simulatedValuesList.getLast();
            
            randomDynPreds.add(simulatedValues[samplingExprs.getRowIndex(outputNode)]);
            
            // for trouble shooting
            if((i > 0) && ((i % 20)==0)){
               System.out.println("[" + new Date() + "]: " + "retrieved " +
                       simulatedValues.length + " simulated values at end of current sample simulation...");
               System.out.println("    Retrieved Values (current sampling simulation):\n        " + 
                                        Arrays.toString(simulatedValues));
               //double currentPredictions = (new double[randomDynPreds.size()]);
               System.out.println("    Current Prediction(s) so far:\n        "  + 
                                        randomDynPreds.toString());
            }
            
            /*
            if(nullPrinter){
                // do nothing...
            }else{
                //printer.println(simulatedValues[samplingExprs.getRowIndex(outputNode)]);
            }
            */  
            
            
        }//repeat process x (sample size)....       
        return(randomDynPreds);
    }
     
    public LinkedList<String> getRandomRules(int number){
        LinkedList<String> rulesList = new LinkedList();
        while(rulesList.size() < number){
            int[] rule = ruleTable.getRule(random.nextInt(27));
            rulesList.add(new Rule(rule).toString().replace("[", "").replace("]",""));
        }
        return(rulesList);    
    }
    
    public String[] getRandomInputs(int number, String[] possibleInputs){
        String[] inputs = new String[number];
        for(int i = 0; i < inputs.length; i++){
            inputs[i] = possibleInputs[random.nextInt(possibleInputs.length)];
        }
        return inputs;
    }
    
    public LinkedList<Rule> getRandomRulesList(int numberOfRules){
        LinkedList<Rule> rulesList = new LinkedList();
        while(rulesList.size() < numberOfRules){
            int[] rule = ruleTable.getRule(random.nextInt(27));
            rulesList.add(new Rule(rule));
        }
        return(rulesList);    
    }
    
    
    public LinkedList<Double> getRandomFitEstimates(String outputNode,
                                                    Table expMat,
                                                        int sampleSize, //number of randomization
                                                            boolean outputIsPheno,
                                                                Table phenoMat,
                                                                    boolean tanTransform,
                                                                        boolean logitTransform, 
                                                                            double k,
                                                                            PrintWriter printer,
                                                                                boolean nullPrinter){
        final int maxNoOfInputs = 5;
        LinkedList<Double> randomFits = new LinkedList();
        String[] rowIds = expMat.getRowIds();
        FitEvaluator fitEvaluator = new FitEvaluator();
        
        for(int i=0; i < sampleSize; i++){
            
            int numberOfInputs = random.nextInt(maxNoOfInputs) + 1; // get number of inputs           
            String[] inputNodes = new String[numberOfInputs]; // get the inputs...
            for(int j = 0; j < inputNodes.length; j++){
                String inputNode;
                do{
                    //inputNode = expMat.getRowIds()[rand.nextInt(rowIdsWithoutOutput.length)];
                    inputNode = rowIds[random.nextInt(rowIds.length)];
                    
                }while(Arrays.asList(inputNodes).contains(inputNode)); // repeat selecting an input node if it is already in the array of inputs
                inputNodes[j] = inputNode;      
            }            
            LinkedList<Rule> inputsRules = new LinkedList(); //get rulesList.
            while(inputsRules.size() < inputNodes.length){
                int[] rule = ruleTable.getRule(random.nextInt(27));
                inputsRules.add(new Rule(rule));
            }
            
            double computedFit = fitEvaluator.evaluateFit(outputNode, 
                                                            inputNodes, 
                                                            inputsRules, 
                                                            expMat, 
                                                            outputIsPheno, 
                                                            phenoMat, 
                                                            tanTransform,
                                                            logitTransform, 
                                                                k);
            
            if(!nullPrinter){
                printer.println(computedFit);
            }else{
                randomFits.add(computedFit);
            }            
            
            if((i % 20)==0)
               System.out.println("[" + new Date() + "]: " +  i  + " sampling already perfomed...");
            
        }//repeat        
        return(randomFits);
    }
    
    
    
    /**
     * 
     * 
    public static void main(String[] args) throws IOException, TableBindingException{
        
        System.out.println("[" + new Date() + "]: " + "Starting...");       
        Date start = new Date();
        long start_time = start.getTime();
        
        
        HashMap<String, String> config = ConfigFileReader.read(args[0]);
        LinkedList<Double> predictions = null;
        ProbabilityEngine probabilityEngine = new ProbabilityEngine();
        
        HashMap<Vertex, LinkedList<Model>> outputToModelsMap = ModelFitFileReader.read(config.get("fitFile"), 
                                                                                       Double.parseDouble(config.get("fitCutOff")));
        String initValues = config.get("initialOutputsValues");
        
        //        
        Simulator.INIT initType = null;        
        //determine initial outputs values enum type...
        if(initValues.equalsIgnoreCase("FIRST"))
            initType = Simulator.INIT.FIRST;
        else if(initValues.equalsIgnoreCase("RANDOM"))
            initType = Simulator.INIT.RANDOM;
        else if(initValues.equalsIgnoreCase("AVERAGE"))
            initType = Simulator.INIT.AVERAGE;
        else if(initValues.equalsIgnoreCase("ALL"))
            initType = Simulator.INIT.ALL;        
        
        //double[] initialValues;
        double alpha = Double.parseDouble(config.get("alpha"));
        int maxIterations = Integer.parseInt(config.get("maxIterations"));
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        String outputNode = config.get("outputNode");
        //int maxNoOfInputs = Integer.parseInt(config.get(""));
        Table exprs = new Table(config.get("exprsMatFile"), Table.TableType.DOUBLE);
        int sampleSize = Integer.parseInt(config.get("sampleSize")); //number of randomization
        boolean outputIsPheno = Boolean.parseBoolean(config.get("outputIsPheno"));
        String phenotypeId = config.get("phenotypeId");
        Table phenoExprs = new Table(config.get("phenoExprsMatFile"), Table.TableType.DOUBLE);
        boolean tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
        boolean logitTransform = Boolean.parseBoolean(config.get("logitTransform")); 
        double k = Double.parseDouble(config.get("kValue"));
        
        
        PrintWriter printer = null;
        boolean nullPrinter = true;
        if(Boolean.parseBoolean(config.get("printRandomization"))){
            printer = new PrintWriter(config.get("randomizationOutputFile"));
            nullPrinter = false;
            //for(double prediction : predictions){
            //    printer.println(prediction);
            //}
        }
        
        
        System.out.println("[" + new Date() + "]: " + "Getting random predictions...");  
        
        if(config.get("randomization").equalsIgnoreCase("NETWORK")){
            predictions = probabilityEngine.getRandomDynamicPredictions(outputToModelsMap, 
                                                                        initType, 
                                                                        alpha, 
                                                                        maxIterations, 
                                                                        eCutOff, 
                                                                        outputNode, //maxIterations, 
                                                                        exprs, 
                                                                        sampleSize, 
                                                                        outputIsPheno, 
                                                                        phenotypeId, 
                                                                        phenoExprs, 
                                                                        tanTransform, 
                                                                        logitTransform, 
                                                                        k,
                                                                        printer,
                                                                        nullPrinter);
        }
        if(config.get("randomization").equalsIgnoreCase("FIT")){
            predictions = probabilityEngine.getRandomFitEstimates(outputNode, 
                                                                    exprs, 
                                                                    sampleSize, 
                                                                    outputIsPheno, 
                                                                    phenoExprs, 
                                                                    tanTransform, 
                                                                    logitTransform, 
                                                                    k,
                                                                    printer,
                                                                    nullPrinter);
        }
        
        if(Boolean.parseBoolean(config.get("printRandomization")))
            printer.close();
        
        
        System.out.println("[" + new Date() + "]: " + "Printing random predictions..."); 
        
        
        System.out.println("\n" + "[" + new Date() + "]: " + "...Done!");        
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
    * 
    */
}
