grammar Lua;

@header {
    package org.factoriodb.luaparser;
}

lua : value + EOF ;

value 
	: STRING
	| NUMBER
	| BOOL
	| KEY
	| map
	| array
	| function
	| pair
	| 'nil'
	;



pair : KEY '=' value;
	
function : KEY '(' value ')' ;

map
	: '{' pair (',' pair)* ',' ? '}'
	| '{' '}'
	;
	
array
	: '{' value (',' value)* ',' ? '}'
	| '{' '}'
	;

NUMBER : '-'? NUMERIC + '.' NUMERIC + | '-'? NUMERIC +;

fragment ESCAPED_QUOTE : '\\"';
STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

fragment UPPERCASE: [A-Z];
fragment LOWERCASE: [a-z];
NUMERIC: [0-9];
COMMENT :  '--' ~( '\r' | '\n' )* -> skip;
WHITESPACE
   : [ \t\n\r] + -> skip
   ;

BOOL : 'true' | 'false';
KEY: (UPPERCASE | LOWERCASE | NUMERIC | '_' | ':')+;
