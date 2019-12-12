/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;
import tables.Table;

/**
 *
 * @author aiyetanpo
 */
public class Fuzzifier { 
    
    /*
    public FuzzySet[][] getFuzzyMatrix(Table exprs) {
        //Table fuzzyTable;
        double[][] mat = exprs.getDoubleMatrix();
        FuzzySet[][] xfMat = new FuzzySet[exprs.getRowIds().length][exprs.getColumnIds().length];
        
        for(int i = 0; i < exprs.getRowIds().length; i++){
            for(int j = 0; j < exprs.getColumnIds().length; j++){
                xfMat[i][j] = this.fuzzify(mat[i][j]);
            }
        }
        return xfMat;
    }
    */
    
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
    
    /*
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
    */
    
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

    
    
 
}
