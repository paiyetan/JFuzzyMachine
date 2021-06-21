/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.graph;

import java.util.Arrays;
import java.util.LinkedList;
import jfuzzymachine.Rule;
import jfuzzymachine.tables.Table;
import jfuzzymachine.utilities.FitEvaluator;

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
    
    public String[] getInputNodesStringArray(){
        String[] ins = new String[inputNodes.size()];
        for(int i = 0; i < ins.length; i++)
            ins[i] = inputNodes.get(i).getId();
        return ins;
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
    
    public LinkedList<Rule> getRulesLinkedList(){
        LinkedList<Rule> rulesList = new LinkedList();
        for(int i = 0; i < rules.size(); i++){
            String rule = rules.get(i);
            String[] ruleArr = rule.split(", ");
            int[] ruleArri = new int[ruleArr.length];
            for(int j = 0; j < ruleArri.length; j++)
                ruleArri[j] = Integer.parseInt(ruleArr[j]);
            rulesList.add(new Rule(ruleArri));
        }       
        return rulesList;
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
    
    public void computeFit(Table exprs){
        FitEvaluator fitEvaluator = new FitEvaluator();
        fit = fitEvaluator.evaluateFit(outputNode.getId(), //String outputNode,
                                        getInputNodesStringArray(), //String[] inputNodes
                                        getRulesLinkedList(), //LinkedList<Rule> rules
                                        exprs, //Table expMat
                                        false, //boolean outputIsPheno
                                        null, //Table phenoMat
                                        false, //boolean tanTransform
                                        true, //boolean logitTransform
                                        0 //double k
                                        );
    }
    
}
