/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author paiyetan
 */
public class ProbableRegulonsMapFileReader {
    
     public HashMap<String, String[]> read(String mapFile) throws IOException{
        HashMap<String, String[]> map = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(mapFile));
        String line;
        int lineIndex = 0;
        while((line = reader.readLine())!=null){
            //if(line.charAt(0)!='#'){ // not a comment line...
            //    String optionValue = line.split("#")[0]; //ignore everything after the config comment character
            //    String[] lineArr = optionValue.split("=");
            //    config.put(lineArr[0].trim(), lineArr[1].trim());
            //}
            lineIndex++;
            if(lineIndex > 1){ //not header line
                String[] lineArr = line.split("\t");
                String out = lineArr[0]; //output...
                String[] ins = lineArr[1].split(", ");
                map.put(out, ins);
            }
            
        }
         
        reader.close();
         
         return map;
     }
    
}
