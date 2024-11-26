// Generated from TQL.g4 by ANTLR 4.13.2
package taack.jdbc.common.tql.gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class TQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, IN_ELEMENTS=7, SELECT=8, 
		FROM=9, WHERE=10, GROUP_BY=11, AND=12, OR=13, AS=14, BOOLEAN_LITTERAL=15, 
		COUNT=16, SUM=17, DISTINCT=18, ELEMENTS=19, TABLE_NAME=20, TABLE_STAR=21, 
		COLUMN_NAME_FRAGMANT=22, COLUMN_NAME_POINTED=23, STRING=24, NEGAT=25, 
		PLUS=26, MINUS=27, TIMES=28, DIV=29, GT=30, GE=31, LT=32, LE=33, EQ=34, 
		POW=35, PI=36, LPAREN=37, RPAREN=38, SCIENTIFIC_NUMBER=39, WS=40;
	public static final int
		RULE_tql = 0, RULE_selectStar = 1, RULE_selectExpression = 2, RULE_groupByExpression = 3, 
		RULE_selectFunctionExpression = 4, RULE_fromExpression = 5, RULE_idTableWithAlias = 6, 
		RULE_idTable = 7, RULE_selFunc = 8, RULE_columnExpression = 9, RULE_aliasColumn = 10, 
		RULE_aliasTable = 11, RULE_idTableStar = 12, RULE_idColumn = 13, RULE_whereClause = 14, 
		RULE_whereExpression = 15, RULE_whereExpressionElement = 16, RULE_junctionOp = 17, 
		RULE_additionalExpression = 18, RULE_multiplyingExpression = 19, RULE_powExpression = 20, 
		RULE_signedAtom = 21, RULE_atom = 22, RULE_scientific = 23, RULE_relOp = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"tql", "selectStar", "selectExpression", "groupByExpression", "selectFunctionExpression", 
			"fromExpression", "idTableWithAlias", "idTable", "selFunc", "columnExpression", 
			"aliasColumn", "aliasTable", "idTableStar", "idColumn", "whereClause", 
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
			"WHERE", "GROUP_BY", "AND", "OR", "AS", "BOOLEAN_LITTERAL", "COUNT", 
			"SUM", "DISTINCT", "ELEMENTS", "TABLE_NAME", "TABLE_STAR", "COLUMN_NAME_FRAGMANT", 
			"COLUMN_NAME_POINTED", "STRING", "NEGAT", "PLUS", "MINUS", "TIMES", "DIV", 
			"GT", "GE", "LT", "LE", "EQ", "POW", "PI", "LPAREN", "RPAREN", "SCIENTIFIC_NUMBER", 
			"WS"
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
		public TerminalNode EOF() { return getToken(TQLParser.EOF, 0); }
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
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
			case TABLE_STAR:
			case COLUMN_NAME_POINTED:
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
			setState(66);
			match(EOF);
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
			setState(68);
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
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TABLE_STAR:
				{
				setState(70);
				idTableStar();
				}
				break;
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
				{
				setState(71);
				selectFunctionExpression();
				}
				break;
			case BOOLEAN_LITTERAL:
			case COLUMN_NAME_POINTED:
			case STRING:
			case NEGAT:
			case PLUS:
			case MINUS:
			case LPAREN:
			case SCIENTIFIC_NUMBER:
				{
				setState(72);
				columnExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(75);
				match(T__1);
				setState(79);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COUNT:
				case SUM:
				case DISTINCT:
				case ELEMENTS:
					{
					setState(76);
					selectFunctionExpression();
					}
					break;
				case BOOLEAN_LITTERAL:
				case COLUMN_NAME_POINTED:
				case STRING:
				case NEGAT:
				case PLUS:
				case MINUS:
				case LPAREN:
				case SCIENTIFIC_NUMBER:
					{
					setState(77);
					columnExpression();
					}
					break;
				case TABLE_STAR:
					{
					setState(78);
					idTableStar();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(85);
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
			setState(88);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COUNT:
			case SUM:
			case DISTINCT:
			case ELEMENTS:
				{
				setState(86);
				selectFunctionExpression();
				}
				break;
			case BOOLEAN_LITTERAL:
			case COLUMN_NAME_POINTED:
			case STRING:
			case NEGAT:
			case PLUS:
			case MINUS:
			case LPAREN:
			case SCIENTIFIC_NUMBER:
				{
				setState(87);
				columnExpression();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(90);
				match(T__1);
				setState(93);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case COUNT:
				case SUM:
				case DISTINCT:
				case ELEMENTS:
					{
					setState(91);
					selectFunctionExpression();
					}
					break;
				case BOOLEAN_LITTERAL:
				case COLUMN_NAME_POINTED:
				case STRING:
				case NEGAT:
				case PLUS:
				case MINUS:
				case LPAREN:
				case SCIENTIFIC_NUMBER:
					{
					setState(92);
					columnExpression();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(99);
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
		public TerminalNode AS() { return getToken(TQLParser.AS, 0); }
		public AliasColumnContext aliasColumn() {
			return getRuleContext(AliasColumnContext.class,0);
		}
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			selFunc();
			setState(101);
			match(LPAREN);
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(102);
				idColumn();
				}
				break;
			case 2:
				{
				setState(103);
				additionalExpression();
				}
				break;
			case 3:
				{
				setState(104);
				match(TIMES);
				}
				break;
			}
			setState(107);
			match(RPAREN);
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(108);
				match(AS);
				setState(109);
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
			setState(114);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(112);
				idTableWithAlias();
				}
				break;
			case 2:
				{
				setState(113);
				idTable();
				}
				break;
			}
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(116);
				match(T__1);
				setState(119);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
				case 1:
					{
					setState(117);
					idTableWithAlias();
					}
					break;
				case 2:
					{
					setState(118);
					idTable();
					}
					break;
				}
				}
				}
				setState(125);
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
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(126);
				idTable();
				setState(127);
				aliasTable();
				}
				break;
			case 2:
				{
				setState(129);
				idTable();
				setState(130);
				match(AS);
				setState(131);
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
			setState(135);
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
			setState(137);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 983040L) != 0)) ) {
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
			setState(141);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(139);
				idColumn();
				}
				break;
			case 2:
				{
				setState(140);
				additionalExpression();
				}
				break;
			}
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(143);
				match(AS);
				setState(144);
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
		enterRule(_localctx, 20, RULE_aliasColumn);
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
		enterRule(_localctx, 22, RULE_aliasTable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
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
		enterRule(_localctx, 24, RULE_idTableStar);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
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
	public static class IdColumnContext extends ParserRuleContext {
		public TerminalNode COLUMN_NAME_POINTED() { return getToken(TQLParser.COLUMN_NAME_POINTED, 0); }
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
		enterRule(_localctx, 26, RULE_idColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			match(COLUMN_NAME_POINTED);
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
			setState(155);
			whereExpressionElement();
			setState(159);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(156);
					whereExpression();
					}
					} 
				}
				setState(161);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
			setState(171);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AND:
			case OR:
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				junctionOp();
				setState(163);
				whereClause();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				match(LPAREN);
				setState(166);
				whereClause();
				setState(167);
				match(RPAREN);
				}
				break;
			case NEGAT:
				enterOuterAlt(_localctx, 3);
				{
				setState(169);
				match(NEGAT);
				setState(170);
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
			setState(189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLUMN_NAME_POINTED:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(173);
				idColumn();
				setState(181);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case GT:
				case GE:
				case LT:
				case LE:
				case EQ:
					{
					{
					setState(174);
					relOp();
					setState(175);
					additionalExpression();
					}
					}
					break;
				case T__2:
					{
					setState(177);
					match(T__2);
					}
					break;
				case T__3:
					{
					setState(178);
					match(T__3);
					}
					break;
				case T__4:
					{
					setState(179);
					match(T__4);
					}
					break;
				case T__5:
					{
					setState(180);
					match(T__5);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				break;
			case COLUMN_NAME_FRAGMANT:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(183);
				aliasTable();
				setState(184);
				match(IN_ELEMENTS);
				setState(185);
				match(LPAREN);
				setState(186);
				idColumn();
				setState(187);
				match(RPAREN);
				}
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
			setState(191);
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
			setState(193);
			multiplyingExpression();
			setState(198);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(194);
					_la = _input.LA(1);
					if ( !(_la==PLUS || _la==MINUS) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(195);
					multiplyingExpression();
					}
					} 
				}
				setState(200);
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
			setState(201);
			powExpression();
			setState(206);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(202);
					_la = _input.LA(1);
					if ( !(_la==TIMES || _la==DIV) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(203);
					powExpression();
					}
					} 
				}
				setState(208);
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
			setState(209);
			signedAtom();
			setState(214);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(210);
					match(POW);
					setState(211);
					signedAtom();
					}
					} 
				}
				setState(216);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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
			setState(223);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(217);
				match(PLUS);
				setState(218);
				signedAtom();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(219);
				match(MINUS);
				setState(220);
				signedAtom();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(221);
				atom();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(222);
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
			setState(235);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SCIENTIFIC_NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(225);
				scientific();
				}
				break;
			case BOOLEAN_LITTERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(226);
				match(BOOLEAN_LITTERAL);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(227);
				match(STRING);
				}
				break;
			case COLUMN_NAME_POINTED:
				enterOuterAlt(_localctx, 4);
				{
				setState(228);
				idColumn();
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 5);
				{
				setState(229);
				match(LPAREN);
				setState(230);
				additionalExpression();
				setState(231);
				match(RPAREN);
				}
				break;
			case NEGAT:
				enterOuterAlt(_localctx, 6);
				{
				setState(233);
				match(NEGAT);
				setState(234);
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
			setState(237);
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
			setState(239);
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
		"\u0004\u0001(\u00f2\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u00006\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000<\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u0000@\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"J\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002"+
		"P\b\u0002\u0005\u0002R\b\u0002\n\u0002\f\u0002U\t\u0002\u0001\u0003\u0001"+
		"\u0003\u0003\u0003Y\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003^\b\u0003\u0005\u0003`\b\u0003\n\u0003\f\u0003c\t\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004j\b\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004o\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0003\u0005s\b\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005x\b\u0005\u0005\u0005z\b\u0005\n\u0005\f\u0005}\t\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0003\u0006\u0086\b\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0003\t\u008e\b\t\u0001\t\u0001\t\u0003\t\u0092\b\t"+
		"\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0005\u000e\u009e\b\u000e\n\u000e\f\u000e\u00a1"+
		"\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u00ac\b\u000f\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0003\u0010\u00b6\b\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00be\b\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00c5"+
		"\b\u0012\n\u0012\f\u0012\u00c8\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0005\u0013\u00cd\b\u0013\n\u0013\f\u0013\u00d0\t\u0013\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0005\u0014\u00d5\b\u0014\n\u0014\f\u0014\u00d8\t\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0003\u0015\u00e0\b\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0003\u0016\u00ec\b\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0000\u0000\u0019\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0\u0000\u0005\u0001"+
		"\u0000\u0010\u0013\u0001\u0000\f\r\u0001\u0000\u001a\u001b\u0001\u0000"+
		"\u001c\u001d\u0001\u0000\u001e\"\u00ff\u00002\u0001\u0000\u0000\u0000"+
		"\u0002D\u0001\u0000\u0000\u0000\u0004I\u0001\u0000\u0000\u0000\u0006X"+
		"\u0001\u0000\u0000\u0000\bd\u0001\u0000\u0000\u0000\nr\u0001\u0000\u0000"+
		"\u0000\f\u0085\u0001\u0000\u0000\u0000\u000e\u0087\u0001\u0000\u0000\u0000"+
		"\u0010\u0089\u0001\u0000\u0000\u0000\u0012\u008d\u0001\u0000\u0000\u0000"+
		"\u0014\u0093\u0001\u0000\u0000\u0000\u0016\u0095\u0001\u0000\u0000\u0000"+
		"\u0018\u0097\u0001\u0000\u0000\u0000\u001a\u0099\u0001\u0000\u0000\u0000"+
		"\u001c\u009b\u0001\u0000\u0000\u0000\u001e\u00ab\u0001\u0000\u0000\u0000"+
		" \u00bd\u0001\u0000\u0000\u0000\"\u00bf\u0001\u0000\u0000\u0000$\u00c1"+
		"\u0001\u0000\u0000\u0000&\u00c9\u0001\u0000\u0000\u0000(\u00d1\u0001\u0000"+
		"\u0000\u0000*\u00df\u0001\u0000\u0000\u0000,\u00eb\u0001\u0000\u0000\u0000"+
		".\u00ed\u0001\u0000\u0000\u00000\u00ef\u0001\u0000\u0000\u000025\u0005"+
		"\b\u0000\u000036\u0003\u0002\u0001\u000046\u0003\u0004\u0002\u000053\u0001"+
		"\u0000\u0000\u000054\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u0000"+
		"78\u0005\t\u0000\u00008;\u0003\n\u0005\u00009:\u0005\n\u0000\u0000:<\u0003"+
		"\u001c\u000e\u0000;9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000"+
		"<?\u0001\u0000\u0000\u0000=>\u0005\u000b\u0000\u0000>@\u0003\u0006\u0003"+
		"\u0000?=\u0001\u0000\u0000\u0000?@\u0001\u0000\u0000\u0000@A\u0001\u0000"+
		"\u0000\u0000AB\u0005\u0001\u0000\u0000BC\u0005\u0000\u0000\u0001C\u0001"+
		"\u0001\u0000\u0000\u0000DE\u0005\u001c\u0000\u0000E\u0003\u0001\u0000"+
		"\u0000\u0000FJ\u0003\u0018\f\u0000GJ\u0003\b\u0004\u0000HJ\u0003\u0012"+
		"\t\u0000IF\u0001\u0000\u0000\u0000IG\u0001\u0000\u0000\u0000IH\u0001\u0000"+
		"\u0000\u0000JS\u0001\u0000\u0000\u0000KO\u0005\u0002\u0000\u0000LP\u0003"+
		"\b\u0004\u0000MP\u0003\u0012\t\u0000NP\u0003\u0018\f\u0000OL\u0001\u0000"+
		"\u0000\u0000OM\u0001\u0000\u0000\u0000ON\u0001\u0000\u0000\u0000PR\u0001"+
		"\u0000\u0000\u0000QK\u0001\u0000\u0000\u0000RU\u0001\u0000\u0000\u0000"+
		"SQ\u0001\u0000\u0000\u0000ST\u0001\u0000\u0000\u0000T\u0005\u0001\u0000"+
		"\u0000\u0000US\u0001\u0000\u0000\u0000VY\u0003\b\u0004\u0000WY\u0003\u0012"+
		"\t\u0000XV\u0001\u0000\u0000\u0000XW\u0001\u0000\u0000\u0000Ya\u0001\u0000"+
		"\u0000\u0000Z]\u0005\u0002\u0000\u0000[^\u0003\b\u0004\u0000\\^\u0003"+
		"\u0012\t\u0000][\u0001\u0000\u0000\u0000]\\\u0001\u0000\u0000\u0000^`"+
		"\u0001\u0000\u0000\u0000_Z\u0001\u0000\u0000\u0000`c\u0001\u0000\u0000"+
		"\u0000a_\u0001\u0000\u0000\u0000ab\u0001\u0000\u0000\u0000b\u0007\u0001"+
		"\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000de\u0003\u0010\b\u0000ei\u0005"+
		"%\u0000\u0000fj\u0003\u001a\r\u0000gj\u0003$\u0012\u0000hj\u0005\u001c"+
		"\u0000\u0000if\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000ih\u0001"+
		"\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kn\u0005&\u0000\u0000lm\u0005"+
		"\u000e\u0000\u0000mo\u0003\u0014\n\u0000nl\u0001\u0000\u0000\u0000no\u0001"+
		"\u0000\u0000\u0000o\t\u0001\u0000\u0000\u0000ps\u0003\f\u0006\u0000qs"+
		"\u0003\u000e\u0007\u0000rp\u0001\u0000\u0000\u0000rq\u0001\u0000\u0000"+
		"\u0000s{\u0001\u0000\u0000\u0000tw\u0005\u0002\u0000\u0000ux\u0003\f\u0006"+
		"\u0000vx\u0003\u000e\u0007\u0000wu\u0001\u0000\u0000\u0000wv\u0001\u0000"+
		"\u0000\u0000xz\u0001\u0000\u0000\u0000yt\u0001\u0000\u0000\u0000z}\u0001"+
		"\u0000\u0000\u0000{y\u0001\u0000\u0000\u0000{|\u0001\u0000\u0000\u0000"+
		"|\u000b\u0001\u0000\u0000\u0000}{\u0001\u0000\u0000\u0000~\u007f\u0003"+
		"\u000e\u0007\u0000\u007f\u0080\u0003\u0016\u000b\u0000\u0080\u0086\u0001"+
		"\u0000\u0000\u0000\u0081\u0082\u0003\u000e\u0007\u0000\u0082\u0083\u0005"+
		"\u000e\u0000\u0000\u0083\u0084\u0003\u0016\u000b\u0000\u0084\u0086\u0001"+
		"\u0000\u0000\u0000\u0085~\u0001\u0000\u0000\u0000\u0085\u0081\u0001\u0000"+
		"\u0000\u0000\u0086\r\u0001\u0000\u0000\u0000\u0087\u0088\u0005\u0014\u0000"+
		"\u0000\u0088\u000f\u0001\u0000\u0000\u0000\u0089\u008a\u0007\u0000\u0000"+
		"\u0000\u008a\u0011\u0001\u0000\u0000\u0000\u008b\u008e\u0003\u001a\r\u0000"+
		"\u008c\u008e\u0003$\u0012\u0000\u008d\u008b\u0001\u0000\u0000\u0000\u008d"+
		"\u008c\u0001\u0000\u0000\u0000\u008e\u0091\u0001\u0000\u0000\u0000\u008f"+
		"\u0090\u0005\u000e\u0000\u0000\u0090\u0092\u0003\u0014\n\u0000\u0091\u008f"+
		"\u0001\u0000\u0000\u0000\u0091\u0092\u0001\u0000\u0000\u0000\u0092\u0013"+
		"\u0001\u0000\u0000\u0000\u0093\u0094\u0005\u0016\u0000\u0000\u0094\u0015"+
		"\u0001\u0000\u0000\u0000\u0095\u0096\u0005\u0016\u0000\u0000\u0096\u0017"+
		"\u0001\u0000\u0000\u0000\u0097\u0098\u0005\u0015\u0000\u0000\u0098\u0019"+
		"\u0001\u0000\u0000\u0000\u0099\u009a\u0005\u0017\u0000\u0000\u009a\u001b"+
		"\u0001\u0000\u0000\u0000\u009b\u009f\u0003 \u0010\u0000\u009c\u009e\u0003"+
		"\u001e\u000f\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u00a1\u0001"+
		"\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u009f\u00a0\u0001"+
		"\u0000\u0000\u0000\u00a0\u001d\u0001\u0000\u0000\u0000\u00a1\u009f\u0001"+
		"\u0000\u0000\u0000\u00a2\u00a3\u0003\"\u0011\u0000\u00a3\u00a4\u0003\u001c"+
		"\u000e\u0000\u00a4\u00ac\u0001\u0000\u0000\u0000\u00a5\u00a6\u0005%\u0000"+
		"\u0000\u00a6\u00a7\u0003\u001c\u000e\u0000\u00a7\u00a8\u0005&\u0000\u0000"+
		"\u00a8\u00ac\u0001\u0000\u0000\u0000\u00a9\u00aa\u0005\u0019\u0000\u0000"+
		"\u00aa\u00ac\u0003\u001c\u000e\u0000\u00ab\u00a2\u0001\u0000\u0000\u0000"+
		"\u00ab\u00a5\u0001\u0000\u0000\u0000\u00ab\u00a9\u0001\u0000\u0000\u0000"+
		"\u00ac\u001f\u0001\u0000\u0000\u0000\u00ad\u00b5\u0003\u001a\r\u0000\u00ae"+
		"\u00af\u00030\u0018\u0000\u00af\u00b0\u0003$\u0012\u0000\u00b0\u00b6\u0001"+
		"\u0000\u0000\u0000\u00b1\u00b6\u0005\u0003\u0000\u0000\u00b2\u00b6\u0005"+
		"\u0004\u0000\u0000\u00b3\u00b6\u0005\u0005\u0000\u0000\u00b4\u00b6\u0005"+
		"\u0006\u0000\u0000\u00b5\u00ae\u0001\u0000\u0000\u0000\u00b5\u00b1\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b2\u0001\u0000\u0000\u0000\u00b5\u00b3\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b4\u0001\u0000\u0000\u0000\u00b6\u00be\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b8\u0003\u0016\u000b\u0000\u00b8\u00b9\u0005"+
		"\u0007\u0000\u0000\u00b9\u00ba\u0005%\u0000\u0000\u00ba\u00bb\u0003\u001a"+
		"\r\u0000\u00bb\u00bc\u0005&\u0000\u0000\u00bc\u00be\u0001\u0000\u0000"+
		"\u0000\u00bd\u00ad\u0001\u0000\u0000\u0000\u00bd\u00b7\u0001\u0000\u0000"+
		"\u0000\u00be!\u0001\u0000\u0000\u0000\u00bf\u00c0\u0007\u0001\u0000\u0000"+
		"\u00c0#\u0001\u0000\u0000\u0000\u00c1\u00c6\u0003&\u0013\u0000\u00c2\u00c3"+
		"\u0007\u0002\u0000\u0000\u00c3\u00c5\u0003&\u0013\u0000\u00c4\u00c2\u0001"+
		"\u0000\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6\u00c4\u0001"+
		"\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7%\u0001\u0000"+
		"\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c9\u00ce\u0003(\u0014"+
		"\u0000\u00ca\u00cb\u0007\u0003\u0000\u0000\u00cb\u00cd\u0003(\u0014\u0000"+
		"\u00cc\u00ca\u0001\u0000\u0000\u0000\u00cd\u00d0\u0001\u0000\u0000\u0000"+
		"\u00ce\u00cc\u0001\u0000\u0000\u0000\u00ce\u00cf\u0001\u0000\u0000\u0000"+
		"\u00cf\'\u0001\u0000\u0000\u0000\u00d0\u00ce\u0001\u0000\u0000\u0000\u00d1"+
		"\u00d6\u0003*\u0015\u0000\u00d2\u00d3\u0005#\u0000\u0000\u00d3\u00d5\u0003"+
		"*\u0015\u0000\u00d4\u00d2\u0001\u0000\u0000\u0000\u00d5\u00d8\u0001\u0000"+
		"\u0000\u0000\u00d6\u00d4\u0001\u0000\u0000\u0000\u00d6\u00d7\u0001\u0000"+
		"\u0000\u0000\u00d7)\u0001\u0000\u0000\u0000\u00d8\u00d6\u0001\u0000\u0000"+
		"\u0000\u00d9\u00da\u0005\u001a\u0000\u0000\u00da\u00e0\u0003*\u0015\u0000"+
		"\u00db\u00dc\u0005\u001b\u0000\u0000\u00dc\u00e0\u0003*\u0015\u0000\u00dd"+
		"\u00e0\u0003,\u0016\u0000\u00de\u00e0\u0003\u001a\r\u0000\u00df\u00d9"+
		"\u0001\u0000\u0000\u0000\u00df\u00db\u0001\u0000\u0000\u0000\u00df\u00dd"+
		"\u0001\u0000\u0000\u0000\u00df\u00de\u0001\u0000\u0000\u0000\u00e0+\u0001"+
		"\u0000\u0000\u0000\u00e1\u00ec\u0003.\u0017\u0000\u00e2\u00ec\u0005\u000f"+
		"\u0000\u0000\u00e3\u00ec\u0005\u0018\u0000\u0000\u00e4\u00ec\u0003\u001a"+
		"\r\u0000\u00e5\u00e6\u0005%\u0000\u0000\u00e6\u00e7\u0003$\u0012\u0000"+
		"\u00e7\u00e8\u0005&\u0000\u0000\u00e8\u00ec\u0001\u0000\u0000\u0000\u00e9"+
		"\u00ea\u0005\u0019\u0000\u0000\u00ea\u00ec\u0003$\u0012\u0000\u00eb\u00e1"+
		"\u0001\u0000\u0000\u0000\u00eb\u00e2\u0001\u0000\u0000\u0000\u00eb\u00e3"+
		"\u0001\u0000\u0000\u0000\u00eb\u00e4\u0001\u0000\u0000\u0000\u00eb\u00e5"+
		"\u0001\u0000\u0000\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00ec-\u0001"+
		"\u0000\u0000\u0000\u00ed\u00ee\u0005\'\u0000\u0000\u00ee/\u0001\u0000"+
		"\u0000\u0000\u00ef\u00f0\u0007\u0004\u0000\u0000\u00f01\u0001\u0000\u0000"+
		"\u0000\u001a5;?IOSX]ainrw{\u0085\u008d\u0091\u009f\u00ab\u00b5\u00bd\u00c6"+
		"\u00ce\u00d6\u00df\u00eb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}