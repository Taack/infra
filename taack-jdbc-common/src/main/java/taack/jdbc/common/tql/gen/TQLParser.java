// Generated from TQL.g4 by ANTLR 4.13.1
package taack.jdbc.common.tql.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class TQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, IN_ELEMENTS=7, SELECT=8, 
		FROM=9, WHERE=10, GROUP_BY=11, AND=12, OR=13, AS=14, BOOLEAN_LITTERAL=15, 
		TABLE_STAR=16, COUNT=17, SUM=18, DISTINCT=19, ELEMENTS=20, TABLE_NAME=21, 
		COLUMN_NAME_POINTED=22, COLUMN_NAME_FRAGMANT=23, STRING=24, NEGAT=25, 
		PLUS=26, MINUS=27, TIMES=28, DIV=29, GT=30, GE=31, LT=32, LE=33, EQ=34, 
		POW=35, PI=36, LPAREN=37, RPAREN=38, SCIENTIFIC_NUMBER=39, WS=40;
	public static final int
		RULE_tql = 0, RULE_selectStar = 1, RULE_selectExpression = 2, RULE_groupByExpression = 3, 
		RULE_selectFunctionExpression = 4, RULE_fromExpression = 5, RULE_idTableWithAlias = 6, 
		RULE_idTable = 7, RULE_selFunc = 8, RULE_columnExpression = 9, RULE_idColumn = 10, 
		RULE_aliasColumn = 11, RULE_aliasTable = 12, RULE_idTableStar = 13, RULE_whereClause = 14, 
		RULE_whereExpression = 15, RULE_whereExpressionElement = 16, RULE_junctionOp = 17, 
		RULE_additionalExpression = 18, RULE_multiplyingExpression = 19, RULE_powExpression = 20, 
		RULE_signedAtom = 21, RULE_atom = 22, RULE_scientific = 23, RULE_relOp = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"tql", "selectStar", "selectExpression", "groupByExpression", "selectFunctionExpression", 
			"fromExpression", "idTableWithAlias", "idTable", "selFunc", "columnExpression", 
			"idColumn", "aliasColumn", "aliasTable", "idTableStar", "whereClause", 
			"whereExpression", "whereExpressionElement", "junctionOp", "additionalExpression", 
			"multiplyingExpression", "powExpression", "signedAtom", "atom", "scientific", 
			"relOp"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "','", "'IS NULL'", "'is null'", "'IS NOT NULL'", "'is not null'", 
			"'in elements'", null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "'!'", "'+'", "'-'", 
			"'*'", "'/'", "'>'", "'>='", "'<'", "'<='", "'='", "'^'", "'pi'", "'('", 
			"')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "IN_ELEMENTS", "SELECT", "FROM", 
			"WHERE", "GROUP_BY", "AND", "OR", "AS", "BOOLEAN_LITTERAL", "TABLE_STAR", 
			"COUNT", "SUM", "DISTINCT", "ELEMENTS", "TABLE_NAME", "COLUMN_NAME_POINTED", 
			"COLUMN_NAME_FRAGMANT", "STRING", "NEGAT", "PLUS", "MINUS", "TIMES", 
			"DIV", "GT", "GE", "LT", "LE", "EQ", "POW", "PI", "LPAREN", "RPAREN", 
			"SCIENTIFIC_NUMBER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TQLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TqlContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(TQLParser.SELECT, 0); }
		public TerminalNode FROM() { return getToken(TQLParser.FROM, 0); }
		public FromExpressionContext fromExpression() {
			return getRuleContext(FromExpressionContext.class,0);
		}
		public SelectStarContext selectStar() {
			return getRuleContext(SelectStarContext.class,0);
		}
		public SelectExpressionContext selectExpression() {
			return getRuleContext(SelectExpressionContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(TQLParser.WHERE, 0); }
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public TerminalNode GROUP_BY() { return getToken(TQLParser.GROUP_BY, 0); }
		public GroupByExpressionContext groupByExpression() {
			return getRuleContext(GroupByExpressionContext.class,0);
		}
		public TqlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tql; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterTql(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitTql(this);
		}
	}

	public final TqlContext tql() throws RecognitionException {
		TqlContext _localctx = new TqlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tql);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			match(SELECT);
			setState(53);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TIMES:
				{
				setState(51);
				selectStar();
				}
				break;
			case BOOLEAN_LITTERAL:
			case TABLE_STAR:
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
			case COLUMN_NAME_POINTED:
			case COLUMN_NAME_FRAGMANT:
			case STRING:
			case NEGAT:
			case PLUS:
			case MINUS:
			case LPAREN:
			case SCIENTIFIC_NUMBER:
				{
				setState(52);
				selectExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(55);
			match(FROM);
			setState(56);
			fromExpression();
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(57);
				match(WHERE);
				setState(58);
				whereClause();
				}
			}

			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP_BY) {
				{
				setState(61);
				match(GROUP_BY);
				setState(62);
				groupByExpression();
				}
			}

			setState(65);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectStarContext extends ParserRuleContext {
		public TerminalNode TIMES() { return getToken(TQLParser.TIMES, 0); }
		public SelectStarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterSelectStar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitSelectStar(this);
		}
	}

	public final SelectStarContext selectStar() throws RecognitionException {
		SelectStarContext _localctx = new SelectStarContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_selectStar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(TIMES);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectExpressionContext extends ParserRuleContext {
		public List<IdTableStarContext> idTableStar() {
			return getRuleContexts(IdTableStarContext.class);
		}
		public IdTableStarContext idTableStar(int i) {
			return getRuleContext(IdTableStarContext.class,i);
		}
		public List<SelectFunctionExpressionContext> selectFunctionExpression() {
			return getRuleContexts(SelectFunctionExpressionContext.class);
		}
		public SelectFunctionExpressionContext selectFunctionExpression(int i) {
			return getRuleContext(SelectFunctionExpressionContext.class,i);
		}
		public List<ColumnExpressionContext> columnExpression() {
			return getRuleContexts(ColumnExpressionContext.class);
		}
		public ColumnExpressionContext columnExpression(int i) {
			return getRuleContext(ColumnExpressionContext.class,i);
		}
		public SelectExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterSelectExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitSelectExpression(this);
		}
	}

	public final SelectExpressionContext selectExpression() throws RecognitionException {
		SelectExpressionContext _localctx = new SelectExpressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_selectExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE_STAR:
				{
				setState(69);
				idTableStar();
				}
				break;
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
				{
				setState(70);
				selectFunctionExpression();
				}
				break;
			case BOOLEAN_LITTERAL:
			case COLUMN_NAME_POINTED:
			case COLUMN_NAME_FRAGMANT:
			case STRING:
			case NEGAT:
			case PLUS:
			case MINUS:
			case LPAREN:
			case SCIENTIFIC_NUMBER:
				{
				setState(71);
				columnExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(74);
				match(T__1);
				setState(78);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COUNT:
				case SUM:
				case DISTINCT:
				case ELEMENTS:
					{
					setState(75);
					selectFunctionExpression();
					}
					break;
				case BOOLEAN_LITTERAL:
				case COLUMN_NAME_POINTED:
				case COLUMN_NAME_FRAGMANT:
				case STRING:
				case NEGAT:
				case PLUS:
				case MINUS:
				case LPAREN:
				case SCIENTIFIC_NUMBER:
					{
					setState(76);
					columnExpression();
					}
					break;
				case TABLE_STAR:
					{
					setState(77);
					idTableStar();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GroupByExpressionContext extends ParserRuleContext {
		public List<SelectFunctionExpressionContext> selectFunctionExpression() {
			return getRuleContexts(SelectFunctionExpressionContext.class);
		}
		public SelectFunctionExpressionContext selectFunctionExpression(int i) {
			return getRuleContext(SelectFunctionExpressionContext.class,i);
		}
		public List<ColumnExpressionContext> columnExpression() {
			return getRuleContexts(ColumnExpressionContext.class);
		}
		public ColumnExpressionContext columnExpression(int i) {
			return getRuleContext(ColumnExpressionContext.class,i);
		}
		public GroupByExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterGroupByExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitGroupByExpression(this);
		}
	}

	public final GroupByExpressionContext groupByExpression() throws RecognitionException {
		GroupByExpressionContext _localctx = new GroupByExpressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_groupByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
				{
				setState(85);
				selectFunctionExpression();
				}
				break;
			case BOOLEAN_LITTERAL:
			case COLUMN_NAME_POINTED:
			case COLUMN_NAME_FRAGMANT:
			case STRING:
			case NEGAT:
			case PLUS:
			case MINUS:
			case LPAREN:
			case SCIENTIFIC_NUMBER:
				{
				setState(86);
				columnExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(89);
				match(T__1);
				setState(92);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COUNT:
				case SUM:
				case DISTINCT:
				case ELEMENTS:
					{
					setState(90);
					selectFunctionExpression();
					}
					break;
				case BOOLEAN_LITTERAL:
				case COLUMN_NAME_POINTED:
				case COLUMN_NAME_FRAGMANT:
				case STRING:
				case NEGAT:
				case PLUS:
				case MINUS:
				case LPAREN:
				case SCIENTIFIC_NUMBER:
					{
					setState(91);
					columnExpression();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelectFunctionExpressionContext extends ParserRuleContext {
		public SelFuncContext selFunc() {
			return getRuleContext(SelFuncContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TQLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TQLParser.RPAREN, 0); }
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public AdditionalExpressionContext additionalExpression() {
			return getRuleContext(AdditionalExpressionContext.class,0);
		}
		public TerminalNode TIMES() { return getToken(TQLParser.TIMES, 0); }
		public SelectFunctionExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectFunctionExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterSelectFunctionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitSelectFunctionExpression(this);
		}
	}

	public final SelectFunctionExpressionContext selectFunctionExpression() throws RecognitionException {
		SelectFunctionExpressionContext _localctx = new SelectFunctionExpressionContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_selectFunctionExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			selFunc();
			setState(100);
			match(LPAREN);
			setState(104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(101);
				idColumn();
				}
				break;
			case 2:
				{
				setState(102);
				additionalExpression();
				}
				break;
			case 3:
				{
				setState(103);
				match(TIMES);
				}
				break;
			}
			setState(106);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FromExpressionContext extends ParserRuleContext {
		public List<IdTableWithAliasContext> idTableWithAlias() {
			return getRuleContexts(IdTableWithAliasContext.class);
		}
		public IdTableWithAliasContext idTableWithAlias(int i) {
			return getRuleContext(IdTableWithAliasContext.class,i);
		}
		public List<IdTableContext> idTable() {
			return getRuleContexts(IdTableContext.class);
		}
		public IdTableContext idTable(int i) {
			return getRuleContext(IdTableContext.class,i);
		}
		public FromExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterFromExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitFromExpression(this);
		}
	}

	public final FromExpressionContext fromExpression() throws RecognitionException {
		FromExpressionContext _localctx = new FromExpressionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fromExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(108);
				idTableWithAlias();
				}
				break;
			case 2:
				{
				setState(109);
				idTable();
				}
				break;
			}
			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(112);
				match(T__1);
				setState(115);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(113);
					idTableWithAlias();
					}
					break;
				case 2:
					{
					setState(114);
					idTable();
					}
					break;
				}
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdTableWithAliasContext extends ParserRuleContext {
		public IdTableContext idTable() {
			return getRuleContext(IdTableContext.class,0);
		}
		public AliasTableContext aliasTable() {
			return getRuleContext(AliasTableContext.class,0);
		}
		public TerminalNode AS() { return getToken(TQLParser.AS, 0); }
		public IdTableWithAliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idTableWithAlias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterIdTableWithAlias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitIdTableWithAlias(this);
		}
	}

	public final IdTableWithAliasContext idTableWithAlias() throws RecognitionException {
		IdTableWithAliasContext _localctx = new IdTableWithAliasContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_idTableWithAlias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(122);
				idTable();
				setState(123);
				aliasTable();
				}
				break;
			case 2:
				{
				setState(125);
				idTable();
				setState(126);
				match(AS);
				setState(127);
				aliasTable();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdTableContext extends ParserRuleContext {
		public TerminalNode TABLE_NAME() { return getToken(TQLParser.TABLE_NAME, 0); }
		public IdTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterIdTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitIdTable(this);
		}
	}

	public final IdTableContext idTable() throws RecognitionException {
		IdTableContext _localctx = new IdTableContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_idTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(TABLE_NAME);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SelFuncContext extends ParserRuleContext {
		public TerminalNode COUNT() { return getToken(TQLParser.COUNT, 0); }
		public TerminalNode DISTINCT() { return getToken(TQLParser.DISTINCT, 0); }
		public TerminalNode ELEMENTS() { return getToken(TQLParser.ELEMENTS, 0); }
		public TerminalNode SUM() { return getToken(TQLParser.SUM, 0); }
		public SelFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterSelFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitSelFunc(this);
		}
	}

	public final SelFuncContext selFunc() throws RecognitionException {
		SelFuncContext _localctx = new SelFuncContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_selFunc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1966080L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColumnExpressionContext extends ParserRuleContext {
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public AdditionalExpressionContext additionalExpression() {
			return getRuleContext(AdditionalExpressionContext.class,0);
		}
		public TerminalNode AS() { return getToken(TQLParser.AS, 0); }
		public AliasColumnContext aliasColumn() {
			return getRuleContext(AliasColumnContext.class,0);
		}
		public ColumnExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterColumnExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitColumnExpression(this);
		}
	}

	public final ColumnExpressionContext columnExpression() throws RecognitionException {
		ColumnExpressionContext _localctx = new ColumnExpressionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_columnExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(135);
				idColumn();
				}
				break;
			case 2:
				{
				setState(136);
				additionalExpression();
				}
				break;
			}
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(139);
				match(AS);
				setState(140);
				aliasColumn();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdColumnContext extends ParserRuleContext {
		public TerminalNode COLUMN_NAME_POINTED() { return getToken(TQLParser.COLUMN_NAME_POINTED, 0); }
		public TerminalNode COLUMN_NAME_FRAGMANT() { return getToken(TQLParser.COLUMN_NAME_FRAGMANT, 0); }
		public IdColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idColumn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterIdColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitIdColumn(this);
		}
	}

	public final IdColumnContext idColumn() throws RecognitionException {
		IdColumnContext _localctx = new IdColumnContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_idColumn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			_la = _input.LA(1);
			if ( !(_la==COLUMN_NAME_POINTED || _la==COLUMN_NAME_FRAGMANT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AliasColumnContext extends ParserRuleContext {
		public TerminalNode COLUMN_NAME_FRAGMANT() { return getToken(TQLParser.COLUMN_NAME_FRAGMANT, 0); }
		public AliasColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasColumn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterAliasColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitAliasColumn(this);
		}
	}

	public final AliasColumnContext aliasColumn() throws RecognitionException {
		AliasColumnContext _localctx = new AliasColumnContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_aliasColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(COLUMN_NAME_FRAGMANT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AliasTableContext extends ParserRuleContext {
		public TerminalNode COLUMN_NAME_FRAGMANT() { return getToken(TQLParser.COLUMN_NAME_FRAGMANT, 0); }
		public AliasTableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasTable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterAliasTable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitAliasTable(this);
		}
	}

	public final AliasTableContext aliasTable() throws RecognitionException {
		AliasTableContext _localctx = new AliasTableContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_aliasTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(COLUMN_NAME_FRAGMANT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IdTableStarContext extends ParserRuleContext {
		public TerminalNode TABLE_STAR() { return getToken(TQLParser.TABLE_STAR, 0); }
		public IdTableStarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idTableStar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterIdTableStar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitIdTableStar(this);
		}
	}

	public final IdTableStarContext idTableStar() throws RecognitionException {
		IdTableStarContext _localctx = new IdTableStarContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_idTableStar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(TABLE_STAR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhereClauseContext extends ParserRuleContext {
		public WhereExpressionElementContext whereExpressionElement() {
			return getRuleContext(WhereExpressionElementContext.class,0);
		}
		public List<WhereExpressionContext> whereExpression() {
			return getRuleContexts(WhereExpressionContext.class);
		}
		public WhereExpressionContext whereExpression(int i) {
			return getRuleContext(WhereExpressionContext.class,i);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitWhereClause(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_whereClause);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			whereExpressionElement();
			setState(155);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(152);
					whereExpression();
					}
					} 
				}
				setState(157);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhereExpressionContext extends ParserRuleContext {
		public JunctionOpContext junctionOp() {
			return getRuleContext(JunctionOpContext.class,0);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TQLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TQLParser.RPAREN, 0); }
		public TerminalNode NEGAT() { return getToken(TQLParser.NEGAT, 0); }
		public WhereExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterWhereExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitWhereExpression(this);
		}
	}

	public final WhereExpressionContext whereExpression() throws RecognitionException {
		WhereExpressionContext _localctx = new WhereExpressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_whereExpression);
		try {
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
			case OR:
				enterOuterAlt(_localctx, 1);
				{
				setState(158);
				junctionOp();
				setState(159);
				whereClause();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(161);
				match(LPAREN);
				setState(162);
				whereClause();
				setState(163);
				match(RPAREN);
				}
				break;
			case NEGAT:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
				match(NEGAT);
				setState(166);
				whereClause();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhereExpressionElementContext extends ParserRuleContext {
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public RelOpContext relOp() {
			return getRuleContext(RelOpContext.class,0);
		}
		public AdditionalExpressionContext additionalExpression() {
			return getRuleContext(AdditionalExpressionContext.class,0);
		}
		public AliasTableContext aliasTable() {
			return getRuleContext(AliasTableContext.class,0);
		}
		public TerminalNode IN_ELEMENTS() { return getToken(TQLParser.IN_ELEMENTS, 0); }
		public TerminalNode LPAREN() { return getToken(TQLParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(TQLParser.RPAREN, 0); }
		public WhereExpressionElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereExpressionElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterWhereExpressionElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitWhereExpressionElement(this);
		}
	}

	public final WhereExpressionElementContext whereExpressionElement() throws RecognitionException {
		WhereExpressionElementContext _localctx = new WhereExpressionElementContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_whereExpressionElement);
		try {
			setState(185);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(169);
				idColumn();
				setState(177);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case GT:
				case GE:
				case LT:
				case LE:
				case EQ:
					{
					{
					setState(170);
					relOp();
					setState(171);
					additionalExpression();
					}
					}
					break;
				case T__2:
					{
					setState(173);
					match(T__2);
					}
					break;
				case T__3:
					{
					setState(174);
					match(T__3);
					}
					break;
				case T__4:
					{
					setState(175);
					match(T__4);
					}
					break;
				case T__5:
					{
					setState(176);
					match(T__5);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(179);
				aliasTable();
				setState(180);
				match(IN_ELEMENTS);
				setState(181);
				match(LPAREN);
				setState(182);
				idColumn();
				setState(183);
				match(RPAREN);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class JunctionOpContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(TQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(TQLParser.OR, 0); }
		public JunctionOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_junctionOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterJunctionOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitJunctionOp(this);
		}
	}

	public final JunctionOpContext junctionOp() throws RecognitionException {
		JunctionOpContext _localctx = new JunctionOpContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_junctionOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AdditionalExpressionContext extends ParserRuleContext {
		public List<MultiplyingExpressionContext> multiplyingExpression() {
			return getRuleContexts(MultiplyingExpressionContext.class);
		}
		public MultiplyingExpressionContext multiplyingExpression(int i) {
			return getRuleContext(MultiplyingExpressionContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(TQLParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(TQLParser.PLUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(TQLParser.MINUS); }
		public TerminalNode MINUS(int i) {
			return getToken(TQLParser.MINUS, i);
		}
		public AdditionalExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additionalExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterAdditionalExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitAdditionalExpression(this);
		}
	}

	public final AdditionalExpressionContext additionalExpression() throws RecognitionException {
		AdditionalExpressionContext _localctx = new AdditionalExpressionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_additionalExpression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			multiplyingExpression();
			setState(194);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(190);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(191);
					multiplyingExpression();
					}
					} 
				}
				setState(196);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MultiplyingExpressionContext extends ParserRuleContext {
		public List<PowExpressionContext> powExpression() {
			return getRuleContexts(PowExpressionContext.class);
		}
		public PowExpressionContext powExpression(int i) {
			return getRuleContext(PowExpressionContext.class,i);
		}
		public List<TerminalNode> TIMES() { return getTokens(TQLParser.TIMES); }
		public TerminalNode TIMES(int i) {
			return getToken(TQLParser.TIMES, i);
		}
		public List<TerminalNode> DIV() { return getTokens(TQLParser.DIV); }
		public TerminalNode DIV(int i) {
			return getToken(TQLParser.DIV, i);
		}
		public MultiplyingExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplyingExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterMultiplyingExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitMultiplyingExpression(this);
		}
	}

	public final MultiplyingExpressionContext multiplyingExpression() throws RecognitionException {
		MultiplyingExpressionContext _localctx = new MultiplyingExpressionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_multiplyingExpression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			powExpression();
			setState(202);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(198);
					_la = _input.LA(1);
					if ( !(_la==TIMES || _la==DIV) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(199);
					powExpression();
					}
					} 
				}
				setState(204);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PowExpressionContext extends ParserRuleContext {
		public List<SignedAtomContext> signedAtom() {
			return getRuleContexts(SignedAtomContext.class);
		}
		public SignedAtomContext signedAtom(int i) {
			return getRuleContext(SignedAtomContext.class,i);
		}
		public List<TerminalNode> POW() { return getTokens(TQLParser.POW); }
		public TerminalNode POW(int i) {
			return getToken(TQLParser.POW, i);
		}
		public PowExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_powExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterPowExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitPowExpression(this);
		}
	}

	public final PowExpressionContext powExpression() throws RecognitionException {
		PowExpressionContext _localctx = new PowExpressionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_powExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			signedAtom();
			setState(210);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(206);
					match(POW);
					setState(207);
					signedAtom();
					}
					} 
				}
				setState(212);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SignedAtomContext extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(TQLParser.PLUS, 0); }
		public SignedAtomContext signedAtom() {
			return getRuleContext(SignedAtomContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(TQLParser.MINUS, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public SignedAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signedAtom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterSignedAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitSignedAtom(this);
		}
	}

	public final SignedAtomContext signedAtom() throws RecognitionException {
		SignedAtomContext _localctx = new SignedAtomContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_signedAtom);
		try {
			setState(219);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(213);
				match(PLUS);
				setState(214);
				signedAtom();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(215);
				match(MINUS);
				setState(216);
				signedAtom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(217);
				atom();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(218);
				idColumn();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AtomContext extends ParserRuleContext {
		public ScientificContext scientific() {
			return getRuleContext(ScientificContext.class,0);
		}
		public TerminalNode BOOLEAN_LITTERAL() { return getToken(TQLParser.BOOLEAN_LITTERAL, 0); }
		public TerminalNode STRING() { return getToken(TQLParser.STRING, 0); }
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(TQLParser.LPAREN, 0); }
		public AdditionalExpressionContext additionalExpression() {
			return getRuleContext(AdditionalExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(TQLParser.RPAREN, 0); }
		public TerminalNode NEGAT() { return getToken(TQLParser.NEGAT, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitAtom(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_atom);
		try {
			setState(231);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SCIENTIFIC_NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(221);
				scientific();
				}
				break;
			case BOOLEAN_LITTERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(222);
				match(BOOLEAN_LITTERAL);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(223);
				match(STRING);
				}
				break;
			case COLUMN_NAME_POINTED:
			case COLUMN_NAME_FRAGMANT:
				enterOuterAlt(_localctx, 4);
				{
				setState(224);
				idColumn();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 5);
				{
				setState(225);
				match(LPAREN);
				setState(226);
				additionalExpression();
				setState(227);
				match(RPAREN);
				}
				break;
			case NEGAT:
				enterOuterAlt(_localctx, 6);
				{
				setState(229);
				match(NEGAT);
				setState(230);
				additionalExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ScientificContext extends ParserRuleContext {
		public TerminalNode SCIENTIFIC_NUMBER() { return getToken(TQLParser.SCIENTIFIC_NUMBER, 0); }
		public ScientificContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scientific; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterScientific(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitScientific(this);
		}
	}

	public final ScientificContext scientific() throws RecognitionException {
		ScientificContext _localctx = new ScientificContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_scientific);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			match(SCIENTIFIC_NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelOpContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(TQLParser.EQ, 0); }
		public TerminalNode GT() { return getToken(TQLParser.GT, 0); }
		public TerminalNode GE() { return getToken(TQLParser.GE, 0); }
		public TerminalNode LT() { return getToken(TQLParser.LT, 0); }
		public TerminalNode LE() { return getToken(TQLParser.LE, 0); }
		public RelOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).enterRelOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TQLListener ) ((TQLListener)listener).exitRelOp(this);
		}
	}

	public final RelOpContext relOp() throws RecognitionException {
		RelOpContext _localctx = new RelOpContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_relOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 33285996544L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001(\u00ee\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u00006\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000<\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000@\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002I\b\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002O\b\u0002"+
		"\u0005\u0002Q\b\u0002\n\u0002\f\u0002T\t\u0002\u0001\u0003\u0001\u0003"+
		"\u0003\u0003X\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"]\b\u0003\u0005\u0003_\b\u0003\n\u0003\f\u0003b\t\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004i\b\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0003\u0005o\b\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0003\u0005t\b\u0005\u0005\u0005v\b\u0005"+
		"\n\u0005\f\u0005y\t\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006\u0082\b\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0003\t\u008a\b\t\u0001\t"+
		"\u0001\t\u0003\t\u008e\b\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0005\u000e\u009a\b"+
		"\u000e\n\u000e\f\u000e\u009d\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0003\u000f\u00a8\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00b2\b\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0003\u0010\u00ba\b\u0010\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0005\u0012\u00c1\b\u0012\n\u0012\f\u0012\u00c4\t\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00c9\b\u0013\n\u0013\f\u0013"+
		"\u00cc\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0005\u0014\u00d1\b"+
		"\u0014\n\u0014\f\u0014\u00d4\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015\u00dc\b\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u00e8\b\u0016\u0001\u0017"+
		"\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0000\u0000\u0019\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&(*,.0\u0000\u0006\u0001\u0000\u0011\u0014\u0001\u0000\u0016"+
		"\u0017\u0001\u0000\f\r\u0001\u0000\u001a\u001b\u0001\u0000\u001c\u001d"+
		"\u0001\u0000\u001e\"\u00fa\u00002\u0001\u0000\u0000\u0000\u0002C\u0001"+
		"\u0000\u0000\u0000\u0004H\u0001\u0000\u0000\u0000\u0006W\u0001\u0000\u0000"+
		"\u0000\bc\u0001\u0000\u0000\u0000\nn\u0001\u0000\u0000\u0000\f\u0081\u0001"+
		"\u0000\u0000\u0000\u000e\u0083\u0001\u0000\u0000\u0000\u0010\u0085\u0001"+
		"\u0000\u0000\u0000\u0012\u0089\u0001\u0000\u0000\u0000\u0014\u008f\u0001"+
		"\u0000\u0000\u0000\u0016\u0091\u0001\u0000\u0000\u0000\u0018\u0093\u0001"+
		"\u0000\u0000\u0000\u001a\u0095\u0001\u0000\u0000\u0000\u001c\u0097\u0001"+
		"\u0000\u0000\u0000\u001e\u00a7\u0001\u0000\u0000\u0000 \u00b9\u0001\u0000"+
		"\u0000\u0000\"\u00bb\u0001\u0000\u0000\u0000$\u00bd\u0001\u0000\u0000"+
		"\u0000&\u00c5\u0001\u0000\u0000\u0000(\u00cd\u0001\u0000\u0000\u0000*"+
		"\u00db\u0001\u0000\u0000\u0000,\u00e7\u0001\u0000\u0000\u0000.\u00e9\u0001"+
		"\u0000\u0000\u00000\u00eb\u0001\u0000\u0000\u000025\u0005\b\u0000\u0000"+
		"36\u0003\u0002\u0001\u000046\u0003\u0004\u0002\u000053\u0001\u0000\u0000"+
		"\u000054\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000078\u0005\t\u0000"+
		"\u00008;\u0003\n\u0005\u00009:\u0005\n\u0000\u0000:<\u0003\u001c\u000e"+
		"\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<?\u0001\u0000"+
		"\u0000\u0000=>\u0005\u000b\u0000\u0000>@\u0003\u0006\u0003\u0000?=\u0001"+
		"\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000\u0000\u0000"+
		"AB\u0005\u0001\u0000\u0000B\u0001\u0001\u0000\u0000\u0000CD\u0005\u001c"+
		"\u0000\u0000D\u0003\u0001\u0000\u0000\u0000EI\u0003\u001a\r\u0000FI\u0003"+
		"\b\u0004\u0000GI\u0003\u0012\t\u0000HE\u0001\u0000\u0000\u0000HF\u0001"+
		"\u0000\u0000\u0000HG\u0001\u0000\u0000\u0000IR\u0001\u0000\u0000\u0000"+
		"JN\u0005\u0002\u0000\u0000KO\u0003\b\u0004\u0000LO\u0003\u0012\t\u0000"+
		"MO\u0003\u001a\r\u0000NK\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000"+
		"NM\u0001\u0000\u0000\u0000OQ\u0001\u0000\u0000\u0000PJ\u0001\u0000\u0000"+
		"\u0000QT\u0001\u0000\u0000\u0000RP\u0001\u0000\u0000\u0000RS\u0001\u0000"+
		"\u0000\u0000S\u0005\u0001\u0000\u0000\u0000TR\u0001\u0000\u0000\u0000"+
		"UX\u0003\b\u0004\u0000VX\u0003\u0012\t\u0000WU\u0001\u0000\u0000\u0000"+
		"WV\u0001\u0000\u0000\u0000X`\u0001\u0000\u0000\u0000Y\\\u0005\u0002\u0000"+
		"\u0000Z]\u0003\b\u0004\u0000[]\u0003\u0012\t\u0000\\Z\u0001\u0000\u0000"+
		"\u0000\\[\u0001\u0000\u0000\u0000]_\u0001\u0000\u0000\u0000^Y\u0001\u0000"+
		"\u0000\u0000_b\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000`a\u0001"+
		"\u0000\u0000\u0000a\u0007\u0001\u0000\u0000\u0000b`\u0001\u0000\u0000"+
		"\u0000cd\u0003\u0010\b\u0000dh\u0005%\u0000\u0000ei\u0003\u0014\n\u0000"+
		"fi\u0003$\u0012\u0000gi\u0005\u001c\u0000\u0000he\u0001\u0000\u0000\u0000"+
		"hf\u0001\u0000\u0000\u0000hg\u0001\u0000\u0000\u0000ij\u0001\u0000\u0000"+
		"\u0000jk\u0005&\u0000\u0000k\t\u0001\u0000\u0000\u0000lo\u0003\f\u0006"+
		"\u0000mo\u0003\u000e\u0007\u0000nl\u0001\u0000\u0000\u0000nm\u0001\u0000"+
		"\u0000\u0000ow\u0001\u0000\u0000\u0000ps\u0005\u0002\u0000\u0000qt\u0003"+
		"\f\u0006\u0000rt\u0003\u000e\u0007\u0000sq\u0001\u0000\u0000\u0000sr\u0001"+
		"\u0000\u0000\u0000tv\u0001\u0000\u0000\u0000up\u0001\u0000\u0000\u0000"+
		"vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000"+
		"\u0000x\u000b\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z{\u0003"+
		"\u000e\u0007\u0000{|\u0003\u0018\f\u0000|\u0082\u0001\u0000\u0000\u0000"+
		"}~\u0003\u000e\u0007\u0000~\u007f\u0005\u000e\u0000\u0000\u007f\u0080"+
		"\u0003\u0018\f\u0000\u0080\u0082\u0001\u0000\u0000\u0000\u0081z\u0001"+
		"\u0000\u0000\u0000\u0081}\u0001\u0000\u0000\u0000\u0082\r\u0001\u0000"+
		"\u0000\u0000\u0083\u0084\u0005\u0015\u0000\u0000\u0084\u000f\u0001\u0000"+
		"\u0000\u0000\u0085\u0086\u0007\u0000\u0000\u0000\u0086\u0011\u0001\u0000"+
		"\u0000\u0000\u0087\u008a\u0003\u0014\n\u0000\u0088\u008a\u0003$\u0012"+
		"\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u0088\u0001\u0000\u0000"+
		"\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u008c\u0005\u000e\u0000"+
		"\u0000\u008c\u008e\u0003\u0016\u000b\u0000\u008d\u008b\u0001\u0000\u0000"+
		"\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u0013\u0001\u0000\u0000"+
		"\u0000\u008f\u0090\u0007\u0001\u0000\u0000\u0090\u0015\u0001\u0000\u0000"+
		"\u0000\u0091\u0092\u0005\u0017\u0000\u0000\u0092\u0017\u0001\u0000\u0000"+
		"\u0000\u0093\u0094\u0005\u0017\u0000\u0000\u0094\u0019\u0001\u0000\u0000"+
		"\u0000\u0095\u0096\u0005\u0010\u0000\u0000\u0096\u001b\u0001\u0000\u0000"+
		"\u0000\u0097\u009b\u0003 \u0010\u0000\u0098\u009a\u0003\u001e\u000f\u0000"+
		"\u0099\u0098\u0001\u0000\u0000\u0000\u009a\u009d\u0001\u0000\u0000\u0000"+
		"\u009b\u0099\u0001\u0000\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000"+
		"\u009c\u001d\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0003\"\u0011\u0000\u009f\u00a0\u0003\u001c\u000e\u0000\u00a0"+
		"\u00a8\u0001\u0000\u0000\u0000\u00a1\u00a2\u0005%\u0000\u0000\u00a2\u00a3"+
		"\u0003\u001c\u000e\u0000\u00a3\u00a4\u0005&\u0000\u0000\u00a4\u00a8\u0001"+
		"\u0000\u0000\u0000\u00a5\u00a6\u0005\u0019\u0000\u0000\u00a6\u00a8\u0003"+
		"\u001c\u000e\u0000\u00a7\u009e\u0001\u0000\u0000\u0000\u00a7\u00a1\u0001"+
		"\u0000\u0000\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u001f\u0001"+
		"\u0000\u0000\u0000\u00a9\u00b1\u0003\u0014\n\u0000\u00aa\u00ab\u00030"+
		"\u0018\u0000\u00ab\u00ac\u0003$\u0012\u0000\u00ac\u00b2\u0001\u0000\u0000"+
		"\u0000\u00ad\u00b2\u0005\u0003\u0000\u0000\u00ae\u00b2\u0005\u0004\u0000"+
		"\u0000\u00af\u00b2\u0005\u0005\u0000\u0000\u00b0\u00b2\u0005\u0006\u0000"+
		"\u0000\u00b1\u00aa\u0001\u0000\u0000\u0000\u00b1\u00ad\u0001\u0000\u0000"+
		"\u0000\u00b1\u00ae\u0001\u0000\u0000\u0000\u00b1\u00af\u0001\u0000\u0000"+
		"\u0000\u00b1\u00b0\u0001\u0000\u0000\u0000\u00b2\u00ba\u0001\u0000\u0000"+
		"\u0000\u00b3\u00b4\u0003\u0018\f\u0000\u00b4\u00b5\u0005\u0007\u0000\u0000"+
		"\u00b5\u00b6\u0005%\u0000\u0000\u00b6\u00b7\u0003\u0014\n\u0000\u00b7"+
		"\u00b8\u0005&\u0000\u0000\u00b8\u00ba\u0001\u0000\u0000\u0000\u00b9\u00a9"+
		"\u0001\u0000\u0000\u0000\u00b9\u00b3\u0001\u0000\u0000\u0000\u00ba!\u0001"+
		"\u0000\u0000\u0000\u00bb\u00bc\u0007\u0002\u0000\u0000\u00bc#\u0001\u0000"+
		"\u0000\u0000\u00bd\u00c2\u0003&\u0013\u0000\u00be\u00bf\u0007\u0003\u0000"+
		"\u0000\u00bf\u00c1\u0003&\u0013\u0000\u00c0\u00be\u0001\u0000\u0000\u0000"+
		"\u00c1\u00c4\u0001\u0000\u0000\u0000\u00c2\u00c0\u0001\u0000\u0000\u0000"+
		"\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3%\u0001\u0000\u0000\u0000\u00c4"+
		"\u00c2\u0001\u0000\u0000\u0000\u00c5\u00ca\u0003(\u0014\u0000\u00c6\u00c7"+
		"\u0007\u0004\u0000\u0000\u00c7\u00c9\u0003(\u0014\u0000\u00c8\u00c6\u0001"+
		"\u0000\u0000\u0000\u00c9\u00cc\u0001\u0000\u0000\u0000\u00ca\u00c8\u0001"+
		"\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\'\u0001\u0000"+
		"\u0000\u0000\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00d2\u0003*\u0015"+
		"\u0000\u00ce\u00cf\u0005#\u0000\u0000\u00cf\u00d1\u0003*\u0015\u0000\u00d0"+
		"\u00ce\u0001\u0000\u0000\u0000\u00d1\u00d4\u0001\u0000\u0000\u0000\u00d2"+
		"\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3"+
		")\u0001\u0000\u0000\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d5\u00d6"+
		"\u0005\u001a\u0000\u0000\u00d6\u00dc\u0003*\u0015\u0000\u00d7\u00d8\u0005"+
		"\u001b\u0000\u0000\u00d8\u00dc\u0003*\u0015\u0000\u00d9\u00dc\u0003,\u0016"+
		"\u0000\u00da\u00dc\u0003\u0014\n\u0000\u00db\u00d5\u0001\u0000\u0000\u0000"+
		"\u00db\u00d7\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000\u0000\u0000"+
		"\u00db\u00da\u0001\u0000\u0000\u0000\u00dc+\u0001\u0000\u0000\u0000\u00dd"+
		"\u00e8\u0003.\u0017\u0000\u00de\u00e8\u0005\u000f\u0000\u0000\u00df\u00e8"+
		"\u0005\u0018\u0000\u0000\u00e0\u00e8\u0003\u0014\n\u0000\u00e1\u00e2\u0005"+
		"%\u0000\u0000\u00e2\u00e3\u0003$\u0012\u0000\u00e3\u00e4\u0005&\u0000"+
		"\u0000\u00e4\u00e8\u0001\u0000\u0000\u0000\u00e5\u00e6\u0005\u0019\u0000"+
		"\u0000\u00e6\u00e8\u0003$\u0012\u0000\u00e7\u00dd\u0001\u0000\u0000\u0000"+
		"\u00e7\u00de\u0001\u0000\u0000\u0000\u00e7\u00df\u0001\u0000\u0000\u0000"+
		"\u00e7\u00e0\u0001\u0000\u0000\u0000\u00e7\u00e1\u0001\u0000\u0000\u0000"+
		"\u00e7\u00e5\u0001\u0000\u0000\u0000\u00e8-\u0001\u0000\u0000\u0000\u00e9"+
		"\u00ea\u0005\'\u0000\u0000\u00ea/\u0001\u0000\u0000\u0000\u00eb\u00ec"+
		"\u0007\u0005\u0000\u0000\u00ec1\u0001\u0000\u0000\u0000\u00195;?HNRW\\"+
		"`hnsw\u0081\u0089\u008d\u009b\u00a7\u00b1\u00b9\u00c2\u00ca\u00d2\u00db"+
		"\u00e7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}