package norn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lib6005.parser.ParseTree;
import lib6005.parser.Parser;
import lib6005.parser.UnableToParseException;

/**
 * Immutable helper class for parsing expressions
 * into an abstract syntax tree given by the EmailList interface.
 * Used to encapsulate the grammar, grammar file, and parser objects
 * for use by EmailList's parse method.
 *
 */
public class EmailListParser {
    private static final File GRAMMAR_FILE = new File("src/norn/EmailList.g");

    // the nonterminals of the grammar
    private enum EmailListGrammar {
        ROOT, SEQUENCE, ASSIGNMENT, UNION, DIFFERENCE, INTERSECTION, PRIMITIVE, EMAIL, USERNAME, LISTNAME, WHITESPACE
    };

    private static Parser<EmailListGrammar> parser = makeParser(GRAMMAR_FILE);
    
    /**
     * Compile the grammar into a parser.
     * 
     * @param grammar file containing the grammar
     * @return parser for the grammar
     * @throws RuntimeException if grammar file can't be read or has syntax errors
     */
    private static Parser<EmailListGrammar> makeParser(final File grammar) {
        try {

            return Parser.compile(grammar, EmailListGrammar.ROOT);

            // translate these checked exceptions into unchecked
            // RuntimeExceptions,
            // because these failures indicate internal bugs rather than client
            // errors
        } catch (IOException e) {
            throw new RuntimeException("can't read the grammar file", e);
        } catch (UnableToParseException e) {
            throw new RuntimeException("the grammar has a syntax error", e);
        }
    }

    /**
     * Parse a string into an email list.
     * @param string string to parse
     * @param environment a mapping from previously created listnames to the email lists they are assigned to.
     * Modifies environment by setting a corresponding mapping
     * from listname to EmailList for every listname definition in string.
     * @return EmailList parsed from the string
     * @throws UnableToParseException if the string doesn't match the Expression grammar
     */
    public static EmailList parse(final String string, final Map<String, EmailList> environment) throws UnableToParseException {
        // parse the example into a parse tree
        final ParseTree<EmailListGrammar> parseTree = parser.parse(string);

        // make an AST from the parse tree
        final EmailList emailList = makeAbstractSyntaxTree(parseTree, environment, Optional.empty());

        return emailList;
    }
    
    /**
     * Convert a parse tree into an abstract syntax tree.
     * 
     * @param parseTree constructed according to the grammar in EmailList.g
     * @param environment a mapping from previously created listnames to the email lists they are assigned to.
     * Modifies environment by setting a corresponding mapping
     * from listname to EmailList for every listname definition in parseTree.
     * @return abstract syntax tree corresponding to parseTree
     * @throws IllegalArgumenetException if a listname is referenced prior to being assigned in environment.
     */
    private static EmailList makeAbstractSyntaxTree(final ParseTree<EmailListGrammar> parseTree , final Map<String, EmailList> environment, Optional<String> listNameToBeExtended) {
        switch (parseTree.name()) {
        case ROOT: //root ::= sequence;
            {
                final ParseTree<EmailListGrammar> child = parseTree.children().get(0);
                return makeAbstractSyntaxTree(child, environment, listNameToBeExtended);
            }
        case ASSIGNMENT: // assignment ::= listname '=' union;
            {
                final List<ParseTree<EmailListGrammar>> children = parseTree.children();
                final String listname = children.get(0).text().toLowerCase();
                EmailList emailList = makeAbstractSyntaxTree(children.get(1), environment, Optional.of(listname));
                Set<String> newlyReferredListsExcludeSelf = emailList.getReferencedLists(environment);
                newlyReferredListsExcludeSelf.remove(listname);
                for (String referredList : newlyReferredListsExcludeSelf){
                    if (environment.get(referredList).getReferencedLists(environment).contains(listname)){
                        throw new IllegalArgumentException("Recursive list definition not allowed!");
                    }
                }
                environment.put(listname, emailList);
                return emailList;
            }
        case UNION: // union ::= difference (',' difference)*;

            {
                final List<ParseTree<EmailListGrammar>> children = parseTree.children();
                EmailList emailList = makeAbstractSyntaxTree(children.get(0), environment, listNameToBeExtended);
                for (int i = 1; i < children.size(); ++i) {
                    emailList = EmailList.union(emailList, makeAbstractSyntaxTree(children.get(i), environment, listNameToBeExtended));
                }
                return emailList;
            }

        case DIFFERENCE: // difference ::= intersection ('!' intersection)*;
            {
                final List<ParseTree<EmailListGrammar>> children = parseTree.children();
                EmailList emailList = makeAbstractSyntaxTree(children.get(0), environment, listNameToBeExtended);
                for (int i = 1; i < children.size(); ++i) {
                    emailList = EmailList.difference(emailList, makeAbstractSyntaxTree(children.get(i), environment, listNameToBeExtended));
                }
                return emailList;
            }
        
        case INTERSECTION: // intersection ::= primitive ('*' primitive)*;
            { 
                final List<ParseTree<EmailListGrammar>> children = parseTree.children();
                EmailList emailList = makeAbstractSyntaxTree(children.get(0), environment, listNameToBeExtended);
                for (int i = 1; i < children.size(); ++i) {
                    emailList = EmailList.intersection(emailList, makeAbstractSyntaxTree(children.get(i), environment, listNameToBeExtended));
                }
                return emailList;
            }
        case SEQUENCE: // sequence ::= union (';' union)*;
            {
                final List<ParseTree<EmailListGrammar>> children = parseTree.children();
                EmailList emailList = makeAbstractSyntaxTree(children.get(0), environment, listNameToBeExtended);
                for (int i = 1; i < children.size(); ++i) {
                    emailList = makeAbstractSyntaxTree(children.get(i), environment, listNameToBeExtended);
                }
                return emailList;
            }        
        case PRIMITIVE: // primitive ::= email | listname | '(' sequence ')' | assignment;
            {
                final ParseTree<EmailListGrammar> child = parseTree.children().get(0); 
                EmailList emailList = makeAbstractSyntaxTree(child, environment, listNameToBeExtended);
                return emailList;
            }

        case EMAIL: // email ::= (username '@' listname)?;
            {
                final String emailAddress = parseTree.text();
                EmailList emailList = EmailList.empty();
                if(emailAddress.length() > 0){
                    emailList = EmailList.single(emailAddress);
                }
                return emailList;
            }
        case LISTNAME: // listname ::= [A-Za-z0-9\_\-'.']+;
        {
            final String listname = parseTree.text().toLowerCase();
            if(environment.containsKey(listname)){
                if (listNameToBeExtended.isPresent() && listNameToBeExtended.get().equals(listname)){
                    return environment.get(listname);
                }
            } else {
                environment.put(listname, EmailList.empty()); //added this line in 
            }
            return EmailList.listName(listname);
            
        }
        case USERNAME: // username ::= [A-Za-z0-9\_\-'.''+']+;
        {
            throw new IllegalArgumentException("Invalid expression!");
        }
        default:
            throw new AssertionError("should never get here");
        }

    }
}
