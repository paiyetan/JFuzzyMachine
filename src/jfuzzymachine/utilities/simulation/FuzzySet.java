/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.simulation;

/**
 *
 * @author aiyetanpo
 */
public class FuzzySet {
    
    private double y1, y2, y3;

    public FuzzySet(double y1, double y2, double y3) {
        this.y1 = y1; // degree of low
        this.y2 = y2; // degree of medium
        this.y3 = y3; // degree of high
    }
    
    public FuzzySet(double[] fzArr) {
        this.y1 = fzArr[0]; // degree of low
        this.y2 = fzArr[1]; // degree of medium
        this.y3 = fzArr[2]; // degree of high
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
    
    public double get(int index){
        //double value = 0;
        switch (index) {
            case 1:
                return y1;
            case 2:
                return y2;
            case 3:
                return y3;
            default:
                return 0;
        }
    }
}
