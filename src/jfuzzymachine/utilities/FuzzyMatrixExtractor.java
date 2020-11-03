/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.FuzzySet;
import jfuzzymachine.JFuzzyMachine;
import jfuzzymachine.tables.Table;
import org.apache.commons.math3.util.Precision;

/**
 *
 * @author paiyetan
 */
public class FuzzyMatrixExtractor {
    
    private final Table exprs;
    private final FuzzySet[][] exprsFMat;
    private final Fuzzifier fuzzifier;
    private final String exprsFilePath;
    
    
    public FuzzyMatrixExtractor(String exprsFilePath) throws IOException{
        //JFuzzyMachine jfuzz = new JFuzzyMachine(config);
        fuzzifier = new Fuzzifier();
        this.exprs = new Table(exprsFilePath, Table.TableType.DOUBLE);
        this.exprsFMat = fuzzifier.getFuzzyMatrix(exprs, JFuzzyMachine.ExpressionType.GENOTYPE);
        this.exprsFilePath = exprsFilePath;
        
    }
    
    public void printFuzzyMatrix() throws FileNotFoundException{
        String outFile = exprsFilePath.replace(".txt", "").replace(".tsv", "");
        outFile = outFile + ".fMat"; 
        PrintWriter printer = new PrintWriter(outFile);
        
        // print header...
        printer.print("Features");
        for(String columnID : exprs.getColumnIds()){
           printer.print("\t" + columnID);
        }
        printer.print("\n");
        
        // print the body
        for(int i = 0; i < exprs.getRowIds().length; i++){
            printer.print(exprs.getRowIds()[i]);
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                //printer.print("\t" + Arrays.toString(exprsFMat[i][j].getSetAsArray()));
                printer.print("\t" + getFuzzySetString(exprsFMat[i][j]));
            }
            printer.print("\n");
        }        
        printer.close();
    }
        
    private String getFuzzySetString(FuzzySet fuzzySet) {
        String fstr;        
        double[] fs = fuzzySet.getSetAsArray();
        for(int i = 0; i < fs.length; i++){
            fs[i] = Precision.round(fs[i], 4);
        }
        fstr = Arrays.toString(fs);
        return fstr;
    }
    
    public static void main(String[] args) throws IOException{
        System.out.println("Starting...");
        String exprsFile = args[0];
        FuzzyMatrixExtractor fMExtr = new FuzzyMatrixExtractor(exprsFile);
        fMExtr.printFuzzyMatrix();
        System.out.println("...Done!");
    }

    
}
