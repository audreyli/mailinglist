/* Copyright (c) 2015-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

package norn;

import java.util.Map;
import java.util.Set;

import lib6005.parser.UnableToParseException;

/**
 * An immutable data type representing an email list.
 */
public interface EmailList {
    
    // Datatype definition:
    //    EmailList = Empty()
    //                  + Single(recipient:String)
    //                  + Union(left:EmailList, right:EmailList)
    //                  + Intersection(left:EmailList, right:EmailList) 
    //                  + Difference(left:EmailList, right: EmailList)
    //                  + ListName(name: String)
    
    
    /**
     * Evaluate an expression string given by the input. The structure of the expression must
     * be valid as defined by the project handout.
     * <p>
     * Valid expression include:
     * <ul>
     *  <li>empty expression (will mean an empty email list)
     *  <li>parsable email lists containing a number of email addresses, list names, and operators for
     *      union, difference and intersection<br>
     *      usernames and domain names of email addresses and list names are nonempty case-insensitive 
     *      strings of letters, digits, underscores, dashes, and period</li>
     *  <li>assignment of email lists or list names to list names characterized by a "=" in the given expression<br>
     *  will modify environment</li>
     *  <li>sequence or a single concatenated expression consisting of multiple valid expressions separated by ";"</li>
     * </ul>
     * 
     * @param input the valid expression to parse and evaluate
     * @param environment map of list names to their corresponding email lists,
     *                    will be modified if the expression is an assignment or an evaluation of a previously
     *                    undefined list name.
     * @return a valid email list expression with the same meaning as the input<br>
     *         a valid email list expression consisting solely of the email list expression
     *         being assigned to the list name with the same meaning, if the expression is an assignment<br>
     *         a valid email list expression consisting solely of the result of the evaluation
     *         of the last expression in the sequence with the same meaning,
     *         if the expression is a single concatenated expression
     * @throws IllegalArgumentException if the expression input is invalid
     */
    public static EmailList evaluate(String input, Map<String, EmailList> environment) {
        try { 
            return EmailListParser.parse(input, environment);
        } catch (UnableToParseException e){
            throw new IllegalArgumentException("Invalid expression!");
        }
    }

    
    /**
     * Make an empty email list
     * @return an empty email list
     */
    public static EmailList empty(){
        return new Empty();
    }
    
    /**
     * Make a email list with a single recipient
     * An email address of a recipient contains a username and domain name, separated by "@", 
     * like bitdiddle@mit.edu. Usernames and domain names are nonempty case-insensitive strings of letters, digits, 
     * underscores, dashes, and periods.
     * 
     * @param recipient email address of the recipient
     * @return a email list representing a email
     */
    public static EmailList single(String recipient){
        return new Single(recipient);
    }
    
    /**
     * Make a email list representing the union of two lists
     * @param left the left email list of the union
     * @param right the right email list of the union
     * @return a new mailing that is the union of two email lists
     */
    public static EmailList union(EmailList left, EmailList right){
        return new Union(left, right);
    }
    
    /**
     * Make a email list representing the intersection of two lists
     * @param left the left email list of the intersection
     * @param right the right email list of the intersection
     * @return a new email list that is the intersection of two email lists
     */
    public static EmailList intersection(EmailList left, EmailList right){
        return new Intersection(left, right); 
    }
    
    /**
     * Make a email list representing the difference of two lists
     * @param left the left email list of the difference
     * @param right the email list of the list to be differenced from left
     * @return a new email list that is the difference of two email lists
     */
    public static EmailList difference(EmailList left, EmailList right){
        return new Difference(left, right); 
    }

    /**
     * Make a email list representing a single listname email list
     * @param name the name of the list
     * @return a new listname email list with the name as given by name
     */
    public static EmailList listName(String name){
        return new ListName(name); 
    }
    
    /**
     * @return a parsable representation of this email list, such that
     * for all e:EmailList, e.equals(EmailList.parse(e.toString())).
     * 
     * Email addresses in the list are all lowercase and separated by a single comma and a space, 
     * with no extra characters anywhere else in the string.
     * If the email list is empty, returns the empty string.
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are equal EmailLists as defined.
     * 
     * Two EmailLists are equal if and only if they are structurally equal.
     * 
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of
     * equality, such that for all e1,e2:EmailList,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
  
    /**
     * Get the list of recipients
     * @param environment the environment in which the value of email lists are stored 
     * @return an immutable set of lowercased email addresses that the EmailList contains 
     *          after operations (union, difference, intersection) are performed
     */
    public Set<String> recipients(Map<String, EmailList> environment);
    
    /**
     * Get all the list names that are referred to in the definition of the EmailList expression, not including the given name
     * of the EmailList expression.
     * @param environment the environment in which the value of email lists are stored
     * @return a set of Strings with each String representing a listname of a defined email list that this EmailList expression refers to.
     *          If no defined list is referred to, returns an empty set
     */
    public Set<String> getReferencedLists(Map<String, EmailList> environment);
}
