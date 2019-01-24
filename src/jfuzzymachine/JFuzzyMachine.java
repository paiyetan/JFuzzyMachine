/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.IOException;
import java.util.HashMap;
import table.Table;
import utilities.ConfigFileReader;

/**
 *
 * @author aiyetanpo
 */
public class JFuzzyMachine {
    
    public JFuzzyMachine(Table exprs, HashMap<String, String> config){
        //Normalize
        //Fuzzyfy
        FuzzySet[][] fMat = getFuzzyMatrix(exprs);
        
        //Search...
        //Defuzzyfy...
        //save result
    }
    
    public FuzzySet fuzzify(double value){
         FuzzySet fz;
        // y1 = (value < 0) ? -value : 0 ; 
        // y2 = 1 - Math.abs(value);
        // y3 = (value < 0) ? 0: value ;
        double y1 = (value < 0) ? -value : 0 ; 
        double y2 = 1 - Math.abs(value);
        double y3 = (value <= 0) ? 0: value ;
        
        fz = new FuzzySet(y1, y2, y3);
        return fz;
    }
    
    public double deFuzzify(FuzzySet fz){
        double dfz;
        dfz = (fz.getY3() - fz.getY1())/(fz.getY1() + fz.getY2() + fz.getY3());        
        return dfz;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        // Read input file...
        ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = cReader.read(args[0]);
        
        Table exprs = new Table(config.get("inputFile"), Table.TableType.DOUBLE);
        JFuzzyMachine jfuzz = new JFuzzyMachine(exprs, config);
    }

    private FuzzySet[][] getFuzzyMatrix(Table exprs) {
        //Table fuzzyTable;
        double[][] mat = exprs.getMatrix(Table.TableType.DOUBLE);
        FuzzySet[][] fMat = new FuzzySet[exprs.getRowIds().length][exprs.getColumnIds().length];
        
        for(int i = 0; i < exprs.getRowIds().length; i++){
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                fMat[i][j] = this.fuzzify(mat[i][j]);
            }
        }
        return fMat;
    }

    
}
