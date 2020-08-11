/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.tables;

import java.util.Arrays;

/**
 *
 * @author paiyetan
 */
public class RuleTable {
    
    private final int[][] ruleTable = {{1, 1, 1}, //0
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
                          
    
   
    public int[][] getRuleTable() {
        return ruleTable;
    }
    
    public int[] getRule(int rowIndex){
        int[] rule = ruleTable[rowIndex];
        return rule;
    }
    
    /*
    public static void main(String[] args){
        RuleTable rT = new RuleTable();
        for(int i = 0; i < 5; i++){
            System.out.println(Arrays.toString(rT.getRule(i)));
        }
    }
    */
}
