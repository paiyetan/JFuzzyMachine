/*
  jFuzzyMachine (c) 2020, by Paul Aiyetan

  jFuzzyMachine is licensed under a
  Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.

  You should have received a copy of the license along with this
  work. If not, see <http://creativecommons.org/licenses/by-nc-nd/4.0/>
 */
package jfuzzymachine.utilities.graph;

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
