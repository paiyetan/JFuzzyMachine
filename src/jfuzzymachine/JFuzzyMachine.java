/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.apache.commons.math3.util.Combinations;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.ConfigFileReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import jfuzzymachine.utilities.ProbableRegulonsMapFileReader;
import org.apache.commons.math3.stat.descriptive.moment.Mean;


/**
 *
 * @author aiyetanpo
 */
public class JFuzzyMachine {
    
    private final HashMap<String, String> config;
    private final Table exprs;
    private final FuzzySet[][] exprsFMat;
    private final Fuzzifier fuzzifier;
    private final boolean modelPhenotype; 
    private Table phenoExprs = null; //
    private FuzzySet[][] phenoFMat = null;
    public enum ExpressionType {PHENOTYPE, GENOTYPE};
    
    private final boolean useProbableRegulonsMap; //a less exhaustive but guided search approach...
        
    
    public JFuzzyMachine(HashMap<String, String> config) throws IOException{
        
        System.out.println("Starting JFuzzyMachine...");
        Date start = new Date();
        long start_time = start.getTime();
        
        String outFile = config.get("inputFile"); //expression matrix file....
        outFile = outFile.replace(".txt", "").replace(".tsv", "");
        String outputDir = config.get("outputDir");
        outFile = outputDir + File.separator + new File(outFile).getName();        
               
        outFile = outFile + "." + 
                    config.get("iGeneStart") + "." + 
                        config.get("iGeneEnd") + "." +
                            config.get("numberOfInputs");        
         
        
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
        System.out.println("     modelPhenotype = " + config.get("modelPhenotype"));
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
        printer.println("     modelPhenotype = " + config.get("modelPhenotype"));
        printer.println();
        
        System.out.println("Initiating...");
        printer.println("Initiating...");
        
        //JFuzzyMachine jfuzz = new JFuzzyMachine(config);        
        fuzzifier = new Fuzzifier();
        this.config = config;
        this.exprs = new Table(config.get("inputFile"), Table.TableType.DOUBLE);
        this.exprsFMat = fuzzifier.getFuzzyMatrix(exprs, ExpressionType.GENOTYPE); 
        this.modelPhenotype = Boolean.parseBoolean(config.get("modelPhenotype"));
        this.useProbableRegulonsMap = Boolean.parseBoolean(config.get("useProbableRegulonsMap"));
        
        if(this.modelPhenotype){
            phenoExprs = new Table(config.get("inputPhenoFile"), Table.TableType.DOUBLE);
            phenoFMat = fuzzifier.getFuzzyMatrix(phenoExprs, ExpressionType.PHENOTYPE); 
        }
               
        
        System.out.println("Searching (Exhaustive Search)...");
        printer.println("Searching (Exhaustive Search)...");
        
        // -------------------- //
        
        this.search(printer);
        
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
    
       
    private void searchHelper5(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        //ESearch esearch,
                                        PrintWriter printer){  
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues;
        Mean mean = new Mean();
        double xBar;
        double deviationSquaredSum = 0;
        if(this.modelPhenotype){
            outputGeneExpValues = 
                    phenoExprs.getRow(phenoExprs.getRowIndex(outputGene), 
                                      Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
                 
        }else{
            outputGeneExpValues = 
                    exprs.getRow(exprs.getRowIndex(outputGene), 
                                 Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
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
                                                                exprsFMat,
                                                                otherGenes,
                                                                outputGene,
                                                                phenoExprs,
                                                                phenoFMat,
                                                                modelPhenotype);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper5(printer, config));

        }else{
            ESearchEngine esearch = new ESearchEngine();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithFiveInputs(inputsCombination, 
                                            eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             exprsFMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs,
                                             phenoFMat,
                                             modelPhenotype);
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
        double[] outputGeneExpValues;
        Mean mean = new Mean();
        double xBar;
        double deviationSquaredSum = 0;
        if(this.modelPhenotype){
            outputGeneExpValues = 
                    phenoExprs.getRow(phenoExprs.getRowIndex(outputGene), 
                                      Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
                 
        }else{
            outputGeneExpValues = 
                    exprs.getRow(exprs.getRowIndex(outputGene), 
                                 Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
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
                                                                exprsFMat,
                                                                otherGenes,
                                                                outputGene,
                                                                phenoExprs,
                                                                phenoFMat,
                                                                modelPhenotype);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper4(printer, config));

        }else{
            ESearchEngine esearch = new ESearchEngine();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithFourInputs(inputsCombination, 
                                            eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             exprsFMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs,
                                             phenoFMat,
                                             modelPhenotype);
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
        double[] outputGeneExpValues;
        Mean mean = new Mean();
        double xBar;
        double deviationSquaredSum = 0;
        if(this.modelPhenotype){
            outputGeneExpValues = 
                    phenoExprs.getRow(phenoExprs.getRowIndex(outputGene), 
                                      Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
                 
        }else{
            outputGeneExpValues = 
                    exprs.getRow(exprs.getRowIndex(outputGene), 
                                 Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
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
                                                                exprsFMat,
                                                                otherGenes,
                                                                outputGene,
                                                                phenoExprs,
                                                                phenoFMat,
                                                                modelPhenotype);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper3(printer, config));

        }else{
            ESearchEngine esearch = new ESearchEngine();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithThreeInputs(inputsCombination, 
                                            eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             exprsFMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs,
                                             phenoFMat,
                                             modelPhenotype);
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
        double[] outputGeneExpValues;
        Mean mean = new Mean();
        double xBar;
        double deviationSquaredSum = 0;
        if(this.modelPhenotype){
            outputGeneExpValues = 
                    phenoExprs.getRow(phenoExprs.getRowIndex(outputGene), 
                                      Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
                 
        }else{
            outputGeneExpValues = 
                    exprs.getRow(exprs.getRowIndex(outputGene), 
                                 Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
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
                                                                exprsFMat,
                                                                otherGenes,
                                                                outputGene,
                                                                phenoExprs,
                                                                phenoFMat,
                                                                modelPhenotype);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper2(printer, config));

        }else{
            ESearchEngine esearch = new ESearchEngine();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithTwoInputs(inputsCombination, 
                                            eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             exprsFMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs,
                                             phenoFMat,
                                             modelPhenotype);
            }
        }  
    }

    private void searchHelper1(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                        PrintWriter printer){  
        // get all possible combinations of inputs (from otherGenes)
        double eCutOff = Double.parseDouble(config.get("eCutOff"));
        Combinations inputsCombinations = new Combinations(otherGenes.length, numberOfInputs);
        
        // get the expression profile of output gene across all samples
        double[] outputGeneExpValues;
        Mean mean = new Mean();
        double xBar;
        double deviationSquaredSum = 0;
        if(this.modelPhenotype){
            outputGeneExpValues = phenoExprs.getRow(phenoExprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
                deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
                 
        }else{
            outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
            xBar = mean.evaluate(outputGeneExpValues); // average expression value for output outputGene
            for(int i = 0; i < outputGeneExpValues.length; i++)
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
                                                                exprsFMat,
                                                                otherGenes,
                                                                outputGene,
                                                                phenoExprs,
                                                                phenoFMat,
                                                                modelPhenotype);
                ics.add(ic);
            }
            ics.parallelStream().forEach(ic -> ic.searchHelper1(printer, config));

        }else{
            ESearchEngine esearch = new ESearchEngine();
            for (int[] inputsCombination : inputsCombinations) { // for each combination of inputs...
                //get input genes...
                esearch.searchWithOneInput(inputsCombination, 
                                            eCutOff, 
                                             outputGeneExpValues, 
                                             deviationSquaredSum, 
                                             exprs, 
                                             exprsFMat, 
                                             otherGenes, 
                                             outputGene, 
                                             printer, 
                                             config,
                                             phenoExprs,
                                             phenoFMat,
                                             modelPhenotype);
            }
        }         
    }  
    
    public void searchHelper(int numberOfInputs, 
                                String outputGene, 
                                    String[] otherGenes,
                                        PrintWriter printer){    
        switch(numberOfInputs){
            case 5:
                searchHelper5(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                            printer
                );
                break;
            case 4:
                searchHelper4(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                            printer
                );
                break;
            case 3:
                searchHelper3(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        printer
                );
                break;
            case 2:
                searchHelper2(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        printer
                );
                break;
            default:
                //results = 
                searchHelper1(numberOfInputs, 
                                outputGene, 
                                    otherGenes, 
                                        printer
                );
                break;
                
        }
    }
     
    public void search(PrintWriter printer) throws FileNotFoundException, IOException {
                
        ESearchEngine esearch = new ESearchEngine(); // NOTE: to avoid Heap overflow error, use this only for printing...
        HashMap<String, String[]> regulonsMap = new HashMap();
        // at this stage, check if the search should be exhaustive or streamlined around a set of input genes
        if(useProbableRegulonsMap){
            String regulonsMapFile = config.get("regulonsMapFile");
            ProbableRegulonsMapFileReader regReader = new ProbableRegulonsMapFileReader();
            regulonsMap = regReader.read(regulonsMapFile);
        }
        
        
        if(modelPhenotype){
            String outputGene = phenoExprs.getRowIds()[0];
            String[] otherGenes; // null;
            if(useProbableRegulonsMap){
                otherGenes = regulonsMap.get(outputGene);
            }else{
                otherGenes = exprs.getRowIds();
            }           
            //Trouble shoot...
            System.out.println("                 All Genes#: " + otherGenes.length);
            System.out.println("   Output Nodes Considered#: " + outputGene);
            System.out.println("> Begin Search Result Table: ");
            
            printer.println("              All Genes#: " + otherGenes.length);
            printer.println("Output Nodes Considered#: " + outputGene);
            printer.println("> Begin Search Result Table ");   
            
            esearch.printESearchResultFileHeader(printer, config); // printoutput header...
            
            int maxInputs = Integer.parseInt(config.get("maxNumberOfInputs")); // get max # of inputs   
            if(maxInputs <= 0){ // a flag to simply use the specified "number of inputs"
                int inputs = Integer.parseInt(config.get("numberOfInputs"));                
                //results = 
                this.searchHelper(inputs, 
                                    outputGene, 
                                        otherGenes, 
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
                                                printer
                                        );
                }
            }
            
        }else{
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
            System.out.println("                 All Genes#: " + allgenes.length);
            System.out.println("   Output Nodes Considered#: " + outputGenes.length);
            System.out.println("> Begin Search Result Table: ");

            printer.println("                 All Genes#: " + allgenes.length);
            printer.println("   Output Nodes Considered#: " + outputGenes.length);
            printer.println("> Begin Search Result Table: ");   
            
            esearch.printESearchResultFileHeader(printer, config); // printoutput header...
            // for each outputGene,
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
        }
        printer.println("> End Search Result Table "); 
        System.out.println("> End Search Result Table "); 
        
    }
    
    public void done() throws Throwable{       
                
    }
     
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Throwable {
        
        // check args are well specified on commandline
        if(args.length < 1){
            System.out.println("Error: ");
            System.out.println("Usage: java [-cp path-to-jar][-Xmx4G] jfuzzymachine.JFuzzyMachine path-to-config-file [iGeneStart] [iGeneEnd] [numberOfInputs] [eCutOff] ");
            Runtime.getRuntime().exit(-1);
        }
        
        //ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = ConfigFileReader.read(args[0]); // configuration file path
        JFuzzyMachine jfuzzy = new JFuzzyMachine(config);
        //jfuzzy.finalize();
        
    }
   
}
