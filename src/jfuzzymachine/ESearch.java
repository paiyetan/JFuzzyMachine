/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;


/**
 *
 * @author aiyetanpo
 */
public class ESearch {

    private LinkedList<ESearchResult> results;

    @SuppressWarnings("NonPublicExported")
    public ESearch(LinkedList<ESearchResult> results) {
        this.results = results;
    }

    public ESearch() {
        this.results = new LinkedList();
    }
    
    @SuppressWarnings("NonPublicExported")
    public void add(ESearchResult result){
        results.add(result);
    }

    public LinkedList<ESearchResult> getResults() {
        return results;
    }
    
    public ESearchResult get(int index){
        return results.get(index);
    } 
    
    public void printESearch(String out) throws FileNotFoundException{
        PrintWriter printer = new PrintWriter(out);
        //print table header
        printESearchResultFileHeader(printer);
        for(ESearchResult result : results){
            printESearchResult(result, printer);
        }        
        printer.close();
    }
    
    public void printESearchResultFileHeader(PrintWriter printer){
        printer.println("Output\tNumberOfInput(s)\tInput(s)\tRuleInde(ces)x\tError(E)");
    }

    @SuppressWarnings("NonPublicExported")
    public void printESearchResult(ESearchResult result, PrintWriter printer) {
        
        printer.println(result.getOutputGene()  + "\t" + 
                        result.getNumOfInputs() + "\t" +
                        Arrays.toString(result.getInputGenes()) + "\t" +
                        Arrays.toString(result.getRules()) + "\t" +
                        result.getError()
                        );
        
    }
    

}
