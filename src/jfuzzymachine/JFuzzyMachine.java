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
import table.Table;
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
    
    private final int ruleTable[][] =
                          {{1, 1, 1}, //0
                            {1, 1, 2}, //1
                            {1, 1, 3}, //2
                            {1, 2, 1}, //3
                            {1, 2, 2}, //4
                            {1, 2, 3}, //5
                            {1, 3, 1}, //6
                            {1, 3, 2}, //7 
                            {1, 3, 3}, //8
                            {2, 1, 1}, //9
                            {2, 1, 2}, //10
                            {2, 1, 3}, //11
                            {2, 2, 1}, //12
                            {2, 2, 2}, //13
                            {2, 2, 3}, //14
                            {2, 3, 1}, //15
                            {2, 3, 2}, //16
                            {2, 3, 3}, //17
                            {3, 1, 1}, //18
                            {3, 1, 2}, //19
                            {3, 1, 3}, //20
                            {3, 2, 1}, //21
                            {3, 2, 2}, //22
                            {3, 2, 3}, //23
                            {3, 3, 1}, //24
                            {3, 3, 2}, //25
                            {3, 3, 3}}; //26
    
    public JFuzzyMachine(HashMap<String, String> config) throws IOException{
        
        this.config = config;
        this.exprs = new Table(config.get("inputFile"), Table.TableType.DOUBLE);
        this.fMat = getFuzzyMatrix();
       
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
    
    public ESearch search() throws FileNotFoundException {
        
        double r2CutOff = Double.parseDouble(config.get("R2CutOff"));
        ESearch results = new ESearch();
        PrintWriter printer = new PrintWriter(config.get("inputFile") + ".tsv");  //jFuzzyMachine Search
        if(config.get("outputInRealtime").equalsIgnoreCase("TRUE")){
            // print output file header...
            results.printESearchResultFileHeader(printer);
        }
       
                 
        // for each gene,
        String[] genes = exprs.getRowIds();
        for(String gene : genes){
            String[] otherGenes = exprs.removeItem(genes, gene); // get other genes to get combinations of       
            int[] numInputs = new int[Integer.parseInt(config.get("maxNumberOfInputs"))]; // get max # of inputs       
        
            // for each 1 to max # of inputs
            for (int i = 0; i < numInputs.length; i++ ){
                int numInput = i + 1; 
                // get all possible combinations of inputs (from otherGenes)
                Combinations inputCombinations = new Combinations(otherGenes.length, numInput);
            
                // for each combination of inputs...
                for (int[] inputCombns : inputCombinations) {                
                    // get all possible combinations of rules with order (permutations)
                    Combinations ruleCombinations = new Combinations(ruleTable.length, inputCombns.length);
                                            //NOTE: rule combinations are the row indeces of ruleTable
                    // for each combination of rules...
                    for(int[] ruleCombns : ruleCombinations){ 
                        // to consider order, permute...
                        PermutateArray pa = new PermutateArray();
                        List<List<Integer>> inputsRulePermutations = pa.permute(ruleCombns);
                    
                        // get each permuation...
                        for(List<Integer> inputsRulePermutation : inputsRulePermutations){
                            // NOTE: the length of the permutation list should be the same as inputCombns..                        
                            
                            int[] ruleIndeces = new int[inputsRulePermutation.size()];
                            String[] inputGenes = new String[inputsRulePermutation.size()];
                            // at this stage evaluate the effect of input_gene(s) on output_gene(s)
                            // [r_1, r_2, r_3,..r_n]
                            // [in_1, in_2, in_3, ...in_n]
                            
                            for(int r = 0; r < inputsRulePermutation.size(); r++){
                                ruleIndeces[r] = inputsRulePermutation.get(r);
                                inputGenes[r] = otherGenes[inputCombns[r]];
                            }
                            
                            double error = evaluateE(gene, inputGenes, ruleIndeces); 
                            ESearchResult result = new ESearchResult(gene, numInput, inputGenes, ruleIndeces, error);
                            
                            if(error >= r2CutOff){
                                
                                if(config.get("outputInRealtime").equalsIgnoreCase("TRUE")){
                                    results.printESearchResult(result, printer);
                                }else{
                                    results.add(result);
                                } 
                            }
                        }                                               
                    }                                       
                }
            }
        }              
        
        printer.close();
        return(results);
    }
    
    private double evaluateE(String gene, String[] inputGenes, 
                                int[] ruleIndeces) {
        double E = 0;
        
        // Given the fuzzified expression of an input gene y = [y1 y2 y3]; 
        // and the general fuzzy rule r = [r1 r2 r3]; 
        // the resulting fuzzified expression of the output gene z will be z = [y_r1 y_r2 y_r3];
        double[][] exprsMatrix = exprs.getMatrix(Table.TableType.DOUBLE);        
        //double[] xValues = exprs.getRow(exprs.getRowIndex(gene), Table.TableType.DOUBLE); //crisp experimental data
        double[] xValues = exprsMatrix[exprs.getRowIndex(gene)]; //crisp experimental data
        double[] xCaretBarValues = new double[xValues.length]; // defuzzified experimental prediction values...
        
        Mean m = new Mean();
        double xBar = m.evaluate(xValues);
        
        // get defuzzified (xCaretBarValues) values...
        for(int i = 0; i < xValues.length; i++){
            /*
            double exprsCrispValue = xValues[i]; // get gene value at position_i   
            FuzzySet exprsFuzzyValue = fMat[exprs.getRowIndex(gene)][i];
            */
            
            FuzzySet[] zArr = new FuzzySet[inputGenes.length]; // intermediate Z values...  
            
            for(int j = 0; j < zArr.length; j++){ // each intermediate z value is derived by apply rule to input gene value
                
                String inputGene = inputGenes[j];
                int ruleIndex = ruleIndeces[j];
                /*
                   recall Gomley et al (2011): The state of an output node z = [z1 z2 z3] is determined 
                    by the fuzzy state of an input gene y = [y1 y2 y3] and the rule describing the relation 
                     from input to output r = [r1 r2 r3] as follows: z = [y_r1 y_r2 y_r3]
                */
                FuzzySet inputGeneFuzzyValue = fMat[exprs.getRowIndex(inputGene)][i];
                // apply rule at ruleIndex in ruleTable on inputGene fuzzyValue
                zArr[j] = evaluateZ(inputGeneFuzzyValue, ruleIndex);
            }
            
            FuzzySet z = sumIntermediateZValues(zArr);
            double dfz = deFuzzify(z);
            xCaretBarValues[i] = dfz;
                        
        }
        
       /*
        E = 1 - [ (summation(x_i - xCaretBar_i)^2) / (summation(x_i - xBar)^2) ]        
        */
       double upperSS = 0;
       double lowerSS = 0;
       for( int i = 0; i < xValues.length; i++){
           upperSS = upperSS + Math.pow((xValues[i] - xCaretBarValues[i]), 2);
           lowerSS = lowerSS + Math.pow((xValues[i] - xBar), 2);
       }
       E = 1 - ((upperSS/lowerSS)); 
       return E;
              
    }

    
    private FuzzySet evaluateZ(FuzzySet inputFuzzyValue, int ruleIndex) {
        
        FuzzySet z;
        
        int[] rule = ruleTable[ruleIndex];
        double lo = inputFuzzyValue.getY1();  // degree of low
        double med = inputFuzzyValue.getY2(); // degree of medium
        double hi = inputFuzzyValue.getY3(); // degree of high
        
        /*
            LinkedList<Double> zLoList = new LinkedList();
            LinkedList<Double> zMedList = new LinkedList();
            LinkedList<Double> zHiList = new LinkedList();
        */
        
        double zLo = 0;
        double zMe = 0;
        double zHi = 0;        
        
        switch (rule[0]) {
            case 3:
                // if input is low, output is high
                // zHiList.add(lo);
                zHi = lo;
                break;
            case 2: 
                // if input is low, output is medium
                // zMedList.add(lo);
                zMe = lo;
                break;
            case 1:
                // if input is low, output is low
                // zLoList.add(lo);
                zLo = lo;
                break;
            default:
                break;
        }
        
        switch (rule[1]) {
            case 3:
                // if input is medium, output is high
                // zHiList.add(med);
                zHi = med;
                break;
            case 2:
                // if input is medium, output is medium
                // zMedList.add(med);
                zMe = med;
                break;
            case 1:
                // if input is medium, output is low
                // zLoList.add(med);
                zLo = med;
                break;
            default:
                break;
        }
        
        switch (rule[2]) {
            case 3:
                // if input is high, output is high
                // zHiList.add(hi);
                zHi = hi;
                break;
            case 2:
                // if input is high, output is medium
                // zMedList.add(hi);
                zMe = hi;
                break;
            case 1:
                // if input is high, output is low
                // zLoList.add(hi);
                zLo = hi;
                break;
            default:
                break;
        }
        
        z = new FuzzySet(zLo, zMe, zHi);       
        return z;
        
    }

    private FuzzySet sumIntermediateZValues(FuzzySet[] zArr) {
        
        FuzzySet z;
        double l = 0;
        double m = 0;
        double h = 0;
        
        for (FuzzySet zArr1 : zArr) {
            l = l + zArr1.getY1(); //low
            m = m + zArr1.getY2(); //medium
            h = h + zArr1.getY3(); //high
        }
        z = new FuzzySet(l, m, h);
        return z;
        
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
        HashMap<String, String> config = cReader.read(args[0]);
        
        System.out.println("Initiating...");
        JFuzzyMachine jfuzz = new JFuzzyMachine(config);
        
        System.out.println("Searching (Exhaustive Search)...");
        jfuzz.search();
        
        System.out.println("...Done!");
    }
   

}
