package norn;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for sequence expression inputs to EmailList.evaluate()
 */
public class EmailListEvaluateSequenceTest {
    
    /**
     * Testing strategy for EmailList.evaluate() 
     * Tests will include integrated testing for sequences or concatenation
     * of valid expressions like assignments and mailing list expressions
     * =======================================================================
     * Partition the input as:
     * -----------------------------------------------------------------------
     * input:
     *     # expressions   (n): 0, 2, >2
     *     type            (t): assignment, union, difference, intersection,
     *                          empty, single
     *     last expression (l): assignment, single, union, difference,
     *                          intersection, empty     
     *     relations       (r): (1) no relation between concatenated expressions,
     *                          (2) assignment then use of assigned list name,
     *                          (3) assignment then one or more disjoint expressions 
     *                              between before referencing assignment again
     *                          (4) nested assignments
     *     whitespaces     (w): 0, 1, >1
     *         
     * environment (for non-assignment expressions):
     *     relation to set of list names in the expression (s):
     *         same set, superset
     * 
     * Phase 2:
     * [Testing Lists that depend on each other can be created in any order]:
     * [Input]:
     * First Sequence:
     *      # expressions in sequence 3, >3
     *      assignment of depending list: start, middle, end 
     * Second Sequence:
     *      # expressions in sequence 3, >3
     *      assignment of depending list: start, middle, end 
     * [Output]:
     * True
     * 
     * [Testing List Can Be Redefined Using Previous Definition (Nesting)]:
     *      #expressions in sequence: 3, >3
     *      listname # of occurrences in nesting: 1, >1
     * [Testing Dependent Lists See Edits]:
     *      # expressions in sequence: 4, >4
     *      # dependent listnames in an expression of a sequence: 1, >1
     *      # dependent listnames changed in expression of a sequence: 1, >1
     * [Testing Fully-Closed List Expressions]:
     *      cases: union, difference, intersection, definitionlistname=e, sequence, grouping 
     *      subexpression1: union, difference, intersection 
     *      subexpression2: union, difference, intersection 
     * [Testing Loops Disallowed]:
     *     # expressions in sequence: 2, >2
     *     direct loop (loop after 2 expressions), indirect loop (loop after >2 expressions)
     */
    
    // Covers n=0, t=empty, l=empty, r=1, w=0, s=same set
    @Test
    public void testEvaluateSequenceBlankInput() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("", environment);
        assertEquals("expected empty list", EmailList.empty(), list);
    }
    
    // Covers n>2, t=empty, l=empty, r=1, w=1, s=same set
    @Test
    public void testEvaluateSequenceSeveralBlankInputs() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate(" ; ; ; ; ; ", environment);
        assertEquals("expected empty list", EmailList.empty(), list);
    }
    
    // Covers n=2, t=assignment, l=assignment, r=1, w>1, s=same set
    @Test
    public void testEvaluateSequenceAssignments() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a=kw@mit  ;  1=jc@csail,ak@mit", environment);
        
        EmailList expected = EmailList.union(EmailList.single("jc@csail"), EmailList.single("ak@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a", EmailList.single("kw@mit"));
        expectedEnvironment.put("1", expected);
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers n=2, t=single, l=single, r=1, w=0, s=same set
    @Test
    public void testEvaluateSequenceTwoRandomSingles() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("kw@mit  ;  jc@csail", environment);
        
        EmailList expected = EmailList.single("jc@csail");
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers n=2, t=assignment, difference, l=difference, r=2, w=0, s=same set
    @Test
    public void testEvaluateSequenceAssignmentThenUseIt() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("2a=kw@mit,jc@csail,ak@mit ; 2a ! jc@csail", environment);
        
        EmailList expected = EmailList.union(EmailList.single("kw@mit"), EmailList.single("ak@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("2a", EmailList.union(EmailList.union(EmailList.single("kw@mit"), 
                EmailList.single("ak@mit")), EmailList.single("jc@csail")));
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers n>2, t=assignment, union, l=union, r=1, w=0, s=same set
    @Test
    public void testEvaluateSequenceAssigmentsThenDisjointUnion() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a=kw@mit ; 1=jc@csail; kw@mit, ak@mit; jc@csail, ak@mit", environment);
        
        EmailList expected = EmailList.union(EmailList.single("jc@csail"), EmailList.single("ak@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a", EmailList.single("kw@mit"));
        expectedEnvironment.put("1", EmailList.single("jc@csail"));
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers n>2, t=union, single, intersection, union, l=intersection, r=4, w=0, s=same set
    @Test
    public void testEvaluateSequenceAssigmentThenUnionThenIntersection() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a=kw@mit ; b=jc@csail, ak@mit, a; "
                + "c = b * ak@mit; c", environment);
        
        EmailList expected = EmailList.single("ak@mit");
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a", EmailList.single("kw@mit"));
        expectedEnvironment.put("b", EmailList.union(EmailList.union(EmailList.single("kw@mit"),
                EmailList.single("jc@csail")), EmailList.single("ak@mit")));
        expectedEnvironment.put("c", EmailList.single("ak@mit"));
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers n>2, t=assignment, union, single, difference, l=difference, r=3, w=0, s=superset
    @Test
    public void testEvaluateSequenceAssigmentsThenRandomStuffThenUseAssignments() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a=ak@mit; 1=jc@mit, kw@mit, ak@mit; "
                + "kw@mit, sb@jc; ..=dots@d0Ts; 1 ! a", environment);
        
        EmailList expected = EmailList.difference(EmailList.union(EmailList.union(EmailList.single("kw@mit"),
                EmailList.single("jc@mit")), EmailList.single("ak@mit")), EmailList.single("ak@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a", EmailList.single("ak@mit"));
        expectedEnvironment.put("1", EmailList.union(EmailList.union(EmailList.single("kw@mit"),
                EmailList.single("jc@mit")), EmailList.single("ak@mit")));
        expectedEnvironment.put("..", EmailList.single("dots@d0Ts"));
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers result of concatenated expression same as separated expression
    @Test
    public void testEvaluateSequenceWithLOTR() {
        Map<String, EmailList> environmentSequence = new HashMap<>();
        EmailList listSequence = EmailList.evaluate("hobbits = bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire ; "
                + "gandalf@cosmos, hobbits ; "
                + "bagginses = bilbo@shire, frodo@shire ; "
                + "hobbits !bagginses", environmentSequence);
        
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("hobbits = bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire", environment);
        EmailList.evaluate("gandalf@cosmos, hobbits ", environment);
        EmailList.evaluate("bagginses = bilbo@shire, frodo@shire ", environment);
        EmailList list = EmailList.evaluate("hobbits !bagginses", environment);
        
        Map<String, EmailList> environmentExpected = new HashMap<>();
        environmentExpected.put("hobbits", 
                EmailList.evaluate("bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire", environmentExpected));
        environmentExpected.put("bagginses", 
                EmailList.evaluate("bilbo@shire, frodo@shire", environmentExpected));
        EmailList expected = EmailList.evaluate("merry@shire, sam@shire, pippin@shire", environmentExpected);
        
        assertEquals("expected environment", environmentExpected, environment);
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
        assertEquals("expected same environment", environmentSequence, environment);
        assertEquals("expected same list", listSequence, list);  
    }
    @Test
    public void testEvaluateSequenceRedefinePrevDefLengthThreeNestOnce(){
        Map<String, EmailList> environmentSequence = new HashMap<>();
        EmailList.evaluate("room1=alice@mit.edu;"
                + "room1=room1,eve@mit.edu;"
                + "room1", environmentSequence);
        
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("room1", EmailList.evaluate("alice@mit.edu,eve@mit.edu", expectedEnvironment));
        assertEquals("expected environment", expectedEnvironment, environmentSequence);
    }
    
    //covers length of sequence >3, listname occurrence in nesting greater than once
    @Test
    public void testEvaluateSequenceRedefinePrevDefLengthMultRedefineMultNest(){
        Map<String, EmailList> environmentSequence = new HashMap<>();
        EmailList.evaluate("room1=alice@mit.edu;"
                + "room1=room1,room1,room1;"+
                "room2=room1, a@mit.edu;"+
                "room1", environmentSequence);
        
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("room1", EmailList.evaluate("alice@mit.edu,alice@mit.edu, alice@mit.edu", expectedEnvironment));
        expectedEnvironment.put("room2", EmailList.union(EmailList.listName("room1"), EmailList.single("a@mit.edu")));
        assertEquals("expected environment", expectedEnvironment, environmentSequence);
    }
    
//    * [Testing Dependent Lists See Edits]:
//    *      # expressions in sequence: 4, >4
//    *      # dependent listnames in an expression of a sequence: 1, >1
//    *      # listnames changed in expression of a sequence: 1, >1
    
    

    //covers # expression in sequence 4, #dependent listnames in expression of sequence 1, #listnames changed 
    // in expression of a sequence 1
    @Test
    public void testEvaluateSequenceDependentLengthFourUpdateOne(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.evaluate("team=cool@mit.edu;"+
        "allteams = team"+
                "team = bowling@mit.edu;"+
        "allteams", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bowling@mit.edu");
        assertEquals("bowling", true, exp.recipients(environment).equals(expected));
    }
    //covers # expression in sequence >4, #dependent listnames in expression of sequence >1, #listnames changed
    // in expression of a sequence 1
    @Test
    public void testEvaluateSequenceDependentUpdateChangeOne(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.evaluate("room1=alice@mit.edu;"+
        "room2=bob@mit.edu;"+
                "suite=room1,room2;"+"room1=eve@mit.edu;"+
        "suite", environment);
        Set<String> expected = new HashSet<>();
        expected.add("eve@mit.edu");
        expected.add("bob@mit.edu");
        assertEquals("bob and eve", true, exp.recipients(environment).equals(expected));
    }
    //covers #expressions in sequence >4, #dependent listnames in expression of sequence >1, #listnames changed >1
    @Test
    public void testEvaluateSequenceDependentUpdateChangeMultiple(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.evaluate("room1=alice@mit.edu;"+
                "room2=bob@mit.edu;"+
        "apples=room1, room2;"+
                "room1=oranges@fruit;"+"room2=lemons@morefruit;"+
        "apples", environment);
        Set<String> expected = new HashSet<>();
        expected.add("oranges@fruit");
        expected.add("lemons@morefruit");
        assertEquals("oranges and lemons", true, exp.recipients(environment).equals(expected));
    }
    
    //covers First Sequence: #expressions in sequence >3, assignment of depending list start
    //covers Sequence Sequence: #expression in sequence >3, assignment of depending list end
    //covers true
    @Test
    public void testEvaluateSequenceDependOnEachOtherFirstLast(){
        Map<String, EmailList> environment1 = new HashMap<>();
        EmailList exp1 = EmailList.evaluate("suite=room1,room2;"+
                "room1=alice@mit.edu;"+
        "room2=bob@mit.edu;"+
                "suite", environment1);
        
        Map<String, EmailList> environment2 = new HashMap<>();
        EmailList exp2 = EmailList.evaluate(
                "room1=alice@mit.edu;"+
        "room2=bob@mit.edu;"+
        "suite=room1,room2;"+
                "suite", environment2);
        assertEquals("same recipients", true, exp1.recipients(environment1).equals(exp2.recipients(environment2)));
    }
    
    //covers First Sequence: #expressions in sequence >3, assignment of depending list start
    //covers Sequence Sequence: #expression in sequence >3, assignment of depending list middle
    //covers true
    @Test
    public void testEvaluateSequenceDependOnEachOtherStartMiddle(){
        Map<String, EmailList> environment1 = new HashMap<>();
        EmailList exp1 = EmailList.evaluate("suite=room1,room2;"+
                "room1=alice@mit.edu;"+
        "room2=bob@mit.edu;"+
                "suite", environment1);
        
        Map<String, EmailList> environment2 = new HashMap<>();
        EmailList exp2 = EmailList.evaluate(
                "room1=alice@mit.edu;"+
        "suite=room1,room2;"+
        "room2=bob@mit.edu;"+
                "suite", environment2);
        assertEquals("same recipients", true, exp1.recipients(environment1).equals(exp2.recipients(environment2)));
    }
    
    //covers First Sequence: #expressions in sequence >3, assignment of depending list middle
    //covers Sequence Sequence: #expression in sequence >3, assignment of depending list end
    //covers true
    @Test
    public void testEvaluateSequenceDependOnEachOtherMiddleEnd(){
        Map<String, EmailList> environment1 = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "room2=bob@mit.edu;"+
                "suite=room1,room2;"+
                "room1=alice@mit.edu;"+
                "suite", environment1);
        
        Map<String, EmailList> environment2 = new HashMap<>();
        EmailList exp2 = EmailList.evaluate(
                "room1=alice@mit.edu;"+
        "room2=bob@mit.edu;"+
        "suite=room1,room2;"+
                "suite", environment2);
        assertEquals("same recipients", true, exp1.recipients(environment1).equals(exp2.recipients(environment2)));
    }
//    * [Testing Fully-Closed List Expressions]:
//    *      cases: union, difference, intersection, definitionlistname=e, sequence, grouping 
//    *      subexpression1: union, difference, intersection 
//    *      subexpression2: union, difference, intersection 
//    *  
    //covers case = union, subexpression1=union, subexpression2=union
    @Test
    public void testEvaluateSequenceFullyClosedUnionOfUnions(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "b=carl@mit.edu;"+
                "bleh=george@mit.edu;"+
                "cool=akuang@mit.edu;"+
                "c=a,b;"+
                "d=bleh,cool;"+
                "e=c,d", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bob@mit.edu");
        expected.add("akuang@mit.edu");
        expected.add("carl@mit.edu");
        expected.add("george@mit.edu");
        assertEquals("expected contains correct unions", true, exp1.recipients(environment).equals(expected));
    }
    
    //covers case = difference, subexpression1=difference, subexpression2=difference
    @Test
    public void testEvaluateSequenceFullyClosedDifferenceOfDifferences(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "b=carl@mit.edu;"+
                "bleh=george@mit.edu;"+
                "cool=akuang@mit.edu;"+
                "c=a!b;"+
                "d=bleh!cool;"+
                "e=c,d", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bob@mit.edu");
        expected.add("george@mit.edu");
        assertEquals("expected contains correct unions", true, exp1.recipients(environment).equals(expected));
    }
    //covers case = intersection, subexpression1=intersection, subexpression2=intersection
    @Test
    public void testEvaluateSequenceFullyClosedIntersectionOfIntersections(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "b=carl@mit.edu, bob@mit.edu;"+
                "bleh=george@mit.edu;"+
                "cool=akuang@mit.edu, george@mit.edu;"+
                "c=a*b;"+
                "d=bleh*cool;"+
                "e=c,d", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bob@mit.edu");
        expected.add("george@mit.edu");
        assertEquals("expected contains correct unions", true, exp1.recipients(environment).equals(expected));
    }
    
    //covers case = definition listname
    @Test
    public void testEvaluateSequenceFullyClosedDefinitionListname(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "c=a", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bob@mit.edu");
        assertEquals("expected contains correct unions", true, exp1.recipients(environment).equals(expected));
    }
    
    //covers case = sequence, subexpression1 =union, subexpression2=union 
    @Test
    public void testEvaluateSequenceFullyClosedSequenceOfUnions(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "b=carl@mit.edu, bob@mit.edu;"+
                "bleh=george@mit.edu;"+
                "cool=akuang@mit.edu, george@mit.edu;"+
                "c=a;b;", environment);
        Set<String> expected = new HashSet<>();
        assertEquals("expected contains correct unions", true, exp1.recipients(environment).equals(expected));
    }
    
    //covers case = intersection, subexpression1=intersection, subexpression2=intersection
    @Test
    public void testEvaluateSequenceFullyClosedIntersectionOfIntersectionsGrouping(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=bob@mit.edu;"+
                "b=carl@mit.edu, bob@mit.edu;"+
                "bleh=george@mit.edu;"+
                "cool=akuang@mit.edu, george@mit.edu;"+
                "c=a*(b);"+
                "d=bleh*cool;"+
                "e=c,(d)", environment);
        Set<String> expected = new HashSet<>();
        expected.add("bob@mit.edu");
        expected.add("george@mit.edu");
        assertEquals("expected contains correct unions and grouping okay", true, exp1.recipients(environment).equals(expected));
    }

    @Test(expected=IllegalArgumentException.class)
    //covers #expression in sequenced 2, direct loop
    public void testEvaluateSequenceDirectLoopDisallowed(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=b;b=a", environment);   
    }
    
    @Test(expected=IllegalArgumentException.class)
    //covers #expression in sequence > 2, indirect loop
    public void testEvaluateSequenceIndirectLoopDisallowed(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate(
                "a=b;b=c;c=a", environment);   
    }
}