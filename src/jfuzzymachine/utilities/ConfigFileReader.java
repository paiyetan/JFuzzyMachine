/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author aiyetanpo
 * 
 * 
 */
public class ConfigFileReader {
    
    public static HashMap<String, String> read(String configFilePath) throws FileNotFoundException, IOException{
        HashMap<String, String> config = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(configFilePath));
        String line;
        while((line = reader.readLine())!=null){
            if(line.charAt(0)!='#'){ // not a comment line...
                String optionValue = line.split("#")[0]; //ignore everything after the config comment character
                String[] lineArr = optionValue.split("=");
                config.put(lineArr[0].trim(), lineArr[1].trim());
            }
        }
        return config;
    }
    
}
