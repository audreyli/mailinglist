/* Copyright (c) 2015-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

@skip whitespace {
    root ::= sequence;
    assignment ::= listname '=' union;
    sequence ::= union (';' union)*;
    union ::= difference (',' difference)*;
    difference ::= intersection ('!' intersection)*;
    intersection ::= primitive ('*' primitive)*;
    primitive ::= email | listname | '(' sequence ')' | assignment;
}
email ::= (username '@' listname)?;
listname ::= [A-Za-z0-9\_\-'.']+;
username ::= [A-Za-z0-9\_\-'.''+']+;
whitespace ::= [ \t\r\n]+;