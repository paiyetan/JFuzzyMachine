/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.simulation;

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
