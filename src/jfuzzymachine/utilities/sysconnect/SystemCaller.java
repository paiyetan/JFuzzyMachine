/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
http://stackoverflow.com/questions/13008526/runtime-getruntime-execcmd-hanging
 */
package jfuzzymachine.utilities.sysconnect;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paiyetan
 */
public class SystemCaller {
    
    public void execute(String cmd){

        Process p = null;
        try{
            p = Runtime.getRuntime().exec(cmd);            
            StreamReader s1;
            StreamReader s2;
            s1 = new StreamReader("stdin", p.getInputStream());
            s2 = new StreamReader("stderr", p.getErrorStream());
            s1.start();
            s2.start();
            p.waitFor(); 

        } catch (IOException ex) {  
            Logger.getLogger(SystemCaller.class.getName()).log(Level.SEVERE, null, ex);  
        }catch (Exception e) {  
            e.printStackTrace();                 
        } finally {
            if(p != null)
                p.destroy();
        }
        //return;
     }   
}
