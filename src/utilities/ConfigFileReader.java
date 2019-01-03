/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author aiyetanpo
 */
public class ConfigFileReader {
    
    public HashMap<String, String> read(String configFilePath) throws FileNotFoundException, IOException{
        HashMap<String, String> config = new HashMap();
        BufferedReader reader = new BufferedReader(new FileReader(configFilePath));
        String line;
        while((line = reader.readLine())!=null){
            String optionValue = line.split("#")[0]; //ignore everything after the config comment character
            String[] lineArr = optionValue.split("\t");
            config.put(lineArr[0], lineArr[1]);
        }
        return config;
    }
    
}
