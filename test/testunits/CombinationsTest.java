/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testunits;

import org.apache.commons.math3.util.Combinations;

/**
 *
 * @author aiyetanpo
 */
public class CombinationsTest {
    
    public static void main(String[] args){
        Combinations inputCombinations = new Combinations(3, 2);
        for(int[] inputCombns : inputCombinations){
            for(int in : inputCombns){
                System.out.print(in + " ");
            }
            System.out.println();
        }   
    }
    
}
