/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import tables.RuleTable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.apache.commons.math3.util.Combinations;
import tables.Table;
import utilities.ConfigFileReader;
import java.util.ArrayList;
import java.util.List;
//import jfuzzymachine.ESearch.ESearchResult;
import org.apache.commons.math3.stat.descriptive.moment.Mean;


/**
 *
 * @author aiyetanpo
 */
public class JFuzzyMachine {
    
    private final HashMap<String, String> config;
    private final Table exprs;
    private final FuzzySet[][] fMat;
    //private final int[][] ruleTable;
        
    public JFuzzyMachine(HashMap<String, String> config) throws IOException{
        
        RuleTable ruleT = new RuleTable();
        this.config = config;
        this.exprs = new Table(config.get("inputFile"), Table.TableType.DOUBLE);
        this.fMat = getFuzzyMatrix();
        //this.ruleTable = ruleT.getRuleTable();
       
    }
    
    private FuzzySet[][] getFuzzyMatrix() {
        //Table fuzzyTable;
        double[][] mat = exprs.getMatrix(Table.TableType.DOUBLE);
        FuzzySet[][] xfMat = new FuzzySet[exprs.getRowIds().length][exprs.getColumnIds().length];
        
        for(int i = 0; i < exprs.getRowIds().length; i++){
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                xfMat[i][j] = this.fuzzify(mat[i][j]);
            }
        }
        return xfMat;
    }

    public FuzzySet fuzzify(double value){
         FuzzySet fz;
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
    
    private ESearch searchHelper5(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                    ESearch results) {
        //ESearch results = new ESearch();
        
        return results;
    }

    private ESearch searchHelper4(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                    ESearch results) {
        //ESearch results = new ESearch();
        
        return results;
    }

    private ESearch searchHelper3(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                    ESearch results) {
        //ESearch results = new ESearch();
        
        return results;
    }

    private ESearch searchHelper2(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                    ESearch results) {
        //ESearch results = new ESearch();
        // get all possible combinations of inputs (from otherGenes)
        Combinations inputCombinations = new Combinations(otherGenes.length, numberOfInputs);
        
        double[] outputGeneExpValues = exprs.getRow(exprs.getRowIndex(outputGene), Table.TableType.DOUBLE);
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputGeneExpValues); // average exoression value for output outputGene
        double deviationSquaredSum = 0;
        for(int i = 0; i < outputGeneExpValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputGeneExpValues[i] - xBar), 2);
        }
        // for each combination of inputs...
        for (int[] inputCombns : inputCombinations) {
            
            //LinkedList<Rule> ruleCombns = new LinkedList();
            String[] rules = new String[inputCombns.length];
            for(int i = 1; i <= 3; i++){
                for(int j = 1; j <= 3; j++){
                    for(int k = 1; k <= 3; k++){

                        for(int l = 1; l <= 3; l++){
                            for(int m = 1; m <= 3; m++){
                                for(int n = 1; n <= 3; n++){

                                    //ruleCombns.add(new Rule(i,j,k));
                                    //ruleCombns.add(new Rule(l,m,n));
                                    rules[0] = new Rule(i,j,k).toString();
                                    rules[1] = new Rule(l,m,n).toString();
                                    
                                    String[] inputGenes = new String[inputCombns.length];
                                    
                                    double[] xCaretValues = new double[outputGeneExpValues.length];
                                    double residualSquaredSum = 0;
                                    
                                    for(int index = 0; index < xCaretValues.length; index++){
                                        //get input genes 
                                        // NOTE: there are two inputs here
                                        String input1 = otherGenes[inputCombns[0]];
                                        String input2 = otherGenes[inputCombns[1]];
                                        
                                        inputGenes[0] = input1;
                                        inputGenes[1] = input2;
                                        //get fuzzyValues of input genes, 
                                        FuzzySet fz1 = fMat[exprs.getRowIndex(input1)][index];
                                        FuzzySet fz2 = fMat[exprs.getRowIndex(input2)][index];
                                        //get Zx,y,or z values using the Union Rule Configuration (URC) on rule combination
                                        double zx = fz1.get(i) + fz2.get(l);
                                        double zy = fz1.get(j) + fz2.get(m);
                                        double zz = fz1.get(k) + fz2.get(n);
                                        //get defuzzified (xCaretValue) value of Z @ position index
                                        double dfz = this.deFuzzify(new FuzzySet(zx, zy, zz));
                                        xCaretValues[index] = dfz;
                                        // compute residual and residual squared sum...
                                        residualSquaredSum = residualSquaredSum + Math.pow((outputGeneExpValues[index] - dfz), 2);
                                        
                                    }
                                    
                                    //compute error..
                                    double err = 1 - (residualSquaredSum/deviationSquaredSum);
                                    
                                    ESearchResult sResults = new ESearchResult(outputGene, 
                                                                                inputCombns.length,
                                                                                    inputGenes,
                                                                                        rules,
                                                                                            err
                                                                                        );
                                    results.add(sResults);
                                }
                            }
                        }
                    }
                }
            }
            
        }
        
        return results;
    }

    private ESearch searchHelper1(int numberOfInputs, 
                                 String outputGene, 
                                    String[] otherGenes,
                                    ESearch results) {
        //ESearch results = new ESearch();
        
        
        return results;
    }

      
    
    
    public ESearch searchHelper(int numberOfInputs, 
                                String outputGene, 
                                    String[] otherGenes,
                                        ESearch results){
        //ESearch results = new ESearch();        
        switch(numberOfInputs){
            case 5:
                results = searchHelper5(numberOfInputs, 
                        outputGene, otherGenes, results);
                break;
            case 4:
                results = searchHelper4(numberOfInputs, 
                        outputGene, otherGenes, results);
                break;
            case 3:
                results = searchHelper3(numberOfInputs, 
                        outputGene, otherGenes, results);
                break;
            case 2:
                results = searchHelper2(numberOfInputs, 
                        outputGene, otherGenes, results);
                break;
            default:
                results = searchHelper1(numberOfInputs, 
                        outputGene, otherGenes, results);
                break;
                
        }
        return results;
    }
     
    public void search() throws FileNotFoundException {
        
        double r2CutOff = Double.parseDouble(config.get("R2CutOff"));
        ESearch results = new ESearch();
        PrintWriter printer = new PrintWriter(config.get("inputFile") + ".jfuz");  //jFuzzyMachine Search          
                 
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
              outputGenes[i] = expGenes[istart+i];
            }
        }
        
        for(String outputGene : outputGenes){
            String[] otherGenes = exprs.removeItem(allgenes, outputGene); // get other genes to get combinations of
            int maxInputs = Integer.parseInt(config.get("maxNumberOfInputs")); // get max # of inputs            
            if(maxInputs <= 0){
                int inputs = Integer.parseInt(config.get("numberOfInputs"));                
                results = this.searchHelper(inputs, outputGene, otherGenes, results);
            }else{
                // for each 1 to max # of inputs
                for (int i = 0; i < maxInputs; i++ ){
                    int inputs = i + 1;
                    results = this.searchHelper(inputs, outputGene, otherGenes, results);
                }
            }
        }
        results.printESearch(printer, config);       
        printer.close();
        return;
    }
    
    
    
 
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
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        // Read input file...
        ConfigFileReader cReader = new ConfigFileReader();
        HashMap<String, String> config = cReader.read(args[0]); // configuration file path
        
        System.out.println("Initiating...");
        JFuzzyMachine jfuzz = new JFuzzyMachine(config);
        
        System.out.println("Searching (Exhaustive Search)...");
        jfuzz.search();
        
        System.out.println("...Done!");
    }
   

}
