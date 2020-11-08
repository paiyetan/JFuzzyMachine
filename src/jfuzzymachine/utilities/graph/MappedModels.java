/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.graph;

import java.util.Collections;
import java.util.LinkedList;

/**
 * a container class to hold mapped models an help reduce memory footprint....
 * @author paiyetan
 */
public class MappedModels {
    
    private Vertex vertex;
    private LinkedList<Model> mappedModels; 
    private int maxMappedModels;    
    private int numOfMappedModels;
    private double maxEstimatedFit; // bestFit
    private double minEstimatedFit; 
    
    
    public MappedModels(Vertex vertex, 
                        LinkedList<Model> mappedModels,
                        int maxMappedModels,
                        double maxEstimatedFit,
                        double minEstimatedFit) {
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
        this.vertex = vertex;
        this.mappedModels = mappedModels;
        this.maxMappedModels = maxMappedModels;
        this.maxEstimatedFit = maxEstimatedFit;
        this.minEstimatedFit = minEstimatedFit;
        
        this.numOfMappedModels = mappedModels.size();
    }

    
    public void add(Model model) {
        mappedModels.add(model);
        if(model.getFit() >= maxEstimatedFit){
            maxEstimatedFit = model.getFit();
        }
        if(model.getFit() <= minEstimatedFit){
            minEstimatedFit = model.getFit();
        }
        this.numOfMappedModels++;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public LinkedList<Model> getMappedModels() {
        return mappedModels;
    }

    public int getMaxMappedModels() {
        return maxMappedModels;
    }

    public int getNumOfMappedModels() {
        return numOfMappedModels;
    }

    public double getMaxEstimatedFit() {
        return maxEstimatedFit;
    }

    public double getMinEstimatedFit() {
        return minEstimatedFit;
    }

    public void removeLeastFitted() {
        Collections.sort(mappedModels);// sort mappedModels in ascending order of estimated fit.
        mappedModels.removeFirst(); //remove the least fitted
        //update min and max estimated fits
        minEstimatedFit = mappedModels.getFirst().getFit();
        //maxEstimatedFit = mappedModels.getLast().getFit();
        numOfMappedModels--;
    }
    
    
    
    
    
}
