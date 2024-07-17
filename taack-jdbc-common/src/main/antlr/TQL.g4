grammar TQL;

@header{package taack.jdbc.common.tql.gen;}

tql : SELECT (selectStar | selectExpression)
    FROM fromExpression
    (WHERE whereClause)?
    (GROUP_BY groupByExpression)? ';';

selectStar: '*';

selectExpression: ( idTableStar | selectFunctionExpression | columnExpression ) (',' ( selectFunctionExpression | columnExpression | idTableStar ))*;

groupByExpression: ( selectFunctionExpression | columnExpression) (',' ( selectFunctionExpression | columnExpression ))*;

selectFunctionExpression: selFunc LPAREN ( idColumn | additionalExpression | '*') RPAREN;

fromExpression: ( idTableWithAlias | idTable ) (',' ( idTableWithAlias | idTable ) )*;

idTableWithAlias: ( idTable aliasTable | idTable AS aliasTable );
idTable : TABLE_NAME;
selFunc: COUNT | DISTINCT | ELEMENTS | SUM;

//alias: COLUMN_FRAGMANT;
columnExpression: (idColumn | additionalExpression) ( AS aliasColumn)?;
idColumn:  COLUMN_NAME_POINTED | COLUMN_NAME_FRAGMANT;
aliasColumn: COLUMN_NAME_FRAGMANT;
aliasTable: COLUMN_NAME_FRAGMANT;
idTableStar: TABLE_STAR;

IN_ELEMENTS: 'in elements';
SELECT: 'select' | 'SELECT';
FROM: 'from' | 'FROM';
WHERE: 'where' | 'WHERE';
GROUP_BY: 'group by' | 'GROUP BY';
AND: 'AND' | 'and';
OR : 'OR' | 'or';
AS: 'AS' | 'as';
BOOLEAN_LITTERAL: 'true' | 'false';
TABLE_STAR: COLUMN_NAME_FRAGMANT '.*';
COUNT: 'COUNT' | 'count';
SUM: 'SUM' | 'sum';
DISTINCT: 'DISTINCT' | 'distinct';
ELEMENTS: 'ELEMENTS' | 'elements';
TABLE_NAME: UPPER_CHARS ALL_ASCII*;
COLUMN_NAME_POINTED: COLUMN_NAME_FRAGMANT ('.' COLUMN_NAME_FRAGMANT)+;
COLUMN_NAME_FRAGMANT : LOWER_CHARS ALL_ASCII* ;

fragment LOWER_CHARS : ('a' .. 'z');
fragment UPPER_CHARS : ('A' .. 'Z');
fragment NUMBER_CHARS: ('0' .. '9');
fragment ALL_ASCII : LOWER_CHARS | UPPER_CHARS | NUMBER_CHARS;

whereClause: whereExpressionElement (whereExpression)*;
whereExpression:
    junctionOp whereClause
    | LPAREN whereClause RPAREN
    | NEGAT whereClause;

whereExpressionElement
    : (idColumn ((relOp additionalExpression) | 'IS NULL' | 'is null' | 'IS NOT NULL' | 'is not null')) | (aliasTable IN_ELEMENTS LPAREN idColumn RPAREN);

junctionOp: AND | OR;

additionalExpression
   : multiplyingExpression ((PLUS | MINUS) multiplyingExpression)*;

multiplyingExpression
   : powExpression ((TIMES | DIV) powExpression)*;

powExpression
   : signedAtom (POW signedAtom)*;

signedAtom
   : PLUS signedAtom
   | MINUS signedAtom
   | atom
   | idColumn
   ;

atom
   : scientific
   | BOOLEAN_LITTERAL
   | STRING
   | idColumn
   | LPAREN additionalExpression RPAREN
   | NEGAT additionalExpression
   ;

scientific: SCIENTIFIC_NUMBER;

STRING:	'\'' STRING_CHARACTERS? '\'';

fragment STRING_CHARACTERS : StringCharacter+;
fragment StringCharacter : ~['"\\\r\n];

relOp: EQ | GT | GE | LT | LE;
NEGAT: '!';
PLUS : '+';
MINUS: '-';
TIMES: '*';
DIV: '/';
GT : '>';
GE : '>=';
LT : '<';
LE : '<=';
EQ : '=';
POW: '^';
PI : 'pi';
LPAREN: '(';
RPAREN: ')';
SCIENTIFIC_NUMBER: NUMBER (SIGN? NUMBER)?;
fragment NUMBER: ('0' .. '9') + ('.' ('0' .. '9') +)?;
fragment SIGN: ('+' | '-');

WS : [ \r\n\t] + -> skip;
