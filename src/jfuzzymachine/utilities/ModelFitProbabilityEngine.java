/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Random;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.FuzzySet;
import jfuzzymachine.JFuzzyMachine;
import jfuzzymachine.Rule;
import jfuzzymachine.tables.RuleTable;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author paiyetan
 */
public class ModelFitProbabilityEngine {
    
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
                      double[] initialValues,
                      double alpha,
                          int maxIterations,
                            String outputNode,
                                int maxNoOfInputs,
                                    Table expMat,
                                        int sampleSize, //number of randomization
                                            boolean outputIsPheno,
                                                Table phenoMat,
                                                    boolean tanTransform,
                                                        boolean logitTransform, 
                                                            double k){
        
        LinkedList<Double> randomDynPreds = new LinkedList();
        RuleTable ruleTable = new RuleTable();
        Random rand = new Random();
        //get output nodes in network nodes...
        //Set<Vertex> outputVertex = outputToModelsMap.keySet();
        for(int i = 0; i < sampleSize; i++){
            HashMap<Vertex, LinkedList<Model>> randomOutputToModelsMap = new HashMap();
            //for each output node...
              //get a random number of inputs
              //for each input, associate a random rule
            //run network simulation x (number of iterations) 
            //get the derived desired output value at the end of the simulation
            //repeat process x (sample size)....
        }
        
        
        return(randomDynPreds);
    }
    
    public LinkedList<Double> getRandomFitEstimates(String outputNode,
                                                int maxNoOfInputs,
                                                    Table expMat,
                                                        int sampleSize, //number of randomization
                                                            boolean outputIsPheno,
                                                                Table phenoMat,
                                                                    boolean tanTransform,
                                                                        boolean logitTransform, 
                                                                            double k){
        LinkedList<Double> randomFits = new LinkedList();
        
        RuleTable ruleTable = new RuleTable();
        Random rand = new Random();
        //Random inputsRandomizer = new Random();
        String[] rowIds = expMat.getRowIds();
        //String[] rowIdsWithoutOutput = expMat.removeItem(rowIds, outputNode);
        FitEvaluator fitEvaluator = new FitEvaluator();
        
        for(int i=0; i < sampleSize; i++){
            
            int numberOfInputs = rand.nextInt(maxNoOfInputs) + 1; // get number of inputs           
            String[] inputNodes = new String[numberOfInputs]; // get the inputs...
            for(int j = 0; j < inputNodes.length; j++){
                String inputNode;
                do{
                    //inputNode = expMat.getRowIds()[rand.nextInt(rowIdsWithoutOutput.length)];
                    inputNode = rowIds[rand.nextInt(rowIds.length)];
                    
                }while(Arrays.asList(inputNodes).contains(inputNode)); // repeat selecting an input node if it is already in the array of inputs
                inputNodes[j] = inputNode;      
            }            
            LinkedList<Rule> inputsRules = new LinkedList(); //get rules.
            while(inputsRules.size() < inputNodes.length){
                int[] rule = ruleTable.getRule(rand.nextInt(27));
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
            randomFits.add(computedFit);
        }        
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
    
}
