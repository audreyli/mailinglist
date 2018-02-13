package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable data type representing a email list with a single recipient
 */
public class Single implements EmailList {
    private final String recipient;
    
    // Abstraction function
    //    AF(recipient) = an email list with email address recipient
    // Rep invariant
    //    recipient contains a single "@"
    //    recipient is non-empty, and username and domain are non-empty.
    // Safety from rep exposure
    //    recipient is private and final, and is a String so it is immutable.
    
    /** Make a email list with a single recipient. 
     * 
     * @param recipient the single email address in the email list
     * An email address contains a username and domain name, separated by "@", like bitdiddle@mit.edu. 
     * Usernames and domain names are nonempty case-insensitive strings of letters, digits, 
     * underscores, dashes, and periods.
     */
    public Single(String recipient) {
        this.recipient = recipient.toLowerCase();
        checkRep();
    }
    
    // checks if email address has the appropriate characters
    private void checkRep(){
        assert recipient.matches("[a-z0-9_\\-\\.\\+]+@[a-z0-9_\\-\\.]+");
    }
    
    @Override
    public String toString() {
        return this.recipient;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Single) { return this.recipient.equals(((Single)obj).recipient);}
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.recipient.hashCode();
    }
    
    @Override
    public Set<String> recipients(Map<String, EmailList> environment) {
        return Collections.unmodifiableSet(Collections.singleton(this.recipient));
    }

    @Override
    public Set<String> getReferencedLists(Map<String, EmailList> environment) {
        Set<String> result = new HashSet<>();
        return result;
    }
}
