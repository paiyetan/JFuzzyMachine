/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.FuzzySet;
import jfuzzymachine.JFuzzyMachine;
import jfuzzymachine.Rule;
import jfuzzymachine.tables.RuleTable;
import jfuzzymachine.tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class PhenoPredictionSimulator {
    
    public void run(Table expMat, int iterations, String outputDir) throws FileNotFoundException{
        
        PrintWriter printer = new PrintWriter(outputDir + File.separator + "phenoOutSimulation.tsv");
        //printHeader...
        printer.println("numberOfInputs\t" +
                        "inputsExpValueColumn\t" +
                        "inputFeaturesIds\t" +
                        "inputRules\t" +
                        "rawDefuzz\t" +      //defuzzify( TanTransform=FALSE, logitTransform=FALSE )
                        "tanTDefuzz\t" +     //defuzzify( TanTransform=TRUE, logitTransform==FALSE )
                        "logitDefuzz1-1\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=1 )  
                        "logitDefuzz1-2\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=2 )
                        "logitDefuzz1-3\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=3 )
                        "logitDefuzz1-4\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=4 )
                        "logitDefuzz1-5\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=5 )
                        "logitDefuzz1-6\t" + //defuzzify( TanTransform=FALSE, logitTransform=TRUE, k=6 )
                        "logitDefuzz2-1\t" + //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=1 )
                        "logitDefuzz2-2\t" + //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=2 )
                        "logitDefuzz2-3\t" + //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=3 )
                        "logitDefuzz2-4\t" + //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=4 )
                        "logitDefuzz2-5\t" + //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=5 )
                        "logitDefuzz2-6"     //defuzzify( TanTransform=TRUE, logitTransform==TRUE, k=6 )            
                        );
        int count = 0;
        String[] rowIds = expMat.getRowIds();
        String[] columnIds = expMat.getColumnIds();
        Random r = new Random(); //random number generator...
        
        while(count < iterations){
            int numberOfInputs = r.nextInt(5) + 1; //radomly select number of inputs between 1 - 5...
            LinkedList<String> features = new LinkedList(); //randomly select inputs
            while(features.size() < numberOfInputs){
                String feature = rowIds[r.nextInt(rowIds.length)];
                if(!features.contains(feature))
                    features.add(feature);
            }            
            //randomly perturbation (column) to use to select values for inputs... 
            int inputsExpValuesColumn = r.nextInt(columnIds.length);
            double[] expValues = null;
            try{
                expValues = expMat.getColumn(inputsExpValuesColumn, Table.TableType.DOUBLE);
            }catch(NullPointerException e){
                e.printStackTrace();
                System.out.println("\ninputsExpValuesColumn: " + inputsExpValuesColumn +
                                   "\nexpMatColumnLength: " + columnIds.length + 
                                   "\nnumberOfInputs: " + numberOfInputs + 
                                   "\ncount: " + count);
            }
            //get expression value of input features..
            double[] inputsExpValues = new double[features.size()];
            for(int i = 0; i < inputsExpValues.length; i++)
                inputsExpValues[i] = expValues[expMat.getRowIndex(features.get(i))];             
            
            //get the fuzzyset array 
            Fuzzifier fzir = new Fuzzifier();
            FuzzySet[] inputsFuzzArr = new FuzzySet[inputsExpValues.length];
            for(int i = 0; i < inputsFuzzArr.length; i++)
                inputsFuzzArr[i] = fzir.fuzzify(inputsExpValues[i], JFuzzyMachine.ExpressionType.GENOTYPE);
            
            //random select applicable rules to inputs...
            RuleTable rT = new RuleTable();
            LinkedList<Rule> inputsRules = new LinkedList();
            for(int i = 0; i < inputsFuzzArr.length; i++)
                inputsRules.add(new Rule(rT.getRule(r.nextInt(27))));                        
            
            FuzzySet zValue = fzir.getZValue(inputsFuzzArr, inputsRules);
            
            // print table values...
            printer.println(numberOfInputs + "\t" +
                            inputsExpValuesColumn + "\t" +
                            Arrays.toString(features.toArray(new String[features.size()])) + "\t" +
                            Arrays.toString(Rule.rulesArray(inputsRules)) + "\t" +
                            fzir.deFuzzify(zValue, false, false, -1.00) + "\t" +
                            fzir.deFuzzify(zValue, true, false, -1.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 1.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 2.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 3.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 4.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 5.00) + "\t" +
                            fzir.deFuzzify(zValue, false, true, 6.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 1.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 2.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 3.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 4.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 5.00) + "\t" +
                            fzir.deFuzzify(zValue, true, true, 6.00)
                           );
            
            count++;
        }        
        printer.close();
    }
       
    public static void main(String[] args) throws IOException{
        
        System.out.println("Starting...");
        String expMatFilePath = args[0];
        int iterations = Integer.parseInt(args[1]);
        String outputDir = args[2];
                
        Date start = new Date();
        long start_time = start.getTime();
        
        System.out.println("Running...");
        PhenoPredictionSimulator simulator = new PhenoPredictionSimulator();
        simulator.run(new Table(expMatFilePath, Table.TableType.DOUBLE), iterations, outputDir);
        
        System.out.println("...Done!!!");
        
        Date end = new Date();
        long end_time = end.getTime();
        
        System.out.println("\n   Started: " + start_time + ": " + start.toString());
        System.out.println("     Ended: " + end_time + ": " + end.toString());
        System.out.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + " seconds.");
        
        
        
    }
    
}
