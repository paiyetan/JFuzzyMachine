/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;
import java.util.LinkedList;
import tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class Fuzzifier { 
    
    
    FuzzySet[][] getFuzzyMatrix(Table exprs, JFuzzyMachine.ExpressionType expressionType) {
        //Table fuzzyTable;
        double[][] mat = exprs.getDoubleMatrix();
        FuzzySet[][] xfMat = new FuzzySet[exprs.getRowIds().length][exprs.getColumnIds().length];
        
        for(int i = 0; i < exprs.getRowIds().length; i++){
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                xfMat[i][j] = this.fuzzify(mat[i][j], expressionType);
            }
        }
       
        return xfMat;
    }    
    
    public FuzzySet[] getFuzzyArray(double exprs[], JFuzzyMachine.ExpressionType expressionType) {
        FuzzySet[] xfArr = new FuzzySet[exprs.length];       
        for(int i = 0; i < exprs.length; i++){           
            xfArr[i] = this.fuzzify(exprs[i], expressionType);            
        }
        return xfArr;
    }
    
    public FuzzySet fuzzify(double value, JFuzzyMachine.ExpressionType expressionType){
        
        FuzzySet fz;
        double y1 = 0, y2 = 0, y3 = 0;
        value = Math.atan(value); //apply the atan normalization...
        value = value / (Math.PI/2); // 
        
        switch(expressionType){
            
            case PHENOTYPE:
                y1 = (value < 0.5) ? (1 - (2*value)) : 0 ;
                y2 = 1 - Math.abs((2*value) - 1);
                y3 = (value < 0.5) ? 0 : ((2*value) - 1) ;
                break;
                
            default: // GENOTYPE
                y1 = (value < 0) ? -value : 0 ; 
                y2 = 1 - Math.abs(value);
                y3 = (value <= 0) ? 0: value ;
                break;
        
        }
        
        fz = new FuzzySet(y1, y2, y3);        
        return fz;
    }     
    
    
    public FuzzySet getZValue(FuzzySet[] inputsFuzzArr, 
                              LinkedList<Rule> inputsRules) {
        FuzzySet tmpZ = inputsFuzzArr[0];
        Rule rule = inputsRules.get(0);
        double[] setAsArr = tmpZ.getSetAsArray();
        FuzzySet z = new FuzzySet(setAsArr[rule.getLow()-1], 
                                  setAsArr[rule.getMid()-1], 
                                  setAsArr[rule.getHigh()-1]);       
        //int l = inputsFuzzArr.length;
        if(inputsFuzzArr.length > 1){
            for(int i = 1; i < inputsFuzzArr.length; i++){
                tmpZ = inputsFuzzArr[i];
                rule = inputsRules.get(i);
                setAsArr = tmpZ.getSetAsArray();
                FuzzySet zi = new FuzzySet(setAsArr[rule.getLow()-1], 
                                           setAsArr[rule.getMid()-1], 
                                           setAsArr[rule.getHigh()-1]);
                z.add(zi);
            }
        }
    
        return z;
    }

    public double deFuzzify(FuzzySet fz, JFuzzyMachine.ExpressionType expressionType){
        double dfz;
        dfz = (fz.getY3() - fz.getY1())/(fz.getY1() + fz.getY2() + fz.getY3());  
                
        switch(expressionType){           
            case PHENOTYPE:
                //dfz = dfz * (Math.PI/2);//
                //dfz = Math.tan(dfz);   // 
                dfz = 1/( 1 + Math.pow(Math.E, ( -6 * ((2*dfz)-1)) ));
                break;
                
            default: // GENOTYPE               
                dfz = dfz * (Math.PI/2);
                dfz = Math.tan(dfz);        
                break;        
        }       
        return dfz;
    } 

    
    public double deFuzzify(FuzzySet fz, 
                               boolean tanTransform,
                                   boolean logitTransform, 
                                       double k){
        double dfz;
        dfz = (fz.getY3() - fz.getY1())/(fz.getY1() + fz.getY2() + fz.getY3()); 
        
        if(tanTransform){
            dfz = dfz * (Math.PI/2);
            dfz = Math.tan(dfz); 
        }
        
        if(logitTransform){
            dfz = 1/( 1 + Math.pow(Math.E, ( -k * dfz)) );
        } 
        
        return dfz;
    }
    
   
 
}
