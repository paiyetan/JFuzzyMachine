/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.rconnect;

/**
 *
 * @author paiyetan
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author paiyeta1
 */
public class StreamReader implements Runnable{
    
    String name;
    InputStream is;
    Thread thread;   
    
    public StreamReader(String name, InputStream is) {
        this.name = name;
        this.is = is;
    }  
    
    public void start () {
        thread = new Thread (this);
        thread.start ();
    }
    
    @Override
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (is);
            BufferedReader br = new BufferedReader (isr);   
            while (true) {
                String s = br.readLine ();
                if (s == null) break;
                System.out.println ("[" + name + "] " + s);
            }
            is.close ();    
        } catch (Exception ex) {
            System.out.println ("Problem reading stream " + name + "... :" + ex);
            ex.printStackTrace ();
        }
    }   
}
