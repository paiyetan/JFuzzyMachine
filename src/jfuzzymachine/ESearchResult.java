/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

/**
 *
 * @author aiyetanpo
 */
public class ESearchResult{

    private final String outputGene;
    private final int numOfInputs;
    private final String[] inputGenes;
    private final String[] rules;
    private final double error;

    public ESearchResult(
                         String outputGene, 
                         int numOfInputs, 
                         String[] inputGenes, 
                         String[] rules, 
                         double error
    ) {

        this.outputGene = outputGene;
        this.numOfInputs = numOfInputs;
        this.inputGenes = inputGenes;
        this.rules = rules;
        this.error = error;

    }

    public String getOutputGene() {
        return outputGene;
    }

    public int getNumOfInputs() {
        return numOfInputs;
    }

    public String[] getInputGenes() {
        return inputGenes;
    }

    public String[] getRules() {
        return rules;
    }

    public double getError() {
        return error;
    }                
    
}
