package norn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable data type representing a single listname email list
 */
public class ListName implements EmailList {
        private final String name;
        
        private static String VALID_SPECIAL_CHARACTER = "._-";
        // Abstraction function
        //    AF(name) = a ListName email list with the name given by name.
        // Rep invariant
        //    name must consist of letters, digits, underscores, dashes and periods only;
        // Safety from rep exposure
        //    all fields are private and final;
        //    there is no mutator;
        //    even if some observers return the field directly, since the returned field is of type String, 
        //      which is a primitive type, there is no rep exposure.
        
        /** 
         * Make a ListName which has the name value given by name
         * @param name the name value that the ListName takes on
         */
        public ListName(String name) {
            this.name = name.toLowerCase();
            checkRep();
        }
        
        /**
         * Check that the rep invariant is not violated.
         */
        private void checkRep(){
            // check if it contains only letters and periods, underscores and dashes 
            boolean checkCharacter = true;
            for (int i = 0; i < this.name.length(); i++){
                if (Character.isLetter(this.name.charAt(i)) 
                        || VALID_SPECIAL_CHARACTER.contains(Character.toString(this.name.charAt(i))) 
                        || Character.isDigit(this.name.charAt(i)))
                    continue;
                else
                    checkCharacter = false;
            }
            assert checkCharacter;
        }
        /**
         * Returns the String representation of the ListName, which is the name value that it takes on.
         */
        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ListName){
                return this.name.equals(((ListName) obj).name);
            }
            return false;
        }
        
        @Override 
        public int hashCode() {
            return this.name.hashCode();
        }
        
        @Override
        public Set<String> recipients(Map<String, EmailList> environment) {
            final Set<String> set = environment.get(this.name).recipients(environment);
            return Collections.unmodifiableSet(set);
        }

        @Override
        public Set<String> getReferencedLists(Map<String, EmailList> environment) {
            Set<String> result = new HashSet<>();
            result.addAll(environment.get(this.name).getReferencedLists(environment));
            result.add(this.name);
            return result;
        }
}
