grammar TDL;

@header{package taack.jdbc.common.tql.gen;}

tdl: displayKind columnExpressions EOF;

displayKind: TABLE | BARCHART;
columnExpressions: columnExpression (',' columnExpression)*;
columnExpression: (idColumn) ( AS aliasColumn)?;
idColumn:  COLUMN_NAME_FRAGMANT;

AS: 'AS' | 'as';
TABLE: 'TABLE' | 'table';
BARCHART: 'BARCHART' | 'barchart';
COLUMN_NAME_FRAGMANT : LOWER_CHARS ALL_ASCII* ;

aliasColumn: STRING;
STRING:	'"' STRING_CHARACTERS? '"';

fragment STRING_CHARACTERS : StringCharacter+;
fragment StringCharacter : ~['"\\\r\n];

fragment LOWER_CHARS : ('a' .. 'z');
fragment UPPER_CHARS : ('A' .. 'Z');
fragment NUMBER_CHARS: ('0' .. '9');
fragment ALL_ASCII : LOWER_CHARS | UPPER_CHARS | NUMBER_CHARS;

WS : [ \r\n\t] + -> skip;
