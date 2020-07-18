/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import jfuzzymachine.utilities.graph.Model;
import jfuzzymachine.utilities.graph.Vertex;
import jfuzzymachine.utilities.simulation.Fuzzifier;
import jfuzzymachine.utilities.simulation.FuzzySet;
import jfuzzymachine.utilities.simulation.Table;
import jfuzzymachine.exceptions.TableBindingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
//import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author aiyetanpo
 */
public class ModelValidator {
    
    private Table validationTable = null; 
    // a table showing the estimated value of rowIds in respective perturbation given the [best] fitted models for the output 
    private HashMap<String, Double> outputToRecomputedFitMap = null;
    
    public void validate(Table exprs, 
                            HashMap<Vertex, LinkedList<Model>> outputsToModelsMap,
                                boolean includesPheno,
                                    String phenotypeId,
                                        boolean tanTransform, boolean logitTransform,
                                            double k){
               
        // get elements of validation Table (rowIds, colIds, and matrix/table data...  
        Set<Vertex> vertices = outputsToModelsMap.keySet();
        String[] rowIds = new String[vertices.size()];
        String[] colIds = exprs.getColumnIds();
        double[][] dMatrix = new double[rowIds.length][colIds.length];
        Fuzzifier fzr = new Fuzzifier();
        
        FuzzySet[][] fMat = fzr.getFuzzyMatrix(exprs);
        
        int rowIndex = 0; 
        for(Vertex outputNode : vertices){
            rowIds[rowIndex] = outputNode.getId(); // populate table rowIds...
            LinkedList<Model> mappedModels = outputsToModelsMap.get(outputNode);
            
            // troubleshooting...
            //System.out.println(outputNode.getId() + " mapped models: " + mappedModels.size());
            //if(mappedModels.size()==1){
            //    System.out.println("   " + mappedModels.getLast().getInputNodesString() +
            //            ", " + mappedModels.getLast().getRulesString());
            //}
            
            Collections.sort(mappedModels);
            Model bestFitModel = mappedModels.getLast();
            LinkedList<Vertex> inputNodes = bestFitModel.getInputNodes();
            LinkedList<String> rules = bestFitModel.getRules();
            
            //for troubleshooting...
            //System.out.println("Found best model: OutputNode, " + bestFitModel.getOutputNode().getId() +
            //                           "; InputNodes: " + bestFitModel.getInputNodesString() +
            //                           "; Rules: " + bestFitModel.getRulesString());
            
            //String output = outputNode.getId();
            for(int j = 0; j < colIds.length; j++){
                
                double zy1 = 0;
                double zy2 = 0;
                double zy3 = 0;
                int ruleIndex = 0;
                for (Vertex inputNode : inputNodes) {
                    
                    String inputNodeId = inputNode.getId(); //get input fuzzySet Value, but first get input node id
                    int inputIndex = exprs.getRowIndex(inputNodeId); // then, get the row index for input node
                    FuzzySet fzSet = null; // get input fuzzySet...
                    String rule = null;                    
                    try{
                        fzSet = fMat[inputIndex][j];
                        rule = rules.get(ruleIndex);// get rule associated with input...
                    }catch(ArrayIndexOutOfBoundsException e){
                        //throw new ArrayIndexBoundsException(ruleIndex, inputIndex, inputNode, outputNode);
                        System.out.println("Caught Exception: ArrayIndexOutOfBoundsException");
                        System.out.println("       ruleIndex: " + ruleIndex);
                        System.out.println("      inputIndex: " + inputIndex);
                        System.out.println("       inputNode: " + inputNode.getId());
                        System.out.println("      outputNode: " + outputNode.getId());
                        System.out.println("               j: " + j);
                        e.printStackTrace();
                        System.exit(1);
                    }
                    
                    FuzzySet fzSet_i = fzr.applyRule(fzSet, rule); //apply rule
                    zy1 = zy1 + fzSet_i.getY1();
                    zy2 = zy2 + fzSet_i.getY2();
                    zy3 = zy3 + fzSet_i.getY3();
                    
                    ruleIndex++;
                }
                
                FuzzySet fzz = new FuzzySet(zy1, zy2, zy3);
                double inferredValue;// = fzr.deFuzzify(fzz); // I_n
                if(includesPheno && outputNode.getId().equalsIgnoreCase(phenotypeId)){
                    inferredValue = fzr.deFuzzify(fzz, tanTransform, logitTransform, k);
                }else{
                    inferredValue = fzr.deFuzzify(fzz); 
                }                    
                dMatrix[rowIndex][j] = inferredValue;
            }
            
            rowIndex++;
            
        }
        
        validationTable = new Table(rowIds, colIds, dMatrix);
        //return validationTable;
    }

    public Table getValidationTable() {
        return validationTable;
    }
    
    public void printValidationTable(String outputFile, Table.TableType tbType) throws FileNotFoundException{
        validationTable.print(outputFile, tbType);
    }
    
    private void recomputeFit(Table exprs) {
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        outputToRecomputedFitMap = new HashMap();
        String[] outputs = validationTable.getRowIds();
        
        for(String output : outputs){
            // get predicted values associated with the output from the validation table
            double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(output), Table.TableType.DOUBLE);
            double[] predValues = validationTable.getRow(validationTable.getRowIndex(output), Table.TableType.DOUBLE);            
            double deviationSquaredSum = 0;
            double residualSquaredSum = 0; 
            double xBar;
            // compute deviation sum squared...
            Mean mean = new Mean();   
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
            
            for(int i = 0; i < predValues.length; i++)
                residualSquaredSum = residualSquaredSum + Math.pow((exprs.getDoubleMatrix()[exprs.getRowIndex(output)][i] - predValues[i]), 2);
            
            // compute error..
            double err = 1 - (residualSquaredSum/deviationSquaredSum);
            outputToRecomputedFitMap.put(output, err);
        }
    }
    
    public void printRecomputedFit(String outputFile) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(outputFile);
        //print headder
        printer.println("Feature\tValidationFit");
        Set<String> outputs = this.outputToRecomputedFitMap.keySet();
        for(String output : outputs)
            printer.println(output + "\t" + this.outputToRecomputedFitMap.get(output));
        
        printer.close();
    }
    
    
    
    public static void main(String[] args) throws IOException, TableBindingException{
        
        
                
        System.out.println("Starting...");       
        Date start = new Date();
        long start_time = start.getTime();
        
        String exprsToValidate;
        String fittedModelsFile;
        double fitCutOff;
        HashMap<Vertex, LinkedList<Model>> outputsToModelsMap;
        Table exprs;
        
        boolean includesPheno;
        String phenotypeId;
        String phenoExprsMatFile;
        boolean tanTransform; 
        boolean logitTransform;
        double k;
        
        // recomputeFit=TRUE
        boolean recomputeFit; // to quantitatively determine how well the model fits an independent dataset or re-validate/verify computed fit for training data
        
        String validationType;
        
        
        
        System.out.println("Reading configs...");  
        HashMap<String, String> config = ConfigFileReader.read(args[0]);
        exprsToValidate = config.get("exprsToValidate");
        fittedModelsFile = config.get("fitFile");
        fitCutOff = Double.parseDouble(config.get("fitCutOff"));
        
        includesPheno = Boolean.parseBoolean(config.get("includesPheno"));
        phenotypeId = config.get("phenotypeId"); 
        phenoExprsMatFile = config.get("phenoExprsMatFile");
        
        tanTransform = Boolean.parseBoolean(config.get("tanTransform"));
        logitTransform = Boolean.parseBoolean(config.get("logitTransform"));
        k = Double.parseDouble(config.get("kValue"));
        
        recomputeFit = Boolean.parseBoolean(config.get("recomputeFit"));
        
        
        validationType = config.get("validationType");
                
        
        exprs = new Table(exprsToValidate, Table.TableType.DOUBLE);
        if(includesPheno){            
            Table phenoExprs = new Table(phenoExprsMatFile, Table.TableType.DOUBLE); //merge pheno and exprs Table into a single table
            exprs = exprs.bind(phenoExprs, Table.BindType.ROW);
        }
        
        
        System.out.println("Loading models...");  
        outputsToModelsMap = ModelFitFileReader.read(fittedModelsFile, fitCutOff);
        
        System.out.println("Validating models...");  
        ModelValidator validator = new ModelValidator();
        validator.validate(exprs, 
                            outputsToModelsMap, 
                                includesPheno,
                                    phenotypeId,
                                        tanTransform, logitTransform,
                                            k);        
        
        System.out.println("Printing validations...");
        
        
        String validationOutputFile = fittedModelsFile.replace(".fit", "").replace(".fit2", "") + "." + validationType + ".val";
        validator.printValidationTable(validationOutputFile, Table.TableType.DOUBLE);       
        
        if(recomputeFit){ //
            System.out.println("Recomputing fit (errors)...");
            validator.recomputeFit(exprs);
            
            System.out.println("Printing Recomputed fit (errors...");
            String vFitFile = fittedModelsFile.replace(".fit", "").replace(".fit2", "") + "." + validationType + ".vfit";
            validator.printRecomputedFit(vFitFile);
        }
            
        
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
