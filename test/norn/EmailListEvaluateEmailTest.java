/* Copyright (c) 2015-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for EmailList.evaluate()
 */
public class EmailListEvaluateEmailTest {

    
    // Testing strategy: 
    //   Partitions for evaluate():
    //   [Inputs]:
    //   input:
    //       username :
    //          length: 1, >1
    //          number of letters: 0, 1, >1
    //              letter case: uppercase, lowercase, mixed
    //          number of digits: 0, 1, >1
    //          number of underscores: 0, 1, >1
    //          number of dashes: 0, 1, >1
    //          number of periods: 0, 1, >1
    //       domain name:
    //          length: 1, >1
    //          number of letters: 0, 1, >1
    //              letter case: uppercase, lowercase, mixed
    //          number of digits: 0, 1, >1
    //          number of underscores: 0, 1, >1
    //          number of dashes: 0, 1, >1
    //          number of periods: 0, 1, >1
    //       number of operators: 0, 1, >1
    //       operators between emails: , * ! ;
    //       whitespace between operators: 0, 1, >1
    //       list expressions on either side of operators: different, same, same up to case 
    //       parentheses in input: around single list expression, nested
    //   Special cases: empty String, comma operator only, comma operator with list expressions, multiple commas
    //   Illegal Arguments: whitespace in between username and @, whitespace between domain name and @, 
    //             whitespace between domain name and @ & username and @,
    //             illegal character in domain name, illegal character in username, illegal characters in both,
    //             username without domain name,
    //             domain name without username,
    //             no @symbol,
    //             spaces in username, spaces in domain name
    //   [Outputs]:
    //   an evaluated EmailList from the input, IllegalArgumentException
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    //covers empty expression to evaluate
    @Test
    public void testEvaluateEmpty(){
      EmailList input = EmailList.evaluate("", Collections.emptyMap());
      EmailList expected = EmailList.empty();
      assertTrue("expected equals", input.equals(expected));
      assertTrue("expected equal hash", input.hashCode()==(expected.hashCode()));
    }
    
    // ========================================================================================================================
    // EMPTY/SINGLE EVALUATE TESTS
    // ========================================================================================================================

    //covers illegalArgument whitespace between username and @ 
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentSpaceBetweenUserAndAt() {
        EmailList.evaluate("a @b", Collections.emptyMap());
    }
    
    //covers illegalArgument whitespace between @ and domain name
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentSpaceBetweenAtAndDomainName() {
        EmailList.evaluate("a@ b", Collections.emptyMap());
    }
    
    //covers illegalArgument whitespace between @ and username and @ and domain name
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentSpaceBothSidesOfAt() {
        EmailList.evaluate("a.ZA- @ b.-S", Collections.emptyMap());
    }
    
    //covers illegalArgument username without domain name
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentUserNameOnly() {
        EmailList.evaluate("a@", Collections.emptyMap());
    }
    
    //covers illegalArgument domain name without username
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentDomainNameOnly() {
        EmailList.evaluate("@jkfafjksfajklsaf.", Collections.emptyMap());
    }
    
    //covers illegalArgument illegalcharacters multiple @s.
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentIllegalCharactersMultipleAts() {
        EmailList.evaluate("asfkfsakjfa@bob@ASSA.c", Collections.emptyMap());
    }
    
    
    //covers illegalArgument spaces in username
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentSpacesInUserName() {
        EmailList.evaluate("a b@c.com", Collections.emptyMap());
    }
    
    //covers illegalArgument spaces in domain name
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentSpacesInDomainName() {
        EmailList.evaluate("ab@c . com", Collections.emptyMap());
    }
    
    // Character partition
    // illegal character in domain name, illegal character in username, illegal characters in both,
    
    //covers illegalArgument illegal character in username
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentIllegalCharUsername() {
        EmailList.evaluate("CA$H@findme.com", Collections.emptyMap());
    }
    
    //covers illegalArgument illegal character in domain name
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentIllegalDomainName() {
        EmailList.evaluate("Ilikefood@G^8", Collections.emptyMap());
    }
    
    //covers illegalArgument illegal characters both
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateIllegalArgumentIllegalCharactersBoth() {
        EmailList.evaluate("$FRIENDS@#COOL", Collections.emptyMap());
    }
    
    //covers length 1 username, 1 letter in username, lowercase, 0 digits, 0 underscores, 0 dashes, 0 periods
    //covers length >1 domain name, number of letters=1, lowercase, 0 digits, 0 underscores, 0 dashes, 0 periods, 0 operators
    @Test
    public void testEvaluateOneLetterEmails(){
        EmailList email = EmailList.evaluate("a@b", Collections.emptyMap());
        EmailList e1 = EmailList.single("a@b");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    //covers length >1 username, number of letters >1, uppercase, 0 digits, 0 underscores, 0 dashes, 0 periods, 
    //covers length domain name >1, lettercase lower, 0 digits, 0 underscores, 0 dashes, 1 period, 0 operators
    @Test
    public void testEvaluateUppercaseLongerLetterEmails(){
        EmailList email = EmailList.evaluate("AB@cd.com", Collections.emptyMap());
        EmailList e1 = EmailList.single("AB@cd.com");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    @Test 
    public void testEvaluateWhitespaceAround() {
        EmailList email = EmailList.evaluate(" ab@cd.com ", Collections.emptyMap());
        EmailList e1 = EmailList.single("ab@cd.com");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode() == email.hashCode());
    }
    
    //covers >length 1 username, 0 letters, 1 digit, 0 underscores, 1 dash, 0 periods, 
    //covers length 1 domain name, number of letters = 0, 1 digit, 0 underscores, 0 dashes, 0 periods, 0 operators
    @Test
    public void testEvaluateDigitsDashesEmails(){
        EmailList email = EmailList.evaluate("5-@2", Collections.emptyMap());
        EmailList e1 = EmailList.single("5-@2");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    //covers >length 1 username, >1 letters, mixed case, number of digits >1, number of underscores 0, number of dashes 0, number of periods >1
    //covers length >1 domain name, number of letters >1, mixed case, number of digits 0, number underscores 0, number of dashes 0, number of periods 0,
    @Test
    public void testEvaluateDigitsMixedCaseMultipleDigitsMultipleDotsEmails(){
        EmailList email = EmailList.evaluate("aB.cD.e12@fG", Collections.emptyMap());
        EmailList e1 = EmailList.single("ab.cd.e12@fg");
        assertTrue("expected equals case insensitive", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    //covers number of underscores 1, number of dashes >1 
    //covers domain name uppercase, number of digits 0
    @Test
    public void testEvaluateDashesOneUnderscoreDomainNameUppercase(){
        EmailList email = EmailList.evaluate("xy-a_-@QW", Collections.emptyMap());
        EmailList e1 = EmailList.single("xy-a_-@QW");
        assertTrue("expected equals case insensitive", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    //covers one letter
    //covers number of digits >1, number of underscores >1, dashes 1, periods >1,
    @Test
    public void testEvaluateUsernameMultipleUnderscoresDomainNameDigitsDashes(){
        EmailList email = EmailList.evaluate("a@1_2.5.7_5-8", Collections.emptyMap());
        EmailList e1 = EmailList.single("a@1_2.5.7_5-8");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    //covers number of underscores >1, one period, 
    //covers number of dashes >1
    @Test
    public void testEvaluateUsernameMultipleUnderscoresDomainNameMultipleDashes(){
        EmailList email = EmailList.evaluate("a_b_f.2@B-z-8", Collections.emptyMap());
        EmailList e1 = EmailList.single("a_b_f.2@B-z-8");
        assertTrue("expected equals", e1.equals(email));
        assertTrue("expected equal hash", e1.hashCode()==(email.hashCode()));
    }
    
    // ========================================================================================================================
    // UNION EVALUATE TESTS
    // ========================================================================================================================
    
    //covers multiple operators that are , one whitespace
    @Test
    public void testEvaluateMultipleCommas(){
        EmailList input = EmailList.evaluate("a@bc, c@db, 1@23", Collections.emptyMap());
        EmailList email1 = EmailList.single("a@bc");
        EmailList email2 = EmailList.single("c@db");
        EmailList email3 = EmailList.single("1@23");
        EmailList e1 = EmailList.union(email1, email2);
        EmailList e2 = EmailList.union(e1, email3);
        assertTrue("expected equals", e2.equals(input));
        assertTrue("expected equal hash", e2.hashCode()==(input.hashCode()));
    }
    
    //covers multiple operators that are , multiple whitespace
    @Test
    public void testEvaluateMultipleCommasMultipleWhitespace(){
        EmailList input = EmailList.evaluate("a@bc,  c@db,  1@23", Collections.emptyMap());
        EmailList email1 = EmailList.single("a@bc");
        EmailList email2 = EmailList.single("c@db");
        EmailList email3 = EmailList.single("1@23");
        EmailList e1 = EmailList.union(email1, email2);
        EmailList e2 = EmailList.union(e1, email3);
        assertTrue("expected equals", e2.equals(input));
        assertTrue("expected equal hash", e2.hashCode()==(input.hashCode()));
    }
    
    //covers multiple operators that are , no whitespace
    @Test
    public void testEvaluateMultipleCommasNoWhitespace(){
        EmailList input = EmailList.evaluate("a@bc,c@db,1@23", Collections.emptyMap());
        EmailList email1 = EmailList.single("a@bc");
        EmailList email2 = EmailList.single("c@db");
        EmailList email3 = EmailList.single("1@23");
        EmailList e1 = EmailList.union(email1, email2);
        EmailList e2 = EmailList.union(e1, email3);
        assertTrue("expected equals", e2.equals(input));
        assertTrue("expected equal hash", e2.hashCode()==(input.hashCode()));
    }
    
    //covers Special case: operator , with no list expressions on either side.
    @Test
    public void testEvaluateEmptySetComma(){
        EmailList input = EmailList.evaluate(",", Collections.emptyMap());
        EmailList expected = EmailList.union(new Empty(), new Empty());
        assertEquals("expected equal", expected, input);
        assertTrue("expected equal hash", expected.hashCode()==(input.hashCode()));
        
        EmailList expected2 = EmailList.empty();
        assertTrue("expected hash", expected2.hashCode()==input.hashCode());
    }
    
    //covers multiple operators that are , no whitespace
    @Test
    public void testEvaluateSameExpressionsUpToCase(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("bitdiddle@mit.edu,Bitdiddle@mit.edu,alyssap@mit.edu", 
                Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.single("bitdiddle@mit.edu"),
                EmailList.single("alyssap@mit.edu"));
        assertEquals("expected equal up to case insensitive", expected.recipients(environment), input.recipients(environment));
    }

    //covers multiple operators, case insensitive, list expressions on either side of operators same up to case.
    @Test
    public void testEvaluateManyEmailsSameUpToCase(){
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("bitdiddle@mit.edu,Bitdiddle@mit.edu, ALYSSAP@mit.edu, alyssap@mit.edu",
                Collections.emptyMap());
        EmailList expected1 = EmailList.union(EmailList.single("alyssap@mit.edu"), EmailList.single("bitdiddle@mit.edu"));
        EmailList expected = EmailList.union(expected1, EmailList.single("alyssap@mit.edu"));
        assertTrue("expected equals", input.recipients(environment).equals(expected.recipients(environment)));
    }
    
    //covers one operator comma, special case operator then list expression.
    @Test
    public void testEvaluateCommaThenExpression(){
        EmailList input = EmailList.evaluate(", bob@Fud.c1--r-_om", Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.empty(), EmailList.single("bob@Fud.c1--r-_om"));
        assertEquals("expected equals comma then expression", expected, input);
        assertEquals("expected equal hash", expected.hashCode(), input.hashCode());
    }
    
    //covers multiple operators commas
    @Test
    public void testEvaluateMultipleCommasEmpty(){
        EmailList input = EmailList.evaluate(",,,", Collections.emptyMap());
        EmailList expected1 = EmailList.union(EmailList.empty(), EmailList.empty());
        EmailList expected = EmailList.union(EmailList.union(expected1, EmailList.empty()), EmailList.empty());
        assertEquals("expected equals comma then expression", expected, input);
        assertEquals("expected equal hash", expected.hashCode(), input.hashCode());
        
        EmailList expected2 = EmailList.empty();
        assertTrue("expected hash", expected2.hashCode()==input.hashCode());
    }
    
    //covers parentheses around single list expression
    @Test
    public void testEvaluateParenthesesSingle(){
        EmailList input = EmailList.evaluate("(bob.the@builder.com)", Collections.emptyMap());
        EmailList expected = EmailList.single("bob.the@builder.com");
        assertEquals("expected equals comma then expression", expected, input);
        assertEquals("expected equal hash", expected.hashCode(), input.hashCode());
    }
    
    //covers parentheses around single list expression combination
    @Test
    public void testEvaluateParenthesesAroundSingleEmailLists(){
        EmailList input = EmailList.evaluate("(1@2.com),(3@4.com)", Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.single("1@2.com"),EmailList.single("3@4.com"));
        assertEquals("expected equals comma then expression", expected, input);
        assertEquals("expected equal hash", expected.hashCode(), input.hashCode());
    }
    
    //covers parentheses around list expressions nested
    @Test
    public void testEvaluateParenthesesAroundNestedEmailLists(){
        EmailList input = EmailList.evaluate("(1@2.com,(3@4.com), 5@6.com)", Collections.emptyMap());
        EmailList expected1 = EmailList.union(EmailList.single("1@2.com"),EmailList.single("3@4.com"));
        EmailList expected = EmailList.union(expected1,EmailList.single("5@6.com"));
        assertEquals("expected equals comma then expression", expected, input);
        assertEquals("expected equal hash", expected.hashCode(), input.hashCode());
    }
    
    // ========================================================================================================================
    // DIFFERENCE EVALUATE TESTS
    // ========================================================================================================================
    
    // operator with no expressions on either side
    @Test public void testDiffLoneOperator() {
        EmailList input = EmailList.evaluate("!", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.empty(), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
        
        EmailList expected2 = EmailList.empty();
        assertTrue("expected hash", expected2.hashCode()==input.hashCode());
    }
    
    // one operator - different expressions
    @Test public void testDiffOneOperator() {
        EmailList input = EmailList.evaluate("a@bc.com!DEF@MIT.EDU", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("a@bc.com"), EmailList.single("DEF@MIT.EDU"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // one operator - same expression on both sides
    @Test public void testDiffOneOperatorSameList() {
        EmailList input = EmailList.evaluate("abc@def.com!abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("abc@def.com"), EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators with whitespace
    @Test public void testDiffMultipleOpsWhitespace() {
        EmailList input = EmailList.evaluate("abc@def.com !   def@abc.com ! abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.difference(
                EmailList.difference(EmailList.single("abc@def.com"), EmailList.single("def@abc.com")), 
                EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators without whitespace
    @Test public void testDiffMultipleOpsNoWhitespace() {
        EmailList input = EmailList.evaluate("abc@def.com!def@abc.com!abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.difference(
                EmailList.difference(EmailList.single("abc@def.com"), EmailList.single("def@abc.com")), 
                EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // single operator - testing case insensitivity
    @Test public void testDiffCaseInsensitive() {
        EmailList input = EmailList.evaluate("abc@def.com ! AbC@dEf.CoM", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("abc@def.com"), EmailList.single("AbC@dEf.CoM"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }

    // left empty right non-empty
    @Test public void testDiffEmptyAndNonEmpty() {
        EmailList input = EmailList.evaluate("! abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.empty(), EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // left non-empty right empty
    @Test public void testDiffNonEmptyAndEmpty() {
        EmailList input = EmailList.evaluate("abc@def.com!", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("abc@def.com"), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators only
    @Test public void testDiffMultipleLoneOperators() {
        EmailList input = EmailList.evaluate("!!!", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.difference(EmailList.difference(EmailList.empty(), EmailList.empty()), EmailList.empty()), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses around single expression
    @Test public void testDiffParensSingleExp() {
        EmailList input = EmailList.evaluate("((a@bc.com!DEF@MIT.EDU))", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("a@bc.com"), EmailList.single("DEF@MIT.EDU"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses - test grouping
    @Test public void testDiffParensGrouping() {
        EmailList input = EmailList.evaluate("abc@def !( ! abc@def)", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("abc@def"), 
                EmailList.difference(EmailList.empty(), EmailList.single("abc@def")));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses - nested
    @Test public void testDiffParensNested() {
        EmailList input = EmailList.evaluate("((abc@def ! (((def@abc) ! ab@cd)! abc@def)))", Collections.emptyMap());
        EmailList expected = EmailList.difference(EmailList.single("abc@def"), 
                EmailList.difference(
                        EmailList.difference(EmailList.single("def@abc"), EmailList.single("ab@cd")), 
                        EmailList.single("abc@def")));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    
    // ========================================================================================================================
    // INTERSECTION EVALUATE TESTS
    // ========================================================================================================================
    
    // operator with no expressions on either side
    @Test public void testIntersectLoneOperator() {
        EmailList input = EmailList.evaluate("*", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.empty(), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // one operator - different expressions
    @Test public void testIntersectOneOperator() {
        EmailList input = EmailList.evaluate("a@bc.com*DEF@MIT.EDU", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("a@bc.com"), EmailList.single("DEF@MIT.EDU"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // one operator - same expression on both sides
    @Test public void testIntersectOneOperatorSameList() {
        EmailList input = EmailList.evaluate("abc@def.com*abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("abc@def.com"), EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators with whitespace
    @Test public void testIntersectMultipleOpsWhitespace() {
        EmailList input = EmailList.evaluate("abc@def.com  * def@abc.com *  abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.intersection(
                EmailList.intersection(EmailList.single("abc@def.com"), EmailList.single("def@abc.com")), 
                EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators without whitespace
    @Test public void testIntersectMultipleOpsNoWhitespace() {
        EmailList input = EmailList.evaluate("abc@def.com*abc@def.com*abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.intersection(
                EmailList.intersection(EmailList.single("abc@def.com"), EmailList.single("abc@def.com")), 
                EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // single operator - testing case insensitivity
    @Test public void testIntersectCaseInsensitive() {
        EmailList input = EmailList.evaluate("abc@def.com * AbC@dEf.CoM", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("abc@def.com"), EmailList.single("AbC@dEf.CoM"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }

    // left empty right non-empty
    @Test public void testIntersectEmptyAndNonEmpty() {
        EmailList input = EmailList.evaluate("* abc@def.com", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.empty(), EmailList.single("abc@def.com"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // left non-empty right empty
    @Test public void testIntersectNonEmptyAndEmpty() {
        EmailList input = EmailList.evaluate("abc@def.com*", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("abc@def.com"), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // multiple operators only
    @Test public void testIntersectMultipleLoneOperators() {
        EmailList input = EmailList.evaluate("***", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.intersection(EmailList.intersection(EmailList.empty(), EmailList.empty()), EmailList.empty()), EmailList.empty());
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses around single expression
    @Test public void testIntersectParensSingleExp() {
        EmailList input = EmailList.evaluate("((a@bc.com*DEF@MIT.EDU))", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("a@bc.com"), EmailList.single("DEF@MIT.EDU"));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses - test grouping
    @Test public void testIntersectParensGrouping() {
        EmailList input = EmailList.evaluate("abc@def *( * abc@def)", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("abc@def"), 
                EmailList.intersection(EmailList.empty(), EmailList.single("abc@def")));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // parentheses - nested
    @Test public void testIntersectParensNested() {
        EmailList input = EmailList.evaluate("((abc@def * (((def@abc) * ab@cd)* abc@def)))", Collections.emptyMap());
        EmailList expected = EmailList.intersection(EmailList.single("abc@def"), 
                EmailList.intersection(
                        EmailList.intersection(EmailList.single("def@abc"), EmailList.single("ab@cd")), 
                        EmailList.single("abc@def")));
        assertEquals("expected equal mailing lists", expected, input);
        assertEquals("expected equal hashcode", expected.hashCode(), input.hashCode());
    }
    
    // ========================================================================================================================
    // MIXED OPERATIONS EVALUATE TESTS
    // ========================================================================================================================
    
    // Partitions
    // union, difference, intersection
    // case insensitivity, whitespace, empty, nested, grouping, parentheses
    
    // union empty with difference
    @Test public void testUnionEmptyDiff() {
        EmailList input = EmailList.evaluate(",(hello@bye!)", Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.empty(), EmailList.difference(EmailList.single("hello@bye"), EmailList.empty()));
        assertEquals("expected equal mailing lists", expected, input);
    }
    
    // union difference with intersection, whitespace
    @Test public void testUnionDiffIntersect() {
        EmailList input = EmailList.evaluate("((    a@b, c@d) ! (c@d))  , ( (e@f, g@h)* (g@h))", Collections.emptyMap());
        EmailList expected = EmailList.union(
                EmailList.difference(EmailList.union(EmailList.single("a@b"), EmailList.single("c@d")), EmailList.single("c@d")), 
                EmailList.intersection(EmailList.union(EmailList.single("e@f"), EmailList.single("g@h")), EmailList.single("g@h")));
        assertEquals("expected equal mailing lists", expected, input);
    }
    
    // union single with intersection, grouping
    @Test public void testUnionSingleIntersection() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("abc@def, abc@def!ac@df", Collections.emptyMap());
        EmailList expected = EmailList.union( EmailList.single("abc@def"),
                EmailList.intersection(EmailList.single("abc@def"), EmailList.single("ac@df")));
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // difference intersection with empty
    @Test public void testDiffIntersectEmpty() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("((one@num, (two@num, three@num)) * three@num)!", Collections.emptyMap());
        EmailList expected = EmailList.single("three@num");
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // difference union with single, parentheses
    @Test public void testDiffUnionSingle() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("(one@num, two@num, three@num) ! one@num", Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.single("three@num"), EmailList.single("two@num"));
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // intersection union with difference
    @Test public void testIntersectUnionDiff() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("(one@num, two@num, three@num) * (one@num, two@num, five@num) ! (one@num,two@num)", Collections.emptyMap());
        EmailList expected = EmailList.empty();
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // intersection difference with single
    @Test public void testIntersectDiffSingle() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("(abc@def, def@ghi)!(abc@def) * (abc@def, def@ghi)", Collections.emptyMap());
        EmailList expected = EmailList.single("def@ghi");
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // intersection union with empty
    @Test public void testIntersectUnionEmpty() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("(a@b, b@c)*", Collections.emptyMap());
        EmailList expected = EmailList.empty();
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // checking order of operations
    @Test public void testOrderOfOps() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("a@b, c@d, e@f ! (e@f, c@d) * e@f", Collections.emptyMap());
        EmailList expected = EmailList.union(EmailList.single("a@b"), EmailList.single("c@d"));
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
    
    // checking order of operations with grouping by parentheses
    @Test public void testOrderOfOpsVsGrouping() {
        Map<String, EmailList> environment = new HashMap<>();
        EmailList input = EmailList.evaluate("(a@b, ((c@d, e@f) ! e@f), c@d) * e@f", Collections.emptyMap());
        EmailList expected = EmailList.empty();
        assertEquals("expected equal mailing lists", expected.recipients(environment), input.recipients(environment));
    }
}
