/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.apache.commons.math3.util.Combinations;
import tables.Table;
import utilities.ConfigFileReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.stat.descriptive.moment.Mean;


/**
 *
 * @author aiyetanpo
 */
public class JFuzzyMachine {
    
    private final HashMap<String, String> config;
    private final Table exprs;
    private final FuzzySet[][] fMat;
    private final Fuzzifier fuzzifier;
        
    public JFuzzyMachine(HashMap<String, String> config) throws IOException{
        fuzzifier = new Fuzzifier();
        this.config = config;
        this.exprs = new Table(config.get("inputFile"), Table.TableType.DOUBLE);
        this.fMat = fuzzifier.getFuzzyMatrix(exprs);        
    }
    
    private void searchHelper5(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){  
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        } 
        
        if(Boolean.parseBoolean(config.get("useParallel"))){
            // --- insert [Multithreaded] parallel stream computation here --- 
            LinkedList<InputsCombination> ics = new LinkedList();
            for (int[] inputsCombination : inputsCombinations) {
                InputsCombination ic = new InputsCombination( inputsCombination, 
                                                                eCutOff, 
                                                                outputGeneExpValues, 
                                                                deviationSquaredSum, 
                                                                exprs, 
                                                                fMat,
                                                                otherGenes,
                                                                outputGene);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper5(printer, config));
            
        }else{
            ESearch esearch = new ESearch();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithFiveInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config);
            }
        }    
    }

    private void searchHelper4(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){  
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        }
        
        if(Boolean.parseBoolean(config.get("useParallel"))){
            // --- insert [Multithreaded] parallel stream computation here --- 
            LinkedList<InputsCombination> ics = new LinkedList();
            for (int[] inputsCombination : inputsCombinations) {
                InputsCombination ic = new InputsCombination( inputsCombination, 
                                                                eCutOff, 
                                                                outputGeneExpValues, 
                                                                deviationSquaredSum, 
                                                                exprs, 
                                                                fMat,
                                                                otherGenes,
                                                                outputGene);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper4(printer, config));
            
        }else{
            ESearch esearch = new ESearch();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithFourInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config);
            }
        }     
    }

    private void searchHelper3(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        }
        
        if(Boolean.parseBoolean(config.get("useParallel"))){
            // --- insert [Multithreaded] parallel stream computation here --- 
            LinkedList<InputsCombination> ics = new LinkedList();
            for (int[] inputsCombination : inputsCombinations) {
                InputsCombination ic = new InputsCombination( inputsCombination, 
                                                                eCutOff, 
                                                                outputGeneExpValues, 
                                                                deviationSquaredSum, 
                                                                exprs, 
                                                                fMat,
                                                                otherGenes,
                                                                outputGene);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper3(printer, config));
            
        }else{
            ESearch esearch = new ESearch();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithThreeInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config);
            }
        }     
    }

    private void searchHelper2(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        }
        
        if(Boolean.parseBoolean(config.get("useParallel"))){
            // --- insert [Multithreaded] parallel stream computation here --- 
            LinkedList<InputsCombination> ics = new LinkedList();
            for (int[] inputsCombination : inputsCombinations) {
                InputsCombination ic = new InputsCombination( inputsCombination, 
                                                                eCutOff, 
                                                                outputGeneExpValues, 
                                                                deviationSquaredSum, 
                                                                exprs, 
                                                                fMat,
                                                                otherGenes,
                                                                outputGene);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper2(printer, config));
            
        }else{
            ESearch esearch = new ESearch();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithTwoInputs(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config);
            }
        }  
    }

    private void searchHelper1(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){  
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        }
        
        if(Boolean.parseBoolean(config.get("useParallel"))){
            // --- insert [Multithreaded] parallel stream computation here --- 
            LinkedList<InputsCombination> ics = new LinkedList();
            for (int[] inputsCombination : inputsCombinations) {
                InputsCombination ic = new InputsCombination( inputsCombination, 
                                                                eCutOff, 
                                                                outputGeneExpValues, 
                                                                deviationSquaredSum, 
                                                                exprs, 
                                                                fMat,
                                                                otherGenes,
                                                                outputGene);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper1(printer, config));
            
        }else{
            ESearch esearch = new ESearch();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithOneInput(inputsCombination, eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             fMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config);
            }
        }  
    }  
    
    public void searchHelper(int numberOfInputs, 
                                String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){    
        //ESearch results = new ESearch();        
        switch(numberOfInputs){
            case 5:
                //results = 
                searchHelper5(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        //esearch,
                                            printer
                );
                break;
            case 4:
                //results = 
                searchHelper4(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        //esearch,
                                            printer
                );
                break;
            case 3:
                //results = 
                searchHelper3(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        //esearch,
                                    printer
                );
                break;
            case 2:
                //results = 
                searchHelper2(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        //esearch,
                                    printer
                );
                break;
            default:
                //results = 
                searchHelper1(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        //esearch,
                                    printer
                );
                break;
                
        }
        //return results;
    }
     
    public void search(PrintWriter printer) throws FileNotFoundException {
                
        ESearch esearch = new ESearch(); // NOTE: to avoid Heap overflow error, use this only for printing...
        //PrintWriter printer = new PrintWriter(config.get("inputFile") + ".jfuz");  //jFuzzyMachine Search     
        
                 
        // for each outputGene,
        String[] allgenes = exprs.getRowIds();
        String[] outputGenes;
        if(config.get("useAllGenesAsOutput").equalsIgnoreCase("TRUE")){
            outputGenes = allgenes;
        }else{
            int istart = Integer.parseInt(config.get("iGeneStart"));
            int iend = Integer.parseInt(config.get("iGeneEnd"));
            int tot = (iend - istart) + 1; // number of output genes to consider
            String[] expGenes = exprs.getRowIds();
            outputGenes = new String[tot];
            for(int i = 0; i < tot; i++){
              outputGenes[i] = expGenes[(istart-1)+i];
            }
        }
        
        //Trouble shoot...
        System.out.println("               All Genes#: " + allgenes.length);
        System.out.println("Output Nodes Considered#: " + outputGenes.length);
        System.out.println("> Begin Search Result Table: ");
        
        printer.println("              All Genes#: " + allgenes.length);
        printer.println("Output Nodes Considered#: " + outputGenes.length);
        
        printer.println("> Begin Search Result Table ");        
        esearch.printESearchResultFileHeader(printer, config); // printoutput header...        
        for(String outputGene : outputGenes){
            String[] otherGenes = exprs.removeItem(allgenes, outputGene); // get other genes to get combinations of
            int maxInputs = Integer.parseInt(config.get("maxNumberOfInputs")); // get max # of inputs            
            if(maxInputs <= 0){ // a flag to simply use the specified "number of inputs"
                int inputs = Integer.parseInt(config.get("numberOfInputs"));                
                //results = 
                this.searchHelper(inputs, 
                                    outputGene, 
                                        otherGenes, 
                                            //esearch,
                                                printer
                                        );
            }else{ // otherwise use the 
                // for each 1 to max # of inputs
                for (int i = 0; i < maxInputs; i++ ){
                    int inputs = i + 1;
                    //results = 
                    this.searchHelper(inputs, 
                                        outputGene, 
                                            otherGenes, 
                                                //esearch,
                                                    printer
                                        );
                }
            }
        }
        printer.println("> End Search Result Table "); 
        System.out.println("> End Search Result Table "); 
        //results.printESearch(printer, config);       
        //printer.close();
    }
    
    
    
 /*
    class PermutateArray {        
        
	public List<List<Integer>> permute(int[] arr) {
            List<List<Integer>> list = new ArrayList<>();
            permuteHelper(list, new ArrayList<>(), arr);
            return list;
	}
 
	private void permuteHelper(List<List<Integer>> list, List<Integer> resultList, int [] arr){ 
            // Base case
            if(resultList.size() == arr.length){
                list.add(new ArrayList<>(resultList));
            } 
            else{
                for(int i = 0; i < arr.length; i++){ 
                    if(resultList.contains(arr[i])){
                        // If element already exists in the list then skip
                        continue; 
                    }
                    // Choose element
                    resultList.add(arr[i]);
                    // Explore
                    permuteHelper(list, resultList, arr);
                    // Unchoose element
                    resultList.remove(resultList.size() - 1);
                }
            }
	}  
    }  
 */   
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        // check args are well specified on commandline
        if(args.length < 1){
            System.out.println("Error: ");
            System.out.println("Usage: java [-cp path-to-jar][-Xmx4G] jfuzzymachine.JFuzzyMachine path-to-config-file [iGeneStart] [iGeneEnd] [numberOfInputs] [eCutOff] ");
            Runtime.getRuntime().exit(-1);
        }
        
        
        System.out.println("Starting...");
        Date start = new Date();
        long start_time = start.getTime();

        // Read input file...
        ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = cReader.read(args[0]); // configuration file path
        // instantiate print object...
        String outFile = config.get("inputFile");
        outFile = outFile.replace(".txt", "").replace(".tsv", "");
               
        if(args.length > 1){ // input includes other commandLine parameters; these supercede those specified in the config file....           
            config.replace("iGeneStart", args[1]);
            config.replace("iGeneEnd", args[2]);
            outFile = outFile + "." + args[1] + "." + args[2];
            if(args.length > 3){ // has more commandline parameters..
                config.replace("numberOfInputs", args[3]);
                outFile = outFile + "." + args[3];
                if(args.length > 4){
                    config.replace("eCutOff", args[4]);
                }
                if(args.length > 5){
                    config.replace("useParallel", args[5]);
                }
            }
        }
        outFile = outFile + "." + config.get("useParallel") + ".jfuz";
        PrintWriter printer = new PrintWriter(outFile);  
        //Print Parammeters to stderr and        
        System.out.println("> StartTime: " + start.toString());
        System.out.println("> Search Parameters: ");
        System.out.println("          inputFile = " + config.get("inputFile"));
        System.out.println("  maxNumberOfInputs = " + config.get("maxNumberOfInputs"));
        System.out.println("     numberOfInputs = " + config.get("numberOfInputs"));
        System.out.println("   outputInRealtime = " + config.get("outputInRealtime"));
        System.out.println("            eCutOff = " + config.get("eCutOff"));
        System.out.println("useAllGenesAsOutput = " + config.get("useAllGenesAsOutput"));
        System.out.println("         iGeneStart = " + config.get("iGeneStart"));
        System.out.println("           iGeneEnd = " + config.get("iGeneEnd"));
        System.out.println("        useParallel = " + config.get("useParallel"));
        System.out.println("         outputFile = " + outFile);
        System.out.println();
        
        printer.println("> StartTime: " + start.toString());
        printer.println("> Search Parameters: ");
        printer.println("          inputFile = " + config.get("inputFile"));
        printer.println("  maxNumberOfInputs = " + config.get("maxNumberOfInputs"));
        printer.println("     numberOfInputs = " + config.get("numberOfInputs"));
        printer.println("   outputInRealtime = " + config.get("outputInRealtime"));
        printer.println("            eCutOff = " + config.get("eCutOff"));
        printer.println("useAllGenesAsOutput = " + config.get("useAllGenesAsOutput"));
        printer.println("         iGeneStart = " + config.get("iGeneStart"));
        printer.println("           iGeneEnd = " + config.get("iGeneEnd"));
        printer.println("        useParallel = " + config.get("useParallel"));
        printer.println("         outputFile = " + outFile);
        printer.println();
        
        System.out.println("Initiating...");
        printer.println("Initiating...");
        JFuzzyMachine jfuzz = new JFuzzyMachine(config);
        
        System.out.println("Searching (Exhaustive Search)...");
        printer.println("Searching (Exhaustive Search)...");
        
        // -------------------- //
        
        jfuzz.search(printer);
        
        // -------------------- //
        
        System.out.println("\n...Done!");
        printer.println("\n...Done!");
        
        Date end = new Date();
        long end_time = end.getTime();
        
        printer.println("> Epilogue "); 
        System.out.println("> Epilogue "); 
        System.out.println("\n   Started: " + start_time + ": " + start.toString());
        System.out.println("     Ended: " + end_time + ": " + end.toString());
        System.out.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + " seconds.");
        
        printer.println("\n   Started: " + start_time + ": " + start.toString());
        printer.println("     Ended: " + end_time + ": " + end.toString());
        printer.println("Total time: " + (end_time - start_time) + " milliseconds; " + 
                        TimeUnit.MILLISECONDS.toMinutes(end_time - start_time) + " min(s), "
                        + (TimeUnit.MILLISECONDS.toSeconds(end_time - start_time) - 
                           TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end_time - start_time))) + " seconds.");
        
        printer.close();
    }
   

}
