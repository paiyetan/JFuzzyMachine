/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.PrintWriter;
import java.util.HashMap;
import tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class InputsCombination {
    
    private final int[] inputsCombination;
    private final double eCutOff;
    private final double[] outputGeneExpValues;
    private final double deviationSquaredSum;    
    private final Table exprs;
    private final FuzzySet[][] fMat;
    //private final Fuzzifier fuzzifier;
    private final String[] otherGenes;
    private final String outputGene;
    private final Table phenoExprs;
    private final FuzzySet[][] phenoFMat;
    private final boolean modelPhenotype;
     
    
    public InputsCombination(int[] inputsCombination, 
                                double eCutOff, 
                                double[] outputGeneExpValues, 
                                double deviationSquaredSum, 
                                Table exprs, 
                                FuzzySet[][] fMat,
                                String[] otherGenes,
                                String outputGene,
                                Table phenoExprs, 
                                FuzzySet[][] phenoFMat,
                                boolean modelPhenotype
    ) {
        
        this.inputsCombination = inputsCombination;
        this.eCutOff = eCutOff;
        this.outputGeneExpValues = outputGeneExpValues;
        this.deviationSquaredSum = deviationSquaredSum;
        this.exprs = exprs;
        this.fMat = fMat;
        //fuzzifier = new Fuzzifier();
        this.otherGenes = otherGenes;
        this.outputGene = outputGene;
        this.phenoExprs = phenoExprs;
        this.phenoFMat = phenoFMat;
        this.modelPhenotype = modelPhenotype;       
    }

    public void searchHelper5(PrintWriter printer, HashMap<String, String> config) {
        ESearchEngine esearch = new ESearchEngine();
        esearch.searchWithFiveInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs, 
                                             phenoFMat,
                                             modelPhenotype);
        
    }
    
    public void searchHelper4(PrintWriter printer, HashMap<String, String> config) {
        ESearchEngine esearch = new ESearchEngine();
        esearch.searchWithFourInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs, 
                                             phenoFMat,
                                             modelPhenotype);
        
    }
    
    public void searchHelper3(PrintWriter printer, HashMap<String, String> config) {
        ESearchEngine esearch = new ESearchEngine();
        esearch.searchWithThreeInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs, 
                                             phenoFMat,
                                             modelPhenotype);
        
    }
    
    public void searchHelper2(PrintWriter printer, HashMap<String, String> config) {
        ESearchEngine esearch = new ESearchEngine();
        esearch.searchWithTwoInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs, 
                                             phenoFMat,
                                             modelPhenotype);
        
    }
    
    public void searchHelper1(PrintWriter printer, HashMap<String, String> config) {
        ESearchEngine esearch = new ESearchEngine();
        esearch.searchWithOneInput(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs, 
                                             phenoFMat,
                                             modelPhenotype);
        
    }
    
    
    
    
    
    
}

