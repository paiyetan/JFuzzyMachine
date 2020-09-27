/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.FuzzySet;
import jfuzzymachine.JFuzzyMachine;
import jfuzzymachine.Rule;
import jfuzzymachine.exceptions.TableBindingException;
import jfuzzymachine.tables.RuleTable;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import jfuzzymachine.utilities.simulation.Simulation;
import jfuzzymachine.utilities.simulation.Simulator;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author paiyetan
 */
public class ProbabilityEngine {
    
    private final RuleTable ruleTable = new RuleTable();
    private final Random random = new Random();
            
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
        
        //get output nodes in network nodes...
        Set<Vertex> outputVertices = outputToModelsMap.keySet();
        for(int i = 0; i < sampleSize; i++){
            HashMap<Vertex, LinkedList<Model>> outputToRandomModelsMap = new HashMap();
            //for each output node...
            for(Vertex outputVertex : outputVertices){
                //get a random number of inputs or get the number of inputs
                int vertexModelsNumber = outputToModelsMap.get(outputVertex).size();
                LinkedList<Model> randomModels = new LinkedList();
                for(int j = 0; j < vertexModelsNumber; j++){ //for each input, associate a random rule
                    Model vertexModel = outputToModelsMap.get(outputVertex).get(j);
                    LinkedList<Vertex> inputVertices = vertexModel.getInputNodes();
                    LinkedList<String> rulesList = getRandomRules(inputVertices.size());
                    double fit = vertexModel.getFit();
                    
                    Model randomModel = new Model(outputVertex, inputVertices, rulesList, fit);
                    randomModels.add(randomModel);
                }               
                outputToRandomModelsMap.put(outputVertex, randomModels);                
            }
            //run network simulation x (number of iterations)
            //LinkedList<Double> simulationValues = runSimulations();
            boolean includesPheno;
            includesPheno = outputIsPheno;
            if(includesPheno){            
                exprs = exprs.bind(phenoExprs, Table.BindType.ROW);
            }
            initialValues = simulator.getInitialValues(exprs, initType);
                    
            Simulation simulation = new Simulation(outputToModelsMap,
                                                      initialValues,
                                                      exprs,
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
            
            if(!nullPrinter){
                printer.println(simulatedValues[exprs.getRowIndex(outputNode)]);
            }else{
                randomDynPreds.add(simulatedValues[exprs.getRowIndex(outputNode)]);
            }
            
            if((i % 20)==0)
               System.out.println(i + " sampling already perfomed...");
            
        }//repeat process x (sample size)....       
        return(randomDynPreds);
    }
     
    public LinkedList<String> getRandomRules(int numberOfRules){
        LinkedList<String> rulesList = new LinkedList();
        while(rulesList.size() < numberOfRules){
            int[] rule = ruleTable.getRule(random.nextInt(27));
            rulesList.add(new Rule(rule).toString().replace("[", "").replace("]",""));
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
               System.out.println(i + " sampling already perfomed...");
            
        }//repeat        
        return(randomFits);
    }
    
    
    class FitEvaluator{
        
        double evaluateFit(String outputNode,
                            String[] inputNodes,
                                LinkedList<Rule> rules,
                                    Table expMat,
                                        boolean outputIsPheno,
                                                Table phenoMat,
                                                    boolean tanTransform,
                                                       boolean logitTransform, 
                                                           double k){
            double fit = 0;
            Fuzzifier fuzzifier = new Fuzzifier();
            //get the output expression values...
            double[] outputExprValues = null;
            if(outputIsPheno){
                outputExprValues = phenoMat.getRow(phenoMat.getRowIndex(outputNode), Table.TableType.DOUBLE);
            }else{
                outputExprValues = expMat.getRow(expMat.getRowIndex(outputNode), Table.TableType.DOUBLE);
            }
            
            double deviationSquaredSum = 0;
            double residualSquaredSum = 0; 
            Mean mean = new Mean();
            double xBar = mean.evaluate(outputExprValues); // average expression value for output outputGene
            for(int i = 0; i < outputExprValues.length; i++){
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputExprValues[i] - xBar), 2);
            }
                        
            for(int index = 0; index < outputExprValues.length; index++){                                                                                                                                                        
                //get fuzzyValues of input genes,                 
                double[] expValues = expMat.getColumn(index, Table.TableType.DOUBLE);
                //get expression value of input features..
                double[] inputsExpValues = new double[inputNodes.length];
                for(int i = 0; i < inputsExpValues.length; i++){
                    inputsExpValues[i] = expValues[expMat.getRowIndex(inputNodes[i])];             
                }
                //get the fuzzyset array 
                FuzzySet[] inputsFuzzArr = new FuzzySet[inputsExpValues.length];
                for(int i = 0; i < inputsFuzzArr.length; i++){
                    inputsFuzzArr[i] = fuzzifier.fuzzify(inputsExpValues[i], JFuzzyMachine.ExpressionType.GENOTYPE);
                }
                FuzzySet zValue = fuzzifier.getZValue(inputsFuzzArr, rules);
                //get defuzzified (xCaretValue) value of Z @ position index
                //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
                double dfz = fuzzifier.deFuzzify(zValue, tanTransform, logitTransform, k);
                // compute residual and cummulative residual squared sum...
                residualSquaredSum = residualSquaredSum + Math.pow((outputExprValues[index] - dfz), 2);                                        
            }
            // compute error..
            fit = 1 - (residualSquaredSum/deviationSquaredSum);
            
            return fit;
        }
        
    }
            
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
}
