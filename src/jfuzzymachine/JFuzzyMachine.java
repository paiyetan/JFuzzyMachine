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
        //Search...
        //Defuzzyfy...
        //save result
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

    
}
