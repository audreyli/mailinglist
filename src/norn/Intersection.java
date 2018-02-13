package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable data type representing the set intersection of two EmailLists.
 */
public class Intersection implements EmailList {
    private final EmailList left, right;
    
    private static final int HASH_BASE = 47;
    // Abstraction function
    //    AF(left, right) = the email list that is the set intersection of left and right.
    // Rep invariant
    //    true
    // Safety from rep exposure:
    //    all fields are private and final
    //    left and right are type EmailList so they are immutable as given in the Project Handout.
    
    /** 
     * Make a email list which is the set intersection of left and right.
     * 
     * @param left the left email list
     * @param right the right email list
     */
    public Intersection(EmailList left, EmailList right) {
        this.left = left;
        this.right = right;
        checkRep();
    }
    
    /**
     * Checks that the rep invariant is not violated.
     */
    private void checkRep(){
        assert left != null;
        assert right != null;
    }

    @Override
    public String toString() {
        checkRep();
        return "(" + this.left.toString() + ") * (" + this.right.toString() + ")";
    }
    
    @Override
    public int hashCode() {
        checkRep();
        return this.left.hashCode() + HASH_BASE * this.right.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Intersection){
            return this.left.equals(((Intersection) obj).left) && this.right.equals(((Intersection) obj).right);
        }
        return false;
    }
    
    @Override
    public Set<String> recipients(Map<String, EmailList> environment) {
        Set<String> intersection = new HashSet<>();
        intersection.addAll(this.left.recipients(environment));
        intersection.retainAll(this.right.recipients(environment));
        return Collections.unmodifiableSet(intersection);
    }

    @Override
    public Set<String> getReferencedLists(Map<String, EmailList> environment) {
        Set<String> result = new HashSet<>();
        result.addAll(this.left.getReferencedLists(environment));
        result.addAll(this.right.getReferencedLists(environment));
        return result;
    }
}
