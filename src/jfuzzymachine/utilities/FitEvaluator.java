/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.util.LinkedList;
import jfuzzymachine.Fuzzifier;
import jfuzzymachine.FuzzySet;
import jfuzzymachine.JFuzzyMachine;
import jfuzzymachine.Rule;
import jfuzzymachine.tables.Table;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author aiyetanpo
 */
public class FitEvaluator{
      
    //private Fuzzifier fuzzifier;
    public double evaluateFit(String outputNode,
                        String[] inputNodes,
                            LinkedList<Rule> rules,
                                Table expMat){
        double fit = 0;
        Fuzzifier fuzzifier = new Fuzzifier();
        //get the output expression values...
        double[] outputExprValues = null;
        outputExprValues = expMat.getRow(expMat.getRowIndex(outputNode), Table.TableType.DOUBLE);
        
        /* 
     recall that the fit is computed by 
    E = \frac{\sum_{j=1}^M(x_i - \tilde{x)_i)^2}{\sum_{j=1}^M(x_i - \bar{x)_i)^2}    
    */  
        double deviationSquaredSum = 0;
        double residualSquaredSum = 0; 
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputExprValues); // average expression value for output outputGene
        for(int i = 0; i < outputExprValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputExprValues[i] - xBar), 2);
        }

        for(int index = 0; index < outputExprValues.length; index++){                                                                                                                                                        
            //get fuzzyValues of input genes,                 
            double[] expValues = expMat.getColumn(index, Table.TableType.DOUBLE);
            //get expression value of input features..
            double[] inputsExpValues = new double[inputNodes.length];
            for(int i = 0; i < inputsExpValues.length; i++){
                inputsExpValues[i] = expValues[expMat.getRowIndex(inputNodes[i])];             
            }           
            //double dfz = fuzzifier.deFuzzify(zValue, tanTransform, logitTransform, k);
            double dfz = getPrediction(inputsExpValues, rules, fuzzifier);
            // compute residual and cummulative residual squared sum...
            residualSquaredSum = residualSquaredSum + Math.pow((outputExprValues[index] - dfz), 2);                                        
        }
        // compute error..
        fit = 1 - (residualSquaredSum/deviationSquaredSum);
        return fit;
    }
    
    public double evaluateFit(String outputNode,
                        String[] inputNodes,
                            LinkedList<Rule> rules,
                                Table expMat,
                                    boolean outputIsPheno,
                                            Table phenoMat,
                                                boolean tanTransform,
                                                   boolean logitTransform, 
                                                       double k){
        double fit = 0;
        Fuzzifier fuzzifier = new Fuzzifier();
        //get the output expression values...
        double[] outputExprValues = null;
        if(outputIsPheno){
            outputExprValues = phenoMat.getRow(phenoMat.getRowIndex(outputNode), Table.TableType.DOUBLE);
        }else{
            outputExprValues = expMat.getRow(expMat.getRowIndex(outputNode), Table.TableType.DOUBLE);
        }

        double deviationSquaredSum = 0;
        double residualSquaredSum = 0; 
        Mean mean = new Mean();
        double xBar = mean.evaluate(outputExprValues); // average expression value for output outputGene
        for(int i = 0; i < outputExprValues.length; i++){
            deviationSquaredSum = deviationSquaredSum + Math.pow((outputExprValues[i] - xBar), 2);
        }

        for(int index = 0; index < outputExprValues.length; index++){                                                                                                                                                        
            //get fuzzyValues of input genes,                 
            double[] expValues = expMat.getColumn(index, Table.TableType.DOUBLE);
            //get expression value of input features..
            double[] inputsExpValues = new double[inputNodes.length];
            for(int i = 0; i < inputsExpValues.length; i++){
                inputsExpValues[i] = expValues[expMat.getRowIndex(inputNodes[i])];             
            }           
            //double dfz = fuzzifier.deFuzzify(zValue, tanTransform, logitTransform, k);
            double dfz = getPrediction(inputsExpValues, rules, fuzzifier);
            // compute residual and cummulative residual squared sum...
            residualSquaredSum = residualSquaredSum + Math.pow((outputExprValues[index] - dfz), 2);                                        
        }
        // compute error..
        fit = 1 - (residualSquaredSum/deviationSquaredSum);
        return fit;
    }
    
    public double getPrediction(double[] inputsExpValues,
                                LinkedList<Rule> rules,
                                Fuzzifier fuzzifier){
        double prediction = 0;
        //get the fuzzyset array 
        FuzzySet[] inputsFuzzArr = new FuzzySet[inputsExpValues.length];
        for(int i = 0; i < inputsFuzzArr.length; i++){
            try{
                inputsFuzzArr[i] = fuzzifier.fuzzify(inputsExpValues[i], JFuzzyMachine.ExpressionType.GENOTYPE);
            }catch(NullPointerException ne){
                System.out.println("i = " + i);
                System.out.println("value = " + inputsExpValues[i]);
                ne.printStackTrace();
                System.exit(1);
            }           
        }
        FuzzySet zValue = fuzzifier.getZValue(inputsFuzzArr, rules);
        //get defuzzified (xCaretValue) value of Z @ position index
        //double dfz = fuzzifier.deFuzzify(new FuzzySet(zx, zy, zz), ExpressionType.PHENOTYPE);
        prediction = fuzzifier.deFuzzify(zValue, false, false, 0);       
        return prediction;
    }
}
     