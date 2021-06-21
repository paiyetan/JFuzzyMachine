/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.graph;
import java.util.LinkedList;

/**
 *
 * @author aiyetanpo
 */
public class TestedModel extends Model implements Comparable{
    
    private double testFit;
    private double delta;
    
    public TestedModel(Vertex outputNode, 
                       LinkedList<Vertex> inputNodes, 
                       LinkedList<String> rules, 
                       double fit,
                       double testFit,
                       double delta) {
        super(outputNode, inputNodes, rules, fit);
        this.testFit = testFit;
        this.delta = delta;
    }

    public double getTestFit() {
        return testFit;
    }

    public double getDelta() {
        return delta;
    } 

    @Override
    public int compareTo(Object o) {
        //return super.compareTo(o); 
        //To change body of generated methods, choose Tools | Templates.
        TestedModel ob = (TestedModel) o;
        if(this.getDelta() < ob.getDelta())
            return -1;
        else if(this.getDelta() > ob.getDelta())
            return +1;
        else
            return 0;
    }
    
    
}
