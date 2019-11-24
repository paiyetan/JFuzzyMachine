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
    
    public FuzzySet[] getFuzzyArray(double exprs[]) {
        FuzzySet[] xfArr = new FuzzySet[exprs.length];       
        for(int i = 0; i < exprs.length; i++){           
            xfArr[i] = this.fuzzify(exprs[i]);            
        }
        return xfArr;
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
    
    
 
}
