grammar TDL;

@header{package taack.jdbc.common.tql.gen;}

tdl : displayKind columnsExpression;

displayKind: TABLE | BARCHART;

//alias: COLUMN_FRAGMANT;
columnsExpression: columnExpression (',' columnExpression)*;
columnExpression: (idColumn) ( AS aliasColumn)?;
idColumn:  COLUMN_NAME_FRAGMANT;
aliasColumn: STRING;

TABLE: 'TABLE' | 'table';
BARCHART: 'BARCHART' | 'barchart';
AS: 'AS' | 'as';
COLUMN_NAME_FRAGMANT : LOWER_CHARS ALL_ASCII* ;
STRING:	'\'' STRING_CHARACTERS? '\'';

fragment STRING_CHARACTERS : StringCharacter+;
fragment StringCharacter : ~['"\\\r\n];

fragment LOWER_CHARS : ('a' .. 'z');
fragment UPPER_CHARS : ('A' .. 'Z');
fragment NUMBER_CHARS: ('0' .. '9');
fragment ALL_ASCII : LOWER_CHARS | UPPER_CHARS | NUMBER_CHARS;

WS : [ \r\n\t] + -> skip;
