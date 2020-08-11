/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine;

/**
 *
 * @author aiyetanpo
 */
public class ESearchResult{

    private final String outputGene;
    private final int numOfInputs;
    private final String[] inputGenes;
    private final String[] rules;
    private final double error;

    public ESearchResult(
                         String outputGene, 
                         int numOfInputs, 
                         String[] inputGenes, 
                         String[] rules, 
                         double error
    ) {

        this.outputGene = outputGene;
        this.numOfInputs = numOfInputs;
        this.inputGenes = inputGenes;
        this.rules = rules;
        this.error = error;

    }

    public String getOutputGene() {
        return outputGene;
    }

    public int getNumOfInputs() {
        return numOfInputs;
    }

    public String[] getInputGenes() {
        return inputGenes;
    }

    public String[] getRules() {
        return rules;
    }

    public double getError() {
        return error;
    }                
    
}
