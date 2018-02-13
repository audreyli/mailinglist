package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable data type representing an empty email list
 */
public class Empty implements EmailList{
    
    // Abstraction function
    //    AF() = an empty email list of recipients
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    no rep so no rep exposure
    
    public Empty() {
    }
    
    @Override 
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Empty) { return true; }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Collections.emptySet().hashCode();
    }
    
    @Override
    public Set<String> recipients(Map<String, EmailList> environment) {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getReferencedLists(Map<String, EmailList> environment) {
        Set<String> result = new HashSet<String>();
        return result;
    }
    

}
