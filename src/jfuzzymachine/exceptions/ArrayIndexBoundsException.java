/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.exceptions;

import jfuzzymachine.utilities.graph.Vertex;

/**
 *
 * @author paiyetan
 */
public class ArrayIndexBoundsException extends Exception {

    public ArrayIndexBoundsException(int ruleIndex, 
            int inputIndex, 
            Vertex inputNode, 
            Vertex outputNode) {
    }
    
}
