/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jfuzzymachine.tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class RandomFitsGenerator {
    
    public static void main(String[] args) throws IOException{
        
        System.out.println("Starting...");
        String exprMatFile = args[0];
        final String outputDir = args[1];
        final int numberOfFits = Integer.parseInt(args[2]);
        
        if(!(new File(outputDir).exists())){
            new File(outputDir).mkdirs();
        }
              
        final Table exprsTable = new Table(exprMatFile, Table.TableType.DOUBLE);
        String[] outputs = exprsTable.getRowIds();
        LinkedList<String> outputsList = new LinkedList();
        outputsList.addAll(Arrays.asList(outputs));
        
        System.out.println("Generating...");
        outputsList.parallelStream().forEach((outputNodeId) -> {
            //Thread.currentThread().setName(outputNodeId);
            System.out.println("Generating and estimating random fits for output node, " + outputNodeId + "...");
            ProbabilityEngine pe = new ProbabilityEngine();
            String outputFile = outputDir + File.separator + outputNodeId + ".rjfuz";
            try {
                pe.getRandomFits(numberOfFits, outputNodeId, exprsTable, outputFile);
            } catch (Exception ex) {
                //Logger.getLogger(RandomFitsGenerator.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Exception thrown while exploring node, " + outputNodeId);
                ex.printStackTrace();
                System.exit(1);
            }
            System.out.println("Done generating and estimating random fits for output node, " + outputNodeId + "...");
        });       
        System.out.println("...Done!");        
    }   
}
