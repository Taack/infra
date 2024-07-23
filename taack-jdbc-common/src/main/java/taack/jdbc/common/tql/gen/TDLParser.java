// Generated from TDL.g4 by ANTLR 4.13.1
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
public class TDLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, AS=2, TABLE=3, BARCHART=4, COLUMN_NAME_FRAGMANT=5, STRING=6, WS=7;
	public static final int
		RULE_tdl = 0, RULE_displayKind = 1, RULE_columnExpressions = 2, RULE_columnExpression = 3, 
		RULE_idColumn = 4, RULE_aliasColumn = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"tdl", "displayKind", "columnExpressions", "columnExpression", "idColumn", 
			"aliasColumn"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, "AS", "TABLE", "BARCHART", "COLUMN_NAME_FRAGMANT", "STRING", 
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
	public String getGrammarFileName() { return "TDL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TDLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TdlContext extends ParserRuleContext {
		public DisplayKindContext displayKind() {
			return getRuleContext(DisplayKindContext.class,0);
		}
		public ColumnExpressionsContext columnExpressions() {
			return getRuleContext(ColumnExpressionsContext.class,0);
		}
		public TerminalNode EOF() { return getToken(TDLParser.EOF, 0); }
		public TdlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tdl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterTdl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitTdl(this);
		}
	}

	public final TdlContext tdl() throws RecognitionException {
		TdlContext _localctx = new TdlContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tdl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			displayKind();
			setState(13);
			columnExpressions();
			setState(14);
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
	public static class DisplayKindContext extends ParserRuleContext {
		public TerminalNode TABLE() { return getToken(TDLParser.TABLE, 0); }
		public TerminalNode BARCHART() { return getToken(TDLParser.BARCHART, 0); }
		public DisplayKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_displayKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterDisplayKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitDisplayKind(this);
		}
	}

	public final DisplayKindContext displayKind() throws RecognitionException {
		DisplayKindContext _localctx = new DisplayKindContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_displayKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			_la = _input.LA(1);
			if ( !(_la==TABLE || _la==BARCHART) ) {
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
	public static class ColumnExpressionsContext extends ParserRuleContext {
		public List<ColumnExpressionContext> columnExpression() {
			return getRuleContexts(ColumnExpressionContext.class);
		}
		public ColumnExpressionContext columnExpression(int i) {
			return getRuleContext(ColumnExpressionContext.class,i);
		}
		public ColumnExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnExpressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterColumnExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitColumnExpressions(this);
		}
	}

	public final ColumnExpressionsContext columnExpressions() throws RecognitionException {
		ColumnExpressionsContext _localctx = new ColumnExpressionsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_columnExpressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			columnExpression();
			setState(23);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(19);
				match(T__0);
				setState(20);
				columnExpression();
				}
				}
				setState(25);
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
	public static class ColumnExpressionContext extends ParserRuleContext {
		public IdColumnContext idColumn() {
			return getRuleContext(IdColumnContext.class,0);
		}
		public TerminalNode AS() { return getToken(TDLParser.AS, 0); }
		public AliasColumnContext aliasColumn() {
			return getRuleContext(AliasColumnContext.class,0);
		}
		public ColumnExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterColumnExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitColumnExpression(this);
		}
	}

	public final ColumnExpressionContext columnExpression() throws RecognitionException {
		ColumnExpressionContext _localctx = new ColumnExpressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_columnExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(26);
			idColumn();
			}
			setState(29);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(27);
				match(AS);
				setState(28);
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
		public TerminalNode COLUMN_NAME_FRAGMANT() { return getToken(TDLParser.COLUMN_NAME_FRAGMANT, 0); }
		public IdColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_idColumn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterIdColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitIdColumn(this);
		}
	}

	public final IdColumnContext idColumn() throws RecognitionException {
		IdColumnContext _localctx = new IdColumnContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_idColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
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
	public static class AliasColumnContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(TDLParser.STRING, 0); }
		public AliasColumnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasColumn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).enterAliasColumn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TDLListener ) ((TDLListener)listener).exitAliasColumn(this);
		}
	}

	public final AliasColumnContext aliasColumn() throws RecognitionException {
		AliasColumnContext _localctx = new AliasColumnContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_aliasColumn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			match(STRING);
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
		"\u0004\u0001\u0007$\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002\u0016"+
		"\b\u0002\n\u0002\f\u0002\u0019\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003\u001e\b\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0000\u0000\u0006\u0000\u0002\u0004\u0006\b\n\u0000\u0001"+
		"\u0001\u0000\u0003\u0004\u001f\u0000\f\u0001\u0000\u0000\u0000\u0002\u0010"+
		"\u0001\u0000\u0000\u0000\u0004\u0012\u0001\u0000\u0000\u0000\u0006\u001a"+
		"\u0001\u0000\u0000\u0000\b\u001f\u0001\u0000\u0000\u0000\n!\u0001\u0000"+
		"\u0000\u0000\f\r\u0003\u0002\u0001\u0000\r\u000e\u0003\u0004\u0002\u0000"+
		"\u000e\u000f\u0005\u0000\u0000\u0001\u000f\u0001\u0001\u0000\u0000\u0000"+
		"\u0010\u0011\u0007\u0000\u0000\u0000\u0011\u0003\u0001\u0000\u0000\u0000"+
		"\u0012\u0017\u0003\u0006\u0003\u0000\u0013\u0014\u0005\u0001\u0000\u0000"+
		"\u0014\u0016\u0003\u0006\u0003\u0000\u0015\u0013\u0001\u0000\u0000\u0000"+
		"\u0016\u0019\u0001\u0000\u0000\u0000\u0017\u0015\u0001\u0000\u0000\u0000"+
		"\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u0005\u0001\u0000\u0000\u0000"+
		"\u0019\u0017\u0001\u0000\u0000\u0000\u001a\u001d\u0003\b\u0004\u0000\u001b"+
		"\u001c\u0005\u0002\u0000\u0000\u001c\u001e\u0003\n\u0005\u0000\u001d\u001b"+
		"\u0001\u0000\u0000\u0000\u001d\u001e\u0001\u0000\u0000\u0000\u001e\u0007"+
		"\u0001\u0000\u0000\u0000\u001f \u0005\u0005\u0000\u0000 \t\u0001\u0000"+
		"\u0000\u0000!\"\u0005\u0006\u0000\u0000\"\u000b\u0001\u0000\u0000\u0000"+
		"\u0002\u0017\u001d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}