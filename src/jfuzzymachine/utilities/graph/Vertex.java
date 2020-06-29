/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.graph;

/**
 *
 * @author aiyetanpo
 */
public class Vertex implements Comparable<Vertex>{
    
    private String id;

    public Vertex(String id) {
        this.id = id;
    }

    public String getId() { 
        return id;
    }

    @Override
    public int compareTo(Vertex v) {
        return this.id.compareToIgnoreCase(v.getId());
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        }   
        /* Check if o is an instance of Egde or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Vertex)) { 
            return false; 
        }           
        // typecast o to Complex so that we can compare data members  
        Vertex v = (Vertex) o;          
        // Compare the data members and return accordingly  
        return this.id.equals(v.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode(); 
    }
    
    
    
    
    
}
