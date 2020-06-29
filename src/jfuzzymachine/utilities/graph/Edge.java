/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfuzzymachine.utilities.graph;

/**
 *
 * @author aiyetanpo
 */
public class Edge implements Comparable<Edge>{
    
    //private int id;
    private Vertex origin;
    private Vertex destination;
    private String rule;
    private double weight;    

    public Edge(Vertex origin, Vertex destination){
        this.origin = origin;
        this.destination = destination;
    }
    
    public Edge(Vertex origin, Vertex destination, 
                String rule) {
        this(origin, destination);
        this.rule = rule;             
    }
    
    public Edge(Vertex origin, Vertex destination, 
                String rule,
                double weight) {
        this(origin, destination, rule);
        this.weight = weight;       
    }
    
    /*
    public int getId() {
        return id;
    }
    */

    public Vertex getOrigin() {
        return origin;
    }

    public Vertex getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public String getRule() {
        return rule;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge e) {
        int value = 0;
        if (this.weight < e.getWeight())
            value = -1;
        if (this.weight > e.getWeight())
            value = +1;
        //return (int) this.weight - (int) e.getWeight();  //lossy conversion...
        return value;
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        }   
        /* Check if o is an instance of Egde or not 
          "null instanceof [type]" also returns false */
        //if (!(o instanceof Edge)) { 
        //    return false; 
        //}           
        // typecast o to Complex so that we can compare data members  
        Edge e = (Edge) o; 
          
        // Compare the data members and return accordingly  
        return this.origin.equals(e.getOrigin()) &&
                this.destination.equals(e.getDestination()); 
    }

    @Override
    public int hashCode() {
        int hash = 7;
        //hash = 59 * hash + Objects.hashCode(this.origin);
        //hash = 59 * hash + Objects.hashCode(this.destination);
        //return hash;
        
        String str = origin.getId() + destination.getId();
        hash = str.hashCode();
        return hash;
    }
    
    
    
    
}
