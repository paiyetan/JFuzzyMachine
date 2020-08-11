/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
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
        
        reader.close();
        return config;
    }
    
}
