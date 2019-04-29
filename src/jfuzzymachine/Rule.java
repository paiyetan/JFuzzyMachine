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
public class Rule {
    
    private int low;
    private int mid;
    private int high;

    public Rule(int low, int mid, int high) {
        this.low = low;
        this.mid = mid;
        this.high = high;
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
    
    
    
}
