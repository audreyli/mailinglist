package norn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the Expression abstract data type and its variant classes.
 */

public class EmailListTest {

    // Test using factory methods, toString, hashCode, and equals
    //  ensure equals follows equality spec and compares correctly
    //  ensure hashCodes are equal when two objects are equal
    //  ensure toString gives what is expected from the expression
    //  test will use factory methods, which call variant class constructors, but both will be tested/interchanged
    
    /* Partitions
     * recipients():
     *     Empty: returns empty set
     *     Single: input lowercase, uppercase, mixed case
     *     Union: left, right: Empty, Single, union, difference, intersection
     *          size of set ==, != size of expression
     *     Difference: left, right: Empty, Single, Union, Difference, Intersection
     *          size of set ==, != size of expression
     *     Intersection: left, right: Empty, Single, Union, Difference, Intersection
     *          size of set ==, != size of expression
     * toString():
     *     Empty: true (curly)
     *     Single:
     *         input: lowercase, uppercase, mixed case
     *     Union: 
     *          left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     *     Difference: 
     *          left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     *     Intersection: 
     *          left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     * equals():
     *     Empty:  true
     *     Single: lowercase vs uppercase vs mixed case
     *          reflexive, symmetric, transitive, case insensitivity
     *     Union: left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     *          reflexive, symmetric, transitive, case insensitivity
     *          ordering, grouping should not affect equality
     *     Difference: left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     *          case insensitivity, ordering 
     *     Intersection: left, right: Empty, Single, union, Difference, Intersection
     *          left and right equal, not equal
     *          reflexive, symmetric, transitive, case insensitivity
     *          ordering, grouping should not affect equality
     *     Mathematical Equality, Structural Equality
     * hashCode():
     *     All e1,e2:EmailList,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    
    private static final EmailList EMPTY = EmailList.empty();
    private static final EmailList LOWER = EmailList.single("a@b");
    private static final EmailList UPPER = EmailList.single("A@B");
    private static final EmailList MIXED = EmailList.single("a@B");
    private static final EmailList SINGLE = EmailList.single("a@A");
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // ========================================================================================================================
    //  RECIPIENTS TESTS
        // empty returns empty set
        // single lowercase
        // single uppercase
        // single mixed case
        // union of empty with empty -> 0 elements
        // union of empty with single -> 1 element, test ordering
        // union of single with single, left/right unequal, test ordering
        // union of empty with union (of single with single - duplicates), test grouping
        // union of union and union -> total of 4 elements, 1 set of duplicates, test grouping
        // union of difference with difference
        // union of intersection with intersection
    
        // difference of empty and empty ->0 elements
        // difference of empty with single -> 0 elements
        // difference of single with single, left/right unequal-> 1 element left.
        // difference of single with single, left and right equal->empty
        // difference of union with single, union not contain single->left set
        // difference of union with single, union contains single -> subset of left
        // difference of union with union, disjoint ->only left set
        // difference of union with union, some overlap ->subset of left
        // difference of union with union, same set ->0 elements
        // difference of difference with difference
        // difference of intersection with intersection
    
        // intersection of empty and empty ->0 elements
        // intersection of empty with single -> 0 elements
        // intersection of single with empty ->0 elements
        // intersection of single with single, left/right unequal-> 0 elements
        // intersection of single with single, left/right equal-> single
        // intersection of union with single, union not contain single->0 elements
        // intersection of union with single, union contains single->single
        // intersection of union with union, disjoint->0 elts
        // intersection of union with union, some overlap->subset
        // intersection of union with union, complete overlap->the union
        // intersection of difference with difference
        // intersection of intersection with intersection
    
    // empty returns empty set
    @Test public void testRecipientEmpty() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.empty();
        assertEquals("expected different set", Collections.emptySet(), exp.recipients(environment));
    }
    
    // single lowercase
    @Test public void testRecipientSingleLower() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.single("abc@def");
        assertEquals("expected different set", Collections.singleton("abc@def"), exp.recipients(environment));
    }
    
    // single uppercase
    @Test public void testRecipientSingleUpper() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.single("F_D-S.A@A.S-D_F");
        assertEquals("expected different set", Collections.singleton("f_d-s.a@a.s-d_f"), exp.recipients(environment));
    }
    
    // single mixed case
    @Test public void testRecipientSingleMixedCase() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.single("bEnBiTdIdDlE@cSaIl.MiT.eDu");
        assertEquals("expected different set", Collections.singleton("benbitdiddle@csail.mit.edu"), exp.recipients(environment));
    }
    
    // union of empty with empty -> 0 elements
    @Test public void testRecipientUnionEmpty() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp = EmailList.union(EmailList.empty(), EmailList.empty());
        assertEquals("expected different set", Collections.emptySet(), exp.recipients(environment));
    }
    
    // union of empty with single -> 1 element, test ordering
    @Test public void testRecipientUnionEmptySingle() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.union(EmailList.empty(), EmailList.single("abc@mit.edu"));
        assertEquals("expected different set", Collections.singleton("abc@mit.edu"), exp1.recipients(environment));
        
        EmailList exp2 = EmailList.union(EmailList.single("abc@mit.edu"), EmailList.empty());
        assertEquals("expected different set", Collections.singleton("abc@mit.edu"), exp2.recipients(environment));
    }
    // union of single with single, left/right unequal, test ordering
    @Test public void testRecipientUnionSingle() {
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("abc@mit");
        expected.add("def@mit");
        
        EmailList exp1 = EmailList.union(EmailList.single("abc@mit"), EmailList.single("def@mit"));
        assertEquals("expected different set", expected, exp1.recipients(environment));
        
        EmailList exp2 = EmailList.union(EmailList.single("def@mit"), EmailList.single("abc@mit"));
        assertEquals("expected different set", expected, exp2.recipients(environment));
    }
    
    // union of empty with union (of single with single - duplicates), test grouping
    @Test public void testRecipientUnionEmptyWithUnion() {
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = Collections.singleton("abc@def");
        
        EmailList exp1 = EmailList.union(EmailList.empty(), 
                EmailList.union(EmailList.single("abc@DEF"), EmailList.single("ABC@def")));
        assertEquals("expected different set", expected, exp1.recipients(environment));
        
        EmailList exp2 = EmailList.union(EmailList.union(EmailList.empty(), EmailList.single("abc@DEF")), 
                        EmailList.single("ABC@def"));
        assertEquals("expected different set", expected, exp2.recipients(environment));
    }
    
    // union of empty with difference
    @Test public void testRecipientUnionEmptyWithDifference() {
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = Collections.singleton("abc@def");
        
        EmailList exp1 = EmailList.union(EmailList.empty(), 
                EmailList.difference(EmailList.single("abc@DEF"), EmailList.single("asfsas@def")));
        assertEquals("expected singleton set", expected, exp1.recipients(environment));
    }
    //union of empty with intersection
    @Test public void testRecipientUnionEmptyIntersection() {
        Set<String> expected = new HashSet<>();
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.union(EmailList.empty(), 
                EmailList.intersection(EmailList.single("abc@DEF"), EmailList.single("asfsas@def")));
        assertEquals("expected empty set", expected, exp1.recipients(environment));
    }
    
    //union of difference with intersection
    @Test public void testRecipientUnionDifferenceIntersection() {
        Set<String> expected = new HashSet<>();
        Map<String, EmailList> environment = new HashMap<>();
        expected.add("abc@d");
        expected.add("abc@def");
        EmailList exp1 = EmailList.union(EmailList.difference(EmailList.single("abc@d"), EmailList.single("123@4")), 
                EmailList.intersection(EmailList.single("abc@DEF"), EmailList.single("abc@DEF")));
        assertEquals("expected correct union difference intersection", expected, exp1.recipients(environment));
    }
    
    // union of union and union -> total of 4 elements, 1 set of duplicates, test grouping
    @Test public void testRecipientUnionTwoUnions() {
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("kevin_w@mit");
        expected.add("janice.c@eecs");
        expected.add("andy-k@csail");
        
        EmailList email1 = EmailList.single("kevin_W@MIT"), 
                email2 = EmailList.single("Janice.c@EECS"), 
                email3 = EmailList.single("Andy-K@csail"), 
                email4 = EmailList.single("andy-k@CSAIL");
        
        EmailList exp1 = EmailList.union(EmailList.union(email1, email2), EmailList.union(email3, email4)),
                exp2 = EmailList.union(EmailList.union(email1, email4), EmailList.union(email2, email3)),
                exp3 = EmailList.union(EmailList.union(EmailList.union(email2, email4), email1), email3);
        
        assertEquals("expected different set", expected, exp1.recipients(environment));
        assertEquals("expected different set", expected, exp2.recipients(environment));
        assertEquals("expected different set", expected, exp3.recipients(environment));
    }
    //difference of empty and empty ->total of 0 elements
    @Test public void testRecipientDifferenceTwoEmpties(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.empty();
        EmailList email2 = EmailList.empty();
        EmailList exp1 = EmailList.difference(email1, email2);
        
        assertEquals("expected different set", expected, exp1.recipients(environment));
    }
    
    //difference of empty and single ->total of 0 elements
    @Test public void testRecipientDifferenceEmptySingle(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.empty();
        EmailList email2 = EmailList.single("Janice.c@EECS");
        EmailList exp1 = EmailList.difference(email1, email2);
        
        assertEquals("expected different set", expected, exp1.recipients(environment));
    }
    
    //difference of single and single, left and right unequal ->total of 1 element
    @Test public void testRecipientDifferenceSingleSingleUnequal(){
        Set<String> expected = new HashSet<>();
        Map<String, EmailList> environment = new HashMap<>();
        expected.add("akuang@gmail.com");
        EmailList email1 = EmailList.single("akuang@gmail.com");
        EmailList email2 = EmailList.single("Janice.c@EECS");
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected only left set", expected, exp1.recipients(environment));
    }
    
    //difference of single and single, left and right unequal ->total of 1 element
    @Test public void testRecipientDifferenceSingleSingleEqual(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.single("akuang@gmail.com");
        EmailList email2 = EmailList.single("akuang@gmail.com");
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected no set", expected, exp1.recipients(environment));
    }
    
    //difference of union with single, union not contain single->left set
    @Test public void testRecipientDifferenceUnionSingleNotContains(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        expected.add("kdubs@cool");
        EmailList email1 = EmailList.union(EmailList.single("akuang@gmail.com"), EmailList.single("KDUBS@COOL"));
        EmailList email2 = EmailList.single("helloword@bob");
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected only union set.", expected, exp1.recipients(environment));
    }
    //difference of union with single, union contains single -> subset of left
    @Test public void testRecipientDifferenceUnionSingleContains(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        EmailList email1 = EmailList.union(EmailList.single("akuang@gmail.com"), EmailList.single("KDUBS@COOL"));
        EmailList email2 = EmailList.single("KDUBS@COOl");
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected only subset of union set.", expected, exp1.recipients(environment));
    }
    //difference of union with union, disjoint ->only left set
    @Test public void testRecipientDifferenceUnionUnionDisjoint(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        expected.add("kdubs@cool");
        EmailList email1 = EmailList.union(EmailList.single("akuang@gmail.com"), EmailList.single("KDUBS@COOL"));
        EmailList email2 = EmailList.union(EmailList.single("hello@bob"), EmailList.single("bobthebuilder@g"));
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected only left union set", expected, exp1.recipients(environment));
    }
    
    //difference of union with union, some overlap ->subset of left
    @Test public void testRecipientDifferenceUnionUnionOverlapping(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        EmailList email1 = EmailList.union(EmailList.single("akuang@gmail.com"), EmailList.single("ja@1fas"));
        EmailList email2 = EmailList.union(EmailList.single("ja@1fas"), EmailList.single("afasffa@s"));
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected only left union set", expected, exp1.recipients(environment));
    }
    
    //difference of intersection, intersection
    @Test public void testRecipientDifferenceIntersectionIntersection(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.intersection(EmailList.union(EmailList.single("akuang@gmail.com"),
                EmailList.single("bkuang@gmail.com")), EmailList.single("akuang@gmail.com"));
        EmailList email2 = EmailList.intersection(EmailList.single("akuang@gmail.com"), EmailList.single("akuang@gmail.com"));
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected empty set", expected, exp1.recipients(environment));
    }
    
    //difference of difference left, difference right
    @Test public void testRecipientDifferenceDifferenceDifference(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("123@gmail");
        EmailList email1 = EmailList.difference(EmailList.single("123@gmail"),EmailList.single("456@gmail"));
        EmailList email2 = EmailList.difference(EmailList.single("789@gmail"),EmailList.single("456@gmail"));
        EmailList exp1 = EmailList.difference(email1, email2);
        assertEquals("expected empty set", expected, exp1.recipients(environment));
    }
    
    //intersection of empty with empty -> 0 elements
    @Test public void testRecipientIntersectionEmptyEmpty(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.empty();
        EmailList email2 = EmailList.empty();
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected 0 elements", expected, exp1.recipients(environment));
    }
    
    //intersection of empty with single -> 0 elements
    @Test public void testRecipientIntersectionEmptySingle(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.empty();
        EmailList email2 = EmailList.single("hello@bob");
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected 0 elements", expected, exp1.recipients(environment));
    }
    
    //intersection of single with empty -> 0 elements
    @Test public void testRecipientIntersectionSingleEmpty(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.single("hello@bobafmasksafjfaksj");
        EmailList email2 = EmailList.empty();
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected 0 elements", expected, exp1.recipients(environment));
    }
    // intersection of single with single, left/right unequal-> 0 elements
    @Test public void testRecipientIntersectionSingleSingleUnequal(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.single("hello@bobafmasksafjfaksj");
        EmailList email2 = EmailList.single("bob@gmail.com");
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected 0 elements", expected, exp1.recipients(environment));
    }   
   // intersection of single with single, left/right equal-> single
    @Test public void testRecipientIntersectionSingleSingleEqual(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("hello@bobafmasksafjfaksj");
        EmailList email1 = EmailList.single("hello@bobafmasksafjfaksj");
        EmailList email2 = EmailList.single("hello@bobafmasksafjfaksj");
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected one elt", expected, exp1.recipients(environment));
    }
   // intersection of union with single, union not contain single->0 elements
    @Test public void testRecipientIntersectionUnionSingleNotContains(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.union(EmailList.single("hello@bobafmasksafjfaksj"),
                EmailList.single("abob@gm"));
        EmailList email2 = EmailList.single("asfkfsaf@afsa");
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected one elt", expected, exp1.recipients(environment));
    }
   // intersection of union with single, union contains single->single
    @Test public void testRecipientIntersectionUnionSingleContains(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("abob@gm");
        EmailList email1 = EmailList.union(EmailList.single("hello@bobafmasksafjfaksj"),
                EmailList.single("abob@gm"));
        EmailList email2 = EmailList.single("aBOB@gm");
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected one elt", expected, exp1.recipients(environment));
    }
   // intersection of union with union, disjoint->0 elts
    @Test public void testRecipientIntersectionUnionUnionDisjoint(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.union(EmailList.single("hello@bobafmasksafjfaksj"),
                EmailList.single("abob@gm"));
        EmailList email2 = EmailList.union(EmailList.single("akals@gasa"),
                EmailList.single("afsjkfsajkfak@afkjkjasf"));
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected nothing", expected, exp1.recipients(environment));
    }
    
    // intersection of union with union, some overlap->subset
    @Test public void testRecipientIntersectionUnionUnionSomeOverlap(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("123@456");
        EmailList email1 = EmailList.union(EmailList.single("123@456"),
                EmailList.single("abob@gm"));
        EmailList email2 = EmailList.union(EmailList.single("123@456"),
                EmailList.single("afsjkfsajkfak@afkjkjasf"));
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected one elt", expected, exp1.recipients(environment));
    }
    // intersection of union with union, complete overlap->the union
    @Test public void testRecipientIntersectionUnionUnionCompleteOverlap(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        expected.add("brassrat@wao.com");
        EmailList email1 = EmailList.union(EmailList.single("AKUANG@GMAIL.COM"),
                EmailList.single("BrassRat@wao.com"));
        EmailList email2 = EmailList.union(EmailList.single("AKUaNG@GMAIL.COM"),
                EmailList.single("BrassRat@Wao.com"));
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected just a union", expected, exp1.recipients(environment));
    }
    
    // intersection of intersection with intersection
    @Test public void testRecipientIntersectionIntersectionIntersection(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        EmailList email1 = EmailList.intersection(EmailList.single("AKUANG@GMAIL.COM"),
                EmailList.single("akuang@gmail.com"));
        EmailList email2 = EmailList.intersection(EmailList.single("AKUaNG@GMAIL.COM"),
                EmailList.single("1@Wao.com"));
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected empty set", expected, exp1.recipients(environment));
    }
    
    // intersection of difference with difference
    @Test public void testRecipientIntersectionDifferenceDifference(){
        Map<String, EmailList> environment = new HashMap<>();
        Set<String> expected = new HashSet<>();
        expected.add("akuang@gmail.com");
        EmailList email1 = EmailList.difference(EmailList.single("AKUANG@GMAIL.COM"),
                EmailList.single("bbob@gmail.com"));
        EmailList email2 = EmailList.difference(EmailList.single("akuang@gmail.com"),
                EmailList.single("1@Wao.com"));
        EmailList exp1 = EmailList.intersection(email1, email2);
        assertEquals("expected one elt", expected, exp1.recipients(environment));
    }
    // ========================================================================================================================
    // TOSTRING TESTS
    // empty returns ()
    // empty parenthesized
    // single input: lowercase, uppercase, mixed case -> all return same string
    // single input parenthesized: lower, upper, mixed
    // union: empty with empty -> ()
    // union empty with single -> 1 element
    // union single with single, no duplicates
    // union single with union (single with single)
    // union of union (empty/single) and union (single/single), no duplicates
    // union of union (single/single) and union (single/single) parenthesized, all duplicates.
    // union of intersection and intersection
    // union of difference and difference
    
    
    // difference: empty with empty -> ()
    // difference empty with single -> ()
    // difference of single with single, left/right unequal-> left single
    // difference of single with single, left and right equal->()
    // difference of union with single, union not contain single->left set
    // difference of union with single, union contains single -> subset of left
    // difference of union with union, disjoint ->only left set
    // difference of union with union, some overlap ->subset of left
    // difference of union with union, same set ->0 elements
    // difference of intersection and intersection
    // difference of difference and difference

    // intersection of empty and empty ->()
    // intersection of empty with single -> ()
    // intersection of single with empty ->()
    // intersection of single with single, left/right unequal-> ()
    // intersection of single with single, left/right equal-> single
    // intersection of union with single, union not contain single->()
    // intersection of union with single, union contains single->single
    // intersection of union with union, disjoint->()
    // intersection of union with union, some overlap->subset
    // intersection of union with union, complete overlap->the union
    // intersection of difference and difference
    // intersection of intersection and intersection


    // empty returns {}
    @Test public void testToStringEmpty() {
        EmailList exp = EmailList.empty();
        assertEquals("expected different string", "", exp.toString());
    }
    
    // empty parenthesized
    @Test public void testToStringEmptyParens() {
        EmailList exp = EmailList.evaluate("()", Collections.emptyMap());
        assertEquals("expected different string", "", exp.toString());
    }
    
    // single input: lowercase, uppercase, mixed case -> all return same string
    @Test public void testToStringSingle() {
        EmailList exp1 = EmailList.single("abc@def"),
                exp2 = EmailList.single("ABC@DEF"),
                exp3 = EmailList.single("aBc@DeF");
        
        assertEquals("expected different string", "abc@def", exp1.toString());
        assertEquals("expected different string", "abc@def", exp2.toString());
        assertEquals("expected different string", "abc@def", exp3.toString());
    }
    
    // single input parenthesized: lower, upper, mixed
    @Test public void testToStringSingleParens() {
        EmailList exp1 = EmailList.evaluate("(abc@def)", Collections.emptyMap()),
                exp2 = EmailList.evaluate("(ABC@DEF)", Collections.emptyMap()),
                exp3 = EmailList.evaluate("(aBc@DeF)", Collections.emptyMap());
        
        assertEquals("expected different string", "abc@def", exp1.toString());
        assertEquals("expected different string", "abc@def", exp2.toString());
        assertEquals("expected different string", "abc@def", exp3.toString());
    }
    
    // union: empty with empty 
    @Test public void testToStringUnionEmpty() {
        EmailList exp = EmailList.union(EmailList.empty(), EmailList.empty());
        assertEquals("expected different string", "() , ()", exp.toString());
    }
    
    // union empty with single -> 1 element
    @Test public void testToStringUnionEmptySingle() {
        EmailList exp1 = EmailList.union(EmailList.empty(), EmailList.single("abc@mit.edu"));
        assertEquals("expected different string", "() , (abc@mit.edu)", exp1.toString());
        
        EmailList exp2 = EmailList.union(EmailList.single("abc@mit.edu"), EmailList.empty());
        assertEquals("expected different string", "(abc@mit.edu) , ()", exp2.toString());
    }
    
    // union single with single, no duplicates
    @Test public void testToStringUnionSingle() {
        EmailList exp1 = EmailList.union(EmailList.single("abc@mit.edu"), EmailList.single("def@mit.edu"));
        assertEquals("expected union single with single", "(abc@mit.edu) , (def@mit.edu)", exp1.toString());
        
        EmailList exp2 = EmailList.union(EmailList.single("def@mit.edu"), EmailList.single("abc@mit.edu"));
        assertEquals("expected union single with single", "(def@mit.edu) , (abc@mit.edu)", exp2.toString());
    }
    
    // union single with union (single with single), one set of duplicates
    @Test public void testToStringUnionSingleWithUnion() {
        EmailList exp1 = EmailList.union(EmailList.single("a@b"),
                EmailList.union(EmailList.single("c@D"), EmailList.single("C@D")));
        EmailList exp2 = EmailList.union(EmailList.single("C@D"),
                EmailList.union(EmailList.single("a@b"), EmailList.single("c@D")));
        assertTrue(exp1.toString().equals("(a@b) , ((c@d) , (c@d))"));
        assertTrue(exp2.toString().equals("(c@d) , ((a@b) , (c@d))"));
    }
    
    // union of union (empty/single) and union (single/single), no duplicates
    @Test public void testToStringNestedUnion() {
        EmailList email1 = EmailList.single("kevin_W@MIT"), 
                email2 = EmailList.single("Janice.c@EECS"), 
                email3 = EmailList.single("Andy-K@csail"), 
                email4 = EmailList.single("andy-k@CSAIL");
        Map<String, EmailList> environment = new HashMap<>();
        String emailK = "kevin_w@mit", emailJ = "janice.c@eecs", emailA = "andy-k@csail";
        
        EmailList exp1 = EmailList.union(EmailList.union(email1, email2), EmailList.union(email3, email4)),
                exp2 = EmailList.union(EmailList.union(email1, email4), EmailList.union(email2, email3)),
                exp3 = EmailList.union(EmailList.union(EmailList.union(email2, email4), email1), email3);
        
        assertTrue(exp1.toString().contains(emailK) && exp1.toString().contains(emailJ) && exp1.toString().contains(emailA));
        assertTrue(exp2.recipients(environment).equals(exp1.recipients(environment)) && exp3.recipients(environment).equals(exp1.recipients(environment)));
    }
    
    // union of union (single/single) and union (single/single) parenthesized, all duplicates.
    @Test public void testToStringNestedUnionParens() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList exp1 = EmailList.evaluate("(kevin_W@MIT, Janice.c@EECS),(Andy-K@csail,andy-k@CSAIL)", Collections.emptyMap()),
                exp2 = EmailList.evaluate("(kevin_W@MIT,(Janice.c@EECS,(Andy-K@csail,(andy-k@CSAIL))))", Collections.emptyMap()),
                exp3 = EmailList.evaluate("((Janice.c@EECS),(Andy-K@csail),(kevin_W@MIT),(andy-k@CSAIL))", Collections.emptyMap());
        
        String emailK = "kevin_w@mit", emailJ = "janice.c@eecs", emailA = "andy-k@csail";
        assertTrue(exp1.toString().contains(emailK) && exp1.toString().contains(emailJ) && exp1.toString().contains(emailA));
        assertTrue(exp2.recipients(environment).equals(exp1.recipients(environment)) && exp3.recipients(environment).equals(exp1.recipients(environment)));
    }
    
    // union intersection with intersection, case insensitivity
    @Test public void testToStringUnionIntersectionWithIntersection() {
        EmailList exp1 = EmailList.union(EmailList.intersection(EmailList.single("c@D"), EmailList.single("C@D")),
                EmailList.intersection(EmailList.single("a@D"), EmailList.single("a@d")));
        EmailList exp2 = EmailList.union(EmailList.intersection(EmailList.single("c@D"), EmailList.single("C@D")),
                EmailList.intersection(EmailList.single("a@D"), EmailList.single("A@D")));
        assertTrue(exp1.toString().equals("((c@d) * (c@d)) , ((a@d) * (a@d))"));
        assertTrue(exp2.toString().equals("((c@d) * (c@d)) , ((a@d) * (a@d))"));
    }
    
    // union difference with difference, case insensitivity
    @Test public void testToStringUnionDifferenceWithDifference() {
        EmailList exp1 = EmailList.union(EmailList.difference(EmailList.single("b@ob"), EmailList.single("C@D")),
                EmailList.difference(EmailList.single("c@od"), EmailList.single("a@d")));
        EmailList exp2 = EmailList.union(EmailList.difference(EmailList.single("B@OB"), EmailList.single("C@D")),
                EmailList.difference(EmailList.single("C@OD"), EmailList.single("A@D")));
        assertTrue(exp1.toString().equals("((b@ob) ! (c@d)) , ((c@od) ! (a@d))"));
        assertTrue(exp2.toString().equals("((b@ob) ! (c@d)) , ((c@od) ! (a@d))"));
    }
    //difference: empty with empty -> ()
    @Test public void testToStringDifferenceEmptyEmpty(){
        EmailList exp = EmailList.difference(EmailList.empty(), EmailList.empty());
        assertEquals("expected different string", "() ! ()", exp.toString());
    }
    
    //difference: empty with single
    @Test public void testToStringDifferenceEmptySingle(){
        EmailList exp = EmailList.difference(EmailList.empty(), EmailList.single("hello@bob"));
        assertEquals("expected difference string", "() ! (hello@bob)", exp.toString());
    }
    // difference of single with single, left/right unequal-> left single
    @Test public void testToStringDifferenceSingleSingleUnequal(){
        EmailList exp = EmailList.difference(EmailList.single("a@bob.com"), EmailList.single("hello@bob"));
        assertEquals("expected first string", "(a@bob.com) ! (hello@bob)", exp.toString());
    }
    // difference of single with single, left and right equal -> ()
    @Test public void testToStringDifferenceSingleSingleEqual(){
        EmailList exp = EmailList.difference(EmailList.single("a@gbob"), EmailList.single("a@gbob"));
        assertEquals("expected difference single single", "(a@gbob) ! (a@gbob)", exp.toString());
    }
    
    // difference of union with single, union not contain single->left set
    @Test public void testToStringDifferenceUnionSingleNotContains(){
        EmailList union1 = EmailList.union(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList exp = EmailList.difference(union1, EmailList.single("a@gbob"));
        System.out.println(exp);
        assertTrue(exp.toString().equals("((hello@friends) , (water@bottle)) ! (a@gbob)"));
    }
    // difference of union with single, union contains single -> subset of left
    @Test public void testToStringDifferenceUnionSingleContains(){
        EmailList union1 = EmailList.union(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList exp = EmailList.difference(union1, EmailList.single("water@bottle"));
        assertEquals("expected string", "((hello@friends) , (water@bottle)) ! (water@bottle)", exp.toString());
    }
    //difference of union with union, disjoint ->only left set
    @Test public void testToStringDifferenceUnionUnionDisjoint(){
        EmailList union1 = EmailList.union(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList union2 = EmailList.union(EmailList.single("12@fas"), EmailList.single("afsasf@fasak"));
        EmailList exp = EmailList.difference(union1, union2);
        assertTrue(exp.toString().equals("((hello@friends) , (water@bottle)) ! ((12@fas) , (afsasf@fasak))"));
    }
    //difference of union with union, overlap ->subset of left
    @Test public void testToStringDifferenceUnionUnionOverlap(){
        EmailList union1 = EmailList.union(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList union2 = EmailList.union(EmailList.single("water@bottle"), EmailList.single("afsasf@fasak"));
        EmailList exp = EmailList.difference(union1, union2);
        assertEquals("expected first string", "((hello@friends) , (water@bottle)) ! ((water@bottle) , (afsasf@fasak))", exp.toString());
    }
    
    //difference of union with union, same set 
    @Test public void testToStringDifferenceUnionUnionSame(){
        EmailList union1 = EmailList.union(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList union2 = EmailList.union(EmailList.single("water@bottle"), EmailList.single("hello@friends"));
        EmailList exp = EmailList.difference(union1, union2);
        assertEquals("expected difference expression", "((hello@friends) , (water@bottle)) ! ((water@bottle) , (hello@friends))", exp.toString());
    }
    
    //difference of difference with difference
    @Test public void testToStringDifferenceDifferenceDifference(){
        EmailList diff1 = EmailList.difference(EmailList.single("hello@friends"), EmailList.single("water@bottle"));
        EmailList diff2 = EmailList.difference(EmailList.single("water@bottle"), EmailList.single("hello@friends"));
        EmailList exp = EmailList.difference(diff1, diff2);
        assertEquals("expected difference exp", "((hello@friends) ! (water@bottle)) ! ((water@bottle) ! (hello@friends))", exp.toString());
    }
    
    //difference of intersection with intersection
    @Test public void testToStringDifferenceIntersectionIntersection(){
        EmailList inter1 = EmailList.intersection(EmailList.single("hello@friends"), EmailList.single("hello@friends"));
        EmailList inter2 = EmailList.intersection(EmailList.single("water@bottle"), EmailList.single("hello@friends"));
        EmailList exp = EmailList.difference(inter1, inter2);
        assertEquals("expected hello friends long", "((hello@friends) * (hello@friends)) ! ((water@bottle) * (hello@friends))", exp.toString());
    }
    
    //intersection: empty with empty 
    @Test public void testToStringIntersectionEmptyEmpty(){
        EmailList exp = EmailList.intersection(EmailList.empty(), EmailList.empty());
        assertEquals("expected intersection of empties", "() * ()", exp.toString());
    }
    //intersection: empty with single 
    @Test public void testToStringIntersectionEmptySingle(){
        EmailList exp = EmailList.intersection(EmailList.empty(), EmailList.single("hellos2@s"));
        assertEquals("expected empty string", "() * (hellos2@s)", exp.toString());
    }
    //intersection: single with empty
    @Test public void testToStringIntersectionSingleEmpty(){
        EmailList exp = EmailList.intersection(EmailList.single("hellos2@s"),EmailList.empty());
        assertEquals("expected intersection with empty", "(hellos2@s) * ()", exp.toString());
    }
    //intersection of single with single, left/right unequal-> ()
    @Test public void testToStringIntersectionSingleSingleUnequal(){
        EmailList exp = EmailList.intersection(EmailList.single("hellos2@s"),EmailList.single("as12@s"));
        assertEquals("expected intersection unequal", "(hellos2@s) * (as12@s)", exp.toString());
    }
    // intersection of single with single, left/right equal
    @Test public void testToStringIntersectionSingleSingleEqual(){
        EmailList exp = EmailList.intersection(EmailList.single("as12@s"),EmailList.single("as12@s"));
        assertEquals("expected an intersection", "(as12@s) * (as12@s)", exp.toString());
    }
    // intersection of union with single, union not contain single
    @Test public void testToStringIntersectionUnionSingleEmpty(){
        EmailList union1 = EmailList.union(EmailList.single("bafsfas@fsaf"), EmailList.single("123FA@a"));
        EmailList exp = EmailList.intersection(union1,EmailList.single("as12@s"));
        assertEquals("expected union of intersection", "((bafsfas@fsaf) , (123fa@a)) * (as12@s)", exp.toString());
    }
    // intersection of union with single, union contains single->single
    @Test public void testToStringIntersectionUnionSingleContains(){
        EmailList union1 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("123FA@a"));
        EmailList exp = EmailList.intersection(union1,EmailList.single("Ilikepie@gmail.com"));
        assertEquals("expected pie", "((ilikepie@gmail.com) , (123fa@a)) * (ilikepie@gmail.com)", exp.toString());
    }
    // intersection of union with union, disjoint
    @Test public void testToStringIntersectionUnionUnionDisjoint(){
        EmailList union1 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("123FA@a"));
        EmailList union2 = EmailList.union(EmailList.single("cakes@gmail.com"), EmailList.single("kfsafa@fas"));
        EmailList exp = EmailList.intersection(union1,union2);
        assertEquals("expected intersection of unions", "((ilikepie@gmail.com) , (123fa@a)) * ((cakes@gmail.com) , (kfsafa@fas))", exp.toString());
    }
    // intersection of union with union, some overlap->subset
    @Test public void testToStringIntersectionUnionUnionSomeOverlap(){
        EmailList union1 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("123FA@a"));
        EmailList union2 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("kfsafa@fas"));
        EmailList exp = EmailList.intersection(union1,union2);
        assertEquals("expected pie", "((ilikepie@gmail.com) , (123fa@a)) * ((ilikepie@gmail.com) , (kfsafa@fas))", exp.toString());
    }
    // intersection of union with union, complete overlap->the union
    @Test public void testToStringIntersectionUnionUnionCompleteOverlap(){
        EmailList union1 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("cakes@fine.com"));
        EmailList union2 = EmailList.union(EmailList.single("Ilikepie@gmail.com"), EmailList.single("cakes@fine.com"));
        EmailList exp = EmailList.intersection(union1,union2);
        assertTrue(exp.toString().equals("((ilikepie@gmail.com) , (cakes@fine.com)) * ((ilikepie@gmail.com) , (cakes@fine.com))"));
    }
    
    //intersection of difference with difference
    @Test public void testToStringIntersectionDifferenceDifference(){
        EmailList diff1 = EmailList.difference(EmailList.single("hello@friends"), EmailList.single("fasfaf@friends"));
        EmailList diff2 = EmailList.difference(EmailList.single("hello@friends"), EmailList.single("belhhh@s"));
        EmailList exp = EmailList.intersection(diff1, diff2);
        assertEquals("expected hello friends and long exp", "((hello@friends) ! (fasfaf@friends)) * ((hello@friends) ! (belhhh@s))", exp.toString());
    }
    
    //intersection of intersection with intersection
    @Test public void testToStringIntersectionIntersectionIntersection(){
        EmailList inter1 = EmailList.intersection(EmailList.single("hbob@ss"), EmailList.single("fasfaf@friends"));
        EmailList inter2 = EmailList.intersection(EmailList.single("hello@friends"), EmailList.single("belhhh@s"));
        EmailList exp = EmailList.intersection(inter1, inter2);
        assertEquals("expected long intersections", "((hbob@ss) * (fasfaf@friends)) * ((hello@friends) * (belhhh@s))", exp.toString());
    }
    // ========================================================================================================================
    // EQUALS TESTS
    
    // (Empty) condition=true
    @Test
    public void testEmptyEquals() {
        assertEquals("expected equals", EMPTY, EmailList.empty());
    }

    // (Empty) condition=true, parenthesized
    @Test
    public void testEmptyEqualsParenthesized() {
        final EmailList expression = EmailList.evaluate("( )", Collections.emptyMap());
        final EmailList expected = EmailList.evaluate("", Collections.emptyMap());
        assertEquals("expected equals", expected, expression);
    }

    // (Single) uppercase vs mixed case, equal
    @Test
    public void testSingleEqualsUpperVersusMixedEqual() {
        assertEquals("expected equals", UPPER, MIXED);
    }

    // (Single) lowercase vs mixed case, equal
    @Test
    public void testSingleEqualsLowerVsMixedEqual() {
        assertEquals("expected equals", LOWER, MIXED);
    }

    // (Single) lowercase vs uppercase, equal
    @Test
    public void testSingleEqualsLowerVsUpperEqual() {
        assertEquals("expected equals", LOWER, UPPER);
    }

    // (Single) both lowercase, not equal
    @Test
    public void testSingleEqualsNotEquals() {
        assertFalse("expected not equals", LOWER.equals(EmailList.single("a@a")));
    }

    // (Single) lowercase vs uppercase, parenthesized
    @Test
    public void testSingleEqualsParenthesized() {
        final EmailList expression = EmailList.evaluate("(a@b)", Collections.emptyMap());
        final EmailList expected = EmailList.evaluate("A@B", Collections.emptyMap());
        assertEquals("expected equals", expected, expression);
    }

    // (Union) left=Empty, right=Union, duplicates=0
    @Test
    public void testUnionEqualsEmptyUnionNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList union = EmailList.union(LOWER, SINGLE);
        final EmailList expression = EmailList.union(EMPTY, union);
        assertEquals("expected equals", union.recipients(environment), expression.recipients(environment));
    }
    
    // (Union) left=Empty, right=Union, duplicates=0
    @Test
    public void testUnionEqualsEmptyUnionNoDuplicatesStructural() {
        final EmailList union = EmailList.union(LOWER, SINGLE);
        final EmailList expression = EmailList.union(LOWER, SINGLE);
        assertEquals("expected equals", union, expression);
    }

    // (Union) left=Single, right=Empty, duplicates=0, same ordering
    @Test
    public void testUnionEqualsSingleEmptyNoDuplicates() {
        final EmailList expected = EmailList.union(EmailList.empty(), EmailList.single("a@A"));
        final EmailList expression = EmailList.union(EMPTY, SINGLE);
        assertEquals("expected equals", expected, expression);
    }

    // (Union) left=Union,  right=Single, duplicates=1
    @Test
    public void testUnionStructuralNotEqualsUnionSingleSingleDuplicate() {
        final EmailList union = EmailList.union(LOWER, SINGLE);
        final EmailList expression = EmailList.union(MIXED, union);
        final EmailList expected = EmailList.union(MIXED, SINGLE);
        assertNotEquals("expected equals", expected, expression);
    }
    
    // (Union) left=Union,  right=Single, duplicates=1
    @Test
    public void testUnionEqualRecipientsUnionSingleSingleDuplicate() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList union = EmailList.union(LOWER, SINGLE);
        final EmailList expression = EmailList.union(MIXED, union);
        final EmailList expected = EmailList.union(MIXED, SINGLE);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Union) left=Union,  right=Single, duplicates=1
    @Test
    public void testUnionEqualRecipientsUnionSingleSingleDuplicateStructural() {
        final EmailList union = EmailList.union(LOWER, SINGLE);
        final EmailList expression = EmailList.union(MIXED, union);
        final EmailList expected = EmailList.union(MIXED, EmailList.union(LOWER, SINGLE));
        assertEquals("expected equals", expected, expression);
    }

    // (Union) left=Union, right=Union, duplicates>1, different grouping
    @Test
    public void testUnionEqualsBothUnionsMultipleDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.union(MIXED, EmailList.union(UPPER, SINGLE));
        final EmailList right = EmailList.union(EmailList.union(SINGLE, MIXED), LOWER);
        final EmailList expression = EmailList.union(left, right);
        final EmailList leftExpected = EmailList.union(EmailList.union(MIXED, UPPER), SINGLE);
        final EmailList rightExpected = EmailList.union(SINGLE, EmailList.union(MIXED, LOWER));
        final EmailList expected = EmailList.union(leftExpected, rightExpected);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    

    // (Union) left=Single, right=Single, different ordering
    @Test
    public void testUnionEqualsSingleSingleDifferentOrdering() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expression = EmailList.union(MIXED, SINGLE);
        final EmailList expected = EmailList.union(SINGLE, MIXED);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    

    // (Union) left=Single, right=Single, parenthesized
    @Test
    public void testUnionEqualsParenthesized() {
        final EmailList expression = EmailList.evaluate("(a@A), (a@b)", Collections.emptyMap());
        final EmailList expected = EmailList.evaluate("a@A,a@b", Collections.emptyMap());
        assertEquals("expected equals", expected, expression);
    }
    
    // (Union) left=Intersection, right=Intersection
    @Test
    public void testUnionEqualsIntersectionIntersection() {
        Map <String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.intersection(EmailList.single("akuang@gm"), EmailList.single("AKUANG@GM"));
        final EmailList right = EmailList.intersection(EmailList.single("bob@gm"), EmailList.single("BOB@GM"));
        final EmailList expression = EmailList.union(left, right);
        final EmailList expected = EmailList.union(EmailList.single("akuang@gm"), EmailList.single("bob@gm"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Union) left=Difference, right=Difference
    @Test
    public void testUnionEqualsDifferenceDifference() {
        Map <String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.difference(EmailList.single("akuang@gm"), EmailList.single("asf1@s1"));
        final EmailList right = EmailList.difference(EmailList.single("bob@gm"), EmailList.single("123f2@fasf"));
        final EmailList expression = EmailList.union(left, right);
        final EmailList expected = EmailList.union(EmailList.single("akuang@gm"), EmailList.single("bob@gm"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    
 // (Empty) condition=true
    @Test
    public void testEmptyHashCode() {
        assertEquals("expected same", EMPTY.hashCode(), EmailList.empty().hashCode());
    }

    // (Empty) condition=true, parenthesized
    @Test
    public void testEmptyHashCodeParenthesized() {
        final EmailList empty1 = EmailList.evaluate("( )", Collections.emptyMap());
        final EmailList empty2 = EmailList.evaluate("", Collections.emptyMap());
        assertEquals("expected same", empty1.hashCode(), empty2.hashCode());
    }

    // (Single) condition=true
    @Test
    public void testSingleHashCode() {
        assertEquals("expected same", LOWER.hashCode(), UPPER.hashCode());
    }

    // (Single) condition=true, parenthesized
    @Test
    public void testSingleHashCodeParenthesized() {
        final EmailList single1 = EmailList.evaluate("(a@b)", Collections.emptyMap());
        final EmailList single2 = EmailList.evaluate("A@B", Collections.emptyMap());
        assertEquals("expected same", single1.hashCode(), single2.hashCode());
    }

    // (Union) condition=true
    @Test
    public void testUnionHashCode() {
        final EmailList union1 = EmailList.union(MIXED, SINGLE);
        final EmailList union2 = EmailList.union(MIXED, SINGLE);
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }

    // (Union) condition=true, parenthesized
    @Test
    public void testUnionHashCodeParenthesized() {
        final EmailList union1 = EmailList.evaluate("(a@A), (a@b)", Collections.emptyMap());
        final EmailList union2 = EmailList.evaluate("a@A,a@b", Collections.emptyMap());
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }

    // (Difference) condition=true
    @Test
    public void testDifferenceHashCode() {
        final EmailList union1 = EmailList.difference(UPPER, LOWER);
        final EmailList union2 = EmailList.difference(LOWER, UPPER);
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }

    // (Difference) condition=true, parenthesized
    @Test
    public void testDifferenceHashCodeParenthesized() {
        final EmailList union1 = EmailList.evaluate("(a@A), (a@b)", Collections.emptyMap());
        final EmailList union2 = EmailList.evaluate("a@A,a@b", Collections.emptyMap());
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }
    
    // (Intersect) condition=true
    @Test
    public void testIntersectHashCode() {
        final EmailList union1 = EmailList.intersection(MIXED, SINGLE);
        final EmailList union2 = EmailList.intersection(MIXED, SINGLE);
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }

    // (Intersect) condition=true, parenthesized
    @Test
    public void testIntersectionHashCodeParenthesized() {
        final EmailList union1 = EmailList.evaluate("(a@A), (a@b)", Collections.emptyMap());
        final EmailList union2 = EmailList.evaluate("a@A,a@b", Collections.emptyMap());
        assertEquals("expected same", union1.hashCode(), union2.hashCode());
    }

    // (Difference) left=Single, right=Single, duplicates=0, same ordering
    @Test
    public void testDifferenceEqualsSingleSingleNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.difference(EmailList.single("b@b"),EmailList.empty());
        final EmailList expression = EmailList.difference(EmailList.union(EmailList.single("a@A"),EmailList.single("b@B")),
                EmailList.single("a@A"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Difference) left=Single, right=Single, duplicates=0, same ordering
    @Test
    public void testDifferenceEqualsSingleSingleNoDuplicatesStructural() {
        final EmailList expected = EmailList.difference(EmailList.single("b@b"),EmailList.empty());
        final EmailList expression = EmailList.difference(EmailList.single("b@b"),EmailList.empty());
        assertEquals("expected equals", expected, expression);
    }
    
    // (Difference) left=Empty, right=Empty, duplicates=0, diff ordering
    @Test
    public void testDifferenceEqualsEmptyEmptyDiffOrder() {
        final EmailList empty1 = EmailList.empty();
        final EmailList empty2 = EmailList.empty();
        final EmailList expected = EmailList.difference(empty1, empty2);
        final EmailList expression = EmailList.difference(empty2, empty1);
        assertEquals("expected equals", expected, expression);
    }
    
    // (Difference) left=Single, right=Single, case insensitivity, different ordering, expected False
    @Test
    public void testDifferenceEqualsSingleSingleDifferentOrdering() {
        final EmailList expression = EmailList.difference(MIXED, SINGLE);
        final EmailList expected = EmailList.difference(SINGLE, MIXED);
        assertTrue("expected not equals", !expected.equals(expression));
    }
    // (Difference) left=Empty, right=Union, duplicates=1
    @Test
    public void testDifferenceEqualsEmptyUnionDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.difference(EMPTY, EMPTY);
        final EmailList expression = EmailList.difference(EMPTY, EmailList.union(LOWER, LOWER));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    // (Difference) left=Union, right=Empty, duplicates=1
    @Test
    public void testDifferenceEqualsUnionEmptyDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList union1 = EmailList.union(LOWER, UPPER);
        final EmailList expected = EmailList.difference(UPPER, EMPTY);
        final EmailList expression = EmailList.difference(union1,EMPTY);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    // (Difference) left=Single, right=Empty, duplicates=0, same ordering, case insensitivity
    @Test
    public void testDifferenceEqualsSingleEmptyNoDuplicates() {
        final EmailList expected = EmailList.difference(LOWER, EMPTY);
        final EmailList expression = EmailList.difference(UPPER, EMPTY);
        assertEquals("expected equals", expected, expression);
    }
    
    // (Difference) left = Union, right = Single, duplicates = 0
    @Test
    public void testDifferenceEqualsUnionSingleNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.difference(EmailList.single("b@b"),EMPTY);
        final EmailList expression = EmailList.difference(EmailList.union(EmailList.single("a@A"),EmailList.single("b@B")),
                EmailList.single("a@A"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Difference) left=Single, right=Single, parenthesized
    @Test
    public void testDifferenceEqualsParenthesized() {
        final EmailList expression = EmailList.evaluate("(a@A)! (a@b)", Collections.emptyMap());
        final EmailList expected = EmailList.evaluate("a@A!a@b", Collections.emptyMap());
        assertEquals("expected equals", expected, expression);
    }
    
    // (Difference) left=Difference, right=Difference
    @Test
    public void testDifferenceEqualsDifferenceDifference() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.difference(EmailList.single("akuang@gm"), EmailList.single("asf1@s1"));
        final EmailList right = EmailList.difference(EmailList.single("bob@gm"), EmailList.single("123f2@fasf"));
        final EmailList expression = EmailList.difference(left, right);
        final EmailList expected = EmailList.difference(EmailList.single("akuang@gm"), EMPTY);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Difference) left=Intersection, right=Intersection
    @Test
    public void testDifferenceEqualsIntersectionIntersection() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.intersection(EmailList.single("akuang@gm"), EmailList.single("akuang@gm"));
        final EmailList right = EmailList.intersection(EmailList.single("1234@f"), EmailList.single("sa@fas"));
        final EmailList expression = EmailList.difference(left, right);
        final EmailList expected = EmailList.difference(EmailList.single("akuang@gm"), EMPTY);
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    // (Intersect) left=Union, right=Single, duplicates=0, same ordering
    @Test
    public void testIntersectionEqualsUnionSingleNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.intersection(EmailList.single("a@a"),EmailList.single("a@a"));
        final EmailList expression = EmailList.intersection(EmailList.union(EmailList.single("a@A"),EmailList.single("b@B")),
                EmailList.single("a@A"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    // (Intersect) left=Single, right=Single, duplicates = 0, same ordering, case insensitivity
    @Test
    public void testIntersectionEqualsSingleSingleNoDuplicates() {
        final EmailList expected = EmailList.intersection(LOWER,LOWER);
        final EmailList expression = EmailList.intersection(UPPER, LOWER);
        assertEquals("expected equals", expected, expression);
    }
    
    // (Intersect) left=Single, right=Single, duplicates = 0, same ordering, case insensitivity
    @Test
    public void testIntersectionEqualsSingleSingleNoDuplicatesStructural() {
        final EmailList expected = EmailList.intersection(LOWER,LOWER);
        final EmailList expression = EmailList.intersection(LOWER, LOWER);
        assertEquals("expected equals", expected, expression);
    }
    
    // (Intersect) left=Empty, right=Empty, duplicates=0, diff ordering
    @Test
    public void testIntersectEqualsEmptyEmptyDiffOrder() {
        final EmailList empty1 = EmailList.empty();
        final EmailList empty2 = EmailList.empty();
        final EmailList expected = EmailList.intersection(empty1, empty2);
        final EmailList expression = EmailList.intersection(empty2, empty1);
        assertEquals("expected equals", expected, expression);
    }
    // (Intersect) left=Single, right=Single, case insensitivity, different ordering, expected True
    @Test
    public void testIntersectEqualsSingleSingleDifferentOrdering() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expression = EmailList.intersection(MIXED, SINGLE);
        final EmailList expected = EmailList.intersection(SINGLE, MIXED);
        assertTrue("expectedequals", expected.recipients(environment).equals(expression.recipients(environment)));
    }
    
    // (Intersect) left=Single, right=Single, case insensitivity, different ordering, expected True
    @Test
    public void testIntersectEqualsSingleSingleStructurals() {
        final EmailList expression = EmailList.intersection(MIXED, SINGLE);
        final EmailList expected = EmailList.intersection(MIXED,SINGLE);
        assertTrue("expected equals", expected.equals(expression));
    }
    
    // (Intersect) left=Empty, right=Union, duplicates=1
    @Test
    public void testIntersectEqualsEmptyUnionDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.intersection(EMPTY, EMPTY);
        final EmailList expression = EmailList.intersection(EMPTY, EmailList.union(LOWER, LOWER));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Intersect) left=Empty, right=Union, duplicates=1
    @Test
    public void testIntersectEqualsUnionEmptyDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList union1 = EmailList.union(LOWER, UPPER);
        final EmailList expected = EmailList.intersection(EMPTY, EMPTY);
        final EmailList expression = EmailList.intersection(EMPTY,union1);
        assertEquals("expected empty", expected.recipients(environment), expression.recipients(environment));
    }
    // (Intersect) left=Single, right=Empty, duplicates=0
    @Test
    public void testIntersectionEqualsSingleEmptyNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.intersection(EMPTY, EMPTY);
        final EmailList expression = EmailList.intersection(UPPER, EMPTY);
        assertEquals("expected empty", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Intersect) left = Union, right = Single, duplicates = 0
    @Test
    public void testIntersectEqualsUnionSingleNoDuplicates() {
        Map<String, EmailList> environment = new HashMap<>();
        final EmailList expected = EmailList.intersection(EmailList.single("a@a"),EmailList.single("a@a"));
        final EmailList expression = EmailList.intersection(EmailList.union(EmailList.single("a@A"),EmailList.single("b@B")),
                EmailList.single("a@A"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Intersect) left=Single, right=Single, parenthesized
    @Test
    public void testIntersectEqualsParenthesized() {
        final EmailList expression = EmailList.evaluate("(a@A)* (a@b)", Collections.emptyMap());
        final EmailList expected = EmailList.evaluate("a@A*a@b", Collections.emptyMap());
        assertEquals("expected equals", expected, expression);
    }
    
    // (Intersect) left=Intersection, right=Intersection, case insensitivity
    @Test
    public void testIntersectEqualsIntersectionIntersection() {
        Map <String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.intersection(EmailList.single("akuang@gm"), EmailList.single("akuang@gm"));
        final EmailList right = EmailList.intersection(EmailList.single("AKUANG@GM"), EmailList.single("aKUang@Gm"));
        final EmailList expression = EmailList.intersection(left, right);
        final EmailList expected = EmailList.intersection(EmailList.single("akuang@gm"), EmailList.single("akuang@gm"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }
    
    // (Intersect) left=Difference, right=Difference, case insensitivity
    @Test
    public void testIntersectEqualsDifferenceDifference() {
        Map <String, EmailList> environment = new HashMap<>();
        final EmailList left = EmailList.difference(EmailList.single("akuang@gm"), EmailList.single("132@42"));
        final EmailList right = EmailList.difference(EmailList.single("AKUANG@GM"), EmailList.single("145@g02"));
        final EmailList expression = EmailList.intersection(left, right);
        final EmailList expected = EmailList.intersection(EmailList.single("akuang@gm"), EmailList.single("akuang@gm"));
        assertEquals("expected equals", expected.recipients(environment), expression.recipients(environment));
    }

    

}
