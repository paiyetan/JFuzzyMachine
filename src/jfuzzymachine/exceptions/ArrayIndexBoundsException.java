/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
