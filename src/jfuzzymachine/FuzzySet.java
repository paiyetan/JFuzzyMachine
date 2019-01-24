/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

/**
 *
 * @author aiyetanpo
 */
public class FuzzySet {
    
    private double y1, y2, y3;

    public FuzzySet(double y1, double y2, double y3) {
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public double getY3() {
        return y3;
    }
    
    public double[] getSetAsArray(){
        double[] arr = new double[3];
        arr[0] = y1;
        arr[1] = y2;
        arr[2] = y3;
        
        return arr;
    }
            
}
