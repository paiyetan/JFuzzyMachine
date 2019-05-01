/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine;

import java.util.Arrays;

/**
 *
 * @author aiyetanpo
 */
public class Rule {
    
    private int low;
    private int mid;
    private int high;
    
    private int[] arr;
    

    public Rule(int low, int mid, int high) {
        this.arr = new int[3]; 
        this.low = low;
        this.mid = mid;
        this.high = high;
        
        this.arr[0] = low;
        this.arr[1] = mid;
        this.arr[2] = high;
    }

    public int getLow() {
        return low;
    }

    public int getMid() {
        return mid;
    }

    public int getHigh() {
        return high;
    }
    
    public String toString(){
        return Arrays.toString(arr);
    }
    
    
    
}
