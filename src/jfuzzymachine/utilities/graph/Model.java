/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.graph;

import java.util.Arrays;
import java.util.LinkedList;

/**
 *
 * @author aiyetanpo
 */
public class Model implements Comparable{
    
    private Vertex outputNode;
    private LinkedList<Vertex> inputNodes;
    LinkedList<String> rules;
    private double fit;

    public Model(Vertex outputNode, 
            LinkedList<Vertex> inputNodes, 
            LinkedList<String> rules, 
            double fit
    ) {
        this.outputNode = outputNode;
        this.inputNodes = inputNodes;
        this.rules = rules;
        this.fit = fit;
    }

    public Vertex getOutputNode() {
        return outputNode;
    }

    public LinkedList<Vertex> getInputNodes() {
        return inputNodes;
    }
    
    public String getInputNodesString(){
        String[] ins = new String[inputNodes.size()];
        for(int i = 0; i < ins.length; i++)
            ins[i] = inputNodes.get(i).getId();
        return Arrays.toString(ins);
    }

    public LinkedList<String> getRules() {
        return rules;
    }
    
    public String getRulesString(){
        String[] ins = new String[rules.size()];
        for(int i = 0; i < ins.length; i++)
            ins[i] = "[" + rules.get(i) + "]";
        return Arrays.toString(ins);
    }


    public double getFit() {
        return fit;
    }

    @Override
    public int compareTo(Object o) {
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        Model ob = (Model) o;
        if(this.getFit() < ob.getFit())
            return -1;
        else if(this.getFit() > ob.getFit())
            return +1;
        else
            return 0;
    }
    
    
    
}
