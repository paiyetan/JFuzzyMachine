/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.simulation;

import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author aiyetanpo
 */
public class Simulation {

    public enum ExpressionType {PHENOTYPE, GENOTYPE};
    public enum KnockoutType {SINGLE, DOUBLE, TRIPLE};
    
    private double alpha;
    private int maxIterations; 
    private final double eCutOff;// = 0d;
    
    private HashMap<Vertex, LinkedList<Model>> outputsToModelsMap;
    private LinkedList<double[]> simulatedValues;
    private LinkedList<double[]> deltaValuesList;
    private double[] initialTableValues;
    private Table exprs;

    private int iteration;
    private Fuzzifier fzr;
    
    private boolean includesPheno;
    private String phenotypeId;
    
    private boolean tanTransform;
    private boolean logitTransform; 
    private double k;
    
    private boolean simulateKnockout;
    //private String geneToKnockout;
    
    private String[] knockouts;
    
    public Simulation(HashMap<Vertex, LinkedList<Model>> outputToModelsMap,
                      double[] initialValues,
                      Table exprs,
                      double alpha,
                      int maxIterations,
                      double eCutOff,
                      boolean includesPheno,
                      String phenotypeId,
                      boolean tanTransform,
                      boolean logitTransform, 
                      double k)
    {
        simulatedValues = new LinkedList();
        deltaValuesList = new LinkedList();
        this.fzr = new Fuzzifier();
        this.outputsToModelsMap = outputToModelsMap;
        this.initialTableValues = initialValues;
        this.exprs = exprs;
        this.alpha = alpha;
        this.maxIterations = maxIterations;
        this.eCutOff = eCutOff;
        this.iteration = 0;
        
        this.includesPheno = includesPheno;
        this.phenotypeId = phenotypeId;
        
        this.tanTransform = tanTransform;
        this.logitTransform = logitTransform;
        this.k = k;
        
        this.simulateKnockout = false;//to initiate as not to return nullValuePointer Exception in downstream call
        this.knockouts = null;
        
    }
    
    public void setSimulateKnockout(boolean simulateKnockout){
        this.simulateKnockout = simulateKnockout;
    }

    public void setGeneToKnockOut(String geneToKnockout){
        //this.geneToKnockout = geneToKnockout;
        this.knockouts = new String[1];
        knockouts[0] = geneToKnockout;
    }
    
    public void setKnockOuts(String[] geneKnockouts){
        this.knockouts = geneKnockouts;
    }

    public void run(){
        
        //System.out.println(Thread.currentThread().getName());
        /**
         * Calculate the next value, I_1 of each node by the initial condition and 
           the fuzzy relations inferred from the data; 
           calculate next iteration values linear combination of 
           the inferred values (I_n) and the initial values (I_n-1) as follows:
                            I_n+1 =  α I_n  + (1- α) l_n-1

        */
        double[] currentValues; // current/initial values, I_n-1
        double[] inferredValues; // inferred values, I_n
        double[] nextValues; // derived values, I_n+1 I_n+1 = (alpha * I_n) + ((1-alpha)*I_n-1 ) 
        double[] deltaValues;
        
        //System.out.println("# of nodes in outputsToModelsMap: " + outputsToModelsMap.keySet().size());
        // to ensure the next process is done just once for mapped models..
        Set<Vertex> outputNodes = outputsToModelsMap.keySet();
        for(Vertex outputNode : outputNodes){
            Collections.sort(outputsToModelsMap.get(outputNode));
        }
        //copy values from initial table values to current values...
        //currentValues = new double[initialTableValues.length];
        currentValues = initialTableValues;
        
        //debugging,,,
        //System.out.println("# of nodes in outputsToModelsMap: " + outputsToModelsMap.keySet().size());
        //for(Vertex outputNode : outputNodes){
        //    System.out.printf("  output node found: %s", outputNode.getId());
        //} 
        //int knockedOutGeneIndex; // 
        while (iteration < maxIterations){
            
            if(this.simulateKnockout){           
                Minimum min = new Minimum(currentValues);
                //knockedOutGeneIndex = exprs.getRowIndex(geneToKnockout); //get index of knockedout get                                
                //currentValues[knockedOutGeneIndex] = min.minimum();                
                for(String knockout : knockouts){
                    int knockoutGeneIndex = exprs.getRowIndex(knockout); //get row index of a knockedout gene...
                    
                    try{
                        currentValues[knockoutGeneIndex] = min.minimum();
                    }catch(ArrayIndexOutOfBoundsException e){
                        System.out.println("ArrayIndexOutOfBoundsException@");
                        System.out.println("           knockout: " + knockout);
                        System.out.println("  knockoutGeneIndex: " + knockoutGeneIndex);
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
            
            //infer new values from current values...
            inferredValues = new double[currentValues.length];
            nextValues = new double[inferredValues.length];
            deltaValues = new double[inferredValues.length]; // I_n - I_n-1
            
            for(int i = 0; i < currentValues.length; i++){
                String outputNodeId = exprs.getRowIds()[i].trim();//
                Vertex outputNode = new Vertex(outputNodeId);
                
                //debugging line...
                //System.out.printf("itr# %d, i = %d, outputNode: %s was found\n", iteration, i, outputNodeId);
                
                if(outputsToModelsMap.containsKey(outputNode)){
                    Model bestFitModel = outputsToModelsMap.get(outputNode).getLast();
                    //if(bestFitModel != null){
                    LinkedList<Vertex> inputNodes = bestFitModel.getInputNodes();
                    LinkedList<String> rules = bestFitModel.getRules();
                    double zy1 = 0, zy2 = 0, zy3 = 0;
                    int ruleIndex = 0;
                    for (Vertex inputNode : inputNodes) {

                        String inputNodeId = inputNode.getId(); //get input fuzzySet Value, but first get input node id
                        int inputIndex = exprs.getRowIndex(inputNodeId); // then, get the row index for input node
                        //FuzzySet fzSet = fMat[inputIndex][j]; // get input fuzzySet...
                        double inputNodeValue = currentValues[inputIndex];
                        FuzzySet fzSet = fzr.fuzzify(inputNodeValue);
                        String rule = rules.get(ruleIndex);// get rule associated with input...

                        FuzzySet fzSet_i = fzr.applyRule(fzSet, rule); //apply rule
                        zy1 = zy1 + fzSet_i.getY1();
                        zy2 = zy2 + fzSet_i.getY2();
                        zy3 = zy3 + fzSet_i.getY3();

                        ruleIndex++;
                    }

                    FuzzySet fzz = new FuzzySet(zy1, zy2, zy3);
                    double inferredValue; // I_n NOTE: inferred value will be dependent on whether it is predicting a phenotype output..
                    if(includesPheno && outputNodeId.equalsIgnoreCase(phenotypeId)){
                        inferredValue = fzr.deFuzzify(fzz, tanTransform, logitTransform, k);
                    }else{
                        inferredValue = fzr.deFuzzify(fzz); 
                    }
                    
                    inferredValues[i] = inferredValue;  
                    deltaValues[i] = inferredValues[i] - currentValues[i];
                    //compute next value
                    nextValues[i] = (alpha * inferredValues[i]) + ((1- alpha) * currentValues[i]);
                    
                    //debugging purpose...
                    //System.out.printf("  At iter# %d: found output: %s; zy1: %f; zy2: %f; zy3: %f; infVal = %f, curVal = %f, dVal = %f, nVal = %f \n", 
                    //                    iteration, outputNodeId, zy1, zy2, zy3, inferredValues[i], currentValues[i], deltaValues[i], nextValues[i]);
                    
                } else { // it implies the node (output node) in question has no inputs, therefore it's value should remain the same through all iteration
                    inferredValues[i] = currentValues[i];  
                    nextValues[i] = currentValues[i];
                    deltaValues[i] = inferredValues[i] - currentValues[i];
                }
            }
            
            deltaValuesList.add(deltaValues);
            //double[] pValues = new double[currentValues.length];
            //System.arraycopy(currentValues, 0, pValues, 0, currentValues.length);
            //simulatedValues.add(pValues);
            
            //currentValues = nextValues;
            //System.arraycopy(nextValues, 0, currentValues, 0, nextValues.length);
            simulatedValues.add(currentValues);
            currentValues = nextValues;
          
            iteration++;
        }
    }

    
    public LinkedList<double[]> getSimulatedValues(){
        return this.simulatedValues;
    }   
    
    public LinkedList<double[]> getDeltaValuesList(){
        return this.deltaValuesList;
    }
    
    
    
    
    class Minimum{
        double[] arr;
        
        Minimum(double[] arr){
            this.arr = arr;
        }
        
        double minimum(){
            double min = arr[0];
            for(int i = 1; i < arr.length; i++)
                if(arr[i] < min)
                    min = arr[i];               
            return(min);
        }
    }
}
