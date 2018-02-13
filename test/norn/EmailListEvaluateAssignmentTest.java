package norn;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for assignment expression inputs to EmailList.evaluate()
 */
public class EmailListEvaluateAssignmentTest {
    
    /**
     * Testing strategy for EmailList.evaluate() 
     * Tests will include integrated testing by including list names in 
     * expressions, different from EmailListEvaluateEmailTest as that does
     * not include list names in its evaluations
     * =======================================================================
     * Partition the input as:
     * -----------------------------------------------------------------------
     * input:
     *     assignment expressions (i.e. a = a@mit.edu):
     *         list         (v): empty, single, union, difference,
     *                           intersection, mixed, none
     *         whitespace   (w): none, 1 around "=", >1 around "="
     *         list name:
     *             contains (c): numbers, letters, underscores, dashes, periods, mixed
     *             validity (v): valid, invalid (illegal characters)
     *         parentheses  (p): yes, no
     *         # assigns    (a): 1, >1
     *         
     *     non-assignment expressions (i.e. a, b where a and b are list names):
     *         operators    (o): union, intersect, difference, mixed, none
     *         contains     (t): just list names, mailing list and list names
     *         # operators  (n): 0, 1, >1
     *         list name:
     *             contains (c): numbers, letters, underscores, dashes, periods, mixed
     *             validity (v): valid, invalid (illegal characters)
     * environment (for non-assignment expressions):
     *     relation to set of list names in the expression (s):
     *         same set, superset
     *     
     *[Phase 2]:
     * [Test for undefined lists (listname evaluated without being previously defined)]:
     * [Input]:
     *         list name length: 1, >1
     *         list name: numbers, letters, underscores, dashes, periods, mixed
     *         if letter: uppercase, lowercase
     * [Output]:
     *         an empty EmailList
     */
    
    // Unit Tests
    // Covers v=mixed, w=none, c=mixed, v=invalid, p=yes
    @Test (expected = IllegalArgumentException.class)
    public void testEvaluateAssignmentAssignInvalid() {
        EmailList.evaluate("a{},(b!c)", Collections.emptyMap());
    }
    
    // Covers v=empty, w=none, c=numbers, v=valid, p=yes, a=1
    @Test
    public void testEvaluateAssignmentAssignNumberNameEmptyList() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("1=()", environment);
        
        EmailList expected = EmailList.empty();
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("1", expected);
        
        assertEquals("expected empty list", expected, list);
        assertEquals("expected empty environment", expectedEnvironment, environment);
    }
    
    // Covers v=single, w=1, c=letters, v=valid, p=no, a=1
    @Test
    public void testEvaluateAssignmentAssignLetterNameSingleList() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a = csail@mit", environment);
        
        EmailList expected = EmailList.single("csail@mit");
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a", expected);
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers v=union, w>1, c=underscores, v=valid, p=yes, a=1
    @Test
    public void testEvaluateAssignmentAssignUnderscoreNameUnionList() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("____   = (  csail@mit, rle@mit)", environment);
        
        EmailList expected = EmailList.union(EmailList.single("csail@mit"), EmailList.single("rle@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("____", expected);
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers v=difference, w=0, c=dashes, v=valid, p=no, a=1
    @Test
    public void testEvaluateAssignmentAssignDashesNameDifferenceList() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("-----=csail@mit!rle@mit", environment);
        
        EmailList expected = EmailList.difference(EmailList.single("csail@mit"), EmailList.single("rle@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("-----", expected);
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers v=intersection, w=1, c=periods, v=valid, p=no, a=1
    @Test
    public void testEvaluateAssignmentAssignPeriodNameIntersectionList() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("..... = csail@mit*rle@mit", environment);
        
        EmailList expected = EmailList.intersection(EmailList.single("csail@mit"), EmailList.single("rle@mit"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put(".....", expected);
        
        assertEquals("expected list", expected, list);
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Covers v=mixed, w=1, c=mixed, v=valid, p=yes, a>1
    @Test
    public void testEvaluateAssignmentAssignMixedMultipleAssignments() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList list = EmailList.evaluate("a-b-c = a@b, (c-a@c-d, 1@3) * 1@3", environment);
        EmailList list2 = EmailList.evaluate("1.2.3 = (a@b, c@d) ! c@d", environment);
        
        EmailList expected = EmailList.union(EmailList.single("a@b"), EmailList.intersection(EmailList.union(EmailList.single("c-a@c-d"), EmailList.single("1@3")), EmailList.single("1@3")));
        EmailList expected2 = EmailList.difference(EmailList.union(EmailList.single("a@b"), EmailList.single("c@d")), EmailList.single("c@d"));
        Map<String, EmailList> expectedEnvironment = new HashMap<>();
        expectedEnvironment.put("a-b-c", expected);
        expectedEnvironment.put("1.2.3", expected2);
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
        assertEquals("expected list", expected2.recipients(environment), list2.recipients(environment));
        assertEquals("expected environment", expectedEnvironment, environment);
    }
    
    // Integrated Tests
    // Covers o=none, t=list names, n=0, c=numbers, v=valid, s=same set
    @Test
    public void testEvaluateAssignmentIntegratedNumberName() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("123=a.b@c.d", environment);
        
        EmailList list = EmailList.evaluate("123", environment);
        EmailList expected = EmailList.single("a.b@c.d");
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=none, t=list names, n=0, c=letters, v=valid, s=superset
    @Test
    public void testEvaluateAssignmentIntegratedLetterName() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("abc=a.b@c.d", environment);
        EmailList.evaluate("mit=a-@-.d", environment);
        
        EmailList list = EmailList.evaluate("mit", environment);
        EmailList expected = EmailList.single("a-@-.d");
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=union, t=list names, n>1, c=underscores, v=valid, s=same set
    @Test
    public void testEvaluateAssignmentIntegratedUnderscoresName() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("__=a.b@c.d", environment);
        EmailList.evaluate("___=a-@-.d", environment);
        
        EmailList list = EmailList.evaluate("__, ___", environment);
        EmailList expected = EmailList.union(EmailList.single("a.b@c.d"), EmailList.single("a-@-.d"));
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=difference, t=mixed, n=1, c=dashes, v=valid, s=superset
    @Test
    public void testEvaluateAssignmentIntegratedDashesName() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("--=a.b@c.d", environment);
        EmailList.evaluate("-=a-@-.d", environment);
        EmailList.evaluate("---=a.b@c.d, a-@-.d", environment);
        
        EmailList list = EmailList.evaluate("--- ! a.b@c.d", environment);
        EmailList expected = EmailList.single("a-@-.d");
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=intersect, t=mixed, n=1, c=periods, v=valid, s=same set
    @Test
    public void testEvaluateAssignmentIntegratedPeriodsName() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate(".=a.b@c.d, a-@-.d", environment);
        
        EmailList list = EmailList.evaluate(". * a-@-.d", environment);
        EmailList expected = EmailList.single("a-@-.d");
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=mixed, t=mixed, n>1, c=mixed, v=valid, s=same set
    @Test
    public void testEvaluateAssignmentIntegratedMixedSameSet() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("a-=a.b@c.d", environment);
        EmailList.evaluate("1.2-3=a.b@c.d, 1-@-.2", environment);
        
        EmailList list = EmailList.evaluate("(1.2-3, 23@mit.edu) ! a-", environment);
        EmailList firstUnion = EmailList.union(EmailList.single("a.b@c.d"), EmailList.single("1-@-.2"));
        EmailList expected = EmailList.difference(EmailList.union(firstUnion, EmailList.single("23@mit.edu")), EmailList.single("a.b@c.d"));
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    // Covers o=mixed, t=mixed, n>1, c=mixed, v=valid, s=superset
    @Test
    public void testEvaluateAssignmentIntegratedMixedSuperset() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList.evaluate("--..---=a.b@c.d", environment);
        EmailList.evaluate("asdf.1234=a-@-.d", environment);
        EmailList.evaluate("1.2-d.e=a.b@c.d, a-@-.d", environment);
        
        EmailList list = EmailList.evaluate("(1.2-d.e ! a-@-.d) * (--..---, )", environment);
        EmailList expected = EmailList.single("a.b@c.d");
        
        assertEquals("expected list", expected.recipients(environment), list.recipients(environment));
    }
    
    
    //covers undefined listname, listname length 1, letter, lowercase
    @Test
    public void testEvaluateAssignmentUndefinedListnameOne(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList actual = EmailList.evaluate("a", environment);
        EmailList expectedExpression = EmailList.listName("a");
        EmailList expectedValue = EmailList.empty();
        assertEquals("expected empty expressions equal", expectedExpression, actual);
        assertEquals("expected in environment", true, environment.keySet().contains("a"));
        assertEquals("expected empty in environment", true, environment.get("a").equals(expectedValue));
    }
    
    //covers undefined listname, listname length >1, letters and numbers, lowercase
    @Test
    public void testEvaluateAssignmentUndefinedListnameGreaterOne(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList actual = EmailList.evaluate("a123c", environment);
        EmailList expectedExpression = EmailList.listName("a123c");
        EmailList expectedValue = EmailList.empty();
        assertEquals("expected empty expressions equal", expectedExpression, actual);
        assertEquals("expected in environment", true, environment.keySet().contains("a123c"));
        assertEquals("expected empty in environment", true, environment.get("a123c").equals(expectedValue));
    }
    
    
    //covers list name length>1, listname numbers, letters, underscores, dashes, periods, 
    //covers uppercase
    
    @Test
    public void testEvaluateAssignmentUndefinedListnameGreaterOneUnderscoresDashesPeriods(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList actual = EmailList.evaluate("B1.23_-", environment);
        EmailList expectedExpression = EmailList.listName("B1.23_-");
        EmailList expectedValue = EmailList.empty();
        assertEquals("expected empty expressions equal", expectedExpression, actual);
        assertEquals("expected in environment", true, environment.keySet().contains("b1.23_-"));
        assertEquals("expected empty in environment", true, environment.get("b1.23_-").equals(expectedValue));
    }
}
