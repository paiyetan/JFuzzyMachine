/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unittests;

import java.util.Arrays;
import org.apache.commons.math3.util.Combinations;

/**
 *
 * @author paiyetan
 */
public class StringsTest {
    public static void main(String[] args){
        String str = "> End Search Result Table ";
        String str2 = "Adekolajo.jfuz";
        System.out.println(str.startsWith("> End Search"));
        System.out.println(str2.endsWith(".jfuz"));
        System.out.println(str2.split("\\.")[0]);    
    }
}
