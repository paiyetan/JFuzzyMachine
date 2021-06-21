/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import jfuzzymachine.tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class JFuzzyMachinex {
    //the mini JFuzzyMachine... runs a search on nodes with premapped input nodes..
    private HashMap<String, String[]> outputToMappedInputs;
    private Table exprs;
    private double fitCutOff;

    public JFuzzyMachinex(HashMap<String, String[]> outputToMappedInputs, Table exprs, double fitCutOff) {
        this.outputToMappedInputs = outputToMappedInputs;
        this.exprs = exprs;
        this.fitCutOff = fitCutOff;
    }
     
    public void search(){
        ESearchEngine esearch = new ESearchEngine(); // NOTE: to avoid Heap overflow error, use this only for printing...
        //HashMap<String, String[]> regulonsMap = new HashMap();
        
    }  
    
    static class MappedInputsReader{
        HashMap<String, String[]> read(String filePath) throws FileNotFoundException, IOException{
            HashMap<String, String[]> outputToMappedInputs = new HashMap();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int lineIndex = 0;
            while((line = reader.readLine())!=null){
                if(lineIndex > 0){
                    String[] lineArr = line.split("\t");
                    String outputNode = lineArr[0];
                    String[] inputNodes = lineArr[1].split(", ");
                    outputToMappedInputs.put(outputNode, inputNodes);
                }
                lineIndex++;
            }
            return outputToMappedInputs;
        }       
    }
        
    public static void main(String[] args) throws IOException{
        String mappedInputsFile = args[0];
        String exprMatFile = args[1];
        double fitCutOff = Double.parseDouble(args[2]);
        
        MappedInputsReader ireader = new MappedInputsReader();
        HashMap<String, String[]> outputToMappedInputs = ireader.read(mappedInputsFile);
        Table exprs = new Table(exprMatFile, Table.TableType.DOUBLE);
        
        JFuzzyMachinex jfuzMx = new JFuzzyMachinex(outputToMappedInputs, exprs, fitCutOff);
        jfuzMx.search();
    }
    
    
 
}
