/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.PrintWriter;
import tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class InputsCombination {
    
    private int[] inputsCombination;
    private double eCutOff;
    private double[] outputGeneExpValues;
    private double deviationSquaredSum;    
    private Table exprs;
    private FuzzySet[][] fMat;
    private Fuzzifier fuzzifier;
    private String[] otherGenes;
    private String outputGene;
    
    public InputsCombination(int[] inputsCombination, 
                                double eCutOff, 
                                double[] outputGeneExpValues, 
                                double deviationSquaredSum, 
                                Table exprs, 
                                FuzzySet[][] fMat,
                                String[] otherGenes,
                                String outputGene
    ) {
        
        this.inputsCombination = inputsCombination;
        this.eCutOff = eCutOff;
        this.outputGeneExpValues = outputGeneExpValues;
        this.deviationSquaredSum = deviationSquaredSum;
        this.exprs = exprs;
        this.fMat = fMat;
        fuzzifier = new Fuzzifier();
        this.otherGenes = otherGenes;
        this.outputGene = outputGene;
               
    }

    public void searchHelper5(PrintWriter printer) {
        
    }
    
    public void searchHelper4(PrintWriter printer) {
        
    }
    
    public void searchHelper3(PrintWriter printer) {
        
    }
    
    public void searchHelper2(PrintWriter printer) {
        
    }
    
    public void searchHelper1(PrintWriter printer) {
        
    }
    
    
    
    
    
    
}

