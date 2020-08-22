/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.exceptions;

/**
 *
 * @author paiyetan
 */
public class TableBindingException extends Exception {

    public TableBindingException(String message) {
        System.out.println();
        System.out.println("ERROR: TableBindingException...");
        System.out.println("..." + message);
        System.exit(1);
    }
    
}
