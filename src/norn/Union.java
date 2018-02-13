package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable data type representing the union of two EmailLists
 */
public class Union implements EmailList {
    private final EmailList left, right;
    
    private static final int HASH_BASE = 17;
    // Abstraction function
    //    AF(left, right) = the union of two email lists, left and right.
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    all fields are private and final
    //    left and right are type EmailList so they are immutable as given in the Project Handout.
    
    /** 
     * Make a email list which is the union of left and right. 
     * 
     * @param left the left email list
     * @param right the right email list
     */
    public Union(EmailList left, EmailList right) {
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    /**
     * Check that the rep invariant is not violated.
     */
    private void checkRep(){
        assert left != null;
        assert right != null;
    }
    
    @Override
    public String toString() {
        checkRep();
        return "(" + this.left.toString() + ") , (" + this.right.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        checkRep();
        if (obj instanceof Union){
            return this.left.equals(((Union) obj).left) && this.right.equals(((Union) obj).right);
        }
        return false;
    }
    
    @Override 
    public int hashCode() {
        checkRep();
        return this.left.hashCode() + HASH_BASE * this.right.hashCode();
    }
    
    @Override
    public Set<String> recipients(Map<String, EmailList> environment) {
        final Set<String> set = new HashSet<>();
        set.addAll(this.left.recipients(environment));
        set.addAll(this.right.recipients(environment));
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Set<String> getReferencedLists(Map<String, EmailList> environment) {
        Set<String> result = new HashSet<>();
        result.addAll(this.left.getReferencedLists(environment));
        result.addAll(this.right.getReferencedLists(environment));
        return result;
    }
}
